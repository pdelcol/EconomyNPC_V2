package com.mongoosecountry.pete800.command;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC.NPCType;

public class NPCEdit extends AbstractCommand
{
	public NPCEdit(EconomyNPC plugin)
	{
		super(plugin, true, "edit", "Edit a shop or kit NPC's offerings.", "npc.edit", Arrays.asList("/npc", "edit", "<name>"), null);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		if (args.length > 1)
		{
			PlayerNPC npc = plugin.storage.getNPC(args[0]);
			if (npc == null)
			{
				sender.sendMessage(ChatColor.DARK_RED + "This NPC does not exist.");
				return false;
			}
			
			if (npc.getType() != NPCType.KIT && npc.getType() != NPCType.SHOP)
			{
				sender.sendMessage(ChatColor.DARK_RED + "This NPC does not have an inventory for you to edit.");
				return false;
			}
			
			((Player) sender).openInventory(npc.getInventoryEdit((Player) sender));
			return true;
		}
		
		sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments: " + getUsage());
		return false;
	}
}
