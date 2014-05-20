package com.mongoosecountry.pete800;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
	EconomyNPC npc;
	
	public PlayerListener(EconomyNPC npc)
	{
		this.npc = npc;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		npc.storage.resendPackets(event.getPlayer().getUniqueId());
	}
}
