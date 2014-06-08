package com.mongoosecountry.pete800.betting;

import java.util.HashMap;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.npc.PlayerNPC;

public class BettingHandler {
	UUID player, challanger;
	HashMap<UUID, Integer> playerBetMap = new HashMap<UUID, Integer>();
	HashMap<UUID, Integer> challangerMap = new HashMap<UUID, Integer>();
	
	public BettingHandler()
	{
		
	}
	public void onClick(Player player, PlayerNPC npc, Economy econ)
	{
		
	}
}
