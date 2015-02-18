package com.mongoosecountry.pete800.handler.blacksmith;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.mongoosecountry.pete800.EconomyNPC;

public class BlacksmithTask extends BukkitRunnable
{
	BlacksmithHandler handler;
	UUID player;
	
	public BlacksmithTask(EconomyNPC plugin, BlacksmithHandler handler, UUID player)
	{
		this.handler = handler;
		this.player = player;
	}

	@Override
	public void run() {
		handler.removeTransaction(player);
	}
}