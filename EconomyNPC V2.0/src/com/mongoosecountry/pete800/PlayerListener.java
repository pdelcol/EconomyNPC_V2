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
		//TODO isn't executing?
		for (PlayerNPC p : npc.storage.entities)
		{
			event.getPlayer().sendMessage("quack");
			p.resendPacket(event.getPlayer());
		}
	}
}
