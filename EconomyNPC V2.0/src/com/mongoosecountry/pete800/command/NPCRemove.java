package com.mongoosecountry.pete800.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC;

public class NPCRemove extends AbstractCommand
{
	public NPCRemove(EconomyNPC plugin)
	{
		super(plugin, false, "remove", "Remove an NPC.", "npc.remove", Arrays.asList("/npc", "remove", "<npc>"), null);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		if (args.length == 1)
			return plugin.storage.removeNPC(args[0], sender);
		
		sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments: " + getUsage());
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args)
	{
		List<String> names = new ArrayList<String>();
		for (PlayerNPC npc : plugin.storage.getNPCs())
			if (args[0].equals("") || args[0].toLowerCase().startsWith(npc.getName().toLowerCase()))
				names.add(npc.getName());
		
		return names;
	}
}
