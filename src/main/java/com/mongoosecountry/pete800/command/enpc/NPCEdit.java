package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.CommandArgument.Syntax;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC.NPCType;
import com.mongoosecountry.pete800.npc.InventoryNPC;
import com.mongoosecountry.pete800.util.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class NPCEdit extends AbstractCommand
{
	public NPCEdit()
	{
		super("edit", "Edit a shop or kit NPC's offerings.", Arrays.asList(new CommandArgument("/enpc"), new CommandArgument("edit"), new CommandArgument("name", Syntax.REPLACE, Syntax.REPLACE)), 1, "npc.edit", true);
	}

	@Nonnull
	@Override
	public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        String[] args = splitArgs(arguments);
        if (!testPermission(source))
            return CommandResult.empty();

        Player player = (Player) source;
        if (!minArgsMet(source, args.length))
            return CommandResult.empty();

        AbstractNPC npc = EconomyNPC.npcStorage.getNPC(args[0]);
        if (npc == null)
        {
            source.sendMessage(Utils.darkRedText("This NPC does not exist."));
            return CommandResult.empty();
        }

        if (npc.getType() != NPCType.EXCHANGE && npc.getType() != NPCType.KIT && npc.getType() != NPCType.SHOP)
        {
            source.sendMessage(Utils.darkRedText("This NPC does not have an inventory for you to edit."));
            return CommandResult.empty();
        }

        player.openInventory(((InventoryNPC) npc).getInventoryEdit(), Cause.of(NamedCause.source(EconomyNPC.instance())));
        return CommandResult.success();
	}
}
