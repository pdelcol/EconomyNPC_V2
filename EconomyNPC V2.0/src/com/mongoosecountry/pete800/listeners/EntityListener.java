package com.mongoosecountry.pete800.listeners;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC;

public class EntityListener implements Listener
{
	EconomyNPC plugin;
	
	public EntityListener(EconomyNPC plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		Villager villager = (Villager) event.getEntity();
		for (PlayerNPC npc : plugin.storage.getNPCs())
		{
			if (npc.getVillager().getUniqueId() == villager.getUniqueId())
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event)
	{
		for (PlayerNPC npc : plugin.storage.getNPCs())
			if (event.getTarget().getUniqueId() == npc.getVillager().getUniqueId())
				event.setTarget(null);
	}
}
