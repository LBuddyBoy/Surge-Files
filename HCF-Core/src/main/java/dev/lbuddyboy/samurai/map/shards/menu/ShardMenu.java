package dev.lbuddyboy.samurai.map.shards.menu;

import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ShardMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.GOLD + CC.BOLD + "Shards: " + Samurai.getInstance().getShardMap().getShards(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            buttons.put(i, Button.placeholder(Material.ORANGE_STAINED_GLASS_PANE));
        }

        buttons.put(11, new PartnerPackageButton());
        buttons.put(12, new AirDropButton());
        buttons.put(13, new CommandButton(
                ItemBuilder.of(Material.PAPER).name("&d&lSummer Kit &7(One Time Use)").build(),
                "voucher give " + player.getName() + " onesummerkit 1",
                null,
                70
        ));
        buttons.put(14, new CommandButton(
                ItemBuilder.of(Material.PAPER).name("&6&lSurge Kit &7(One Time Use)").build(),
                "voucher give " + player.getName() + " onesurgekit 1",
                null,
                125
        ));
        buttons.put(15, new CommandButton(
                ItemBuilder.of(Material.LEGACY_MONSTER_EGG).name("&eRandom Pet").build(),
                "pet giveegg " + player.getName() + " RANDOM common 1 1",
                null,
                50
        ));
        buttons.put(20, new CommandButton(
                ItemBuilder.of(Material.RED_DYE).name("&c&l8 Lives &7(Right Click)").build(),
                "voucher give " + player.getName() + " 8lives 1",
                null,
                20
        ));
        buttons.put(21, new CommandButton(
                ItemBuilder.of(Material.LIME_DYE).name("&a&lAURA &fKey").build(),
                "crate give Aura " + player.getName() + " 1",
                null,
                10
        ));
        buttons.put(22, new CommandButton(
                ItemBuilder.of(Material.ORANGE_DYE).name("&e&lS&6&lU&e&lR&6&lG&e&lE &fKey").build(),
                "crate give Surge " + player.getName() + " 1",
                null,
                15
        ));
        buttons.put(23, new CommandButton(
                ItemBuilder.of(Material.AMETHYST_SHARD).name("&d&lPet Candy").build(),
                "pets givecandy " + player.getName() + " 1",
                null,
                25
        ));

        for (AbilityItem partnerPackage : Samurai.getInstance().getAbilityItemHandler().getAbilityItems()) {
            if (partnerPackage.getPartnerItem().getType() == Material.CHORUS_FRUIT) {
                buttons.put(24, new AbilityButton(
                        ItemBuilder.copyOf(partnerPackage.getPartnerItem()).amount(1).build(),
                        ItemBuilder.copyOf(partnerPackage.getPartnerItem().clone()).amount(1).build(),
                        null,
                        350
                ));
            }
        }

        return buttons;
    }

    private static class PartnerPackageButton extends Button {

        private final int requiredGems = Samurai.getInstance().getMapHandler().isKitMap() ? 15 : 20;

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.copyOf(Samurai.getInstance().getPartnerCrateHandler().getCrateItem().clone())
                    .addToLore("")
                    .addToLore(CC.GOLD + "Shards: ◆" + CC.WHITE + requiredGems)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.RED + "You do not have enough inventory space for this!");
                return;
            }

            if (Samurai.getInstance().getShardMap().removeShards(player.getUniqueId(), requiredGems)) {
                InventoryUtils.addAmountToInventory(player.getInventory(), Samurai.getInstance().getPartnerCrateHandler().getCrateItem(), 1);
                player.sendMessage(CC.GREEN + "Successfully purchased one partner package!");
            } else {
                player.sendMessage(CC.RED + "You do not have enough shards for this!");
            }
        }

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }
    }

    private static class AirDropButton extends Button {

        private final int requiredGems = Samurai.getInstance().getMapHandler().isKitMap() ? 100 : 150;

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.copyOf(Samurai.getInstance().getAirdropHandler().getItem().clone())
                    .addToLore("")
                    .addToLore(CC.GOLD + "Shards: ◆" + CC.WHITE + requiredGems)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.RED + "You do not have enough inventory space for this!");
                return;
            }

            if (Samurai.getInstance().getShardMap().removeShards(player.getUniqueId(), requiredGems)) {
                InventoryUtils.addAmountToInventory(player.getInventory(), Samurai.getInstance().getAirdropHandler().getItem(), 1);
                player.sendMessage(CC.GREEN + "Successfully purchased one air drop!");
            } else {
                player.sendMessage(CC.RED + "You do not have enough shards for this!");
            }
        }

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class CommandButton extends Button {

        private final ItemStack itemStack;
        private final String command;
        private final Predicate<Player> predicate;
        private final int cost;

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            boolean test = predicate == null || predicate.test(player);
            if (clickType.isLeftClick() && test) {

                if (Samurai.getInstance().getShardMap().removeShards(player.getUniqueId(), cost)) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    player.sendMessage("§aPurchased " + itemStack.getItemMeta().getDisplayName() + "§a!");
                } else {
                    player.sendMessage(CC.RED + "You do not have enough shards for this!");
                }
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.copyOf(this.itemStack)
                    .addToLore("")
                    .addToLore(CC.GOLD + "Shards: ◆" + CC.WHITE + cost)
                    .build();
        }

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class AbilityButton extends Button {

        private final ItemStack itemStack;
        private final ItemStack stack;
        private final Predicate<Player> predicate;
        private final int cost;

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            boolean test = predicate == null || predicate.test(player);
            if (clickType.isLeftClick() && test) {

                if (Samurai.getInstance().getShardMap().removeShards(player.getUniqueId(), cost)) {
                    ItemUtils.tryFit(player, stack, false);
                    player.sendMessage("§aPurchased " + itemStack.getItemMeta().getDisplayName() + "§a!");
                } else {
                    player.sendMessage(CC.RED + "You do not have enough shards for this!");
                }
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.copyOf(this.itemStack)
                    .addToLore(" ")
                    .addToLore(CC.GOLD + "Shards: ◆" + CC.WHITE + cost)
                    .build();
        }

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }
    }

}
