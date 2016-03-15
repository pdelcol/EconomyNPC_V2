package com.mongoosecountry.pete800.npc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC.NPCType;
import com.mongoosecountry.pete800.util.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NPCStorage
{
    private final ArrayList<AbstractNPC> npcs = new ArrayList<>();
    private final File npcFile;

    public NPCStorage(File dir)
    {
        Logger logger = EconomyNPC.logger;
        npcFile = new File(dir, "npcs.yml");
        if (!npcFile.exists())
        {
            try
            {
                if (!npcFile.createNewFile())
                {
                    logger.error("Error creating npcs.yml!");
                    return;
                }
            }
            catch (IOException e)
            {
                logger.error("Error creating npcs.yml!");
                return;
            }
        }

        ConfigurationNode npcsNode;
        try
        {
            npcsNode = HoconConfigurationLoader.builder().setFile(npcFile).build().load();
        }
        catch (IOException e)
        {
            logger.error("Error reading npcs.yml!");
            return;
        }

        for (Object object : npcsNode.getChildrenMap().keySet())
        {
            String name = object.toString();
            ConfigurationNode npcNode = npcsNode.getNode(name);
            if (npcNode.isVirtual())
                continue;

            AbstractNPC npc = null;
            NPCType type = NPCType.fromName(npcNode.getString("type"));
            if (type == NPCType.BETTING)
                npc = new BettingNPC(name, npcNode);
            else if (type == NPCType.BLACKSMITH)
                npc = new BlacksmithNPC(name, npcNode);
            else if (type == NPCType.EXCHANGE)
                npc = new ExchangeNPC(name, npcNode);
            else if (type == NPCType.KIT)
                npc = new KitNPC(name, npcNode);
            else if (type == NPCType.SELL)
                npc = new ShopNPC(name, npcNode);
            else if (type == NPCType.SHOP)
                npc = new SellNPC(name, npcNode);

            if (type == null)
                logger.error("Invalid type for NPC: " + name);
            else
                npcs.add(npc);
        }
    }

    public boolean createNPC(String name, Player player, NPCType type)
    {
        for (AbstractNPC npc : npcs)
        {
            if (npc.getName().equalsIgnoreCase(name))
            {
                player.sendMessage(Text.builder("You cannot create an NPC with that name. Try a different name").color(TextColors.DARK_RED).build());
                return false;
            }
        }

        AbstractNPC npc = null;
        if (type == NPCType.BETTING)
            npc = new BettingNPC(player.getLocation(), name);
        else if (type == NPCType.BLACKSMITH)
            npc = new BlacksmithNPC(player.getLocation(), name);
        else if (type == NPCType.EXCHANGE)
            npc = new ExchangeNPC(player.getLocation(), name);
        else if (type == NPCType.KIT)
            npc = new KitNPC(player.getLocation(), name);
        else if (type == NPCType.SELL)
            npc = new ShopNPC(player.getLocation(), name);
        else if (type == NPCType.SHOP)
            npc = new SellNPC(player.getLocation(), name);

        npcs.add(npc);
        player.sendMessage(Text.builder("Success!").color(TextColors.DARK_RED).style(TextStyles.BOLD).build());
        return true;
    }

    public boolean removeNPC(String name, CommandSource source)
    {
        boolean exists = false;
        int removeNum = Integer.MAX_VALUE;
        for (int x = 0; x < npcs.size(); x++)
        {
            if (npcs.get(x).getVillager().get(Keys.DISPLAY_NAME).get().equals(Text.of(name)))
            {
                removeNum = x;
                exists = true;
            }
        }

        if (exists && removeNum != Integer.MAX_VALUE)
        {
            npcs.get(removeNum).despawnNPC();
            npcs.remove(removeNum);
            source.sendMessage(Text.builder("NPC removed.").color(TextColors.GREEN).build());
            return true;
        }
        else
        {
            source.sendMessage(Utils.darkRedText("There is no NPC with that name."));
            return false;
        }
    }

    public AbstractNPC getNPC(String name)
    {
        for (AbstractNPC npc : npcs)
            if (npc.getVillager().get(Keys.DISPLAY_NAME).get().equals(Text.of(name)))
                return npc;

        return null;
    }

    public void save(boolean despawnNPCs)
    {
        ConfigurationNode npcNode = SimpleConfigurationNode.root();
        for (AbstractNPC npc : npcs)
        {
            String name = npc.name;
            npcNode.getNode(name).setValue(npc.save());
            if (despawnNPCs)
                npc.despawnNPC();
        }

        try
        {
            HoconConfigurationLoader.builder().setFile(npcFile).build().save(npcNode);
        }
        catch (IOException e)
        {
            EconomyNPC.logger.error("Error saving npcs.conf!");
        }
    }

    public List<AbstractNPC> getNPCs()
    {
        return npcs;
    }
}
