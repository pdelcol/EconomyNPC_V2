package com.mongoosecountry.pete800.npc;

import com.google.common.reflect.TypeToken;
import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.handler.kit.KitHandler;
import com.mongoosecountry.pete800.handler.kit.KitTask;
import com.mongoosecountry.pete800.util.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class KitNPC extends InventoryNPC
{
    KitHandler kitHandler = new KitHandler();
    List<String> kitMessage;

    public KitNPC(Location<World> location, String name)
    {
        super(location, NPCType.KIT, name);
    }

    public KitNPC(String name, ConfigurationNode cn)
    {
        super(name, cn);
        try
        {
            this.kitMessage = cn.getNode("kit-message").getList(TypeToken.of(String.class));
        }
        catch (ObjectMappingException e)
        {
            EconomyNPC.logger.error("Error parsing kits for " + getName() + ".");
            this.kitMessage = new ArrayList<>();
        }
    }

    public List<String> getKitMessage()
    {
        return kitMessage;
    }

    @Override
    public void onInteract(Player player)
    {
        OrderedInventory inv = getInventoryEdit();
        UUID uuid = player.getUniqueId();
        if (!kitHandler.isTransacting(uuid))
        {
            Optional<ItemStack> itemStackOptional = inv.peek(new SlotIndex(inv.size() - 1));
            if (!itemStackOptional.isPresent() || itemStackOptional.get().getItem() != ItemTypes.COAL)
            {
                player.sendMessage(Utils.darkRedText("This NPC is not ready for player interaction."));
                return;
            }

            ItemStack coal = itemStackOptional.get();
            int numTokens = coal.getQuantity();
            player.sendMessage(Text.join(Utils.goldText("Do you really want to spend "), Text.builder(numTokens + "").color(TextColors.DARK_AQUA).build(), Utils.goldText(" tokens for this kit?")));
            for (String message : getKitMessage())
                player.sendMessage(TextSerializers.JSON.deserialize(message));

            kitHandler.newTransaction(uuid);
            Task.builder().execute(new KitTask(kitHandler, uuid)).delayTicks(100).submit(EconomyNPC.instance());
            return;
        }

        Optional<ItemStack> itemStackOptional = inv.peek(new SlotIndex(inv.size() - 1));
        if (!itemStackOptional.isPresent())
        {
            player.sendMessage(Utils.darkRedText("This NPC is not ready for player interaction."));
            return;
        }

        int numTokens = itemStackOptional.get().getQuantity();
        if (EconomyNPC.tokens.removeTokens(uuid, numTokens))
        {
            for (Inventory slot : getInventoryEdit())
            {
                Optional<ItemStack> itemOptional = slot.peek();
                if (itemOptional.get().getItem() != ItemTypes.COAL)
                {
                    ItemStack item = itemOptional.get();
                    if (itemOptional.isPresent())
                    {
                        World world = player.getWorld();
                        Item itemEntity = (Item) world.createEntity(EntityTypes.ITEM, player.getLocation().getPosition()).get();
                        itemEntity.offer(Keys.REPRESENTED_ITEM, item.createSnapshot());
                        world.spawnEntity(itemEntity, Cause.of(NamedCause.source(EconomyNPC.instance())));
                    }
                }
            }

            player.sendMessage(Text.join(Text.builder("You exchanged ").color(TextColors.GREEN).build(), Text.builder(numTokens + "").color(TextColors.AQUA).build(), Text.builder(" tokens for a kit!").color(TextColors.GREEN).build()));
            return;
        }

        player.sendMessage(Utils.darkRedText("You do not have enough tokens to do this!"));
    }
}
