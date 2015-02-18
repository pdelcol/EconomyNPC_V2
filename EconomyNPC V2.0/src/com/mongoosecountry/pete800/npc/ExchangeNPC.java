package com.mongoosecountry.pete800.npc;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.handler.exchange.ExchangeHandler;
import com.mongoosecountry.pete800.handler.exchange.ExchangeTask;

public class ExchangeNPC extends InventoryNPC
{
	ExchangeHandler exchangeHandler = new ExchangeHandler();
	
	public ExchangeNPC(EconomyNPC plugin, Location location, String name)
	{
		super(plugin, location, NPCType.EXCHANGE, name);
	}
	
	public ExchangeNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		super(plugin, name, cs);
	}

	@Override
	public void onInteract(Player player)
	{
		ItemStack item = getInventoryEdit(player).getItem(0);
		if (item == null)
		{
			player.sendMessage(ChatColor.DARK_RED + "This NPC isn't ready for player interaction.");
			return;
		}
		
		double money = plugin.getConfig().getDouble("tokenForMoney", 2000);
		int xp = plugin.getConfig().getInt("tokenForExp", 200);
		Material material = item.getType();
		UUID uuid = player.getUniqueId();
		if (exchangeHandler.isTransacting(uuid))
		{
			if (plugin.tokens.removeTokens(uuid, 1))
			{
				if (material == Material.EMERALD)
				{
					plugin.econ.depositPlayer(player, money);
					player.sendMessage(ChatColor.GOLD + "You have traded 1 token for " + ChatColor.AQUA + "$" + money);
				}
				else if (material == Material.EXP_BOTTLE)
				{
					player.giveExp(xp);
					player.sendMessage(ChatColor.GOLD + "You have traded 1 token for " + ChatColor.AQUA + xp + ChatColor.GOLD + " XP.");
				}
				
				return;
			}
			
			player.sendMessage(ChatColor.DARK_RED + "You do not have enough tokens for that.");
			return;
		}
		
		if (material == Material.EMERALD)
			player.sendMessage(ChatColor.BLUE + "You are about to exchange 1 token for " + ChatColor.GOLD + "$" + money);
		else if (material == Material.EXP_BOTTLE)
			player.sendMessage(ChatColor.BLUE + "You are about to exchange 1 token for " + ChatColor.GOLD + xp + ChatColor.BLUE + " XP.");
		
		player.sendMessage(ChatColor.BLUE + "Right click again to continue");
		exchangeHandler.newTransaction(uuid);
		new ExchangeTask(plugin, exchangeHandler, uuid).runTaskLater(plugin, 100);
	}
}
