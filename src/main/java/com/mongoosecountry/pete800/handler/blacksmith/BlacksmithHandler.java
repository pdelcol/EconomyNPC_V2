package com.mongoosecountry.pete800.handler.blacksmith;

import com.mongoosecountry.pete800.util.Utils;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlacksmithHandler
{
    private final Map<UUID, Double> costMap = new HashMap<>();
    private final Map<UUID, ItemStack> itemMap = new HashMap<>();

    public void newTransaction(double cost, UUID player, ItemStack item)
    {
        costMap.put(player, cost);
        itemMap.put(player, item);
    }

    public double getCost(UUID player)
    {
        return costMap.get(player);
    }

    public ItemStack getItem(UUID player)
    {
        return itemMap.get(player);
    }

    public boolean isPlayerTransacting(UUID player)
    {
        return costMap.containsKey(player) && itemMap.containsKey(player);
    }

    public void removeTransaction(UUID player)
    {
        costMap.remove(player);
        itemMap.remove(player);
    }

    public boolean doesItemMatch(UUID player, ItemStack item)
    {
        List<ItemEnchantment> itemEnchants = Utils.getItemEnchants(item);
        ItemStack is = itemMap.get(player);
        List<ItemEnchantment> isEnchants = Utils.getItemEnchants(is);
        if (is.getItem() == item.getItem() && Utils.getDurabilityData(is) == Utils.getDurabilityData(item) && isEnchants.size() == itemEnchants.size())
        {
            for (int x = 0; x < itemEnchants.size(); x++)
                if (itemEnchants.get(x) != isEnchants.get(x))
                    return false;

            //TODO perhaps use Text#isEmpty()?
            if (Utils.getDisplayNameData(is).displayName().get() == null && Utils.getDisplayNameData(item).displayName().get() == null)
                return true;

            if (Utils.getDisplayNameData(is) == Utils.getDisplayNameData(item))
                return true;
        }

        return false;
    }
}