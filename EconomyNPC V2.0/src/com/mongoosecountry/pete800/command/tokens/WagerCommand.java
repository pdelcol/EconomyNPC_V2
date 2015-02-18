package com.mongoosecountry.pete800.command.tokens;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.HelpCommand;

public class WagerCommand extends AbstractCommand
{
	public WagerCommand(EconomyNPC plugin)
	{
		super(plugin, true, "wager", "View your current wagers.", "npc.wager", Arrays.asList("/wager"), null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))
				return new HelpCommand(plugin, this).onCommand(sender, plugin.moveArguments(args));
			
			for (AbstractCommand command : getSubCommands())
				if (command.getName().equalsIgnoreCase(args[0]))
					return command.onCommand(sender, plugin.moveArguments(args));
		}
		
		if (!canSenderUseCommand(sender))
			return false;
		
		sender.sendMessage(ChatColor.GOLD + "You have " + ChatColor.GREEN);
		return true;
	}
}
