package com.mongoosecountry.pete800.handler.exchange;

import org.bukkit.scheduler.BukkitRunnable;

import com.mongoosecountry.pete800.EconomyNPC;

public class ExchangeTask extends BukkitRunnable{
	ExchangeHandler handler;
	
	public ExchangeTask(EconomyNPC plugin, ExchangeHandler handler)
	{
		this.handler = handler;
	}
	
	@Override
	public void run() {
		handler.setPlayerName(null);
	}
}
