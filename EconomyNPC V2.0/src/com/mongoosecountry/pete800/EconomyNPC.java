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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.mongoosecountry.pete800.listeners.InventoryListener;
import com.mongoosecountry.pete800.listeners.NPCInteractListener;
import com.mongoosecountry.pete800.listeners.PlayerListener;
import com.mongoosecountry.pete800.npc.EntityStorage;
import com.mongoosecountry.pete800.npc.PlayerNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC.NPCType;
import com.mongoosecountry.pete800.util.Prices;
import com.mongoosecountry.pete800.util.TokenHandler;
import com.mongoosecountry.pete800.util.UUIDFinder;
import com.mongoosecountry.pete800.util.packet.WrapperPlayServerNamedEntitySpawn;


public class EconomyNPC extends JavaPlugin
{
	public Economy econ;
	public EntityStorage storage;
	public Logger log;
	public Prices prices;
	public TokenHandler tokens;
	public UUIDFinder uuid;
	
	public void onEnable()
	{
		log = getLogger();
		storage = new EntityStorage(this);
		prices = new Prices(this);
		tokens = new TokenHandler(this);
		
		try
		{
			uuid = new UUIDFinder(this);
		}
		catch (Exception e)
		{
			log.severe("Error with players.yml!");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		for (Player player : getServer().getOnlinePlayers())
			if (uuid.getPlayer(player.getName()) == null)
				uuid.addPlayer(player);
		
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
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new NPCInteractListener(this, PacketType.Play.Client.USE_ENTITY));
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		log.info("EconomyNPC is enabled!");
	}
	
	public void onDisable()
	{
		try
		{
			uuid.saveUUIDs();
		}
		catch (Exception e)
		{
			log.severe("Error saving players.yml");
		}
		
		YamlConfiguration npcs = new YamlConfiguration();
		List<Map<String, Object>> npcList = new ArrayList<Map<String, Object>>();
		for (PlayerNPC npc : storage.getEntities())
		{
			WrapperPlayServerNamedEntitySpawn entity = npc.getEntityData();
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("id", entity.getEntityID());
			values.put("x", entity.getPosition().getX());
			values.put("y", entity.getPosition().getY());
			values.put("z", entity.getPosition().getZ());
			values.put("type", npc.getType().toString());
			values.put("name", entity.getPlayerName());
			values.put("pitch", entity.getPitch());
			values.put("yaw", entity.getYaw());
			if (npc.getType() == NPCType.SHOP)
				values.put("inventory", npc.getInventory());
			
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
				for (PlayerNPC npc : storage.getEntities())
				{
					if (npc.getEntityData().getPlayerName().equals(args[0]))
					{
						player.openInventory(npc.getInventoryEdit(player));
						return true;
					}
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("tokens"))
		{
			if (args.length == 3)
			{
				if (args[0].equalsIgnoreCase("add"))
				{
					OfflinePlayer player = uuid.getOfflinePlayer(args[1]);
					if (player == null)
					{
						sender.sendMessage("Invalid player name.");
						return false;
					}
					
					tokens.addTokens(player.getUniqueId(), Integer.valueOf(args[2]));
					sender.sendMessage("Tokens added.");
					return true;
				}
				else if (args[0].equalsIgnoreCase("take"))
				{
					OfflinePlayer player = uuid.getOfflinePlayer(args[1]);
					if (player == null)
					{
						sender.sendMessage("Invalid player name.");
						return false;
					}
					
					tokens.addTokens(player.getUniqueId(), Integer.valueOf(args[2]));
					sender.sendMessage("Tokens removed.");
					return true;
				}
			}
			
			if (sender instanceof Player && sender.hasPermission("npc.tokens"))
			{
				sender.sendMessage(ChatColor.GOLD + "You have " + tokens.getNumTokens(((Player) sender).getUniqueId()) + " tokens.");
				return true;
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
