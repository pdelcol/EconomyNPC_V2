package com.mongoosecountry.pete800.util;

import com.mongoosecountry.pete800.EconomyNPC;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.catalog.CatalogBlockData;
import org.spongepowered.api.data.manipulator.catalog.CatalogItemData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Prices
{
    Map<ItemStack, Double> prices = new HashMap<>();

    public Prices(File configDir)
    {
        File file = new File(configDir, "prices.yml");
        Logger logger = LoggerFactory.getLogger("EconomyNPC");
        if (!file.exists())
        {
            try
            {
                if (!file.createNewFile())
                {
                    logger.error("Could not create prices.yml");
                    return;
                }
            }
            catch (IOException e)
            {
                logger.error("Could not create prices.yml");
                return;
            }
        }

        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
        ConfigurationNode priceConfig;
        try
        {
            priceConfig = loader.load();
        }
        catch (IOException e)
        {
            logger.error("Error loading prices.yml");
            return;
        }

        for (ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class))
        {
            ConfigurationNode itemTypeNode = priceConfig.getNode(itemType.getId());
            if (itemTypeNode.isVirtual())
                continue;

            ItemStack item = ItemStack.of(itemType, 0);
            if (item.get(CatalogBlockData.STONE_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.STONE_TYPE, Keys.STONE_TYPE);
            else if (item.get(CatalogBlockData.DIRT_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.DIRT_TYPE, Keys.DIRT_TYPE);
            else if (item.get(CatalogBlockData.TREE_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.TREE_TYPE, Keys.TREE_TYPE);
            else if (item.get(CatalogBlockData.SAND_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.SAND_TYPE, Keys.SAND_TYPE);
            else if (item.get(CatalogBlockData.SANDSTONE_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.SANDSTONE_TYPE, Keys.SANDSTONE_TYPE);
            else if (item.get(CatalogBlockData.SHRUB_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.SHRUB_TYPE, Keys.SHRUB_TYPE);
            else if (item.get(CatalogBlockData.DYEABLE_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.DYE_COLOR, Keys.DYE_COLOR);
            else if (item.get(CatalogBlockData.SLAB_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.SLAB_TYPE, Keys.SLAB_TYPE);
            else if (item.get(CatalogBlockData.DISGUISED_BLOCK_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.DISGUISED_BLOCK_TYPE, Keys.DISGUISED_BLOCK_TYPE);
            else if (item.get(CatalogBlockData.BRICK_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.BRICK_TYPE, Keys.BRICK_TYPE);
            else if (item.get(CatalogBlockData.WALL_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.WALL_TYPE, Keys.WALL_TYPE);
            else if (item.get(CatalogBlockData.QUARTZ_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.QUARTZ_TYPE, Keys.QUARTZ_TYPE);
            else if (item.get(CatalogBlockData.PRISMARINE_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.PRISMARINE_TYPE, Keys.PRISMARINE_TYPE);
            else if (item.get(CatalogBlockData.DOUBLE_PLANT_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.DOUBLE_SIZE_PLANT_TYPE, Keys.DOUBLE_PLANT_TYPE);
            else if (item.get(CatalogItemData.COAL_ITEM_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.COAL_TYPE, Keys.COAL_TYPE);
            else if (item.get(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.GOLDEN_APPLE, Keys.GOLDEN_APPLE_TYPE);
            else if (item.get(CatalogItemData.FISH_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.FISH, Keys.FISH_TYPE);
            else if (item.get(CatalogItemData.COOKED_FISH_ITEM_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.COOKED_FISH, Keys.COOKED_FISH);
            else if (item.get(CatalogItemData.SPAWNABLE_DATA).isPresent())
                addItem(itemType, itemTypeNode, CatalogTypes.ENTITY_TYPE, Keys.SPAWNABLE_ENTITY_TYPE);
        }
    }

    private <T extends CatalogType> void addItem(ItemType itemType, ConfigurationNode variationNode, Class<T> typeClass, Key<Value<T>> key)
    {
        for (T type : Sponge.getRegistry().getAllOf(typeClass))
        {
            ConfigurationNode amount = variationNode.getNode(type.getId());
            if (!amount.isVirtual() && type.getId().equalsIgnoreCase(amount.getKey().toString()))
            {
                ItemStack itemStack = ItemStack.of(itemType, 0);
                itemStack.offer(key, type);
                prices.put(itemStack, amount.getDouble());
            }
        }
    }

    public double getBuyPrice(ItemStack itemStack)
    {
        double price = 0.0;
        for (ItemStack is : prices.keySet())
        {
            if (itemStack.getItem() != is && !isSameVariant(itemStack, is))
                continue;

            if (prices.get(is) != null)
                price = prices.get(is) * itemStack.getQuantity();
        }

        return price;
    }

    private boolean isSameVariant(ItemStack itemStack1, ItemStack itemStack2)
    {
        DataContainer container1 = itemStack1.toContainer();
        DataContainer container2 = itemStack2.toContainer();
        for (DataQuery query1 : container1.getKeys(true))
            for (DataQuery query2 : container2.getKeys(true))
                if (query1 == query2)
                    if (container1.get(query1).get() == container2.get(query2).get())
                        return true;

        return false;
    }

    public double getSellPrice(ItemStack item)
    {
        return getBuyPrice(item) * EconomyNPC.config.getSellBackPercentage();
    }
}
