package dev.lbuddyboy.pcore.enchants.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.enchants.model.BlackScroll;
import dev.lbuddyboy.pcore.enchants.model.EnchantBook;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.IntRange;
import dev.lbuddyboy.pcore.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantBookListener implements Listener {

    private static final Map<Material, String> MATERIAL_STRING_MAP = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;


        for (Rarity rarity : pCore.getInstance().getEnchantHandler().getRarities()) {
            if (!rarity.getOpenItem().isSimilar(item)) continue;

            List<CustomEnchant> enchants = new ArrayList<>(rarity.getEnchants());
            boolean finished = false;
            for (int i = 0; i < 500; i++) {

                Collections.shuffle(enchants);
                for (CustomEnchant enchant : enchants) {
                    if (enchant.getChance() > ThreadLocalRandom.current().nextDouble(100.0)) continue;

                    if (item.getAmount() <= 1) {
                        player.getInventory().setItemInHand(null);
                    } else {
                        item.setAmount(item.getAmount() - 1);
                    }
                    IntRange successRange = enchant.getRarity().getSuccessRange(), destroyRange = enchant.getRarity().getDestroyRange(), levelRange = enchant.getRange();
                    int level = ThreadLocalRandom.current().nextInt(levelRange.getMax()) + 1;
                    int success = ThreadLocalRandom.current().nextInt(successRange.getMin(), successRange.getMax());
                    int destroy = ThreadLocalRandom.current().nextInt(destroyRange.getMin(), destroyRange.getMax());

                    player.getInventory().addItem(enchant.getBook(level, success, destroy, false));
                    finished = true;
                    break;
                }
                if (finished) break;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (current == null) return;
        if (!current.getType().name().contains("_") && !MATERIAL_STRING_MAP.containsKey(current.getType())) return;

        EnchantBook enchantBook = new EnchantBook(cursor);
        if (enchantBook.getStack() == null) return;

        CustomEnchant enchant = enchantBook.getEnchant();
        Rarity rarity = enchant.getRarity();
        int level = enchantBook.getLevel(), success = enchantBook.getSuccess(), destroy = enchantBook.getDestroy();

        event.setCancelled(true);

        Map<CustomEnchant, Integer> enchants = pCore.getInstance().getEnchantHandler().getCustomEnchants(current);
        boolean extraEnchant = false, lowerEnchant = false;

        if (MATERIAL_STRING_MAP.containsKey(current.getType()) && !enchant.getApplicable().contains(MATERIAL_STRING_MAP.get(current.getType()))) {
            player.sendMessage(CC.translate("&cThat custom enchant is not applicable to this item!"));
            return;
        }

        if (!MATERIAL_STRING_MAP.containsKey(current.getType()) && !enchant.getApplicable().contains(current.getType().name().split("_")[1])) {
            player.sendMessage(CC.translate("&cThat custom enchant is not applicable to this item!"));
            return;
        }

        if (enchants.containsKey(enchant)) {
            if (enchants.get(enchant) >= level) {
                player.sendMessage(CC.translate("&cThat enchant is already applied to this item."));
                return;
            }
            lowerEnchant = true;
        }

        if (enchants.size() >= pCore.getInstance().getEnchantHandler().getMaxEnchants()) {
            if (enchants.size() + pCore.getInstance().getEnchantHandler().getMaxExtraEnchants() > pCore.getInstance().getEnchantHandler().getMaxEnchants()) {
                player.playSound(player.getLocation(), Sound.DIG_GRASS, 2.0f, 2.0f);
                player.sendMessage(CC.translate("&cYou cannot apply this to an item that has the maximum amount of extra enchants."));
                return;
            }
            if (!pCore.getInstance().getEnchantHandler().hasExtraEnchant(current)) {
                player.playSound(player.getLocation(), Sound.DIG_GRASS, 2.0f, 2.0f);
                player.sendMessage(CC.translate("&cYou cannot apply this to an item that doesn't have an extra enchant."));
                return;
            }

            extraEnchant = true;
        }

        if (CustomEnchant.RANDOM.nextDouble(rarity.getSuccessRange().getMin(), rarity.getSuccessRange().getMax()) > success && success != 100) {
            player.playSound(player.getLocation(), Sound.DIG_GRASS, 2.0f, 2.0f);

            event.getView().setCursor(ItemUtils.takeItem(event.getCursor()));

            if (CustomEnchant.RANDOM.nextDouble(rarity.getDestroyRange().getMin(), rarity.getDestroyRange().getMax()) < destroy) {
                if (!pCore.getInstance().getEnchantHandler().isProtected(current)) {
                    event.setCurrentItem(null);
                    player.sendMessage(CC.translate("&6&l[CUSTOM ENCHANTS] &cYou were unlucky and your item was destroyed due to the enchantment failing!"));
                    return;
                }
                event.setCurrentItem(pCore.getInstance().getEnchantHandler().removeProtected(current));
                if (extraEnchant) event.setCurrentItem(pCore.getInstance().getEnchantHandler().removeExtraEnchant(current));

                player.sendMessage(CC.translate("&6&l[CUSTOM ENCHANTS] &cYou were unlucky, but your item was not destroyed due to your item being white scrolled!"));
                return;
            }

            player.sendMessage(CC.translate("&6&l[CUSTOM ENCHANTS] &cYou were unlucky and the enchantment was not applied!"));
            return;
        }

        /*
        Add the Enchantment
         */

        if (lowerEnchant) current = pCore.getInstance().getEnchantHandler().removeEnchant(current, enchant);

        current = pCore.getInstance().getEnchantHandler().setEnchant(current, enchant, level);

        event.setCurrentItem(current);
        event.getView().setCursor(ItemUtils.takeItem(event.getCursor()));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
    }

    @EventHandler
    public void onWhiteScroll(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem(), cursor = event.getCursor();

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (current == null) return;
        if (cursor == null) return;
        if (!current.getType().name().contains("_") && !MATERIAL_STRING_MAP.containsKey(current.getType())) return;

        if (!cursor.isSimilar(pCore.getInstance().getEnchantHandler().getWHITE_SCROLL())) return;

        event.setCancelled(true);

        if (pCore.getInstance().getEnchantHandler().isProtected(current)) {
            player.sendMessage(CC.translate("&cThat item is already protected with a white scroll."));
            return;
        }

        /*
        Add the Enchantment
         */

        current = pCore.getInstance().getEnchantHandler().setProtected(current);

        event.setCurrentItem(current);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
        event.getView().setCursor(ItemUtils.takeItem(event.getCursor()));
    }

    @EventHandler
    public void onHolyWhiteScroll(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem(), cursor = event.getCursor();

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (current == null) return;
        if (cursor == null) return;
        if (!current.getType().name().contains("_") && !MATERIAL_STRING_MAP.containsKey(current.getType())) return;

        if (!cursor.isSimilar(pCore.getInstance().getEnchantHandler().getHOLY_WHITESCROLL())) return;

        event.setCancelled(true);

        if (!pCore.getInstance().getEnchantHandler().isProtected(current)) {
            player.sendMessage(CC.translate("&cThat item needs to be white scrolled before proceeding."));
            return;
        }

        if (pCore.getInstance().getEnchantHandler().isHolyProtected(current)) {
            player.sendMessage(CC.translate("&cThat item is already protected with a holy white scroll."));
            return;
        }

        /*
        Add the Enchantment
         */

        current = pCore.getInstance().getEnchantHandler().setHolyProtected(current);

        event.setCurrentItem(current);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
        event.getView().setCursor(ItemUtils.takeItem(event.getCursor()));
    }

    @EventHandler
    public void onBlackScroll(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem(), cursor = event.getCursor();

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (current == null) return;
        if (cursor == null) return;
        if (!current.getType().name().contains("_") && !MATERIAL_STRING_MAP.containsKey(current.getType())) return;

        BlackScroll scroll = new BlackScroll(cursor);
        if (!scroll.isBlackScroll()) return;

        int success = scroll.getSuccess(), destroy = scroll.getDestroy();

        event.setCancelled(true);

        if (CustomEnchant.RANDOM.nextDouble(100) > success) {
            if (CustomEnchant.RANDOM.nextDouble(100) > destroy && !pCore.getInstance().getEnchantHandler().isProtected(current)) {
                player.sendMessage(CC.translate("&cThat item has been destroyed due to there not being any protection and you being unlucky with the black scroll."));
                return;
            }
            player.sendMessage(CC.translate("&cThat item would have been destroyed, but it was protected and you being unlucky with the black scroll."));
            event.setCurrentItem(pCore.getInstance().getEnchantHandler().removeProtected(current));
            return;
        }

        ItemStack removed = pCore.getInstance().getEnchantHandler().applyBlackScroll(player, scroll);

        if (removed == null) return;

        current = removed;

        event.setCurrentItem(current);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
        event.getView().setCursor(ItemUtils.takeItem(event.getCursor()));
    }

    @EventHandler
    public void onTransMog(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem(), cursor = event.getCursor();

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (current == null) return;
        if (cursor == null) return;
        if (!current.getType().name().contains("_") && !MATERIAL_STRING_MAP.containsKey(current.getType())) return;

        if (!cursor.isSimilar(pCore.getInstance().getEnchantHandler().getTRANSMOG_SCROLL())) return;

        event.setCancelled(true);

        if (pCore.getInstance().getEnchantHandler().getCustomEnchants(current).isEmpty()) {
            player.sendMessage(CC.translate("&cThat item does not have any custom enchants."));
            return;
        }

        event.setCurrentItem(pCore.getInstance().getEnchantHandler().applyTransMog(current));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
        event.getView().setCursor(ItemUtils.takeItem(event.getCursor()));
    }

    @EventHandler
    public void onExtraEnchant(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem(), cursor = event.getCursor();

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (current == null) return;
        if (cursor == null) return;
        if (!current.getType().name().contains("_") && !MATERIAL_STRING_MAP.containsKey(current.getType())) return;

        if (!cursor.isSimilar(pCore.getInstance().getEnchantHandler().getARMOR_ORB()) && !cursor.isSimilar(pCore.getInstance().getEnchantHandler().getWEAPON_ORB())) return;

        event.setCancelled(true);

        if (pCore.getInstance().getEnchantHandler().getCustomEnchants(current).size() < pCore.getInstance().getEnchantHandler().getMaxEnchants()) {
            player.sendMessage(CC.translate("&cYour item does not have atleast " + pCore.getInstance().getEnchantHandler().getMaxEnchants() + " enchants."));
            return;
        }

        if (pCore.getInstance().getEnchantHandler().hasExtraEnchant(current)) {
            player.sendMessage(CC.translate("&cThat item already has an extra enchant."));
            return;
        }

        event.setCurrentItem(pCore.getInstance().getEnchantHandler().setExtraEnchant(current));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
        event.getView().setCursor(ItemUtils.takeItem(event.getCursor()));
    }


    static {
        MATERIAL_STRING_MAP.put(Material.FISHING_ROD, "FISHING_ROD");
        MATERIAL_STRING_MAP.put(Material.BOW, "BOW");
    }

}
