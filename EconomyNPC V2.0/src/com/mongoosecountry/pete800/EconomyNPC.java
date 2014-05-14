package com.mongoosecountry.pete800;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class EconomyNPC extends JavaPlugin{
	Logger log;
	EntityStorage storage;
	PluginDescriptionFile pdf;
	public void onEnable()
	{
		log = getLogger();
		storage = new EntityStorage(this);
		pdf = this.getDescription();
		log.info("EconomyNPC is enabled!");
	}
	public void onDisable()
	{
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
