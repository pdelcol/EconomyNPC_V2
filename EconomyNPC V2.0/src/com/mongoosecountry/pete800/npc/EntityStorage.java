package com.mongoosecountry.pete800.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC.NPCType;

public class EntityStorage
{
	ArrayList<PlayerNPC> entities = new ArrayList<PlayerNPC>();
	EconomyNPC plugin;
	int id = 1000;
	
	public EntityStorage(EconomyNPC plugin)
	{
		this.plugin = plugin;
	}
	
	public void createEntity(String entityName, UUID playerName, NPCType type)
	{
		boolean exists = false;
		String name = "";
		for(int x = 0; x < entities.size(); x++)
		{
			name = entities.get(x).name;
			if(name.equalsIgnoreCase(entityName))
			{
				exists = true;
				plugin.getServer().getPlayer(playerName).sendMessage(ChatColor.RED + "You cannot create an NPC with that name. Try a different name");
			}
		}
		
		if(!exists)
		{
			PlayerNPC npc = new PlayerNPC(entityName, plugin, type);
			for (PlayerNPC n : entities)
				if (id == n.getEntityData().getEntityID())
					id++;
			
			npc.createNPC(plugin.getServer().getPlayer(playerName).getPlayer(), id);
			entities.add(npc);
			id++;
			plugin.getServer().getPlayer(playerName).sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Success!");
		}
	}
	
	public void removeEntity(String entityName, UUID playerName)
	{
		boolean exists = false;
		String name = "";
		int removeNum = Integer.MAX_VALUE;
		for(int x = 0; x < entities.size(); x++)
		{
			name = entities.get(x).name;
			if(name.equalsIgnoreCase(entityName))
			{
				removeNum = x;
				exists = true;
			}
		}
		if(exists && removeNum != Integer.MAX_VALUE)
		{
			entities.get(removeNum).removeNPC();
			entities.remove(removeNum);
		}
	}
	
	public void resendPackets(UUID playerName)
	{
		for(int x = 0; x < entities.size(); x++)
		{
			entities.get(x).resendPacket(plugin.getServer().getPlayer(playerName));
		}
	}
	
	public PlayerNPC getNPC(String name)
	{
		for (PlayerNPC npc : entities)
			if (npc.spawned.getPlayerName().equals(name))
				return npc;
		
		return null;
	}
	
	public List<PlayerNPC> getEntities()
	{
		return entities;
	}
}
