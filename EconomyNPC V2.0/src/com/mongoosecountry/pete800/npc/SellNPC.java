package com.mongoosecountry.pete800.npc;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.mongoosecountry.pete800.EconomyNPC;

public class SellNPC extends InventoryNPC
{
	public SellNPC(EconomyNPC plugin, Location location, String name)
	{
		super(plugin, location, NPCType.SELL, name);
	}
	
	public SellNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		super(plugin, name, cs);
	}
}
