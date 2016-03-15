package com.mongoosecountry.pete800.handler.tokens;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.util.uuid.UUIDUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenHandler
{
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private final Map<UUID, Integer> tokens = new HashMap<>();

    //Load the tokens
    public void load(File dir)
    {
        Logger logger = EconomyNPC.logger;
        File tokenFile = new File(dir, "tokens.conf");
        if (!tokenFile.exists())
        {
            try
            {
                if (!tokenFile.createNewFile())
                {
                    logger.error("Failed to create tokens.conf");
                    return;
                }
            }
            catch (IOException e)
            {
                logger.error("Failed to create tokens.conf");
                return;
            }
        }

        ConfigurationNode tokenNode;
        loader = HoconConfigurationLoader.builder().setFile(tokenFile).build();
        try
        {
            tokenNode = loader.load();
        }
        catch (IOException e)
        {
            logger.error("Failed to load tokens.conf");
            return;
        }

        for (Object key : tokenNode.getChildrenMap().keySet())
            tokens.put(UUID.fromString(key.toString()), tokenNode.getNode(key, "tokens").getInt(0));
    }

    //Save the tokens
    public void save()
    {
        Logger logger = EconomyNPC.logger;
        try
        {
            ConfigurationNode tokenNode = loader.load();
            for (UUID uuid : tokens.keySet())
            {
                ConfigurationNode data = SimpleConfigurationNode.root();
                data.getNode("name").setValue(UUIDUtils.getNameOf(uuid));
                data.getNode("tokens").setValue(tokens.get(uuid));
                tokenNode.getNode(uuid.toString()).setValue(data);
            }

            loader.save(tokenNode);
        }
        catch (IOException e)
        {
            logger.error("Failed to save tokens.conf");
        }
    }

    //Check to see if the player is in the token list
    public boolean checkPlayer(UUID player)
    {
        return tokens.containsKey(player);
    }

    //Get the number of tokens for a given player
    public int getNumTokens(UUID player)
    {
        if (tokens.get(player) == null)
        {
            tokens.put(player, 0);
        }
        return tokens.get(player);
    }

    //Add tokens to a given players account
    public void addTokens(UUID player, int numTokens)
    {
        if (checkPlayer(player))
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
        if (tokens.containsKey(player))
        {
            if (tokens.get(player) >= numTokens)
            {
                tokens.put(player, tokens.get(player) - numTokens);
                return true;
            }
            return false;
        }
        return false;
    }
}
