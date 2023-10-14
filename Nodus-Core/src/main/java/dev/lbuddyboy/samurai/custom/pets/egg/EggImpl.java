package dev.lbuddyboy.samurai.custom.pets.egg;

import dev.lbuddyboy.samurai.custom.pets.IPet;
import dev.lbuddyboy.samurai.custom.pets.PetRarity;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public interface EggImpl {

    String getName();
    ItemStack applyTags(Player player, PetRarity rarity, ItemStack stack, Object value);
    ItemStack advance(Player player, ItemStack stack);
    ItemStack getItem(PetRarity rarity);
    ItemStack getItem(PetRarity rarity, Object value);

    default void rollPet(Player player, PetRarity rarity) {
        List<IPet> pets = Samurai.getInstance().getPetHandler().getPetsByRarity(rarity);
        IPet pet = pets.get(ThreadLocalRandom.current().nextInt(0, pets.size()));

        player.getInventory().addItem(pet.createPet(1));
        player.getWorld().playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 2.0f);
    }

}
