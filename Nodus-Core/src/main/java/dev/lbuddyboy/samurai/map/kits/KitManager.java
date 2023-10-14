package dev.lbuddyboy.samurai.map.kits;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.flash.util.LocationUtils;
import dev.lbuddyboy.flash.util.StringUtils;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.custom.schedule.command.ScheduleCommand;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.*;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import dev.lbuddyboy.samurai.util.object.SortedList;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Only gets instantiated if the server is in KitMap mode.
 */
public class KitManager {

    private static File DEFAULT_KITS_FILE = new File(Samurai.getInstance().getDataFolder(), "default-kits.json");
    private static Type DEFAULT_KITS_TYPE = new TypeToken<List<DefaultKit>>() {
    }.getType();

    private static File USER_KITS_FILE = new File(Samurai.getInstance().getDataFolder(), "user-kits.json");
    private static Type USER_KITS_TYPE = new TypeToken<Map<UUID, List<Kit>>>() {
    }.getType();

    @Getter
    private List<DefaultKit> defaultKits = new SortedList<>(Comparator.comparingInt(DefaultKit::getOrder));
    private Map<UUID, List<Kit>> userKits = Maps.newHashMap();
    @Getter
    private Map<Location, BlockData> placedBlocks;
    @Getter
    private Map<Location, BlockData> brokenBlocks;
    @Getter
    private final List<Entity> entities;

    @Getter
    private final Map<UUID, PlayerInventorySerializer.PlayerInventoryWrapper> storedStates = new HashMap<>();

    public KitManager() {
        loadDefaultKits();
        loadUserKits();

        // print info
        Samurai.getInstance().getLogger().info("- Kit Manager - Loaded " + defaultKits.size() + " default kits!");
        Samurai.getInstance().getLogger().info("- Kit Manager - Loaded " + userKits.size() + " user kits!");

        Bukkit.getScheduler().runTaskTimerAsynchronously(Samurai.getInstance(), () -> {
            saveDefaultKits();
            saveUserKits();
        }, 20L * 60L * 2L, 20L * 60L * 2L);

        Bukkit.getPluginManager().registerEvents(new KitListener(), Samurai.getInstance());

        placedBlocks = new HashMap<>();
        brokenBlocks = new HashMap<>();
        entities = new ArrayList<>();

        loadData();
        resetEnd();
    }

    public void saveData() {
        MongoDatabase database = Samurai.getInstance().getMongoPool().getDatabase(Samurai.getMONGO_DB_NAME());
        MongoCollection<Document> placements = database.getCollection("EndResetPlacement"), breaks = database.getCollection("EndResetBreaks");

        placements.drop();
        breaks.drop();

        for (Map.Entry<Location, BlockData> entry : placedBlocks.entrySet()) {
            Document document = new Document();

            document.put("location", LocationSerializer.serializeString(entry.getKey()));
            document.put("blockData", entry.getValue().getAsString());

            placements.insertOne(document);
        }

        for (Map.Entry<Location, BlockData> entry : brokenBlocks.entrySet()) {
            Document document = new Document();

            document.put("location", LocationSerializer.serializeString(entry.getKey()));
            document.put("blockData", entry.getValue().getAsString());

            breaks.insertOne(document);
        }

    }

    public void loadData() {
        MongoDatabase database = Samurai.getInstance().getMongoPool().getDatabase(Samurai.getMONGO_DB_NAME());
        MongoCollection<Document> placements = database.getCollection("EndResetPlacement"), breaks = database.getCollection("EndResetBreaks");

        for (Document document : placements.find()) {
            Location location = LocationSerializer.deserializeString(document.getString("location"));

            this.placedBlocks.put(location, null);
        }

        for (Document document : breaks.find()) {
            Location location = LocationSerializer.deserializeString(document.getString("location"));
            BlockData blockData = Bukkit.createBlockData(document.getString("blockData"));

            this.brokenBlocks.put(location, blockData);
        }

        resetEnd();
    }

    public void resetEnd() {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;

        Tasks.run(() -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().getEnvironment() != World.Environment.THE_END) continue;

                SpawnTagHandler.removeTag(player);
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage(CC.translate("&aThe end world has reset, you've been teleported to spawn."));
            }

