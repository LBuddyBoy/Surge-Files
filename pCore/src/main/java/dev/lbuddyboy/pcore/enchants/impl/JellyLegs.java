package dev.lbuddyboy.pcore.enchants.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.IntRange;
import dev.lbuddyboy.pcore.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JellyLegs extends CustomEnchant implements Listener {

    @Override
    public void init() {
        registerListener(this);
    }

    @Override
    public String getName() {
        return "jellylegs";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will NOT receive fall damage!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Jelly Legs");
    }

    @Override
    public String getColor() {
        return getConfig().getString("color", "&5");
    }

    @Override
    public IntRange getRange() {
        String[] parts = getConfig().getString("range", "1-1").split("-");
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public Rarity getRarity() {
        return pCore.getInstance().getEnchantHandler().getRarity(getConfig().getString("rarity", "Epic")).get();
    }

    @Override
    public double getChance() {
        return getConfig().getDouble("chance", 50.0);
    }

    @Override
    public List<String> getApplicable() {
        if (!getConfig().contains("applicable")) return Collections.singletonList("BOOTS");
        return validateApplicable(getConfig().getStringList("applicable"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!hasEnchantApplied(player)) return;

        event.setCancelled(true);
    }

}
