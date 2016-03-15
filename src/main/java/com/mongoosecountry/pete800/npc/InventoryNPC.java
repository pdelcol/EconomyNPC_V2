package com.mongoosecountry.pete800.npc;

import com.mongoosecountry.pete800.EconomyNPC;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.Optional;

public abstract class InventoryNPC extends AbstractNPC
{
    private ConfigurationNode inv;

    @SuppressWarnings("WeakerAccess")
    public InventoryNPC(Location<World> location, NPCType type, String name)
    {
        super(location, type, name);
        this.inv = SimpleConfigurationNode.root();
    }

    @SuppressWarnings("WeakerAccess")
    public InventoryNPC(String name, ConfigurationNode cn)
    {
        super(name, cn);
        ConfigurationNode invNode = cn.getNode("inventory");
        if (invNode.isVirtual())
            this.inv = SimpleConfigurationNode.root();
        else
            this.inv = invNode;
    }

    @Override
    public void onInteract(Player player)
    {
        player.openInventory(getInventory(getName() + "'s Shop"), Cause.of(NamedCause.source(EconomyNPC.instance())));
    }

    public OrderedInventory getInventoryEdit()
    {
        return getInventory(getName() + " - Edit");
    }

    private ItemStack getItemFromConfigNode(int slot)
    {
        return ItemStack.builder().fromContainer(ConfigurateTranslator.instance().translateFrom(inv.getNode(slot))).build();
    }

    private void applyItemPrice(ItemStack itemStack)
    {
        itemStack.offer(Keys.ITEM_LORE, Collections.singletonList(Text.join(EconomyNPC.currencySymbol, Text.of(EconomyNPC.prices.getBuyPrice(itemStack)))));
    }

    private OrderedInventory getInventory(String name)
    {
        OrderedInventory inventory = CustomInventory.builder().name(new FixedTranslation(name)).build();
        if (type != NPCType.SELL)
        {
            for (int slot = 0; slot < inventory.size(); slot++)
            {
                Optional<ItemStack> itemStackOptional = inventory.peek(new SlotIndex(slot));
                if (!itemStackOptional.isPresent())
                    continue;

                ItemStack item = getItemFromConfigNode(slot);
                if (type == NPCType.SHOP)
                    applyItemPrice(item);

                inventory.set(new SlotIndex(slot), item);
            }
        }

        return inventory;
    }

    public void updateInventory(OrderedInventory inventory)
    {
        inv = SimpleConfigurationNode.root();
        for (int slot = 0; slot < inventory.size(); slot++)
        {
            Optional<ItemStack> itemStackOptional = inventory.peek(new SlotIndex(slot));
            if (!itemStackOptional.isPresent())
                continue;

            ItemStack item = itemStackOptional.get();
            if (type == NPCType.SHOP)
                applyItemPrice(item);

            inv.getNode(slot + "").setValue(item);
        }
    }

    public ConfigurationNode getInventory()
    {
        return inv;
    }

    @Override
    public ConfigurationNode save()
    {
        ConfigurationNode cn = super.save();
        cn.getNode("inventory").setValue(inv);
        return cn;
    }
}
