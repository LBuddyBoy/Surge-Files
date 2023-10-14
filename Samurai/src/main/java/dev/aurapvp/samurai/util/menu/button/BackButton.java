package dev.aurapvp.samurai.util.menu.button;

import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BackButton extends Button {

    public int slot;
    public Menu menu;

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.ARROW)
                .setName("&eGo Back")
                .create();
    }

    @Override
    public void action(InventoryClickEvent event) {
        menu.openMenu((Player) event.getWhoClicked());
    }
}
