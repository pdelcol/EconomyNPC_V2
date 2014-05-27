package com.mongoosecountry.pete800.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mongoosecountry.pete800.EconomyNPC;

public class PlayerListener implements Listener
{
	EconomyNPC plugin;
	
	public PlayerListener(EconomyNPC plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		plugin.storage.resendPackets(player.getUniqueId());
		plugin.uuid.addPlayer(player);
		if (plugin.tokens.containsPlayer(player.getUniqueId()))
			plugin.tokens.addTokens(player.getUniqueId(), 0);
	}
}
