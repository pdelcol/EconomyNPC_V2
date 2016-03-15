package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.CommandArgument.Syntax;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class NPCRemove extends AbstractCommand
{
    public NPCRemove()
    {
        super("remove", "Remove an NPC.", Arrays.asList(new CommandArgument("/enpc"), new CommandArgument("remove"), new CommandArgument("name", Syntax.REPLACE, Syntax.REQUIRED)), 1, "npc.remove", false);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        String[] args = splitArgs(arguments);
        if (!testPermission(source))
            return CommandResult.empty();

        if (!minArgsMet(source, args.length))
            return CommandResult.empty();

        return CommandResult.builder().successCount(EconomyNPC.npcStorage.removeNPC(args[0], source) ? 1 : 0).build();
    }
}
