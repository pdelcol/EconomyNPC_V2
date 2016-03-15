package com.mongoosecountry.pete800.util;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.catalog.CatalogItemData;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class Utils
{
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isNumber(String string)
    {
        if (string == null)
            return false;

        int length = string.length();
        if (length == 0)
            return false;

        int i = 0;
        if (string.charAt(0) == '-')
        {
            if (length == 1)
                return false;

            i = 1;
        }

        for (; i < length; i++)
        {
            char c = string.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }

        return true;
    }

    public static DisplayNameData getDisplayNameData(ItemStack itemStack)
    {
        return itemStack.get(CatalogItemData.DISPLAY_NAME_DATA).get();
    }

    public static DurabilityData getDurabilityData(ItemStack itemStack)
    {
        return itemStack.get(CatalogItemData.DURABILITY_DATA).get();
    }

    public static List<ItemEnchantment> getItemEnchants(ItemStack itemStack)
    {
        return itemStack.get(Keys.ITEM_ENCHANTMENTS).get();
    }

    public static Optional<EconomyService> getEconomyService()
    {
        return Sponge.getServiceManager().provide(EconomyService.class);
    }

    public static Profession getProfessionFromName(String name)
    {
        for (Profession profession : Sponge.getRegistry().getAllOf(CatalogTypes.PROFESSION))
            if (profession.toString().equalsIgnoreCase(name))
                return profession;

        return null;
    }

    public static Location<World> deserialize(ConfigurationNode node)
    {
        return new Location<>(Sponge.getServer().getWorld(node.getNode("world").getString()).get(), node.getNode("x").getDouble(), node.getNode("y").getDouble(), node.getNode("z").getDouble());
    }

    public static ConfigurationNode serialize(Location<World> location)
    {
        ConfigurationNode node = SimpleConfigurationNode.root();
        node.getNode("world").setValue(location.getExtent().getName());
        node.getNode("x").setValue(location.getX());
        node.getNode("y").setValue(location.getY());
        node.getNode("z").setValue(location.getZ());
        return node;
    }

    public static Text goldText(String string)
    {
        return Text.builder(string).color(TextColors.GOLD).build();
    }

    public static Text darkRedText(String string)
    {
        return Text.builder(string).color(TextColors.RED).build();
    }
}
