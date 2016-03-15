package com.mongoosecountry.pete800.listeners;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ai.GoalTypes;
import org.spongepowered.api.entity.ai.task.builtin.creature.target.FindNearestAttackableTargetAITask;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.ai.AITaskEvent;

import java.util.stream.Collectors;

public class EntityListener
{
    @Listener
    public void onEntityDamage(DamageEntityEvent event)
    {
        if (event.getTargetEntity().getType() != EntityTypes.VILLAGER)
            return;

        Entity villager = event.getTargetEntity();
        for (AbstractNPC npc : EconomyNPC.npcStorage.getNPCs())
        {
            if (npc.getVillager().getUniqueId() == villager.getUniqueId())
            {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Listener
    public void onEntityTarget(AITaskEvent event)
    {
        if (event.getGoal().getType() != GoalTypes.TARGET)
            return;

        if (!(event.getTask() instanceof FindNearestAttackableTargetAITask))
            return;

        FindNearestAttackableTargetAITask task = (FindNearestAttackableTargetAITask) event.getTask();
        if (task.getTargetClass() == Villager.class)
            return;

        for (AbstractNPC npc : EconomyNPC.npcStorage.getNPCs())
        {
            for (Living living : event.getTargetEntity().getWorld().getEntities().stream().filter(entity -> entity instanceof Living).map(entity -> (Living) entity).collect(Collectors.toList()))
            {
                if (npc.getVillager() != null && living.getUniqueId() == npc.getVillager().getUniqueId())
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
