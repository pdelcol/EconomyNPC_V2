package com.mongoosecountry.pete800.handler.betting;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.npc.PlayerNPC;

public class BettingHandler {
	UUID player, challanger;
	HashMap<UUID, Integer> playerBetMap = new HashMap<UUID, Integer>();
	HashMap<UUID, Integer> challangerMap = new HashMap<UUID, Integer>();
	
	int playerAmtWagered;
	int challangerAmtWagered;
	double playerOdds;
	double challangerOdds;
	
	public BettingHandler()
	{
		
	}
	public void onClick(Player player, PlayerNPC npc, Economy econ)
	{
		
	}
	public void updateOdds()
	{
		Set<UUID> keySet = playerBetMap.keySet();
		
		for(UUID player : keySet)
		{
			playerAmtWagered += playerBetMap.get(player);
		}
		keySet = challangerMap.keySet();
		for(UUID player : keySet)
		{
			challangerAmtWagered += challangerMap.get(player);
		}
		playerOdds = playerOdds/challangerOdds;
		challangerOdds = challangerOdds/playerOdds;
	}
}
