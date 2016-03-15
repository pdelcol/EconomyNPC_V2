package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.CommandArgument.Syntax;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import com.mongoosecountry.pete800.util.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class NPCMove extends AbstractCommand
{
    public NPCMove()
    {
        super("move", "Move an NPC to your location.", Arrays.asList(new CommandArgument("/enpc"), new CommandArgument("move"), new CommandArgument("name", Syntax.REPLACE, Syntax.REQUIRED)), 1, "npc.move", true);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        String[] args = splitArgs(arguments);
        if (!testPermission(source))
            return CommandResult.empty();

        Player player = (Player) source;
        if (!minArgsMet(player, args.length))
            return CommandResult.empty();

        AbstractNPC npc = EconomyNPC.npcStorage.getNPC(args[0]);
        if (npc == null)
        {
            player.sendMessage(Utils.darkRedText("This NPC does not exist."));
            return CommandResult.empty();
        }

        npc.setLocation(player.getLocation());
        player.sendMessage(Text.builder("NPC moved.").color(TextColors.GREEN).build());
        return CommandResult.success();
    }
}
