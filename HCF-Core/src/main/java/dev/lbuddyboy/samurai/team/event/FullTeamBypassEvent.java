package dev.lbuddyboy.samurai.team.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class FullTeamBypassEvent extends Event {

	@Getter private static HandlerList handlerList = new HandlerList();

	private final Player player;
	private final Team team;
	@Setter private boolean allowBypass = false;
	@Setter private int extraSlots = 0;

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
