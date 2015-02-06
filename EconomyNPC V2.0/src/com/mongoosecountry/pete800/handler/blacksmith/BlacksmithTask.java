package com.mongoosecountry.pete800.handler.blacksmith;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongoosecountry.pete800.EconomyNPC;

public class BlacksmithTask extends BukkitRunnable{
	BlacksmithHandler handler;
	public BlacksmithTask(EconomyNPC plugin, BlacksmithHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		handler.playerName = "";
		handler.cost = 0.0;
		handler.material = Material.AIR;
		handler.map = null;
	}
}