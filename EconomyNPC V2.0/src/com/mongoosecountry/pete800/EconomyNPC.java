package com.mongoosecountry.pete800;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongoosecountry.pete800.hanlder.tokens.TokenHandler;
import com.mongoosecountry.pete800.listeners.EntityListener;
import com.mongoosecountry.pete800.listeners.InventoryListener;
import com.mongoosecountry.pete800.listeners.PlayerListener;
import com.mongoosecountry.pete800.npc.EntityStorage;
import com.mongoosecountry.pete800.npc.PlayerNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC.NPCType;
import com.mongoosecountry.pete800.util.Prices;
import com.mongoosecountry.pete800.util.UUIDFetcher;

public class EconomyNPC extends JavaPlugin
{
	public Economy econ;
	public EntityStorage storage;
	public Logger log;
	public Prices prices;
	public TokenHandler tokens;
	
	public void onEnable()
	{
		saveDefaultConfig();
		log = getLogger();
		storage = new EntityStorage(this);
		prices = new Prices(this);
		tokens = new TokenHandler(this);
		tokens.load();
		
		if (!setupEconomy())
		{
			log.warning("Vault not detected. Shutting down.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		log.info("EconomyNPC is enabled!");
	}
	
	public void onDisable()
	{
		tokens.save();
		storage.save();
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
				Player player = (Player) sender;
				storage.createNPC(args[1], player, NPCType.fromName(args[0]));
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("npcremove") && args.length == 1)
		{
			if(sender instanceof Player)
			{
				return storage.removeNPC(args[0], (Player) sender);
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("npcedit") && args.length == 1)
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				for (PlayerNPC npc : storage.getNPCs())
				{
					if (npc.getVillager().getCustomName().equals(args[0]))
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
					OfflinePlayer player = null;
					try
					{
						player = Bukkit.getOfflinePlayer(UUIDFetcher.getUUIDOf(args[1]));
					}
					catch (Exception e)
					{
						sender.sendMessage("Debug: error!");
						return false;
					}
					
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
					OfflinePlayer player = null;
					try
					{
						player = Bukkit.getOfflinePlayer(UUIDFetcher.getUUIDOf(args[1]));
					}
					catch (Exception e)
					{
						sender.sendMessage("Debug: error!");
						return false;
					}
					
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
