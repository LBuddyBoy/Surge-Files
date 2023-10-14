package dev.lbuddyboy.pcore.enchants.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.IntRange;
import dev.lbuddyboy.pcore.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class Reactor extends CustomEnchant implements Listener {

    @Override
    public void init() {
        registerListener(this);
    }

    @Override
    public String getName() {
        return "reactor";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will receive Resistance I for",
                "&f10 seconds upon killing a player!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Reactor");
    }

    @Override
    public String getColor() {
        return getConfig().getString("color", "&6");
    }

    @Override
    public IntRange getRange() {
        String[] parts = getConfig().getString("range", "1-1").split("-");
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public Rarity getRarity() {
        return pCore.getInstance().getEnchantHandler().getRarity(getConfig().getString("rarity", "Legendary")).get();
    }

    @Override
    public double getChance() {
        return getConfig().getDouble("chance", 20.0);
    }

    @Override
    public List<String> getApplicable() {
        if (!getConfig().contains("applicable")) return Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS");
        return validateApplicable(getConfig().getStringList("applicable"));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        if (!hasEnchantApplied(killer)) return;

        killer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10 + 10, 0));
    }

}
