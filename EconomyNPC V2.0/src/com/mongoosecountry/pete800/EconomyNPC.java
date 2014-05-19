package com.mongoosecountry.pete800;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
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
		
		File npcFile = new File(getDataFolder(), "NPC.json");
		if (npcFile.exists())
		{
			JSONParser parser = new JSONParser();
			try
			{
				JSONArray npcs = (JSONArray) parser.parse(new FileReader(new File(getDataFolder(), "NPC.json")));
				for (Object npc : npcs)
				{
					getLogger().info(npc.toString());
					PlayerNPC n = new PlayerNPC(this);
					n.createNPC((JSONObject) npc);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				getLogger().severe("Error parsing NPC.json. Please check formatting.");
			}
		}
		
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
						//TODO debug code
						if (npc.spawned.getEntityID() == use.getTargetID())
							player.sendMessage("quack");
					}
				}
			}
		});
		
		log.info("EconomyNPC is enabled!");
	}
	
	@SuppressWarnings("unchecked")
	public void onDisable()
	{
		JSONArray npcs = new JSONArray();
		for (PlayerNPC npc : storage.entities)
		{
			WrapperPlayServerNamedEntitySpawn entity = npc.spawned;
			JSONObject n = new JSONObject();
			n.put("id", entity.getEntityID());
			n.put("name", entity.getPlayerName());
			JSONObject location = new JSONObject();
			location.put("x", entity.getX());
			location.put("y", entity.getY());
			location.put("z", entity.getZ());
			location.put("pitch", entity.getPitch());
			location.put("yaw", entity.getYaw());
			n.put("position", location);
			WrappedDataWatcher watcher = entity.getMetadata();
			JSONObject meta = new JSONObject();
			meta.put("flag", watcher.getObject(0));
			meta.put("drowningcounter", watcher.getObject(1));
			meta.put("potionbubbles", watcher.getObject(8));
			n.put("metadata", meta);
			npcs.add(n);
		}
		
		File npcFile = new File(getDataFolder(), "NPC.json");
		File npcBackUp = new File(getDataFolder(), "NPC.json.bak");
		if (npcFile.exists())
		{
			npcFile.renameTo(npcBackUp);
		}
		
		try
		{
			npcFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(npcFile.getAbsoluteFile()));
			bw.write(npcs.toJSONString());
			bw.close();
		}
		catch (IOException e)
		{
			getLogger().severe("An error has occurred trying to save NPC.json. Restoring from backup.");
			npcBackUp.renameTo(npcFile);
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
