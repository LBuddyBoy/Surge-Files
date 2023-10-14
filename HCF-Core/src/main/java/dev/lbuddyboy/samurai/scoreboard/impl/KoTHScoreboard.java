package dev.lbuddyboy.samurai.scoreboard.impl;

import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.samurai.scoreboard.SamuraiScoreboard;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardLine;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardTitle;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class KoTHScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public KoTHScoreboard() {
        this.config = new Config(Samurai.getInstance(), "koth", Samurai.getInstance().getScoreboardHandler().getScoreboardDirectory());
        this.title = new ScoreboardTitle(this.config.getConfigurationSection("title"));
        for (String key : this.config.getConfigurationSection("lines").getKeys(false)) {
            this.lines.add(new ScoreboardLine(this.config.getConfigurationSection("lines." + key)));
        }
    }

    @Override
    public Config getFile() {
        return this.config;
    }

    @Override
    public ScoreboardTitle getTitle() {
        return this.title;
    }

    @Override
    public List<ScoreboardLine> getLines() {
        return this.lines;
    }

    @SneakyThrows
    @Override
    public List<String> translateLines(List<String> lines, Player player) {
        List<String> replacement = new ArrayList<>();
        StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());
        SimpleDateFormat sdf = new SimpleDateFormat();

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            if (event.getType() != EventType.DTC && !event.getName().equals(VaultHandler.TEAM_NAME)) {
                KOTH koth = (KOTH) event;
                for (String line : lines) {
                    replacement.add(line
                            .replaceAll("%koth-world%", koth.getWorld())
                            .replaceAll("%koth-time-left%", TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()))
                            .replaceAll("%koth-type%", koth.getType().name())
                            .replaceAll("%koth-name%", koth.getName())
                            .replaceAll("%koth-capturing%", koth.getCurrentCapper() == null ? "&cNone" : koth.getCurrentCapper())
                            .replaceAll("%koth-location%", "" + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ())
                            .replaceAll("%player_name%", player.getName())
                            .replaceAll("%player_kills%", String.valueOf(stats.getKills()))
                            .replaceAll("%player_deaths%", String.valueOf(stats.getDeaths()))
                            .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
                            .replaceAll("%date%", sdf.format(new Date()))
                    );
                }
                return replacement;
            }
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            return true;
        }
        return false;
    }
}
