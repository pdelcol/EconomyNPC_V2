package com.mongoosecountry.pete800.command.tokens;

import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.HelpCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * Incomplete
 */
@SuppressWarnings("all")
@Deprecated
public class WagerCommand extends AbstractCommand
{
    public WagerCommand()
    {
        super("wager", "View your current wagers.", Collections.singletonList(new CommandArgument("/wager")), 0, "npc.wager", true);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException
    {
        String[] args = splitArgs(arguments);
        if (args.length > 0)
        {
            if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))
                return new HelpCommand(this, source).process(source, moveArguments(args));

            for (AbstractCommand command : getSubCommands())
                if (command.getName().equalsIgnoreCase(args[0]))
                    return command.process(source, moveArguments(args));
        }

        if (!testPermission(source))
            return CommandResult.empty();

        source.sendMessage(Text.builder("Not Implemented").build());
        return CommandResult.success();
    }
}
