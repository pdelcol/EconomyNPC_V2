package com.mongoosecountry.pete800.hanlder.tokens;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.util.NameFetcher;

public class TokenHandler
{
	EconomyNPC plugin;
	File tokenFile;
	public Map<UUID, Integer> tokens = new HashMap<UUID, Integer>();
	YamlConfiguration tokenYml = new YamlConfiguration();
	
	public TokenHandler(EconomyNPC plugin)
	{
		this.plugin = plugin;
	}
	
	//Load the tokens
	public void load()
	{
		tokenFile = new File(plugin.getDataFolder(), "tokens.yml");
		if (!tokenFile.exists())
		{
			try
			{
				tokenFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			tokenYml.load(tokenFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		for (Entry<String, Object> entry : tokenYml.getValues(true).entrySet())
		{
			if (entry.getValue() instanceof MemorySection)
			{
				MemorySection data = (MemorySection) entry.getValue();
				tokens.put(UUID.fromString(entry.getKey()), data.getInt("tokens"));
			}
		}
	}
	
	//Save the tokens
	public void save()
	{
		if (!tokenFile.exists())
		{
			try
			{
				tokenFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			tokenYml.load(tokenFile);
			for (UUID uuid : tokens.keySet())
			{
				YamlConfiguration data = new YamlConfiguration();
				data.set("name", NameFetcher.getNameOf(uuid));
				data.set("tokens", tokens.get(uuid));
				tokenYml.set(uuid.toString(), data);
			}
			
			tokenYml.save(tokenFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//Check to see if the player is in the token list
	public void checkName(UUID player)
	{
		if(!tokens.containsKey(player))
		{
			tokens.put(player, 0);
		}
	}
	
	//Get the number of tokens for a given player
	public int getNumTokens(UUID player)
	{
		if(tokens.get(player) == null)
		{
			tokens.put(player, 0);
		}
		return tokens.get(player);
	}
	
	//Add tokens to a given players account
	public void addTokens(UUID player, int numTokens)
	{
		if(containsPlayer(player))
		{
			int num = tokens.get(player);
			tokens.remove(player);
			tokens.put(player, num + numTokens);
		}
		else
			tokens.put(player, numTokens);
	}
	
	//Remove tokens from a given players account
	public boolean removeTokens(UUID player, int numTokens)
	{
		if(tokens.containsKey(player))
		{
			if(tokens.get(player) >= numTokens){
				tokens.put(player, tokens.get(player) - numTokens);
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean containsPlayer(UUID player)
	{
		return tokens.containsKey(player);
	}
}
