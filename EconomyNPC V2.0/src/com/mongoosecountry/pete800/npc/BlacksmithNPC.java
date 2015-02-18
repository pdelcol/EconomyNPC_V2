package com.mongoosecountry.pete800.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.handler.blacksmith.BlacksmithHandler;
import com.mongoosecountry.pete800.handler.blacksmith.BlacksmithTask;
import com.mongoosecountry.pete800.util.EnchantEnums;

public class BlacksmithNPC extends AbstractNPC
{
	BlacksmithHandler blacksmithHandler = new BlacksmithHandler();
	Map<Material, Material> repairables = new HashMap<Material, Material>();
	Map<Double, Integer> expLevels = new HashMap<Double, Integer>();
	
	public BlacksmithNPC(EconomyNPC plugin, Location location, String name)
	{
		super(plugin, location, NPCType.BLACKSMITH, name);
		parseMaps();
	}
	
	public BlacksmithNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		super(plugin, name, cs);
		parseMaps();
	}

	@Override
	public void onInteract(Player player)
	{
		ItemStack item = player.getItemInHand();
		if (item.getType() == Material.AIR)
		{
			player.sendMessage(ChatColor.DARK_RED + "Your hand is empty!");
			return;
		}
		
		Material material = item.getType();
		if (!repairables.containsKey(material) || item.getDurability() == 0)
		{
			player.sendMessage(ChatColor.DARK_RED + "I can not repair that item.");
			return;
		}
		
		Map<Enchantment, Integer> enchants = item.getEnchantments();
		double costPerUnit = plugin.prices.getBuyPrice(new ItemStack(repairables.get(item.getType())));
		double enchantCost = 0;
		double numUnits = round(item.getDurability() / (material.getMaxDurability() / 4));
		int experienceCost = (int) (expLevels.get(numUnits) * (plugin.getConfig().getDouble("tokenForMoney", 2000) / plugin.getConfig().getInt("tokenForExp", 200)));
		int nameCost = (item.getItemMeta().hasDisplayName() ? 55 * 10 : 0);
		UUID uuid = player.getUniqueId();
		if (enchants.size() != 0)
		{
			for (Enchantment enchant : enchants.keySet())
			{
				int weight = 0;
				for (EnchantEnums enumEnchant : EnchantEnums.values())
					if (enchant == enumEnchant.getEnchant())
						weight = enumEnchant.getWeight();
				
				enchantCost += (11 - (10 * weight / 10)) * item.getEnchantmentLevel(enchant);
			}
		}
		
		if (!blacksmithHandler.isPlayerTransacting(uuid))
		{
			double cost = costPerUnit * numUnits + enchantCost + experienceCost + nameCost;				
			player.sendMessage(ChatColor.GOLD + "The tool will cost: " + ChatColor.AQUA + "$" + cost + ChatColor.GOLD + ". If you would like to reforge it, right click me again.");
			blacksmithHandler.newTransaction(cost, uuid, item);
			new BlacksmithTask(plugin, blacksmithHandler, uuid).runTaskLater(plugin, 100);
			return;
		}
		else
		{
			if (blacksmithHandler.doesItemMatch(uuid, item))
			{
				if (plugin.econ.withdrawPlayer(player, blacksmithHandler.getCost(uuid)).transactionSuccess())
				{
					player.getItemInHand().setDurability((short) 0);
					player.sendMessage(ChatColor.GOLD + "Transaction successful!");
				}
				else
					player.sendMessage(ChatColor.DARK_RED + "You don't have enough money for that.");
			}
			else
				player.sendMessage(ChatColor.DARK_RED + "Something's not right with that!");
			
			blacksmithHandler.removeTransaction(uuid);
			return;
		}
	}
	
	private void parseMaps()
	{
		Material wood = Material.WOOD;
		Material carrot = Material.CARROT_ITEM;
		Material flint = Material.FLINT;
		Material stick = Material.STICK;
		Material leather = Material.LEATHER;
		Material cobble = Material.COBBLESTONE;
		Material iron = Material.IRON_INGOT;
		Material gold = Material.GOLD_INGOT;
		Material diamond = Material.DIAMOND;
		repairables.put(Material.IRON_SPADE, iron);
		repairables.put(Material.IRON_PICKAXE, iron);
		repairables.put(Material.IRON_AXE, iron);
		repairables.put(Material.WOOD_SPADE, wood);
		repairables.put(Material.WOOD_PICKAXE, wood);
		repairables.put(Material.WOOD_AXE, wood);
		repairables.put(Material.STONE_SPADE, cobble);
		repairables.put(Material.STONE_PICKAXE, cobble);
		repairables.put(Material.STONE_AXE, cobble);
		repairables.put(Material.DIAMOND_SPADE, diamond);
		repairables.put(Material.DIAMOND_PICKAXE, diamond);
		repairables.put(Material.DIAMOND_AXE, diamond);
		repairables.put(Material.GOLD_SPADE, gold);
		repairables.put(Material.GOLD_PICKAXE, gold);
		repairables.put(Material.GOLD_AXE, gold);
		repairables.put(Material.WOOD_HOE, wood);
		repairables.put(Material.IRON_HOE, iron);
		repairables.put(Material.STONE_HOE, cobble);
		repairables.put(Material.DIAMOND_HOE, diamond);
		repairables.put(Material.GOLD_HOE, gold);
		repairables.put(Material.WOOD_SWORD, wood);
		repairables.put(Material.IRON_SWORD, iron);
		repairables.put(Material.STONE_SWORD, cobble);
		repairables.put(Material.DIAMOND_SWORD, diamond);
		repairables.put(Material.GOLD_SWORD, gold);
		repairables.put(Material.LEATHER_BOOTS, leather);
		repairables.put(Material.CHAINMAIL_BOOTS, iron);
		repairables.put(Material.IRON_BOOTS, iron);
		repairables.put(Material.DIAMOND_BOOTS, diamond);
		repairables.put(Material.GOLD_BOOTS, gold);
		repairables.put(Material.LEATHER_CHESTPLATE, leather);
		repairables.put(Material.CHAINMAIL_CHESTPLATE, iron);
		repairables.put(Material.IRON_CHESTPLATE, iron);
		repairables.put(Material.DIAMOND_CHESTPLATE, diamond);
		repairables.put(Material.GOLD_CHESTPLATE, gold);
		repairables.put(Material.LEATHER_HELMET, leather);
		repairables.put(Material.CHAINMAIL_HELMET, iron);
		repairables.put(Material.IRON_HELMET, iron);
		repairables.put(Material.DIAMOND_HELMET, diamond);
		repairables.put(Material.GOLD_HELMET, gold);
		repairables.put(Material.LEATHER_LEGGINGS, leather);
		repairables.put(Material.CHAINMAIL_LEGGINGS, iron);
		repairables.put(Material.IRON_LEGGINGS, iron);
		repairables.put(Material.DIAMOND_LEGGINGS, diamond);
		repairables.put(Material.GOLD_LEGGINGS, gold);
		repairables.put(Material.BOW, stick);
		repairables.put(Material.FLINT_AND_STEEL, flint);
		repairables.put(Material.FISHING_ROD, stick);
		repairables.put(Material.SHEARS, iron);
		repairables.put(Material.CARROT_STICK, carrot);
		
		expLevels.put(1.0, 7);
		expLevels.put(2.0, 16);
		expLevels.put(3.0, 27);
		expLevels.put(4.0, 40);
	}
	
	private double round(double d)
	{
		if (d > 0 && d <= 1)
			return 1.0;
		else if (d > 1 && d <= 2)
			return 2.0;
		else if (d > 2 && d <= 3)
			return 3.0;
		else if (d > 3 && d <= 4)
			return 4.0;
		else
			return 0.0;
	}
}