            for (World world : Bukkit.getWorlds()) {
                if (world.getEnvironment() != World.Environment.THE_END) continue;

                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Player) {
                        continue;
                    }

                    entity.remove();
                }
            }

            for (Map.Entry<Location, BlockData> entry : placedBlocks.entrySet()) {
                entry.getKey().getBlock().setType(Material.AIR);
            }

            for (Map.Entry<Location, BlockData> entry : brokenBlocks.entrySet()) {
                entry.getKey().getBlock().setBlockData(entry.getValue(), false);
            }

            ScheduleCommand.scheduleAdd(Bukkit.getConsoleSender(), "End-Reset", "15m", "kitadmin resetend");
            this.brokenBlocks.clear();
            this.placedBlocks.clear();
        });
    }

    private void loadDefaultKits() {
        if (DEFAULT_KITS_FILE.exists()) {
            try (Reader reader = Files.newReader(DEFAULT_KITS_FILE, Charsets.UTF_8)) {
                defaultKits = Samurai.PLAIN_GSON.fromJson(reader, DEFAULT_KITS_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
                Samurai.getInstance().getLogger().severe(ChatColor.RED + "Failed to import default-kits.json!");
            }
        }
    }

    public void saveDefaultKits() {
        try {
            Files.write(Samurai.PLAIN_GSON.toJson(defaultKits, DEFAULT_KITS_TYPE), DEFAULT_KITS_FILE, Charsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            Samurai.getInstance().getLogger().severe(ChatColor.RED + "Failed to export default-kits.json!");
        }
    }

    private void loadUserKits() {
        if (USER_KITS_FILE.exists()) {
            try (Reader reader = Files.newReader(USER_KITS_FILE, Charsets.UTF_8)) {
                userKits = Samurai.PLAIN_GSON.fromJson(reader, USER_KITS_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
                Samurai.getInstance().getLogger().severe(ChatColor.RED + "Failed to import user-kits.json!");
            }
        }
    }

    private void saveUserKits() {
        try {
            Files.write(Samurai.PLAIN_GSON.toJson(userKits, USER_KITS_TYPE), USER_KITS_FILE, Charsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            Samurai.getInstance().getLogger().severe(ChatColor.RED + "Failed to export user-kits.json!");
        }
    }

    public Kit getUserKit(UUID player, Kit kit) {
        if (kit == null) {
            return null;
        }

        if (userKits.containsKey(player)) {
            for (Kit userKit : userKits.get(player)) {
                if (userKit.getOriginal() == kit) {
                    return userKit;
                }
            }
        }

        return null;
    }

    public void trackUserKit(UUID player, Kit kit) {
        if (kit == null) {
            return;
        }

        if (!userKits.containsKey(player)) {
            userKits.put(player, new ArrayList<>());
        }

        userKits.get(player).add(kit);
    }

    public void deleteUserKit(UUID player, Kit kit) {
        if (kit == null) {
            return;
        }

        if (userKits.containsKey(player)) {
            userKits.get(player).remove(kit);
        }
    }

    public DefaultKit getDefaultKit(String name) {
        for (DefaultKit kit : defaultKits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }

        return null;
    }

    public Kit getOrCreateDefaultKit(String name) {
        for (Kit kit : defaultKits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }

        DefaultKit kit = new DefaultKit(name);
        defaultKits.add(kit);

        return kit;
    }

    public void deleteDefaultKit(DefaultKit kit) {
        defaultKits.remove(kit);
    }

    public boolean awaitingRestore(Player player) {
        return storedStates.containsKey(player.getUniqueId());
    }

    public void saveState(Player player) {
        storedStates.put(player.getUniqueId(), new PlayerInventorySerializer.PlayerInventoryWrapper(player));
    }

    public void restoreState(Player player) {
        if (storedStates.containsKey(player.getUniqueId())) {
            storedStates.get(player.getUniqueId()).apply(player);
            storedStates.remove(player.getUniqueId());
        }
    }

}
