package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public class VisitActiveKOTHChallenge extends Challenge {

    public VisitActiveKOTHChallenge(String id, String name, int experience, boolean daily) {
        super(id, name, experience, daily);
    }

    @Override
    public Type getAbstractType() {
        return VisitActiveKOTHChallenge.class;
    }

    @Override
    public String getText() {
        return "Visit an active KOTH";
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).isVisitActiveKoth();
    }

}
