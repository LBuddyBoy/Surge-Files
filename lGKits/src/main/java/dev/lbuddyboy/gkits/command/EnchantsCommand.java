package dev.lbuddyboy.gkits.command;

import dev.lbuddyboy.gkits.command.menu.EnchantsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/07/2021 / 9:49 PM
 * GKits / me.lbuddyboy.gkits.command.menu
 */
public class EnchantsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Bad console");
			return false;
		}
		new EnchantsMenu().openMenu((Player) sender);
		return false;
	}
}
