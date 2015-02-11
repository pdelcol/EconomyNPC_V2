package com.mongoosecountry.pete800.command;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;

public class Tokens extends AbstractCommand
{
	public Tokens(EconomyNPC plugin)
	{
		super(plugin, true, "tokens", "Display how many tokens you have.", "npc.tokens", Arrays.asList("/npc", "tokens"), Arrays.asList(new TokensAdd(plugin), new TokensTake(plugin)));
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (args.length > 0)
			for (AbstractCommand command : getSubCommands())
				if (command.getName().equalsIgnoreCase(args[0]))
					return command.onCommand(sender, plugin.moveArguments(args));
		
		if (!canSenderUseCommand(sender))
			return false;
		
		sender.sendMessage(ChatColor.GOLD + "You have " + ChatColor.GREEN + plugin.tokens.getNumTokens(((Player) sender).getUniqueId()) + ChatColor.GOLD + " tokens.");
		return true;
	}
}
