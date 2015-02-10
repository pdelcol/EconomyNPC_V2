package com.mongoosecountry.pete800.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC.NPCType;

public class NPCSpawn extends AbstractCommand
{
	public NPCSpawn(EconomyNPC plugin)
	{
		super(plugin, true, "spawn", "Create an NPC where you are currently stand.", "npc.create", Arrays.asList("/npc", "spawn",  "<type> <name>"), null);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		if (args.length == 2)
			return plugin.storage.createNPC(args[1], (Player) sender, NPCType.fromName(args[0]));
		
		sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments: " + getUsage());
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args)
	{
		List<String> names = new ArrayList<String>();
		for (NPCType npcType : NPCType.values())
		{
			if (args.length == 1 && (args[0].equals("") || args[0].toLowerCase().startsWith(npcType.toString().toLowerCase())))
				names.add(npcType.toString());
			else
				return null;
		}
		
		return names;
	}
}
