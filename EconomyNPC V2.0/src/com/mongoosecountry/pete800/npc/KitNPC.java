package com.mongoosecountry.pete800.npc;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.handler.kit.KitHandler;
import com.mongoosecountry.pete800.handler.kit.KitTask;

public class KitNPC extends InventoryNPC
{
	KitHandler kitHandler = new KitHandler();
	List<String> kitMessage;
	
	public KitNPC(EconomyNPC plugin, Location location, String name)
	{
		super(plugin, location, NPCType.KIT, name);
	}
	
	public KitNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		super(plugin, name, cs);
		this.kitMessage = cs.getStringList("kit-message");
	}
	
	public List<String> getKitMessage()
	{
		return kitMessage;
	}
	
	@Override
	public void onInteract(Player player)
	{
		Inventory inv = getInventoryEdit(player);
		UUID uuid = player.getUniqueId();
		if (!kitHandler.isTransacting(uuid))
		{
			ItemStack coal = inv.getItem(inv.getSize() - 1);
			if (coal == null || coal.getType() != Material.COAL)
			{
				player.sendMessage(ChatColor.DARK_RED + "This NPC is not ready for player interaction.");
				return;
			}
			
			int numTokens = coal.getAmount();
			player.sendMessage(ChatColor.GOLD + "Do you really want to spend " + ChatColor.DARK_AQUA + numTokens + ChatColor.GOLD + " tokens for this kit?");
			for (String message : getKitMessage())
				player.sendMessage(message.replace('&', ChatColor.COLOR_CHAR));
			
			kitHandler.newTransaction(uuid);
			new KitTask(plugin, kitHandler, uuid).runTaskLater(plugin, 100);
			return;
		}
		
		int numTokens = inv.getItem(inv.getSize() - 1).getAmount();
		if (plugin.tokens.removeTokens(uuid, numTokens))
		{
			boolean dropped = false;
			for (ItemStack item : getInventoryEdit(player))
			{
				if (item != null && item.getType() != Material.COAL)
				{
					if (player.getInventory().firstEmpty() != -1)
						player.getInventory().addItem(item);
					else
					{
						player.getWorld().dropItemNaturally(player.getLocation(), item);
						dropped = true;
					}
				}
			}
			
			 if (dropped)
				 player.sendMessage(ChatColor.GREEN + "Your inventory was full so some items were dropped at your feet.");
			 
			 player.sendMessage(ChatColor.GREEN + "You exchanged " + ChatColor.AQUA + numTokens + ChatColor.GREEN + " tokens for a kit!");
			 return;
		}
		
		player.sendMessage(ChatColor.RED + "You do not have enough tokens to do this!");
	}
}
