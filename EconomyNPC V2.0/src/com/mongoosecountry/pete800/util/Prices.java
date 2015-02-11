package com.mongoosecountry.pete800.util;

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

import com.mongoosecountry.pete800.EconomyNPC;

public class Prices
{
	EconomyNPC plugin;
	Map<ItemStack, Double> prices = new HashMap<ItemStack, Double>();
	
	public Prices(EconomyNPC plugin)
	{
		this.plugin = plugin;
		File pricesFile = new File(plugin.getDataFolder(), "prices.yml");
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
			plugin.log.warning("Error, prices.yml is missing.");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
		catch (IOException e)
		{
			plugin.log.warning("Error loading prices.yml");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
		catch (InvalidConfigurationException e)
		{
			plugin.log.warning("Error, invalid format in prices.yml");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
		
		for (Entry<String, Object> entry : priceConfig.getValues(true).entrySet())
		{
			if (!(entry.getValue() instanceof MemorySection))
			{
				String material = entry.getKey().split("\\.")[0].toUpperCase();
				short durability = Short.valueOf(entry.getKey().split("\\.")[1]);
				ItemStack item = new ItemStack(Material.getMaterial(material), 0, durability);
				double price = Double.valueOf(entry.getValue().toString());
				prices.put(item, price);
			}
		}
	}
	
	public double getBuyPrice(ItemStack item)
	{
		double price = 0.0;
		ItemStack is = new ItemStack(item.getType(), 0, item.getDurability());
		if (prices.get(is) != null)
			price = prices.get(is) * item.getAmount();
		
		return price;
	}
	
	public double getSellPrice(ItemStack item)
	{
		return getBuyPrice(item) * plugin.getConfig().getDouble("sellBackPercentage", 0.75);
	}
}
