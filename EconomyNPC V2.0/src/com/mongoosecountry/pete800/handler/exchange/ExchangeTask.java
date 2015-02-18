package com.mongoosecountry.pete800.handler.exchange;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.mongoosecountry.pete800.EconomyNPC;

public class ExchangeTask extends BukkitRunnable
{
	ExchangeHandler handler;
	UUID player;
	
	public ExchangeTask(EconomyNPC plugin, ExchangeHandler handler, UUID player)
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
