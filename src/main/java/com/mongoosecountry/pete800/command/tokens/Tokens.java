package com.mongoosecountry.pete800.command.tokens;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.HelpCommand;
import com.mongoosecountry.pete800.util.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class Tokens extends AbstractCommand
{
	public Tokens()
	{
		super("tokens", "Display how many tokens you have.", Collections.singletonList(new CommandArgument("/tokens")), 0, "npc.tokens", true, Arrays.asList(new TokensAdd(), new TokensTake()));
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

        source.sendMessage(Text.join(Utils.goldText("You have "), Text.builder(EconomyNPC.tokens.getNumTokens(((Player) source).getUniqueId()) + "").color(TextColors.GREEN).build(), Utils.goldText(" tokens.")));
		return CommandResult.success();
	}
}
