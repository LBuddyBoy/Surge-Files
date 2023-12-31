package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("location|whereami|loc|here")
public class LocationCommand extends BaseCommand {

    @Default
    public static void location(Player sender) {
        Location loc = sender.getLocation();
        Team owner = LandBoard.getInstance().getTeam(loc);

        if (owner != null) {
            sender.sendMessage(ChatColor.YELLOW + "You are in " + owner.getName(sender.getPlayer()) + ChatColor.YELLOW + "'s territory.");
            return;
        }

        if (!Samurai.getInstance().getServerHandler().isWarzone(loc)) {
            sender.sendMessage(ChatColor.YELLOW + "You are in " + ChatColor.GRAY + "The Wilderness" + ChatColor.YELLOW + "!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "You are in the " + ChatColor.RED + "Warzone" + ChatColor.YELLOW + "!");
        }
    }

}