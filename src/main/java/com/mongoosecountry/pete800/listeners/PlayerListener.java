package com.mongoosecountry.pete800.listeners;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

public class PlayerListener
{
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event)
    {
        Player player = event.getTargetEntity();
        if (!EconomyNPC.tokens.checkPlayer(player.getUniqueId()))
            EconomyNPC.tokens.addTokens(player.getUniqueId(), 0);

        if (Sponge.getServer().getOnlinePlayers().size() == 1)
        {
            //Needed delay so the NPCs actually spawn when the server has the chunks loaded for the player
            Task.builder().execute(() -> EconomyNPC.npcStorage.getNPCs().forEach(AbstractNPC::respawnNPC)).delayTicks(1L);
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event)
    {
        if (Sponge.getServer().getOnlinePlayers().size() == 1)
            EconomyNPC.npcStorage.getNPCs().forEach(AbstractNPC::despawnNPC);
    }

    @Listener
    public void onEntityInteraction(InteractEntityEvent event)
    {
        Player player = event.getCause().first(Player.class).get();
        if (event.getTargetEntity().getType() != EntityTypes.VILLAGER)
            return;

        Villager villager = (Villager) event.getTargetEntity();
        for (AbstractNPC npc : EconomyNPC.npcStorage.getNPCs())
        {
            if (npc.getVillager().getUniqueId() == villager.getUniqueId())
            {
                npc.onInteract(player);
                event.setCancelled(true);
                return;
            }
        }
    }
}
