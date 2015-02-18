package com.mongoosecountry.pete800.command.enpc;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC.NPCType;
import com.mongoosecountry.pete800.npc.InventoryNPC;

public class NPCEdit extends AbstractCommand
{
	public NPCEdit(EconomyNPC plugin)
	{
		super(plugin, true, "edit", "Edit a shop or kit NPC's offerings.", "npc.edit", Arrays.asList("/enpc", "edit", "<name>"), null);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		Player player = (Player) sender;
		if (args.length > 0)
		{
			AbstractNPC npc = plugin.npcStorage.getNPC(args[0]);
			if (npc == null)
			{
				sender.sendMessage(ChatColor.DARK_RED + "This NPC does not exist.");
				return false;
			}
			
			if (npc.getType() != NPCType.EXCHANGE && npc.getType() != NPCType.KIT && npc.getType() != NPCType.SHOP)
			{
				sender.sendMessage(ChatColor.DARK_RED + "This NPC does not have an inventory for you to edit.");
				return false;
			}
			
			player.openInventory(((InventoryNPC) npc).getInventoryEdit(player));
			return true;
		}
		
		sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments: " + getUsage());
		return false;
	}
}
