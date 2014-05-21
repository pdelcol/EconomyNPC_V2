package com.mongoosecountry.pete800;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener
{
	EconomyNPC npc;
	
	public InventoryListener(EconomyNPC npc)
	{
		this.npc = npc;
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		ItemStack clicked = event.getCurrentItem();
		Inventory inv = event.getInventory();
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
							player.sendMessage(ChatColor.GOLD + "You don't have enough funds.");
					}
				}
			}
		}
	}
}
