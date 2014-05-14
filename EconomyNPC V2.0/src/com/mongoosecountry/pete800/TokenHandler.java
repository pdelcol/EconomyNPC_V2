package com.mongoosecountry.pete800;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenHandler {
	public Map<UUID, Integer> tokens = new HashMap<UUID, Integer>();
	//Constructor
	public TokenHandler()
	{
		//We dont need anything here for right now
	}
	//Load the tokens
	@SuppressWarnings("unchecked")
	public void load()
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("plugins/EconomyNPC/Token_List.bin"));
			Object result = ois.readObject();
			tokens = (Map<UUID, Integer>)result;
			ois.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//Save the tokens
	public void save()
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("plugins/EconomyNPC/Token_List.bin"));
			oos.writeObject(tokens);
			oos.flush();
			oos.close();
			//Handle I/O exceptions
		}
		catch(Exception e)
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
		if(tokens.containsKey(player))
		{
			tokens.put(player, tokens.get(player) + numTokens);
		}
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
}
