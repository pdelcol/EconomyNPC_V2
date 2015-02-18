package com.mongoosecountry.pete800.npc;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mongoosecountry.pete800.EconomyNPC;

public abstract class InventoryNPC extends AbstractNPC
{
	ConfigurationSection inv;
	
	public InventoryNPC(EconomyNPC plugin, Location location, NPCType type, String name)
	{
		super(plugin, location, type, name);
		this.inv = new YamlConfiguration();
	}
	
	public InventoryNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		super(plugin, name, cs);
		if (cs.isSet("inventory"))
			this.inv = cs.getConfigurationSection("inventory");
		else
			this.inv = new YamlConfiguration();
	}
	
	@Override
	public void onInteract(Player player)
	{
		player.openInventory(getInventory(player, getName() + "'s Shop"));
	}
	
	public Inventory getInventoryEdit(Player player)
	{
		return getInventory(player, getName() + " - Edit");
	}
	
	private Inventory getInventory(Player player, String name)
	{
		Inventory inventory = Bukkit.createInventory(player, 54, name);
		if (type != NPCType.SELL)
		{
			for (int slot = 0; slot < inventory.getSize(); slot++)
			{
				ItemStack item = inv.getItemStack(slot + "");
				if (item != null)
				{
					if (type == NPCType.SHOP)
					{
						ItemMeta meta = item.getItemMeta();
						meta.setLore(Arrays.asList("$" + plugin.prices.getBuyPrice(item)));
						item.setItemMeta(meta);
					}
					
					inventory.setItem(slot, item);
				}
			}
		}
		
		return inventory;
	}
	
	public void updateInventory(Inventory inventory)
	{
		inv = new YamlConfiguration();
		for (int slot = 0; slot < inventory.getSize(); slot++)
		{
			ItemStack item = inventory.getItem(slot);
			if (item != null)
			{
				if (type == NPCType.SHOP)
				{
					ItemMeta meta = item.getItemMeta();
					meta.setLore(Arrays.asList("$" + plugin.prices.getBuyPrice(item)));
					item.setItemMeta(meta);
				}
				
				inv.set(slot + "", inventory.getItem(slot));
			}
		}
	}
	
	public ConfigurationSection getInventory()
	{
		return inv;
	}
	
	@Override
	public ConfigurationSection save()
	{
		ConfigurationSection cs = super.save();
		cs.set("inventory", inv);
		return cs;
	}
}
