package com.mongoosecountry.pete800.npc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;

public class BettingNPC extends AbstractNPC
{
	public BettingNPC(EconomyNPC plugin, Location location, String name)
	{
		super(plugin, location, NPCType.BETTING, name);
	}
	
	public BettingNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		super(plugin, name, cs);
	}
	
	@Override
	public void onInteract(Player player)
	{
		player.sendMessage(ChatColor.RED + "Which dummeh spawned me? I'm not even implemented yet.");
	}
}
