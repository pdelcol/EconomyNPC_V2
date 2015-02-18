package com.mongoosecountry.pete800.handler.blacksmith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BlacksmithHandler
{
	Map<UUID, Double> costMap = new HashMap<UUID, Double>();
	Map<UUID, ItemStack> itemMap = new HashMap<UUID, ItemStack>();
	
	public void newTransaction(double cost, UUID player, ItemStack item)
	{
		costMap.put(player, cost);
		itemMap.put(player, item);
	}
	
	public double getCost(UUID player)
	{
		return costMap.get(player);
	}
	
	public ItemStack getItem(UUID player)
	{
		return itemMap.get(player);
	}
	
	public boolean isPlayerTransacting(UUID player)
	{
		return costMap.containsKey(player) && itemMap.containsKey(player);
	}
	
	public void removeTransaction(UUID player)
	{
		costMap.remove(player);
		itemMap.remove(player);
	}
	
	public boolean doesItemMatch(UUID player, ItemStack item)
	{
		ItemStack is = itemMap.get(player);
		if (is.getType() == item.getType() && is.getDurability() == item.getDurability() && is.getEnchantments().size() == item.getEnchantments().size())
		{
			List<Enchantment> itemEnchants = new ArrayList<Enchantment>();
			itemEnchants.addAll(item.getEnchantments().keySet());
			
			List<Enchantment> isEnchants = new ArrayList<Enchantment>();
			isEnchants.addAll(is.getEnchantments().keySet());
			
			for (int x = 0; x < itemEnchants.size(); x++)
				if (itemEnchants.get(x) != isEnchants.get(x) || item.getEnchantmentLevel(itemEnchants.get(x)) != is.getEnchantmentLevel(isEnchants.get(x)))
					return false;
			
			if (is.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
				return true;
			
			if (!is.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
				return true;
		}
		
		return false;
	}
}