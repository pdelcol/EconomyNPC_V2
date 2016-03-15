package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.CommandArgument.Syntax;
import com.mongoosecountry.pete800.npc.AbstractNPC.NPCType;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NPCSpawn extends AbstractCommand
{
    public NPCSpawn()
    {
        super("spawn", "Create an NPC where you are currently stand.", Arrays.asList(new CommandArgument("/enpc"), new CommandArgument("spawn"), new CommandArgument("type", Syntax.REQUIRED, Syntax.REPLACE), new CommandArgument("name", Syntax.REQUIRED, Syntax.REPLACE)), 2, "npc.create", false);
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

        NPCType type = NPCType.fromName(args[0]);
        if (type == null)
        {
            List<String> names = new ArrayList<>();
            for (NPCType npct : NPCType.values())
                names.add(npct.toString());

            StringBuilder sb = new StringBuilder();
            for (String name : names)
            {
                if (sb.length() > 0)
                    sb.append(", ");

                sb.append(name);
            }

            source.sendMessage(Text.builder("Invalid NPC type. Expected: " + sb.toString()).color(TextColors.DARK_RED).build());
            return CommandResult.empty();
        }

        if (EconomyNPC.npcStorage.createNPC(args[1], (Player) source, type))
            return CommandResult.success();

        return CommandResult.empty();
    }
}
