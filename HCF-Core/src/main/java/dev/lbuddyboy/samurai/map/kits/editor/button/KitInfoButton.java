package dev.lbuddyboy.samurai.map.kits.editor.button;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.map.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class KitInfoButton extends Button {

    private final Kit kit;

    KitInfoButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Editing: " + ChatColor.AQUA + kit.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.GRAY + "You are editing '" + kit.getName() + "'"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }

}