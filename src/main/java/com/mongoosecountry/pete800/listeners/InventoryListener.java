package com.mongoosecountry.pete800.listeners;

import com.flowpowered.math.vector.Vector3d;
import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import com.mongoosecountry.pete800.npc.AbstractNPC.NPCType;
import com.mongoosecountry.pete800.npc.InventoryNPC;
import com.mongoosecountry.pete800.util.Utils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO move to individual NPC?
public class InventoryListener
{
	@Listener
	public void onInventoryClickClick(ClickInventoryEvent event)
	{
        Cause cause = Cause.of(NamedCause.source(EconomyNPC.instance()));
        List<Player> players = new ArrayList<>();
        players.addAll(event.getTargetInventory().getViewers());
        Player player = players.get(0);
        //TODO need to test if this works.
        if (!(event instanceof ClickInventoryEvent.Primary))
        {
            players.get(0).sendMessage(Utils.darkRedText("Invalid click! Please LEFT click on the item you want."));
            player.closeInventory(cause);
            event.setCancelled(true);
            return;
        }

        SlotTransaction slotTransaction = event.getTransactions().get(0);
        if (!slotTransaction.getSlot().peek().isPresent())
            return;

		ItemStack clicked = slotTransaction.getOriginal().createStack();
		OrderedInventory shop = (OrderedInventory) event.getTargetInventory();
		if (player.getInventory() == shop)
            return;

		AbstractNPC npc = null;
		if (shop.getName().get().contains("'"))
			npc = EconomyNPC.npcStorage.getNPC(shop.getName().get().substring(0, shop.getName().get().indexOf("'")));
		else if (shop.getName().get().contains("-"))
			return;
		
		if (npc == null)
            return;
		
		if (npc.getType() == NPCType.SHOP)
		{
            ItemType itemType = clicked.getItem();
            double price = EconomyNPC.prices.getBuyPrice(clicked);
            EconomyService econ = Utils.getEconomyService().get();
            if (econ.getOrCreateAccount(player.getUniqueId()).get().withdraw(econ.getDefaultCurrency(), new BigDecimal(price), cause).getResult() == ResultType.SUCCESS)
            {
                event.setCancelled(true);
                player.closeInventory(cause);
                dropItem(clicked, player.getLocation().getPosition(), player.getWorld());
                player.sendMessage(Text.join(Text.builder("You bought ").color(TextColors.AQUA).build(), Text.of(itemType.getName()), Text.builder(" for ").color(TextColors.AQUA).build(), Text.of("$" + price)));
            }
            else
            {
                event.setCancelled(true);
                player.closeInventory(cause);
                player.sendMessage(Utils.goldText("You don't have enough funds."));
            }
		}
    }

	@Listener
	public void onInventoryClose(InteractInventoryEvent.Close event)
	{
		OrderedInventory inv = (OrderedInventory) event.getTargetInventory();
		Player player = event.getCause().first(Player.class).get();
		AbstractNPC npc = null;
		if (inv.getName().get().contains("'s Shop"))
			npc = EconomyNPC.npcStorage.getNPC(inv.getName().get().substring(0, inv.getName().get().indexOf("'")));
		else if (inv.getName().get().contains(" - Edit"))
			npc = EconomyNPC.npcStorage.getNPC(inv.getName().get().substring(0, inv.getName().get().indexOf(" ")));
		
		if (npc == null)
            return;
		
		if (npc.getType() == NPCType.SELL)
		{
			List<ItemStack> items = new ArrayList<>();
			double sell = 0;
			for (Inventory slot : inv)
			{
                Optional<ItemStack> itemStackOptional = slot.peek();
				if (!itemStackOptional.isPresent())
                    continue;

                ItemStack item = itemStackOptional.get();
                if (EconomyNPC.prices.getSellPrice(item) > 0)
                    sell += EconomyNPC.prices.getSellPrice(item);
                else
                    items.add(item);
			}
			
			EconomyService econ = Utils.getEconomyService().get();
            if (econ.getOrCreateAccount(player.getUniqueId()).get().deposit(econ.getDefaultCurrency(), new BigDecimal(sell), Cause.of(NamedCause.source(EconomyNPC.instance()))).getResult() != ResultType.SUCCESS)
            {
                for (Inventory slot : inv)
                {
                    if (!slot.peek().isPresent())
                        continue;

                    dropItem(slot.peek().get(), player.getLocation().getPosition(), player.getWorld());
                }

                return;
            }

			player.sendMessage(Text.join(Text.builder("You have sold items for a total amount of ").color(TextColors.AQUA).build(), Text.of("$" + sell)));
			if (items.size() > 0)
			{
				player.sendMessage(Utils.darkRedText("Some items could not be sold. They have been returned back to you."));
				for (ItemStack item : items)
					dropItem(item, player.getLocation().getPosition(), player.getWorld());
			}
		}
		else if ((npc.getType() == NPCType.EXCHANGE || npc.getType() == NPCType.SHOP || npc.getType() == NPCType.KIT) && inv.getName().get().contains(" - Edit"))
		{
			((InventoryNPC) npc).updateInventory(inv);
			player.sendMessage(Text.builder("Shop updated.").color(TextColors.GREEN).build());
		}
	}

    private void dropItem(ItemStack itemStack, Vector3d position, World world)
    {
        Cause cause = Cause.of(NamedCause.source(EconomyNPC.instance()));
        Item itemEntity = (Item) world.createEntity(EntityTypes.ITEM, position).get();
        itemEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        world.spawnEntity(itemEntity, cause);
    }
}
