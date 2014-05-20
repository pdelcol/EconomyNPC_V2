package com.mongoosecountry.pete800;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mongoosecountry.pete800.util.WrapperPlayClientUseEntity;
import com.mongoosecountry.pete800.util.WrapperPlayServerNamedEntitySpawn;


public class EconomyNPC extends JavaPlugin
{
	Logger log;
	EntityStorage storage;
	PluginDescriptionFile pdf;
	
	public void onEnable()
	{
		log = getLogger();
		storage = new EntityStorage(this);
		pdf = this.getDescription();
		
		File npcFile = new File(getDataFolder(), "npcs.yml");
		if (!npcFile.exists())
		{
			try
			{
				npcFile.createNewFile();
			}
			catch (IOException e)
			{
				getLogger().severe("Error creating npcs.yml!");
				getServer().getPluginManager().disablePlugin(this);
			}
		}
		
		YamlConfiguration npcs = new YamlConfiguration();
		try
		{
			npcs.load(npcFile);
		}
		catch (FileNotFoundException e)
		{
			getLogger().severe("Error, npcs.yml is missing!");
			getServer().getPluginManager().disablePlugin(this);
		}
		catch (IOException e)
		{
			getLogger().severe("Error parsing npcs.yml!");
			getServer().getPluginManager().disablePlugin(this);
		}
		catch (InvalidConfigurationException e)
		{
			getLogger().severe("Formatting error in npcs.yml!");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		
		if (npcs.getList("npcs") instanceof List)
		{
			List<?> npcList = npcs.getList("npcs");
			for (Object obj : npcList)
			{
				if (obj instanceof Map)
				{
					PlayerNPC p = new PlayerNPC(this);
					p.createNPC((Map<?, ?>) obj);
				}
			}
		}
		
		getLogger().info(storage.entities.size() + "");
		for (Player player : getServer().getOnlinePlayers())
			storage.resendPackets(player.getUniqueId());
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY)
		{
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
				Player player = event.getPlayer();
				if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY)
				{
					WrapperPlayClientUseEntity use = new WrapperPlayClientUseEntity(event.getPacket());
					for (PlayerNPC npc : storage.entities)
					{
						//TODO set up inventory window
						if (npc.spawned.getEntityID() == use.getTargetID())
							player.sendMessage("quack");
					}
				}
			}
		});
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		log.info("EconomyNPC is enabled!");
	}
	
	public void onDisable()
	{
		YamlConfiguration npcs = new YamlConfiguration();
		List<Map<String, Object>> npcList = new ArrayList<Map<String, Object>>();
		for (PlayerNPC npc : storage.entities)
		{
			WrapperPlayServerNamedEntitySpawn entity = npc.spawned;
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("id", entity.getEntityID());
			values.put("x", entity.getPosition().getX());
			values.put("y", entity.getPosition().getY());
			values.put("z", entity.getPosition().getZ());
			values.put("name", ChatColor.stripColor(entity.getPlayerName()));
			values.put("pitch", entity.getPitch());
			values.put("yaw", entity.getYaw());
			npcList.add(values);
		}
		
		npcs.set("npcs", npcList);
		try
		{
			npcs.save(new File(getDataFolder(), "npcs.yml"));
		}
		catch (IOException e)
		{
			getLogger().warning("Error saving npcs.yml!");
		}
		
		log.info("EconomyNPC is disabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("npcspawn") && args.length == 1)
		{
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				storage.createEntity(args[0], player.getUniqueId());
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("npcremove") && args.length == 1)
		{
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				storage.removeEntity(args[0], player.getUniqueId());
				return true;
			}
		}
		return false;
		
	}
}
