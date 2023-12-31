package dev.lbuddyboy.samurai.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Data
public class RegionData {

    private RegionType regionType;
    private Team data;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegionData)) {
            return (false);
        }

        RegionData other = (RegionData) obj;
        return (other.regionType == regionType && (data == null || other.data.equals(data)));
    }

    @Override
    public int hashCode() {
        return (super.hashCode());
    }

    public String getName(Player player) {
        if (data == null) {
            switch (regionType) {
                case WARZONE:
                    return (ChatColor.RED + "Warzone");
                case WILDNERNESS:
                    return (ChatColor.GRAY + "The Wilderness");
                default:
                    return (ChatColor.RED + "N/A");
            }
        }

        return (data.getName(player));
    }

}