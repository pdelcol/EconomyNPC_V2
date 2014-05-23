package com.mongoosecountry.pete800;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mongoosecountry.pete800.PlayerNPC.NPCType;
import com.mongoosecountry.pete800.util.WrapperPlayClientUseEntity;
import com.mongoosecountry.pete800.util.WrapperPlayServerNamedEntitySpawn;


public class EconomyNPC extends JavaPlugin
{
	Economy econ;
	EntityStorage storage;
	Logger log;
	PluginDescriptionFile pdf;
	Prices prices;
	
	public void onEnable()
	{
		log = getLogger();
		storage = new EntityStorage(this);
		pdf = getDescription();
		prices = new Prices(this); 
		
		File npcFile = new File(getDataFolder(), "npcs.yml");
		if (!npcFile.exists())
		{
			try
			{
				npcFile.createNewFile();
			}
			catch (IOException e)
			{
				log.severe("Error creating npcs.yml!");
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
			log.severe("Error, npcs.yml is missing!");
			getServer().getPluginManager().disablePlugin(this);
		}
		catch (IOException e)
		{
			log.severe("Error parsing npcs.yml!");
			getServer().getPluginManager().disablePlugin(this);
		}
		catch (InvalidConfigurationException e)
		{
			log.severe("Formatting error in npcs.yml!");
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
		
		if (!setupEconomy())
		{
			log.warning("Vault not detected. Shutting down.");
			getServer().getPluginManager().disablePlugin(this);
		}
		
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
						if (npc.spawned.getEntityID() == use.getTargetID())
						{
							if(npc.type == NPCType.BLACKSMITH)
							{
								npc.handleNonInventoryNPC(player, econ, (EconomyNPC) plugin);
							}else{
								player.openInventory(npc.getInventory(player));
							}
						}
				}
			}
		});
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
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
			values.put("type", npc.type.toString());
			values.put("name", entity.getPlayerName());
			values.put("pitch", entity.getPitch());
			values.put("yaw", entity.getYaw());
			values.put("inventory", npc.inv);
			npcList.add(values);
		}
		
		npcs.set("npcs", npcList);
		try
		{
			npcs.save(new File(getDataFolder(), "npcs.yml"));
		}
		catch (IOException e)
		{
			log.warning("Error saving npcs.yml!");
		}
		
		log.info("EconomyNPC is disabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!sender.hasPermission("npc.commands"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("npcspawn") && args.length == 2)
		{
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				storage.createEntity(args[1], player.getUniqueId(), NPCType.fromName(args[0]));
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
		
		if (cmd.getName().equalsIgnoreCase("npcedit") && args.length == 1)
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				for (PlayerNPC npc : storage.entities)
				{
					if (npc.spawned.getPlayerName().equals(args[0]))
					{
						player.openInventory(npc.getInventoryEdit(player));
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;

		econ = rsp.getProvider();
		return econ != null;
	}
}
