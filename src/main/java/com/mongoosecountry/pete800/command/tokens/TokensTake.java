package com.mongoosecountry.pete800.command.tokens;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.CommandArgument.Syntax;
import com.mongoosecountry.pete800.util.Utils;
import com.mongoosecountry.pete800.util.uuid.UUIDUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class TokensTake extends AbstractCommand
{
    public TokensTake()
    {
        super("take", "Take tokens from a player's account.", Arrays.asList(new CommandArgument("/tokens"), new CommandArgument("take"), new CommandArgument("player", Syntax.REPLACE, Syntax.REQUIRED), new CommandArgument("amount", Syntax.REPLACE, Syntax.REQUIRED)), 2, "npc.tokens.take", false);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        String[] args = splitArgs(arguments);
        if (!testPermission(source))
            return CommandResult.empty();

        if (minArgsMet(source, args.length))
        {
            source.sendMessage(Utils.darkRedText("Not enough arguments: " + getUsage(source)));
            return CommandResult.empty();
        }

        UUID player;
        try
        {
            player = UUIDUtils.getUUIDOf(args[0]);
        }
        catch (IOException e)
        {
            source.sendMessage(Utils.darkRedText("An unexpected error occurred while pinging Mojang's API."));
            return CommandResult.empty();
        }

        if (player == null)
        {
            source.sendMessage(Utils.darkRedText("Invalid player name."));
            return CommandResult.empty();
        }

        if (!Utils.isNumber(args[1]))
        {
            source.sendMessage(Utils.darkRedText("Expected number, instead received " + args[1]));
            return CommandResult.empty();
        }

        EconomyNPC.tokens.removeTokens(player, Integer.parseInt(args[1]));
        source.sendMessage(Text.builder("Tokens removed.").color(TextColors.AQUA).build());
        return CommandResult.success();
    }
}
