package com.mongoosecountry.pete800;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mongoosecountry.pete800.util.WrapperPlayServerNamedEntitySpawn;

public class PlayerNPC {
	String name;
	EconomyNPC npc;
	WrapperPlayServerNamedEntitySpawn spawned;
	YamlConfiguration inv = new YamlConfiguration();
	NPCType type;
	
	public PlayerNPC(EconomyNPC npc)
	{
		this("", npc, null);
	}
	
	public PlayerNPC(String name, EconomyNPC npc, NPCType type)
	{
		this.name = name;
		this.npc = npc;
		this.type = type;
	}
	
	public void createNPC(Player player, int id)
	{
		spawned = new WrapperPlayServerNamedEntitySpawn();
		spawned.setEntityID(id); 
		spawned.setPosition(player.getLocation().toVector());
		
		spawned.setPlayerUUID(UUID.randomUUID().toString());
		spawned.setPlayerName(name); //If you set the name of a npc it renders the skin of the player with that skin!
		spawned.setYaw(player.getLocation().getYaw());
		spawned.setPitch(player.getLocation().getPitch());
		
		WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0); // Flags. Must be a byte.
        watcher.setObject(1, (short) 300); // Drowning counter. Must be short.
        watcher.setObject(8, (byte) 10); // Visible potion "bubbles". Zero means none.
        spawned.setMetadata(watcher);
        ItemStack item = new ItemStack(Material.DIAMOND, 64);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList("$" + npc.prices.getPrice(item)));
        item.setItemMeta(meta);
        inv.set("0", item);
		try {
			ProtocolLibrary.getProtocolManager().broadcastServerPacket(spawned.getHandle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createNPC(Map<?, ?> npcData)
	{
		spawned = new WrapperPlayServerNamedEntitySpawn();
		spawned.setEntityID(Integer.valueOf(npcData.get("id").toString()));
		spawned.setPlayerUUID(UUID.randomUUID().toString());
		this.name = npcData.get("name").toString();
		this.type = NPCType.fromName(npcData.get("type").toString());
		spawned.setPlayerName(name);
		spawned.setPosition(new Vector(Double.valueOf(npcData.get("x").toString()), Double.valueOf(npcData.get("y").toString()), Double.valueOf(npcData.get("z").toString())));
		spawned.setYaw(Float.valueOf(npcData.get("yaw").toString()));
		spawned.setPitch(Float.valueOf(npcData.get("pitch").toString()));
		
		WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0); // Flags. Must be a byte.
        watcher.setObject(1, (short) 300); // Drowning counter. Must be short.
        watcher.setObject(8, (byte) 10); // Visible potion "bubbles". Zero means none.
        spawned.setMetadata(watcher);
        npc.storage.entities.add(this);
        if (!name.equalsIgnoreCase("Sell"))
        {
        	Map<?, ?> inventory = (Map<?, ?>) npcData.get("inventory");
        	for (Entry<?, ?> entry : inventory.entrySet())
            {
            	if (!(entry.getValue() instanceof MemorySection))
            	{
            		int slot = Integer.valueOf(entry.getKey().toString());
            		ItemStack item = (ItemStack) entry.getValue();
            		ItemMeta meta = item.getItemMeta();
            		meta.setLore(Arrays.asList("$" + npc.prices.getPrice(item)));
            		item.setItemMeta(meta);
            		inv.set(slot + "", item);
            	}
            }
        }
        
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
			e.printStackTrace();
		}
	}
	
	public Inventory getInventory(Player player)
	{
		Inventory inventory = Bukkit.createInventory(player, 54, name + "'s Shop");
		if (type == NPCType.SHOP)
			for (int slot = 0; slot < inventory.getSize(); slot++)
				inventory.setItem(slot, inv.getItemStack("" + slot));
		
		return inventory; 
	}
	
	public Inventory getInventoryEdit(Player player)
	{
		Inventory inventory = Bukkit.createInventory(player, 54, spawned.getPlayerName() + " - Edit");
		for (int slot = 0; slot < inventory.getSize(); slot++)
			inventory.setItem(slot, inv.getItemStack("" + slot));
		
		return inventory;
	}
	
	public void updateInventory(Inventory inventory)
	{
		for (int slot = 0; slot < inventory.getSize(); slot++)
		{
			ItemStack item = inventory.getItem(slot);
			if (item != null)
			{
				ItemMeta meta = item.getItemMeta();
				meta.setLore(Arrays.asList("$" + npc.prices.getPrice(item)));
				item.setItemMeta(meta);
				inv.set(slot + "", inventory.getItem(slot));
			}
		}
	}
	
	public static enum NPCType
	{
		// Gambling NPC
		BETTING,
		// Repair tools/armor
		BLACKSMITH,
		// Buy kits
		KIT,
		// Standard shop
		SHOP,
		// Sell items to the NPC
		SELL,
		// Exchange tokens for XP
		XP;
		
		public static NPCType fromName(String name)
		{
			for (NPCType type : values())
			{
				if (name.toUpperCase().equals(type.toString()))
				{
					return type;
				}
			}
			
			return SHOP;
		}
	}
}
