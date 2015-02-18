package com.mongoosecountry.pete800.command.enpc;

import java.util.Arrays;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.CommandSender;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.HelpCommand;

public class ENPC extends AbstractCommand
{
	public ENPC(EconomyNPC plugin)
	{
		super(plugin, false, "enpc", "Base command for NPC handling.", "", Arrays.asList("/enpc", "[?]"),
				Arrays.asList(new NPCEdit(plugin), new NPCLoad(plugin), new NPCMove(plugin), new NPCProfession(plugin), new NPCRemove(plugin), new NPCSave(plugin), new NPCSpawn(plugin)));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		HelpCommand help = new HelpCommand(plugin, this);
		if (args.length > 0)
		{
			if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))
				return help.onCommand(sender, plugin.moveArguments(args));
			
			for (AbstractCommand command : getSubCommands())
				if (command.getName().equalsIgnoreCase(args[0]))
					return command.onCommand(sender, plugin.moveArguments(args));
		}
		
		sender.sendMessage(help.getHeader());
		for (AbstractCommand command : plugin.commands)
			sender.sendMessage(command.getUsage() + ChatColor.RESET + " help " + ChatColor.GREEN + "Display help information for /" + command.getName());
		
		return true;
	}
}
