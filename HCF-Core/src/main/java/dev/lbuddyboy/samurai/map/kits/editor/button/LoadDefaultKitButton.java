package dev.lbuddyboy.samurai.map.kits.editor.button;

import com.google.common.collect.ImmutableList;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
final class LoadDefaultKitButton extends Button {

    private final DefaultKit originalKit;

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Load Default Kit";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                "",
                ChatColor.GRAY + "Click to " + ChatColor.YELLOW+ "load" + ChatColor.GRAY + " the default kit!"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEGACY_WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.YELLOW.getWoolData();
    }

    @Override
    public void clicked(final Player player, int slot, ClickType clickType) {
        /* Duplication fix. When players click this button we must set whatever they might have in their hand to air
         * Otherwise they can duplicate items infinitely. This exploits kits like archer and axe pvp. */
        player.setItemOnCursor(new ItemStack(Material.AIR));

        player.getInventory().setContents(originalKit.getInventoryContents());
        Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), player::updateInventory, 1L);
    }

}