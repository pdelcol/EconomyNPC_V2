package com.mongoosecountry.pete800;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Config
{
    private double sellBackPercentage = 0.75;
    private double tokenForMoney = 2000;
    private final File configDir;
    private int tokenForExp = 200;

    public Config(File dir)
    {
        this.configDir = dir;
        reload();
    }

    @SuppressWarnings("WeakerAccess")
    public void reload()
    {
        File configFile = new File(configDir, "config.conf");
        Logger logger = EconomyNPC.logger;
        if (!configFile.exists())
        {
            if (!configFile.mkdirs())
                logger.error("Failed to create config directory. It may already exist.");

            String CONFIG_CREATE_FAILED = "Failed to create config.conf";
            try
            {
                if (!configFile.createNewFile())
                {
                    logger.error(CONFIG_CREATE_FAILED);
                    return;
                }

                URL url = EconomyNPC.class.getClassLoader().getResource("config.conf");
                if (url == null)
                {
                    logger.error(CONFIG_CREATE_FAILED);
                    return;
                }

                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                InputStream input = connection.getInputStream();
                OutputStream output = new FileOutputStream(configFile);
                byte[] buf = new byte[1024];
                int length;
                while ((length = input.read(buf)) > 0)
                    output.write(buf, 0, length);

                output.close();
                input.close();
            }
            catch (IOException e)
            {
                logger.error(CONFIG_CREATE_FAILED);
                return;
            }
        }

        ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(configFile).build();
        ConfigurationNode config;
        try
        {
            config = configLoader.load();
        }
        catch (IOException e)
        {
            logger.error("An error occurred while reading config.conf");
            return;
        }

        sellBackPercentage = config.getNode("sellBackPercentage").getDouble(0.75);
        tokenForExp = config.getNode("tokenForExp").getInt(2000);
        tokenForMoney = config.getNode("tokenForMoney").getDouble(200);
    }

    public double getSellBackPercentage()
    {
        return sellBackPercentage;
    }

    public int getTokenForExp()
    {
        return tokenForExp;
    }

    public double getTokenForMoney()
    {
        return tokenForMoney;
    }
}
