package com.mongoosecountry.pete800.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC;

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
		if (!plugin.tokens.containsPlayer(player.getUniqueId()))
			plugin.tokens.addTokens(player.getUniqueId(), 0);
		
		if (Bukkit.getOnlinePlayers().size() == 1)
		{
			//Needed delay so the NPCs actually spawn when the server has the chunks loaded for the player
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					for (AbstractNPC npc : plugin.npcStorage.getNPCs())
						npc.respawnNPC();
				}
			}.runTaskLater(plugin, 1L);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if (Bukkit.getOnlinePlayers().size() == 1)
			for (AbstractNPC npc : plugin.npcStorage.getNPCs())
				npc.despawnNPC();
	}
	
	@EventHandler
	public void onEntityInteraction(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		if (event.getRightClicked().getType() != EntityType.VILLAGER)
			return;
		
		Villager villager = (Villager) event.getRightClicked();
		for (AbstractNPC npc : plugin.npcStorage.getNPCs())
		{
			if (npc.getVillager().getUniqueId() == villager.getUniqueId())
			{
				npc.onInteract(player);
				event.setCancelled(true);
				return;
			}
		}
	}
}
