package dev.aurapvp.samurai.util.loottable.editor.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import dev.aurapvp.samurai.util.loottable.LootTableHandler;
import dev.aurapvp.samurai.util.loottable.LootTableItem;
import dev.aurapvp.samurai.util.loottable.editor.ItemEditor;
import dev.aurapvp.samurai.util.loottable.editor.impl.CommandListEdit;
import dev.aurapvp.samurai.util.loottable.editor.impl.ToggleGiveItemEdit;
import dev.aurapvp.samurai.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class EditLootTableItemMenu extends Menu {

    private List<LootTableItem> items;
    private LootTableItem item;

    public EditLootTableItemMenu(LootTableItem item) {
        this.items = new ArrayList<>(Collections.singletonList(item));
        this.item = item;
    }

    public EditLootTableItemMenu(List<LootTableItem> items) {
        this.items = items;
        if (this.items.size() == 1) {
            this.item = this.items.get(0);
        }
    }

    @Override
    public String getTitle(Player player) {
        if (this.item == null) {
            return "Editing: " + this.items.size() + " items";
        }
        return "Item Editor: " + item.getId();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ItemEditor editor : Samurai.getInstance().getLootTableHandler().getEditors()) {
            if (this.items.size() > 1 && editor instanceof CommandListEdit) continue;

            buttons.add(new EditorButton(editor));
        }

        if (item != null) {
            buttons.add(new BackButton(4, new EditLootTableMenu(item.getParent())));
        } else {
            buttons.add(new BackButton(4, new EditLootTableMenu(items.get(0).getParent())));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public int getSize(Player player) {
        return 36;
    }

    @AllArgsConstructor
    public class EditorButton extends Button {

        private ItemEditor editor;

        @Override
        public int getSlot() {
            return this.editor.slot();
        }

        @Override
        public ItemStack getItem() {
            if (item == null) {

                return this.editor.displayItem();
            }

            if (this.editor instanceof ToggleGiveItemEdit giveItemEdit) {
                if (!item.isGiveItem()) {
                    return giveItemEdit.getToggledOffItem();
                }
                return giveItemEdit.getToggledOnItem();
            }
            return this.editor.displayItem();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;
            Conversation conversation = this.editor.conversation(player, items);

            if (conversation == null) {
                this.editor.action(player, items);
                return;
            }

            player.beginConversation(conversation);
            player.closeInventory();
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

}
