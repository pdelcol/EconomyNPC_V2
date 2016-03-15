package com.mongoosecountry.pete800.npc;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SellNPC extends InventoryNPC
{
	public SellNPC(Location<World> location, String name)
	{
		super(location, NPCType.SELL, name);
	}
	
	public SellNPC(String name, ConfigurationNode cn)
	{
		super(name, cn);
	}
}
