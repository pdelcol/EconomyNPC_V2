package com.mongoosecountry.pete800.npc;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ShopNPC extends InventoryNPC
{
    public ShopNPC(Location<World> location, String name)
    {
        super(location, NPCType.SHOP, name);
    }

    public ShopNPC(String name, ConfigurationNode cn)
    {
        super(name, cn);
    }
}
