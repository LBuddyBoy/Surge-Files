package dev.lbuddyboy.pcore.util.loottable.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.loottable.LootTable;
import dev.lbuddyboy.pcore.util.loottable.LootTableItem;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LootTableItemContext implements ContextResolver<LootTableItem, BukkitCommandExecutionContext> {

    @Override
    public LootTableItem getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();
        LootTable crate = (LootTable) c.getResolvedArg(LootTable.class);

        for (LootTableItem item : crate.getItems().values()) {
            if (Objects.equals(item.getId(), source)) return item;
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid loottable item from " + crate.getName()));
    }
}
