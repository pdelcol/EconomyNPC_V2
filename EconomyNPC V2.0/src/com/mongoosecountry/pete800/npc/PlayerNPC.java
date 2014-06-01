package com.mongoosecountry.pete800.npc;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.Exchange.ExchangeHandler;
import com.mongoosecountry.pete800.Exchange.ExchangeTask;
import com.mongoosecountry.pete800.util.blacksmith.*;
import com.mongoosecountry.pete800.util.kit.*;
import com.mongoosecountry.pete800.util.packet.WrapperPlayServerNamedEntitySpawn;

public class PlayerNPC
{
	String name;
	EconomyNPC plugin;
	WrapperPlayServerNamedEntitySpawn spawned;
	YamlConfiguration inv = new YamlConfiguration();
	NPCType type;
	BlacksmithHandler blacksmith;
	KitHandler kit;
	ExchangeHandler exchange;
	public PlayerNPC(EconomyNPC npc)
	{
		this("", npc, null);
	}
	
	public PlayerNPC(String name, EconomyNPC plugin, NPCType type)
	{
		this.name = name;
		this.plugin = plugin;
		this.type = type;
		if (this.type == NPCType.BLACKSMITH)
			this.blacksmith = new BlacksmithHandler();
		if(this.type == NPCType.KIT)
			this.kit = new KitHandler();
		if(this.type == NPCType.XP)
			this.exchange = new ExchangeHandler();
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
		if (type == NPCType.BLACKSMITH)
			blacksmith = new BlacksmithHandler();
		if (type == NPCType.KIT)
			kit = new KitHandler();
		
		spawned.setPlayerName(name);
		spawned.setPosition(new Vector(Double.valueOf(npcData.get("x").toString()), Double.valueOf(npcData.get("y").toString()), Double.valueOf(npcData.get("z").toString())));
		spawned.setYaw(Float.valueOf(npcData.get("yaw").toString()));
		spawned.setPitch(Float.valueOf(npcData.get("pitch").toString()));
		
		WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0); // Flags. Must be a byte.
        watcher.setObject(1, (short) 300); // Drowning counter. Must be short.
        watcher.setObject(8, (byte) 10); // Visible potion "bubbles". Zero means none.
        spawned.setMetadata(watcher);
        plugin.storage.entities.add(this);
        if (this.type == NPCType.SHOP || this.type == NPCType.KIT)
        {
        	Map<?, ?> inventory = (Map<?, ?>) npcData.get("inventory");
        	for (Entry<?, ?> entry : inventory.entrySet())
            {
            	if (!(entry.getValue() instanceof MemorySection))
            	{
            		int slot = Integer.valueOf(entry.getKey().toString());
            		ItemStack item = (ItemStack) entry.getValue();
            		if (this.type == NPCType.SHOP)
            		{
	            		ItemMeta meta = item.getItemMeta();
	            		meta.setLore(Arrays.asList("$" + plugin.prices.getPrice(item)));
	            		item.setItemMeta(meta);
            		}
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
		inv = new YamlConfiguration();
		for (int slot = 0; slot < inventory.getSize(); slot++)
		{
			ItemStack item = inventory.getItem(slot);
			if (item != null)
			{
				if (type == NPCType.SHOP)
				{
					ItemMeta meta = item.getItemMeta();
					meta.setLore(Arrays.asList("$" + plugin.prices.getPrice(item)));
					item.setItemMeta(meta);
				}
				
				inv.set(slot + "", inventory.getItem(slot));
			}
		}
	}
	
	public void handleNonInventoryNPC(Player player, Economy econ)
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
		else if (NPCType.XP == this.type)
		{
			if(exchange.getPlayerName().equals(player.getUniqueId()))
			{
				if (plugin.tokens.removeTokens(player.getUniqueId(), 1))
				{
					//Change as necessary for now.
					double money = 2000.0;
					OfflinePlayer p = plugin.getServer().getOfflinePlayer(player.getUniqueId());
					plugin.econ.depositPlayer(p, money);
					player.sendMessage(ChatColor.GOLD + "You have traded 1 token for $" + money + ".");
				}
				else
					player.sendMessage(ChatColor.RED + "You do not have enough tokens for that");
			}else{
				player.sendMessage(ChatColor.BLUE + "You are about to exchange 1 token for $2000");
				player.sendMessage(ChatColor.BLUE + "Right click again to continue");
				@SuppressWarnings("unused")
				BukkitTask task = new ExchangeTask(this.plugin, exchange).runTaskLater(this.plugin, 100);
			}
		}
		else if (NPCType.KIT == this.type)
		{
			if ((kit.getPlayer() == null || !kit.getPlayer().equals(player.getUniqueId())) && (kit.getNpcName() == null || !kit.getNpcName().equals(this.name)))
			{
				if(getInventory(player).getItem(getInventory(player).getSize()-1).getType() == Material.COAL)
				{
					ItemStack item = getInventory(player).getItem(getInventory(player).getSize()-1);
					int numTokens = item.getAmount();
					player.sendMessage(ChatColor.GOLD + "Do you really want to sell " + numTokens + " tokens for this kit?");
					kit.setNpcName(this.name);
					kit.setNumTokens(numTokens);
					kit.setPlayer(player.getUniqueId());
					@SuppressWarnings("unused")
					BukkitTask task = new KitTask(this.plugin, kit).runTaskLater(this.plugin, 100);
				}
			}
			else if(kit.getPlayer().equals(player.getUniqueId()) && kit.getNpcName().equals(this.name))
			{
				if(plugin.tokens.removeTokens(player.getUniqueId(), kit.getNumTokens()))
				{ 
					boolean dropped = false;
					for (int i = 0; i < getInventory(player).getSize()-1; i++) {
						if (getInventory(player).getItem(i) != null) {
							//int damage = (short)new ItemStack(inv.getItem(i).getType()).getDurability() - inv.getItem(i).getDurability();		
							if(player.getInventory().firstEmpty() != -1){
								player.getInventory()
										.addItem(
												new ItemStack(getInventory(player).getItem(i).getType(), getInventory(player).getItem(i).getAmount(),getInventory(player).getItem(i).getDurability()));
							}
							else
							{
								player.getLocation().getWorld().dropItemNaturally(player.getLocation(), new ItemStack(getInventory(player).getItem(i).getType(), getInventory(player).getItem(i).getAmount(),getInventory(player).getItem(i).getDurability()));
								dropped = true;
							}
						}
					}
					
					if(dropped)
					{
						player.sendMessage("Your inventory was full so some items were dropped at your feet");
					}
					player.sendMessage(ChatColor.GREEN + "You exchanged " + kit.getNumTokens() + " tokens for a kit!");
					
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You do not have enough tokens to do this!");
				}
			}
		}
		else if(NPCType.BETTING == this.type)
		{
			//I'm going to offload this code to another class because it might be a lot of code
		}
	}
	
	public NPCType getType()
	{
		return type;
	}
	
	public WrapperPlayServerNamedEntitySpawn getEntityData()
	{
		return spawned;
	}
	
	public YamlConfiguration getInventory()
	{
		return inv;
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
		// Exchange tokens for XP/Money
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
			
			return null;
		}
	}
}
