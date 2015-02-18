package com.mongoosecountry.pete800;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.enpc.ENPC;
import com.mongoosecountry.pete800.command.tokens.Tokens;
import com.mongoosecountry.pete800.handler.tokens.TokenHandler;
import com.mongoosecountry.pete800.listeners.EntityListener;
import com.mongoosecountry.pete800.listeners.InventoryListener;
import com.mongoosecountry.pete800.listeners.PlayerListener;
import com.mongoosecountry.pete800.npc.NPCStorage;
import com.mongoosecountry.pete800.util.Prices;

public class EconomyNPC extends JavaPlugin
{
	public Economy econ;
	public NPCStorage npcStorage;
	public Logger log;
	public Prices prices;
	public TokenHandler tokens;
	public List<AbstractCommand> commands;
	
	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		log = getLogger();
		npcStorage = new NPCStorage(this);
		prices = new Prices(this);
		tokens = new TokenHandler(this);
		tokens.load();
		
		if (!setupEconomy())
		{
			log.warning("Vault not detected. Shutting down.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		commands = Arrays.asList(new ENPC(this), new Tokens(this));
		
		getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		log.info("EconomyNPC is enabled!");
	}
	
	@Override
	public void onDisable()
	{
		tokens.save();
		npcStorage.save(true);
		log.info("EconomyNPC is disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		for (AbstractCommand command : commands)
			if (command.getName().equalsIgnoreCase(cmd.getName()))
				return command.onCommand(sender, args);
		
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
	
	/* Might need to create a utility class for future utility methods */
	public String[] moveArguments(String[] args)
	{
		List<String> list = new ArrayList<String>();
		Collections.addAll(list, args);
		if (list.size() > 0)
			list.remove(0);
		
		return list.toArray(new String[0]);
	}

	public boolean isNumber(String string)
	{
		try
		{
			Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
	
	public Profession getProfessionFromName(String name)
	{
		for (Profession profession : Profession.values())
			if (profession.toString().equalsIgnoreCase(name))
				return profession;
		
		return null;
	}
}
