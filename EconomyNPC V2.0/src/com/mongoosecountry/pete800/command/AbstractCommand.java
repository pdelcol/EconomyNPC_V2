package com.mongoosecountry.pete800.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;

public abstract class AbstractCommand
{
	EconomyNPC plugin;
	boolean isPlayerOnly;
	List<AbstractCommand> subCommands;
	String name;
	String description;
	String permission;
	String usage;
	
	public AbstractCommand(EconomyNPC plugin, boolean isPlayerOnly, String name, String description, String permission, List<String> usage, List<AbstractCommand> subCommands)
	{
		this.plugin = plugin;
		this.isPlayerOnly = isPlayerOnly;
		this.name = name;
		this.description = description;
		this.permission = permission;
		this.usage = parseUsage(usage);
		this.subCommands = subCommands;
	}
	
	public boolean isPlayerOnly()
	{
		return isPlayerOnly;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getPermission()
	{
		return permission;
	}
	
	public String getUsage()
	{
		return usage;
	}
	
	public List<AbstractCommand> getSubCommands()
	{
		return subCommands;
	}
	
	public abstract boolean onCommand(CommandSender sender, String[] args);
	
	public boolean canSenderUseCommand(CommandSender sender)
	{
		if (isPlayerOnly && !(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command.");
			return false;
		}
		
		if (sender instanceof Player)
		{
			if (sender.hasPermission(getPermission()))
				return true;
			
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do that.");
			return false;
		}
		
		return true;
	}
	
	private String parseUsage(List<String> usageList)
	{
		String usage = ChatColor.GRAY + usageList.get(0);
		if (usageList.size() > 1)
			usage = usage + " " + ChatColor.RESET + usageList.get(1);
		
		if (usageList.size() > 2)
			usage = usage + " " + ChatColor.AQUA + usageList.get(2);
		
		return usage;
	}
}
