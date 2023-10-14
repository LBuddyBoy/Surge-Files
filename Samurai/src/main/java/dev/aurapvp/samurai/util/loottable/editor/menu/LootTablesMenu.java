package dev.aurapvp.samurai.util.loottable.editor.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.loottable.LootTable;
import dev.aurapvp.samurai.util.loottable.LootTableHandler;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootTablesMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "LootTable Editor";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (LootTable lootTable : Samurai.getInstance().getLootTableHandler().getLootTables()) {
            buttons.add(new TableButton(lootTable, i++));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class TableButton extends Button {

        private LootTable lootTable;
        private int slot;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.CHEST).setName(lootTable.getDisplayName()).setLore(Arrays.asList("&7Click to open this loottable's editor.")).create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            new EditLootTableMenu(this.lootTable).openMenu(player);
        }
    }

}
