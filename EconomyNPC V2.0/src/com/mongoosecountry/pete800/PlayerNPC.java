package com.mongoosecountry.pete800;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mongoosecountry.pete800.util.WrapperPlayServerNamedEntitySpawn;
import com.mongoosecountry.pete800.util.BlacksmithHandler;
import com.mongoosecountry.pete800.util.BlacksmithTask;

public class PlayerNPC {
	String name;
	EconomyNPC npc;
	WrapperPlayServerNamedEntitySpawn spawned;
	YamlConfiguration inv = new YamlConfiguration();
	NPCType type;
	BlacksmithHandler blacksmith;
	
	public PlayerNPC(EconomyNPC npc)
	{
		this("", npc, null);
	}
	
	public PlayerNPC(String name, EconomyNPC npc, NPCType type)
	{
		this.name = name;
		this.npc = npc;
		this.type = type;
		if (type.equals(NPCType.BLACKSMITH))
			blacksmith = new BlacksmithHandler();
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
		if (type.equals(NPCType.BLACKSMITH))
			blacksmith = new BlacksmithHandler();
		
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
	
	public void handleNonInventoryNPC(Player player, Economy econ, EconomyNPC plugin)
	{
		if(NPCType.BLACKSMITH == this.type)
		{
			int damage = player.getItemInHand().getDurability() - new ItemStack(player.getItemInHand().getType()).getDurability();
			int costPerDamage = 1;
			
			int multiplyer = 0;
			if(player.getItemInHand().getEnchantments().size() != 0){
				multiplyer = player.getItemInHand().getEnchantments().size();
				Set<Enchantment> keyset = player.getItemInHand().getEnchantments().keySet();
				Iterator<Enchantment> iter = keyset.iterator();
				for(int q = 0; q < player.getItemInHand().getEnchantments().size(); q++)
				{
					if(iter.hasNext())
						multiplyer += player.getItemInHand().getEnchantments().get(iter.next());
				}
			}
			if(damage > 0)
			{
				
				if(blacksmith.getPlayerName().equalsIgnoreCase("")){
					double cost = damage * costPerDamage;
					if(multiplyer > 0){
						cost = cost * multiplyer;
					}
					player.sendMessage(ChatColor.GOLD + "The tool will cost: " + cost + " if you would like to reforge it, right click the blacksmith again");
					blacksmith.addInfo(cost,player.getName(),player.getItemInHand().getType(),player.getItemInHand().getEnchantments());
					
					@SuppressWarnings("unused")
					BukkitTask task = new BlacksmithTask(plugin, blacksmith).runTaskLater(plugin, 100);
					return;
				}
				else if(blacksmith.getPlayerName().equalsIgnoreCase(player.getName()))
				{
					if(player.getItemInHand().getType() == blacksmith.getMaterial()){
						boolean good = true;
						if(player.getItemInHand().getEnchantments().size() == blacksmith.map.size()){
							Set<Enchantment> keyset = player.getItemInHand().getEnchantments().keySet();
							Iterator<Enchantment> iter = keyset.iterator();
							while(iter.hasNext())
							{
								Enchantment enchant = iter.next();
								if(blacksmith.map.containsKey(enchant))
								{
									if(player.getItemInHand().getEnchantmentLevel(enchant) == blacksmith.map.get(enchant))
									{
										
									}else{
										good = false;
									}
								}else{
									good = false;
								}
							}
						}else{
							good = false;
						}
						if(!good)
						{
							player.sendMessage(ChatColor.RED + "Somethings not right with that!");
							return;
						}
						if (econ.withdrawPlayer(player,blacksmith.getCost()).transactionSuccess())
						{
							player.getItemInHand().setDurability(new ItemStack(blacksmith.getMaterial()).getDurability());
							blacksmith = new BlacksmithHandler();
							return;
						}else{
							player.sendMessage(ChatColor.RED + "You do not have enough money for that :(");
						}
					}
				}
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
