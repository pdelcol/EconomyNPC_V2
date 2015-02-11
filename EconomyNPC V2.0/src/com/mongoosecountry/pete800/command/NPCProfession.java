package com.mongoosecountry.pete800.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Villager.Profession;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC;

public class NPCProfession extends AbstractCommand
{
	public NPCProfession(EconomyNPC plugin)
	{
		super(plugin, false, "profession", "Set the outfit of an NPC.", "npc.profession", Arrays.asList("/npc", "profession", "<name> <profession>"), null);
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
			
			Profession profession = plugin.getProfessionFromName(args[1].toUpperCase());
			if (profession == null)
			{
				List<String> names = new ArrayList<String>();
				for (Profession p : Profession.values())
					names.add(p.toString());
				
				StringBuilder sb = new StringBuilder();
				for (String name : names)
				{
					if (sb.length() > 0)
						sb.append(", ");
					
					sb.append(name);
				}
				
				sender.sendMessage(ChatColor.DARK_RED + "Invalid profession. Expected: " + sb.toString());
				return false;
			}
			
			npc.setProfession(profession);
			sender.sendMessage(ChatColor.GREEN + "Profession set.");
			return true;
		}
		
		sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments: " + getUsage());
		return false;
	}
}
