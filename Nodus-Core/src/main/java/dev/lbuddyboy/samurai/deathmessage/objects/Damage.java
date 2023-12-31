package dev.lbuddyboy.samurai.deathmessage.objects;

import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;

public abstract class Damage {

    @Getter private String damaged;
    @Getter private double damage;
    @Getter private long time;

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public abstract String getDeathMessage();

    public String wrapName(String player) {
        return (ChatColor.RED + player + ChatColor.DARK_RED + "[" + Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player).getKills() + "]" + ChatColor.YELLOW);
    }

    public long getTimeDifference() {
        return System.currentTimeMillis() - time;
    }

}