package com.mongoosecountry.pete800;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.NPCEdit;
import com.mongoosecountry.pete800.command.NPCRemove;
import com.mongoosecountry.pete800.command.NPCSpawn;
import com.mongoosecountry.pete800.command.Tokens;
import com.mongoosecountry.pete800.hanlder.tokens.TokenHandler;
import com.mongoosecountry.pete800.listeners.EntityListener;
import com.mongoosecountry.pete800.listeners.InventoryListener;
import com.mongoosecountry.pete800.listeners.PlayerListener;
import com.mongoosecountry.pete800.npc.EntityStorage;
import com.mongoosecountry.pete800.util.Prices;

public class EconomyNPC extends JavaPlugin
{
	public Economy econ;
	public EntityStorage storage;
	public Logger log;
	public Prices prices;
	public TokenHandler tokens;
	private List<AbstractCommand> commands; 
	
	@Override
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
		
		commands = Arrays.asList(new NPCEdit(this), new NPCSpawn(this), new NPCRemove(this), new Tokens(this));
		
		getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		log.info("EconomyNPC is enabled!");
	}
	
	@Override
	public void onDisable()
	{
		tokens.save();
		storage.save();
		log.info("EconomyNPC is disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("npc"))
		{
			if (args.length > 0)
				for (AbstractCommand command : commands)
					if (command.getName().equalsIgnoreCase(args[0]))
						return command.onCommand(sender, moveArguments(args));
			
			List<String> help = new ArrayList<String>();
			String goldUnderline = ChatColor.GOLD + "" + ChatColor.UNDERLINE;
			help.add(goldUnderline + "EconomyNPC v2.0" + ChatColor.RESET + " by " + goldUnderline + "Bruce" + ChatColor.RESET + " & " + goldUnderline + "Pete");
			for (AbstractCommand command : commands)
			{
				help.add(command.getUsage() + ChatColor.DARK_PURPLE + command.getDescription());
				for (AbstractCommand subCommand : command.getSubCommands())
					help.add(subCommand.getUsage() + ChatColor.DARK_PURPLE + subCommand.getDescription());
			}
			
			for (String line : help)
				sender.sendMessage(line);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("npc"))
		{
			List<String> names = new ArrayList<String>();
			for (AbstractCommand command : commands)
			{
				if (args.length == 1 && (args[0].equals("") || command.getName().toLowerCase().startsWith(args[0].toLowerCase())))
					names.add(command.getName());
				else if (args.length == 2 && args[0].equalsIgnoreCase(command.getName()))
					return command.onTabComplete(sender, moveArguments(args));
				else if (args.length == 3)
					return null;
			}
			
			return names;
		}
		
		return null;
	}
	
	/* Might need to create a utility class for future utility methods */
	public String[] moveArguments(String[] args)
	{
		List<String> list = new ArrayList<String>();
		Collections.addAll(list, args);
		list.remove(0);
		return list.toArray(new String[0]);
	}

	public boolean isNumber(String string)
	{
		try
		{
			Integer.valueOf(string);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
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
