package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;

public class FireRes extends PersistentKillstreak {

    public FireRes() {
        super("Fire Resistance", 10);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    }
    
}
