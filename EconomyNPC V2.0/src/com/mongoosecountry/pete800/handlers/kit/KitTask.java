package com.mongoosecountry.pete800.handlers.kit;

import org.bukkit.scheduler.BukkitRunnable;

import com.mongoosecountry.pete800.EconomyNPC;

public class KitTask extends BukkitRunnable{
	KitHandler handler;
	public KitTask(EconomyNPC plugin, KitHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		handler.setNpcName("");
		handler.setNumTokens(0);
		handler.setPlayer(null);
	}
}
