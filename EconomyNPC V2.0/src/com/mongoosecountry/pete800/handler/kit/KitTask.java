package com.mongoosecountry.pete800.handler.kit;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.mongoosecountry.pete800.EconomyNPC;

public class KitTask extends BukkitRunnable
{
	KitHandler handler;
	UUID player;
	
	public KitTask(EconomyNPC plugin, KitHandler handler, UUID player)
	{
		this.handler = handler;
		this.player = player;
	}

	@Override
	public void run()
	{
		handler.endTransaction(player);
	}
}
