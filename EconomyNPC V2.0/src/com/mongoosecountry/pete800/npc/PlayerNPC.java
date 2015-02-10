package com.mongoosecountry.pete800.npc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.handler.blacksmith.BlacksmithHandler;
import com.mongoosecountry.pete800.handler.blacksmith.BlacksmithTask;
import com.mongoosecountry.pete800.handler.exchange.ExchangeHandler;
import com.mongoosecountry.pete800.handler.exchange.ExchangeTask;
import com.mongoosecountry.pete800.handler.kit.KitHandler;
import com.mongoosecountry.pete800.handler.kit.KitTask;

public class PlayerNPC
{
	EconomyNPC plugin;
	Villager villager;
	ConfigurationSection inv;
	NPCType type;
	BlacksmithHandler blacksmith;
	KitHandler kit;
	ExchangeHandler exchange;
	String name;
	Location location;
	
	public PlayerNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		this(plugin, NPCType.fromName(cs.getString("type")), new Location(plugin.getServer().getWorld(cs.getString("world")), cs.getDouble("x"), cs.getDouble("y"), cs.getDouble("z")), name);
		if (cs.isSet("inventory"))
			this.inv = cs.getConfigurationSection("inventory");
		
		if (plugin.getServer().getOnlinePlayers().size() != 0)
			respawnNPC();
	}
	
	public PlayerNPC(EconomyNPC plugin, NPCType type, Location location, String name)
	{
		this.plugin = plugin;
		this.type = type;
		this.name = name;
		this.location = location;
		if (plugin.getServer().getOnlinePlayers().size() != 0)
			respawnNPC();
		
		if (this.type == NPCType.BLACKSMITH)
			this.blacksmith = new BlacksmithHandler();
		if(this.type == NPCType.KIT)
			this.kit = new KitHandler();
		if(this.type == NPCType.XP)
			this.exchange = new ExchangeHandler();
		
		if (this.type == NPCType.KIT || this.type == NPCType.SHOP)
			inv = new YamlConfiguration();
	}
	
	public void respawnNPC()
	{
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName(name);
		villager.setCustomNameVisible(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 6));
		villager.setRemoveWhenFarAway(false);
		villager.setProfession(type.getProfession());
	}
	
	public void despawnNPC()
	{
		if (villager == null)
			return;
		
		villager.remove();
		villager = null;
	}
	
	public Inventory getInventory(Player player)
	{
		Inventory inventory = Bukkit.createInventory(player, 54, villager.getCustomName() + "'s Shop");
		if (type != NPCType.SELL)
			for (int slot = 0; slot < inventory.getSize(); slot++)
				inventory.setItem(slot, inv.getItemStack("" + slot));
		
		return inventory;
	}
	
	public Inventory getInventoryEdit(Player player)
	{
		Inventory inventory = Bukkit.createInventory(player, 54, villager.getCustomName() + " - Edit");
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
					meta.setLore(Arrays.asList("$" + plugin.prices.getBuyPrice(item)));
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
			double money = plugin.getConfig().getDouble("tokenExchange", 2000);
			if(exchange.getPlayerName() != null && exchange.getPlayerName() == player.getUniqueId())
			{
				if (plugin.tokens.removeTokens(player.getUniqueId(), 1))
				{
					OfflinePlayer p = plugin.getServer().getOfflinePlayer(player.getUniqueId());
					plugin.econ.depositPlayer(p, money);
					player.sendMessage(ChatColor.GOLD + "You have traded 1 token for $" + money + ".");
				}
				else
					player.sendMessage(ChatColor.RED + "You do not have enough tokens for that");
			}else{
				player.sendMessage(ChatColor.BLUE + "You are about to exchange 1 token for $" + money);
				player.sendMessage(ChatColor.BLUE + "Right click again to continue");
				this.exchange.setPlayerName(player.getUniqueId());
				@SuppressWarnings("unused")
				BukkitTask task = new ExchangeTask(this.plugin, exchange).runTaskLater(this.plugin, 100);
			}
		}
		else if (NPCType.KIT == this.type)
		{
			if ((kit.getPlayer() == null || !kit.getPlayer().equals(player.getUniqueId())) && (kit.getNpcName() == null || !kit.getNpcName().equals(villager.getCustomName())))
			{
				Inventory inv = getInventory(player);
				if (inv.getItem(inv.getSize() - 1) == null || inv.getItem(inv.getSize() - 1).getType() != Material.COAL)
				{
					player.sendMessage(ChatColor.DARK_RED + "This NPC is not ready for player interaction. :(");
				}
				else if(inv.getItem(inv.getSize()-1).getType() == Material.COAL)
				{
					int numTokens = inv.getItem(inv.getSize() - 1).getAmount();
					player.sendMessage(ChatColor.GOLD + "Do you really want to spend " + numTokens + " tokens for this kit?");
					for (int x = 0; x < getInventory(player).getSize() - 1; x++)
					{
						if (inv.getItem(x) != null)
						{
							ItemStack item = inv.getItem(x);
							player.sendMessage(ChatColor.AQUA + item.getType().toString() + ":" + item.getDurability() + ChatColor.WHITE + " x " + ChatColor.AQUA + item.getAmount());
						}
					}
					
					kit.setNpcName(villager.getCustomName());
					kit.setNumTokens(numTokens);
					kit.setPlayer(player.getUniqueId());
					@SuppressWarnings("unused")
					BukkitTask task = new KitTask(this.plugin, kit).runTaskLater(this.plugin, 100);
				}
			}
			else if(kit.getPlayer().equals(player.getUniqueId()) && kit.getNpcName().equals(villager.getCustomName()))
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
						player.sendMessage(ChatColor.GREEN + "Your inventory was full so some items were dropped at your feet");
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
			player.sendMessage(ChatColor.RED + "Which dummeh spawned me? I'm not even implemented yet.");
		}
	}
	
	public NPCType getType()
	{
		return type;
	}
	
	public Villager getVillager()
	{
		return villager;
	}
	
	public String getName()
	{
		if (villager != null)
			return villager.getCustomName();
		
		return name;
	}
	
	public ConfigurationSection getInventory()
	{
		return inv;
	}
	
	public ConfigurationSection save()
	{
		ConfigurationSection cs = new YamlConfiguration();
		cs.set("type", type.toString());
		cs.set("world", location.getWorld().getName());
		cs.set("x", location.getX());
		cs.set("y", location.getY());
		cs.set("z", location.getZ());
		if (inv != null)
			cs.set("inventory", inv);
		
		return cs;
	}
	
	public static enum NPCType
	{
		// Gambling NPC
		BETTING(Profession.FARMER),
		// Repair tools/armor
		BLACKSMITH(Profession.BLACKSMITH),
		// Buy kits
		KIT(Profession.LIBRARIAN),
		// Standard shop
		SHOP(Profession.PRIEST),
		// Sell items to the NPC
		SELL(Profession.PRIEST),
		// Exchange tokens for XP/Money
		XP(Profession.BUTCHER);
		
		Profession profession;
		
		private NPCType(Profession profession)
		{
			this.profession = profession; 
		}
		
		public Profession getProfession()
		{
			return profession;
		}
		
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
