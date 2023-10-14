package dev.lbuddyboy.crates.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.model.CrateItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CrateCommandCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();
        Crate crate = context.getContextValue(Crate.class);
        CrateItem item = context.getContextValue(CrateItem.class);

        if (input.isEmpty()) {
            completions.addAll(item.getCommands());
        } else {
            for (String s : item.getCommands()) {
                if (String.valueOf(s).startsWith(input)) {
                    completions.add(s);
                }
            }
        }

        return new ArrayList<>(completions);
    }

}
