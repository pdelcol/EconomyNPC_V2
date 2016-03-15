package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.HelpCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class ENPC extends AbstractCommand
{
    public ENPC()
    {
        super("enpc", "Base command for NPC handling.", Collections.singletonList(new CommandArgument("/enpc")), 0, "", false,
                Arrays.asList(new NPCEdit(), new NPCLoad(), new NPCMove(), new NPCProfession(), new NPCRemove(), new NPCSave(), new NPCSpawn()));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException
    {
        String[] args = splitArgs(arguments);
        HelpCommand help = new HelpCommand(this, source);
        if (args.length > 0)
        {
            if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))
                return help.process(source, moveArguments(args));

            for (AbstractCommand command : getSubCommands())
                if (command.getName().equalsIgnoreCase(args[0]))
                    return command.process(source, moveArguments(args));
        }

        return help.process(source, moveArguments(args));
    }
}
