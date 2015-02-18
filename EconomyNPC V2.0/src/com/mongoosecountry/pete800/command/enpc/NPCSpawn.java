package com.mongoosecountry.pete800.command.enpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.npc.AbstractNPC.NPCType;

public class NPCSpawn extends AbstractCommand
{
	public NPCSpawn(EconomyNPC plugin)
	{
		super(plugin, true, "spawn", "Create an NPC where you are currently stand.", "npc.create", Arrays.asList("/enpc", "spawn",  "<type> <name>"), null);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		if (args.length > 1)
		{
			NPCType type = NPCType.fromName(args[0]);
			if (type == null)
			{
				List<String> names = new ArrayList<String>();
				for (NPCType npct : NPCType.values())
					names.add(npct.toString());
				
				StringBuilder sb = new StringBuilder();
				for (String name : names)
				{
					if (sb.length() > 0)
						sb.append(", ");
					
					sb.append(name);
				}
				
				sender.sendMessage(ChatColor.DARK_RED + "Invalid NPC type. Expected: " + sb.toString());
				return false;
			}
			
			return plugin.npcStorage.createNPC(args[1], (Player) sender, type);
		}
		
		sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments: " + getUsage());
		return false;
	}
}
