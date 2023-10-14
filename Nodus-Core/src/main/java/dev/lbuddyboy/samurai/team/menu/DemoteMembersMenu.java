package dev.lbuddyboy.samurai.team.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.menu.button.ChangePromotionStatusButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class DemoteMembersMenu extends Menu {

    @NonNull @Getter Team team;

    @Override
    public String getTitle(Player player) {
        return "Demote captains/co-leaders";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (UUID uuid : team.getColeaders()) {
            buttons.put(index, new ChangePromotionStatusButton(uuid, team, false));
            index++;
        }

        for (UUID uuid : team.getCaptains()) {
            buttons.put(index, new ChangePromotionStatusButton(uuid, team, false));
            index++;
        }

        if (buttons.size() <= 9) {
            buttons.put(9, new BackButton(new TeamManageMenu(team)));
        } else if (buttons.size() <= 18) {
            buttons.put(18, new BackButton(new TeamManageMenu(team)));
        } else if (buttons.size() <= 27) {
            buttons.put(27, new BackButton(new TeamManageMenu(team)));
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}