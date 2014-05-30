package com.mongoosecountry.pete800.util.kit;

import java.util.UUID;

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
		//There is a chance that this will actually make a legit UUID
		//But it is so small that it is pretty much negligible
		handler.setPlayer(null);
	}
}
