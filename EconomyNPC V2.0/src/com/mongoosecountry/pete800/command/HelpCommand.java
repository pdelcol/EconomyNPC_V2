package com.mongoosecountry.pete800.command;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mongoosecountry.pete800.EconomyNPC;

public class HelpCommand extends AbstractCommand
{
	AbstractCommand mainCommand;
	String header = ChatColor.GOLD + "" + ChatColor.UNDERLINE + "EconomyNPC v2.0" + ChatColor.RESET + " by " + ChatColor.GOLD + "" +
			ChatColor.UNDERLINE + "Bruce" + ChatColor.RESET + " & " + ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Pete";
	
	public HelpCommand(EconomyNPC plugin, AbstractCommand mainCommand)
	{
		super(plugin, false, "help", "Display help info for the given command.", "", Arrays.asList("/" + mainCommand.getName(), "help"), null);
		this.mainCommand = mainCommand;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage(header);
		sender.sendMessage(mainCommand.getUsage() + ChatColor.GREEN + " " + mainCommand.getDescription());
		for (AbstractCommand command : mainCommand.getSubCommands())
			sender.sendMessage(command.getUsage() + ChatColor.GREEN + " " + command.getDescription());
		
		return true;
	}
	
	public String getHeader()
	{
		return header;
	}
}
