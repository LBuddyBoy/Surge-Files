package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GoldenApples extends Killstreak {

    @Override
    public String getName() {
        return "3 Golden Apples";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                3
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 3));
    }

}