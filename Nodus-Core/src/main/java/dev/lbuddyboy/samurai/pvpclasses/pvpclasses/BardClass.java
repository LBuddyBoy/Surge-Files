package dev.lbuddyboy.samurai.pvpclasses.pvpclasses;

import dev.lbuddyboy.samurai.custom.ability.items.exotic.KitDisabler;
import dev.lbuddyboy.samurai.pvpclasses.PvPClass;
import dev.lbuddyboy.samurai.pvpclasses.PvPClassHandler;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.bard.BardEffect;
import dev.lbuddyboy.samurai.util.CC;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.items.retired.AntiBard;
import dev.lbuddyboy.samurai.listener.FoxListener;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BardClass extends PvPClass implements Listener {

    /*
            Things commented with // CUSTOM
            are the 'unique' abilities, or things that have custom behaviour not seen by most other effects.
            An example is invis, whose passive cannot be used while its click is on cooldown.
            This is therefore commented with // CUSTOM
     */

    public static final Map<Material, BardEffect> BARD_CLICK_EFFECTS = new HashMap<>();
    public static final Map<Material, BardEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();

    @Getter
    private static Map<String, Long> lastEffectUsage = new ConcurrentHashMap<>();
    @Getter
    private static Map<String, Float> energy = new ConcurrentHashMap<>();

    public static final int BARD_RANGE = 20;
    public static final int EFFECT_COOLDOWN = 10 * 1000;
    public static final float MAX_ENERGY = 100;
    public static final float ENERGY_REGEN_PER_SECOND = 1;

    public BardClass() {
        super("Bard", 15, null);

        // Click buffs
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 20));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25));
        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40));
        BARD_CLICK_EFFECTS.put(Material.INK_SAC, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 45, 0), 45));
        //BARD_CLICK_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, BardEffect.fromEnergy(60));
        BARD_CLICK_EFFECTS.put(Material.WHEAT, BardEffect.fromEnergy(25));

        // Click debuffs
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 35));
        BARD_CLICK_EFFECTS.put(Material.ECHO_SHARD, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DARKNESS, 20 * 5, 0), 50));

        // Passive buffs
        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.INK_SAC, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 0)));
        //BARD_PASSIVE_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 0)));

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (!PvPClassHandler.hasKitOn(player, BardClass.this) || Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        continue;
                    }

                    if (energy.containsKey(player.getName())) {
                        if (energy.get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }

                        energy.put(player.getName(), Math.min(MAX_ENERGY, energy.get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        energy.put(player.getName(), 0F);
                    }

                    int manaInt = energy.get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + "Bard Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }

        }.runTaskTimer(Samurai.getInstance(), 15L, 20L);
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.GOLDEN_HELMET &&
                armor.getChestplate().getType() == Material.GOLDEN_CHESTPLATE &&
                armor.getLeggings().getType() == Material.GOLDEN_LEGGINGS &&
                armor.getBoots().getType() == Material.GOLDEN_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 0), true);

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
        }
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 0));
        }

        player.getItemInHand();
        if (BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType()) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            if (player.getItemInHand().getType() == Material.INK_SAC && player.getItemInHand().getDurability() != 0) {
                return;
            }

            // CUSTOM
            if (player.getItemInHand().getType() == Material.FERMENTED_SPIDER_EYE && getLastEffectUsage().containsKey(player.getName()) && getLastEffectUsage().get(player.getName()) > System.currentTimeMillis()) {
                return;
            }

            if (KitDisabler.getDisabled().onCooldown(player.getUniqueId())) {
                player.sendMessage(CC.translate("&c[Kit Disabler] Your kit ability is disabled for " + KitDisabler.getDisabled().getRemaining(player) + "."));
                return;
            }

            giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
        }
        super.tick(player);
    }


    @Override
    public void remove(Player player) {
        energy.remove(player.getName());

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this) || !energy.containsKey(event.getPlayer().getName())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Bard effects cannot be used while in spawn.");
            return;
        }

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
            return;
        }

        if (getLastEffectUsage().containsKey(event.getPlayer().getName()) && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        BardEffect bardEffect = BARD_CLICK_EFFECTS.get(event.getItem().getType());
        if (event.getItem().getType() == Material.INK_SAC && event.getItem().getDurability() != 0) {
            return;
        }

        if (bardEffect.getEnergy() > energy.get(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough energy for this! You need " + bardEffect.getEnergy() + " energy, but you only have " + energy.get(event.getPlayer().getName()).intValue());
            return;
        }

        if (KitDisabler.getDisabled().onCooldown(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(CC.translate("&c[Kit Disabler] Your kit ability is disabled for " + KitDisabler.getDisabled().getRemaining(event.getPlayer()) + "."));
            return;
        }

        energy.put(event.getPlayer().getName(), energy.get(event.getPlayer().getName()) - bardEffect.getEnergy());

        boolean negative = bardEffect.getPotionEffect() != null && FoxListener.DEBUFFS.contains(bardEffect.getPotionEffect().getType());

        getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        SpawnTagHandler.addOffensiveSeconds(event.getPlayer(), SpawnTagHandler.getMaxTagTime());
        giveBardEffect(event.getPlayer(), bardEffect, !negative, true);

        InventoryUtils.removeAmountFromInventory(event.getPlayer().getInventory(), event.getItem(), 1);
    }

    public void giveBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean persistOldValues) {
        for (Player player : getNearbyPlayers(source, friendly)) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            if (AntiBard.ANTI_BARD_PLAYERS.contains(player.getUniqueId())) {
                continue;
            }

            // CUSTOM
            // Bards can't get Strength.
            // Yes, that does need to use .equals. PotionEffectType is NOT an enum.
            if (PvPClassHandler.hasKitOn(player, this) && bardEffect.getPotionEffect() != null && bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                continue;
            }

            if (bardEffect.getPotionEffect() != null) {
                smartAddPotion(player, bardEffect.getPotionEffect(), persistOldValues, this);
            } else {
                Material material = source.getItemInHand().getType();
                giveCustomBardEffect(player, material);
            }
        }
    }

    public void giveCustomBardEffect(Player player, Material material) {
        switch (material) {
            case WHEAT:
                for (Player nearbyPlayer : getNearbyPlayers(player, true)) {
                    nearbyPlayer.setFoodLevel(20);
                    nearbyPlayer.setSaturation(10F);
                }

                break;
            case FERMENTED_SPIDER_EYE:


                break;
            default:
                Samurai.getInstance().getLogger().warning("No custom Bard effect defined for " + material + ".");
        }
    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = Samurai.getInstance().getTeamHandler().getTeam(player);

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(BARD_RANGE, BARD_RANGE / 2, BARD_RANGE)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (Samurai.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
                    continue;
                }

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly && !isAlly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

}