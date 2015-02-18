package com.mongoosecountry.pete800.command.enpc;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;

public class NPCSave extends AbstractCommand
{
	public NPCSave(EconomyNPC plugin)
	{
		super(plugin, false, "save", "Manually save all NPC data to the config files.", "npc.save", Arrays.asList("/enpc", "save"), null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		plugin.npcStorage.save(false);
		sender.sendMessage(ChatColor.GREEN + "The command was successful, but please check your console for any errors that may have occurred.");
		return true;
	}
}
