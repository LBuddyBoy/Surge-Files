package dev.aurapvp.samurai.enchants.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.aurapvp.samurai.enchants.set.ArmorSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("armorsets")
public class ArmorSetsCommand extends BaseCommand {

    @Default
    public void enchanter(Player sender) {

    }

    @Subcommand("give")
    @CommandPermission("samurai.command.armorsets")
    @CommandCompletion("@armorsets @players")
    public void give(CommandSender sender, @Name("armorset") ArmorSet set, @Name("player") OnlinePlayer onlinePlayer) {
        if (onlinePlayer.getPlayer() == null) {
            return;
        }

        Player player = onlinePlayer.getPlayer();

        set.reward(player);
    }

}
