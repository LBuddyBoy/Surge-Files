package dev.lbuddyboy.samurai.map.game.menu;

import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.map.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class MapVoteMenu extends Menu {

    private Game game;

    @Override
    public String getTitle(Player player) {
        return "Map Votes";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Map.Entry<GameArena, AtomicInteger> entry : game.getArenaOptions().entrySet()) {
            buttons.put(buttons.size(), new MapButton(entry.getKey(), entry.getValue()));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class MapButton extends Button {
        private GameArena arena;
        private AtomicInteger votes;

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW.toString() + ChatColor.BOLD + arena.getName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();
            description.add(ChatColor.GRAY.toString() + "This map has " + ChatColor.GREEN + votes.get() + ChatColor.GRAY + " votes.");

            if (game.getPlayerVotes().containsKey(player.getUniqueId()) && game.getPlayerVotes().get(player.getUniqueId()) == arena) {
                description.add(ChatColor.GRAY.toString() + "You voted for this map!");
            }

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.MAP;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            votes.set(votes.get() + 1);
            game.getPlayerVotes().put(player.getUniqueId(), arena);
        }
    }

}