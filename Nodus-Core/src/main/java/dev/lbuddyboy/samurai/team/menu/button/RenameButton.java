package dev.lbuddyboy.samurai.team.menu.button;

import dev.lbuddyboy.samurai.commands.staff.TeamManageCommand;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class RenameButton extends Button {

    private Team team;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        player.closeInventory();
        TeamManageCommand.renameTeam(player, team);
    }

    @Override
    public String getName(Player player) {
        return "§eRename Team";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEGACY_SIGN;
    }
}
