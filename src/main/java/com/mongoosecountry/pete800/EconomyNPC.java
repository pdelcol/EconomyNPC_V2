package com.mongoosecountry.pete800;

import com.mongoosecountry.pete800.command.enpc.ENPC;
import com.mongoosecountry.pete800.command.tokens.Tokens;
import com.mongoosecountry.pete800.handler.tokens.TokenHandler;
import com.mongoosecountry.pete800.listeners.EntityListener;
import com.mongoosecountry.pete800.listeners.InventoryListener;
import com.mongoosecountry.pete800.listeners.PlayerListener;
import com.mongoosecountry.pete800.npc.NPCStorage;
import com.mongoosecountry.pete800.util.Prices;
import com.mongoosecountry.pete800.util.Utils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.Optional;

@Plugin(id = "com.mongoosecountry.pete800", name = "EconomyNPC", version = "3.0")
public class EconomyNPC
{
    public static Config config;
    public static Logger logger;
    public static NPCStorage npcStorage;
    public static Prices prices;
    public static Text currencySymbol;
    public static TokenHandler tokens;

    @Listener
    public void startUp(GameStartedServerEvent event)
    {
        logger = instance().getLogger();
        Optional<EconomyService> economyServiceOptional = Utils.getEconomyService();
        if (!economyServiceOptional.isPresent())
        {
            logger.error("No economy available. Shutting down.");
            return;
        }

        currencySymbol = economyServiceOptional.get().getDefaultCurrency().getSymbol();
        File configDir = new File("plugins", "EconomyNPC");
        config = new Config(configDir);
        npcStorage = new NPCStorage(configDir);
        prices = new Prices(configDir);
        tokens = new TokenHandler();
        tokens.load(configDir);

        Sponge.getCommandManager().register(this, new ENPC());
        Sponge.getCommandManager().register(this, new Tokens());

        Sponge.getEventManager().registerListeners(this, new EntityListener());
        Sponge.getEventManager().registerListeners(this, new PlayerListener());
        Sponge.getEventManager().registerListeners(this, new InventoryListener());

        logger.info("EconomyNPC is enabled!");
    }

    @Listener
    public void shutDown(GameStoppedServerEvent event)
    {
        tokens.save();
        npcStorage.save(true);
        logger.info("EconomyNPC is disabled!");
    }

    public static PluginContainer instance()
    {
        return Sponge.getPluginManager().getPlugin("ENPC").get();
    }
}
