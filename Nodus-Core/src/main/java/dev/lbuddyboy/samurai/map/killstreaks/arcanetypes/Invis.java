package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;

public class Invis extends PersistentKillstreak {

    public Invis() {
        super("Invis", 30);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    
}
