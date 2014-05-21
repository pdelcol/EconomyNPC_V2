package com.mongoosecountry.pete800;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Prices
{
	EconomyNPC npc;
	Map<ItemStack, Double> prices = new HashMap<ItemStack, Double>();
	
	public Prices(EconomyNPC npc)
	{
		this.npc = npc;
		File pricesFile = new File(npc.getDataFolder(), "prices.yml");
		if (!pricesFile.exists())
		{
			try
			{
				pricesFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		YamlConfiguration priceConfig = new YamlConfiguration();
		try
		{
			priceConfig.load(pricesFile);
		}
		catch (FileNotFoundException e)
		{
			npc.log.warning("Error, prices.yml is missing.");
			npc.getServer().getPluginManager().disablePlugin(npc);
		}
		catch (IOException e)
		{
			npc.log.warning("Error loading prices.yml");
			npc.getServer().getPluginManager().disablePlugin(npc);
		}
		catch (InvalidConfigurationException e)
		{
			npc.log.warning("Error, invalid format in prices.yml");
			npc.getServer().getPluginManager().disablePlugin(npc);
		}
		
		for (Entry<String, Object> entry : priceConfig.getValues(true).entrySet())
		{
			if (!(entry.getValue() instanceof MemorySection))
			{
				String material = entry.getKey().split("\\.")[0].toUpperCase();
				short durability = Short.valueOf(entry.getKey().split("\\.")[1]);
				ItemStack item = new ItemStack(Material.getMaterial(material), 0, durability);
				double price = Short.valueOf(entry.getValue().toString());
				prices.put(item, price);
			}
		}
	}
	
	public double getPrice(ItemStack item)
	{
		double price = 0.0;
		ItemStack is = new ItemStack(item.getType(), 0, item.getDurability());
		if (prices.get(is) != null)
			price = prices.get(is) * item.getAmount();
		
		return price;
	}
}
