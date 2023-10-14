package dev.lbuddyboy.bunkers.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SpawnTagHandler {

    @Getter private static final Map<String, Long> spawnTags = new ConcurrentHashMap<>();

    public static void removeTag(Player player) {
        removeTag(player.getName());
    }

    public static void removeTag(String name) {
        spawnTags.remove(name);
    }

    public static void addOffensiveSeconds(Player player, int seconds) {
        addSeconds(player, seconds);
    }

    public static void addPassiveSeconds(Player player, int seconds) {
        addSeconds(player, seconds);
    }

    private static void addSeconds(Player player, int seconds) {
        int duration = seconds;

        if (isTagged(player)) {
            int secondsTaggedFor = (int) ((spawnTags.get(player.getName()) - System.currentTimeMillis()) / 1000L);
            duration = Math.min(secondsTaggedFor + seconds, getMaxTagTime());
        } else {
            player.sendMessage(ChatColor.YELLOW + "You have been spawn-tagged for §c" + seconds + " §eseconds!");
        }

        spawnTags.put(player.getName(), System.currentTimeMillis() + (duration * 1000L));
    }

    public static long getTag(Player player) {
        return (spawnTags.containsKey(player.getName()) ? spawnTags.get(player.getName()) - System.currentTimeMillis() : 0);
    }

    public static boolean isTagged(Player player) {
        if (player != null) {
            return spawnTags.containsKey(player.getName()) && spawnTags.get(player.getName()) > System.currentTimeMillis();
        } else {
            return false;
        }
    }

    public static boolean isTagged(String name) {
        return spawnTags.containsKey(name) && spawnTags.get(name) > System.currentTimeMillis();
    }

    public static int getMaxTagTime() {
        return 30;
    }

}