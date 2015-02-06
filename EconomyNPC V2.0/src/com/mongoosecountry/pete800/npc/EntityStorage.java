package com.mongoosecountry.pete800.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC.NPCType;

public class EntityStorage
{
	ArrayList<PlayerNPC> entities = new ArrayList<PlayerNPC>();
	EconomyNPC plugin;
	private File npcFile;
	
	public EntityStorage(EconomyNPC plugin)
	{
		this.plugin = plugin;
		npcFile = new File(plugin.getDataFolder(), "npcs.yml");
		if (!npcFile.exists())
		{
			try
			{
				npcFile.createNewFile();
			}
			catch (IOException e)
			{
				plugin.log.severe("Error creating npcs.yml!");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
		
		YamlConfiguration npcs = YamlConfiguration.loadConfiguration(npcFile);
		for (String name : npcs.getStringList("names"))
			entities.add(new PlayerNPC(plugin, name, npcs.getConfigurationSection("npc." + name)));
	}
	
	public boolean createNPC(String npcName, Player player, NPCType type)
	{
		for(PlayerNPC npc : entities)
		{
			if(npc.getVillager().getCustomName().equalsIgnoreCase(npcName))
			{
				player.sendMessage(ChatColor.RED + "You cannot create an NPC with that name. Try a different name");
				return false;
			}
		}
		
		PlayerNPC npc = new PlayerNPC(plugin, type);
		try
		{
			npc.createNPC(player, npcName);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			player.sendMessage("Debug: error!");
			return false;
		}
		
		entities.add(npc);
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Success!");
		return true;
	}
	
	public boolean removeNPC(String npcName, Player player)
	{
		boolean exists = false;
		int removeNum = Integer.MAX_VALUE;
		for(int x = 0; x < entities.size(); x++)
		{
			if(entities.get(x).getVillager().getCustomName().equalsIgnoreCase(npcName))
			{
				removeNum = x;
				exists = true;
			}
		}
		
		if(exists && removeNum != Integer.MAX_VALUE)
		{
			entities.get(removeNum).removeNPC();
			entities.remove(removeNum);
			player.sendMessage("Debug: success!");
			return true;
		}
		else
		{
			player.sendMessage("Debug: error!");
			return false;
		}
	}
	
	public PlayerNPC getNPC(String name)
	{
		for (PlayerNPC npc : entities)
			if (npc.getVillager().getCustomName().equalsIgnoreCase(name))
				return npc;
		
		return null;
	}
	
	public void save()
	{
		YamlConfiguration npcs = new YamlConfiguration();
		List<String> names = new ArrayList<String>();
		for (PlayerNPC npc : entities)
		{
			String name = npc.name;
			npcs.set("npc." + name, npc.save());
			names.add(name);
			npc.despawnNPC();
		}
		
		npcs.set("names", names);
		try
		{
			npcs.save(npcFile);
		}
		catch (IOException e)
		{
			plugin.log.warning("Error saving npcs.yml!");
		}
	}
	
	public List<PlayerNPC> getNPCs()
	{
		return entities;
	}
}
