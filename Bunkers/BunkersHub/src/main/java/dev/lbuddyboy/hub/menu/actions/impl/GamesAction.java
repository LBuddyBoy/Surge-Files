package dev.lbuddyboy.hub.menu.actions.impl;

import dev.lbuddyboy.hub.games.GamesMenu;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.menu.actions.IAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 12:16 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item.actions
 */

@Getter
public class GamesAction extends IAction<Player, String> {

	@Override
	public String getName() {
		return "GAME_MENU";
	}

	@Override
	public void perform(Player player, String value) {
		new GamesMenu().openMenu(player);
	}

}
