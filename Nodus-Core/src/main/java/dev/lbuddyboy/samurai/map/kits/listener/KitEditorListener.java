package dev.lbuddyboy.samurai.map.kits.listener;

import dev.lbuddyboy.samurai.map.kits.editor.button.EditKitMenu;
import dev.lbuddyboy.samurai.map.kits.editor.menu.KitsMenu;
import dev.lbuddyboy.samurai.map.kits.editor.setup.KitEditorItemsMenu;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * "Modifications" needed to make the EditKitMenu work as expected
 */
public final class KitEditorListener implements Listener {

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (Samurai.getInstance().getMapHandler().isKitMap()) {
            Samurai.getInstance().getMapHandler().getKitManager().restoreState(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Menu openedMenu = Menu.currentlyOpenedMenus.getOrDefault(event.getPlayer().getName(), null);
        if (openedMenu instanceof KitsMenu || openedMenu instanceof EditKitMenu || openedMenu instanceof KitEditorItemsMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Menu openedMenu = Menu.currentlyOpenedMenus.getOrDefault(player.getName(), null);
        if (openedMenu instanceof KitsMenu || openedMenu instanceof EditKitMenu || openedMenu instanceof KitEditorItemsMenu) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Menu openedMenu = Menu.currentlyOpenedMenus.getOrDefault(event.getPlayer().getName(), null);
        if (openedMenu instanceof KitsMenu || openedMenu instanceof EditKitMenu || openedMenu instanceof KitEditorItemsMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign sign) {
            if (sign.getLine(0).equals(ChatColor.DARK_AQUA + "- Edit Kits -")) {
                new KitsMenu().openMenu(event.getPlayer());
            }
        }
    }

    /**
     * Prevents placing items into the top inventory and dropping items outside of the inventory while in the menu.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        if (Menu.currentlyOpenedMenus.get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);

            if (event.getAction() == InventoryAction.DROP_ALL_CURSOR || event.getAction() == InventoryAction.DROP_ONE_CURSOR) {
                event.setCursor(null);
                player.updateInventory();
            }
        }
    }

    /**
     * Prevents all forms of dragging (the goal of this is
     * to prevent items being put into the top inventory,
     * but item dragging overall is too complicated to deal
     * with properly so we just disallow dragging.)
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (Menu.currentlyOpenedMenus.get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles adding editor items to a kit by dropping them into a menu.
     */
    @EventHandler
    public void onInsertEditorItem(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        if (event.getCursor() == null) {
            return;
        }

        if (Menu.currentlyOpenedMenus.get(player.getName()) == null) return;

        if (Menu.currentlyOpenedMenus.get(player.getName()) instanceof KitEditorItemsMenu) {
            final ItemStack cursor = event.getCursor().clone();

            event.setCancelled(true);
            event.setCursor(null);

            KitEditorItemsMenu openedMenu = (KitEditorItemsMenu) Menu.currentlyOpenedMenus.get(player.getName());
            openedMenu.getKit().getEditorItems().add(cursor);

            Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();

            openedMenu.openMenu(player);
        }
    }

}