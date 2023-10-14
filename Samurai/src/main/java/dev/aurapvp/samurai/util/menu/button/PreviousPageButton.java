package dev.aurapvp.samurai.util.menu.button;

import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PreviousPageButton extends Button {

    public PagedMenu menu;
    public int slot;
    private Player player;

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ItemStack getItem() {
        Material material = menu.page > 1 ? Material.ORANGE_CARPET : Material.WHITE_CARPET;
        String name = menu.page > 1 ? "&c&lPrevious Page" : "&c&lNo Previous Page";

        return new ItemBuilder(material).setName(name).create();
    }

    @Override
    public void action(InventoryClickEvent event) {
        if (event.getClick().isLeftClick() && menu.page > 1) {
            menu.page -= 1;
            menu.openMenu((Player) event.getWhoClicked());
        }
    }
}
