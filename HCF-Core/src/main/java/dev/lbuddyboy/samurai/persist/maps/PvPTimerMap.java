package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PvPTimerMap extends PersistMap<Integer> {

    public PvPTimerMap() {
        super("PvPTimers", "PvPTimer", false); // dont save this data to mongo
        // This should probably use a bit smarter of a system... but for now it's fine.
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (hasTimer(player.getUniqueId())) {
                        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                            continue;
                        }

                        int newValue = getValue(player.getUniqueId()) - 1;

                        if (newValue % 60 == 0) {
                            int minutes = newValue / 60;

                            if (minutes <= 0) {
                                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your protection has expired!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You have " + ChatColor.BOLD + minutes + ChatColor.RED + " minute" + (minutes == 1 ? "" : "s") + " of protection remaining.");
                            }
                        }

                        updateValueAsync(player.getUniqueId(), newValue);
                    }
                 }
            }

        }.runTaskTimerAsynchronously(Samurai.getInstance(), 20L, 20L);
    }

    @Override
    public String getRedisValue(Integer time) {
        return (String.valueOf(time));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer time) {
        return (time);
    }

    public void removeTimer(UUID update) {
        updateValueAsync(update, 0);
        Samurai.getInstance().getStartingPvPTimerMap().set(update, false);
        Player player = Bukkit.getPlayer(update);
        if (player != null) {
            FrozenNametagHandler.reloadPlayer(player);
        }
    }

    public void createTimer(UUID update, int seconds) {
        updateValueAsync(update, seconds);

        Player player = Bukkit.getPlayer(update);
        if (player != null) {
            FrozenNametagHandler.reloadPlayer(player);
        }
    }

    public void createStartingTimer(UUID update, int seconds) {
        createTimer(update, seconds);
        Samurai.getInstance().getStartingPvPTimerMap().set(update, true);

        Player player = Bukkit.getPlayer(update);
        if (player != null) {
            FrozenNametagHandler.reloadPlayer(player);
        }
    }

    public boolean hasTimer(UUID check) {
        return (getSecondsRemaining(check) > 0);
    }

    public int getSecondsRemaining(UUID check) {
        if (Samurai.getInstance().getMapHandler().isKitMap()) {
            return (0);
        }

        return (contains(check) ? getValue(check) : 0);
    }

}