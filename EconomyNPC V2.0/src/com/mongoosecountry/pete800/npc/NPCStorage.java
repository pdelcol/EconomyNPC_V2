package com.mongoosecountry.pete800.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC.NPCType;

public class NPCStorage
{
	ArrayList<AbstractNPC> npcs = new ArrayList<AbstractNPC>();
	EconomyNPC plugin;
	private File npcFile;
	
	public NPCStorage(EconomyNPC plugin)
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
		{
			ConfigurationSection cs = npcs.getConfigurationSection("npc." + name);
			AbstractNPC npc = null;
			NPCType type = NPCType.fromName(cs.getString("type"));
			if (type == NPCType.BETTING)
				npc = new BettingNPC(plugin, name, cs);
			else if (type == NPCType.BLACKSMITH)
				npc = new BlacksmithNPC(plugin, name, cs);
			else if (type == NPCType.EXCHANGE)
				npc = new ExchangeNPC(plugin, name, cs);
			else if (type == NPCType.KIT)
				npc = new KitNPC(plugin, name, cs);
			else if (type == NPCType.SELL)
				npc = new ShopNPC(plugin, name, cs);
			else if (type == NPCType.SHOP)
				npc = new SellNPC(plugin, name, cs);
			
			if (type == null)
				plugin.getLogger().warning("Invalid type for NPC: " + name);
			else
				this.npcs.add(npc);
		}
	}
	
	public boolean createNPC(String name, Player player, NPCType type)
	{
		for(AbstractNPC npc : npcs)
		{
			if(npc.getName().equalsIgnoreCase(name))
			{
				player.sendMessage(ChatColor.RED + "You cannot create an NPC with that name. Try a different name");
				return false;
			}
		}
		
		AbstractNPC npc = null;
		if (type == NPCType.BETTING)
			npc = new BettingNPC(plugin, player.getLocation(), name);
		else if (type == NPCType.BLACKSMITH)
			npc = new BlacksmithNPC(plugin, player.getLocation(), name);
		else if (type == NPCType.EXCHANGE)
			npc = new ExchangeNPC(plugin, player.getLocation(), name);
		else if (type == NPCType.KIT)
			npc = new KitNPC(plugin, player.getLocation(), name);
		else if (type == NPCType.SELL)
			npc = new ShopNPC(plugin, player.getLocation(), name);
		else if (type == NPCType.SHOP)
			npc = new SellNPC(plugin, player.getLocation(), name);
		
		npcs.add(npc);
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Success!");
		return true;
	}
	
	public boolean removeNPC(String name, CommandSender sender)
	{
		boolean exists = false;
		int removeNum = Integer.MAX_VALUE;
		for(int x = 0; x < npcs.size(); x++)
		{
			if(npcs.get(x).getVillager().getCustomName().equalsIgnoreCase(name))
			{
				removeNum = x;
				exists = true;
			}
		}
		
		if(exists && removeNum != Integer.MAX_VALUE)
		{
			npcs.get(removeNum).despawnNPC();
			npcs.remove(removeNum);
			sender.sendMessage(ChatColor.GREEN + "NPC removed.");
			return true;
		}
		else
		{
			sender.sendMessage(ChatColor.DARK_RED + "There is no NPC with that name.");
			return false;
		}
	}
	
	public AbstractNPC getNPC(String name)
	{
		for (AbstractNPC npc : npcs)
			if (npc.getVillager().getCustomName().equalsIgnoreCase(name))
				return npc;
		
		return null;
	}
	
	public void save(boolean despawnNPCs)
	{
		YamlConfiguration npcs = new YamlConfiguration();
		List<String> names = new ArrayList<String>();
		for (AbstractNPC npc : this.npcs)
		{
			String name = npc.name;
			npcs.set("npc." + name, npc.save());
			names.add(name);
			if (despawnNPCs)
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
	
	public List<AbstractNPC> getNPCs()
	{
		return npcs;
	}
}
