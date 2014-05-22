package com.mongoosecountry.pete800;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener
{
	EconomyNPC npc;
	
	public InventoryListener(EconomyNPC npc)
	{
		this.npc = npc;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		final Player player = (Player) event.getWhoClicked();
		ItemStack clicked = event.getCurrentItem();
		int slot = event.getRawSlot();
		Inventory shop = event.getView().getTopInventory();
		
		for (PlayerNPC n : npc.storage.entities)
		{
			if (shop.getName().equalsIgnoreCase(n.spawned.getPlayerName() + "'s Shop"))
			{
				if (slot < 27 && slot > -1)
				{
					if (event.getAction() == InventoryAction.PICKUP_ALL)
					{
						OfflinePlayer p = npc.getServer().getOfflinePlayer(player.getUniqueId());
						Material material = clicked.getType();
						double price = npc.prices.getPrice(clicked);
						if (npc.econ.withdrawPlayer(p, price).transactionSuccess())
						{
							event.getCursor().setType(Material.AIR);
							event.getCurrentItem().setType(Material.AIR);
							event.setCancelled(true);
							event.setResult(Result.DENY);
							player.closeInventory();
							player.getInventory().addItem(new ItemStack(clicked.getType(), clicked.getAmount(), clicked.getDurability()));
							player.sendMessage("You bought " + material.toString() + " for $" + price + ".");
						}
						else
						{
							event.getCursor().setType(Material.AIR);
							event.getCurrentItem().setType(Material.AIR);
							event.setCancelled(true);
							event.setResult(Result.DENY);
							player.closeInventory();
							player.sendMessage(ChatColor.GOLD + "You don't have enough funds.");
						}
					}
					else
					{
						event.setCancelled(true);
						event.setResult(Result.DENY);
						player.closeInventory();
						player.sendMessage(ChatColor.GOLD + "Invalid click! Please LEFT click on the item you want.");
					}
				}
				else
				{
					event.setCancelled(true);
					event.setResult(Result.DENY);
					// This fixes a packet handling issue that caused the player to disconnect
					Bukkit.getScheduler().scheduleSyncDelayedTask(npc, new Runnable()
					{
						public void run()
						{
							player.closeInventory();
							player.sendMessage(ChatColor.GOLD + "Invalid inventory! Click on the shop's inventory.");
						}
					}, 1L);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Inventory inv = event.getView().getTopInventory();
		Player player = (Player) event.getPlayer();
		
		if (inv.getName().equals("Sell"))
		{
			List<ItemStack> items = new ArrayList<ItemStack>();
			double sell = 0;
			OfflinePlayer p = npc.getServer().getOfflinePlayer(player.getUniqueId());
			for (ItemStack item : inv.getContents())
			{
				if (item != null)
				{
					if (npc.prices.getPrice(item) > 0)
						sell += npc.prices.getPrice(item);
					else
						items.add(item);
				}
			}
			
			npc.econ.depositPlayer(p, sell);
			player.sendMessage("You have sold items for a total amount of $" + sell + ".");
			
			if (items.size() > 0)
			{
				player.sendMessage("Some items could not be sold. They have been returned back to you.");
				for (ItemStack item : items)
					player.getInventory().addItem(item);
			}
		}
		else if (inv.getName().contains(" - Edit") && player.hasPermission("npc.commands"))
		{
			for (PlayerNPC n : npc.storage.entities)
			{
				if (n.name.equals(inv.getName().substring(0, inv.getName().indexOf("-") - 1)))
				{
					n.updateInventory(inv);
					player.sendMessage("Shop updated.");
				}
			}
		}
	}
}
