package com.mongoosecountry.pete800.handler.betting;

import com.mongoosecountry.pete800.npc.AbstractNPC;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("all")
public class BettingHandler
{
    UUID player, challanger;
    HashMap<UUID, Integer> playerBetMap = new HashMap<>();
    HashMap<UUID, Integer> challangerMap = new HashMap<>();

    int playerAmtWagered;
    int challangerAmtWagered;
    double playerOdds;
    double challangerOdds;

    public BettingHandler()
    {

    }

    public void onClick(Player player, AbstractNPC npc)
    {

    }

    public void updateOdds()
    {
        Set<UUID> keySet = playerBetMap.keySet();

        for (UUID player : keySet)
        {
            playerAmtWagered += playerBetMap.get(player);
        }

        keySet = challangerMap.keySet();
        for (UUID player : keySet)
        {
            challangerAmtWagered += challangerMap.get(player);
        }

        playerOdds = playerOdds / challangerOdds;
        challangerOdds = challangerOdds / playerOdds;
    }
}
