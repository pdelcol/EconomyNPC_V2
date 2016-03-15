package com.mongoosecountry.pete800.npc;

import com.mongoosecountry.pete800.EconomyNPC;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class BettingNPC extends AbstractNPC
{
	public BettingNPC(Location<World> location, String name)
	{
		super(location, NPCType.BETTING, name);
	}
	
	public BettingNPC(String name, ConfigurationNode cn)
	{
		super(name, cn);
	}
	
	@Override
	public void onInteract(Player player)
	{
		player.sendMessage(Text.builder("Which dummeh spawned me? I'm not even implemented yet.").color(TextColors.DARK_RED).build());
	}
}
