package com.mongoosecountry.pete800;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mongoosecountry.pete800.util.WrapperPlayServerNamedEntitySpawn;

public class PlayerNPC {
	String name;
	EconomyNPC npc;
	WrapperPlayServerNamedEntitySpawn spawned;
	byte type = 0;
	//0 = Not Set Up
	//1 = Essentials
	//2 = Kit
	//3 = XP
	//4 = Blacksmith
	
	public PlayerNPC(EconomyNPC npc)
	{
		this("", npc);
	}
	
	public PlayerNPC(String name, EconomyNPC npc)
	{
		this.name = name;
		this.npc = npc;
	}
	
	public void createNPC(Player player, int id)
	{
		spawned = new WrapperPlayServerNamedEntitySpawn();
		spawned.setEntityID(id); 
		spawned.setPosition(player.getLocation().toVector());
		
		spawned.setPlayerUUID(UUID.randomUUID().toString());
		spawned.setPlayerName(ChatColor.GREEN + name); //If you set the name of a npc it renders the skin of the player with that skin!
		spawned.setYaw(player.getLocation().getYaw());
		spawned.setPitch(player.getLocation().getPitch());
		
		WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0); // Flags. Must be a byte.
        watcher.setObject(1, (short) 300); // Drowning counter. Must be short.
        watcher.setObject(8, (byte) 10); // Visible potion "bubbles". Zero means none.
        spawned.setMetadata(watcher);
		try {
			
				ProtocolLibrary.getProtocolManager().broadcastServerPacket(spawned.getHandle());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createNPC(JSONObject npc)
	{
		JSONObject loc = (JSONObject) npc.get("position");
		spawned = new WrapperPlayServerNamedEntitySpawn();
		spawned.setEntityID(Integer.valueOf(npc.get("id").toString()));
		spawned.setPlayerUUID(UUID.randomUUID().toString());
		spawned.setPlayerName(npc.get("name").toString());
		spawned.setPosition(new Vector(Double.valueOf(loc.get("x").toString()), Double.valueOf(loc.get("y").toString()), Double.valueOf(loc.get("z").toString())));
		spawned.setYaw(Float.valueOf(loc.get("yaw").toString()));
		spawned.setPitch(Float.valueOf(loc.get("pitch").toString()));
		
		JSONObject meta = (JSONObject) npc.get("metadata");
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(0, Byte.valueOf(meta.get("flag").toString()));
		watcher.setObject(1, Short.valueOf(meta.get("drowningcounter").toString()));
		watcher.setObject(8, Byte.valueOf(meta.get("potionbubbles").toString()));
		spawned.setMetadata(watcher);
		try
		{
			ProtocolLibrary.getProtocolManager().broadcastServerPacket(spawned.getHandle());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void removeNPC()
	{
		try {
			WrappedDataWatcher watcher = new WrappedDataWatcher();
	        watcher.setObject(0, (byte) 0); // Flags. Must be a byte.
	        watcher.setObject(6, (float) 0);
	        watcher.setObject(1, (short) 300); // Drowning counter. Must be short.
	        watcher.setObject(8, (byte) 0); // Visible potion "bubbles". Zero means none.
	        spawned.setMetadata(watcher);
	        ProtocolLibrary.getProtocolManager().broadcastServerPacket(spawned.getHandle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void resendPacket(Player p)
	{
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, spawned.getHandle());
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
