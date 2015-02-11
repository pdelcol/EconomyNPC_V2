package com.mongoosecountry.pete800.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.util.UUIDFetcher;

public class TokensAdd extends AbstractCommand
{
	public TokensAdd(EconomyNPC plugin)
	{
		super(plugin, false, "add", "Add tokens to a player's account.", "npc.tokens.add", Arrays.asList("/npc", "tokens add", "<player> <amount>"), null);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		if (args.length > 2)
		{
			OfflinePlayer player = null;
			try
			{
				player = Bukkit.getOfflinePlayer(UUIDFetcher.getUUIDOf(args[0]));
			}
			catch (Exception e)
			{
				sender.sendMessage(ChatColor.DARK_RED + "An unexpected error occurred while pinging Mojang's API.");
				return false;
			}
			
			if (player == null)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Invalid player name.");
				return false;
			}
			
			if (!plugin.isNumber(args[1]))
			{
				sender.sendMessage(ChatColor.DARK_RED + "Expected number, instead received " + args[1]);
				return false;
			}
			
			plugin.tokens.addTokens(player.getUniqueId(), Integer.valueOf(args[1]));
			sender.sendMessage(ChatColor.AQUA + "Tokens added.");
			return true;
		}
		
		sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments: " + getUsage());
		return false;
	}
}
