package com.mongoosecountry.pete800.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
	
	public boolean createNPC(String name, Player player, NPCType type)
	{
		for(PlayerNPC npc : entities)
		{
			if(npc.getVillager().getCustomName().equalsIgnoreCase(name))
			{
				player.sendMessage(ChatColor.RED + "You cannot create an NPC with that name. Try a different name");
				return false;
			}
		}
		
		entities.add(new PlayerNPC(plugin, type, player.getLocation(), name));
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Success!");
		return true;
	}
	
	public boolean removeNPC(String name, CommandSender sender)
	{
		boolean exists = false;
		int removeNum = Integer.MAX_VALUE;
		for(int x = 0; x < entities.size(); x++)
		{
			if(entities.get(x).getVillager().getCustomName().equalsIgnoreCase(name))
			{
				removeNum = x;
				exists = true;
			}
		}
		
		if(exists && removeNum != Integer.MAX_VALUE)
		{
			entities.get(removeNum).despawnNPC();
			entities.remove(removeNum);
			sender.sendMessage(ChatColor.GREEN + "NPC removed.");
			return true;
		}
		else
		{
			sender.sendMessage(ChatColor.DARK_RED + "There is no NPC with that name.");
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
