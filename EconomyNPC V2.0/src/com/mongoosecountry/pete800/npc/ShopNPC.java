package com.mongoosecountry.pete800.npc;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.mongoosecountry.pete800.EconomyNPC;

public class ShopNPC extends InventoryNPC
{
	public ShopNPC(EconomyNPC plugin, Location location, String name)
	{
		super(plugin, location, NPCType.SHOP, name);
	}
	
	public ShopNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		super(plugin, name, cs);
	}
}
