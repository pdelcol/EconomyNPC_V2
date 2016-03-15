package com.mongoosecountry.pete800.npc;

import com.mongoosecountry.pete800.Config;
import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.handler.blacksmith.BlacksmithHandler;
import com.mongoosecountry.pete800.handler.blacksmith.BlacksmithTask;
import com.mongoosecountry.pete800.util.EnchantEnums;
import com.mongoosecountry.pete800.util.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BlacksmithNPC extends AbstractNPC
{
	BlacksmithHandler blacksmithHandler = new BlacksmithHandler();
    Map<Double, Integer> expLevels = new HashMap<>();
    Map<ItemType, ItemType> repairables = new HashMap<>();
    Map<ItemType, Integer> maxDurabilities = new HashMap<>();
	
	public BlacksmithNPC(Location<World> location, String name)
	{
		super(location, NPCType.BLACKSMITH, name);
		parseMaps();
	}
	
	public BlacksmithNPC(String name, ConfigurationNode cn)
	{
		super(name, cn);
		parseMaps();
	}

	@Override
	public void onInteract(Player player)
	{
        Optional<ItemStack> itemStackOptional = player.getItemInHand();
        if (!itemStackOptional.isPresent())
        {
            player.sendMessage(Utils.darkRedText("Your hand is empty!"));
            return;
        }

		ItemStack itemStack = itemStackOptional.get();
        DurabilityData durabilityData = Utils.getDurabilityData(itemStack);
        ItemType itemType = itemStack.getItem();
		if (!repairables.containsKey(itemType) || durabilityData.durability().get() == 0)
		{
			player.sendMessage(Utils.darkRedText("I can not repair that item."));
			return;
		}

        Config config = EconomyNPC.config;
		double costPerUnit = EconomyNPC.prices.getBuyPrice(ItemStack.of(repairables.get(itemStack.getItem()), 0));
        double enchantCost = 0;
        //Max durability not available sticking with a hardcoded solution for now.
        double numUnits = round(durabilityData.durability().get() / (maxDurabilities.get(itemType) / 4));
        int experienceCost = (int) (expLevels.get(numUnits) * (config.getTokenForMoney() / config.getTokenForExp()));
        //TODO need to test to see if an item always has a "display name". If so then change this to check if ItemStack#getItem()#getName() against all ItemTypes to see if the item is named.
        int nameCost = (itemStack.get(Keys.DISPLAY_NAME).get() == null ? 55 * 10 : 0);
        List<ItemEnchantment> enchants = Utils.getItemEnchants(itemStack);
        UUID uuid = player.getUniqueId();
        if (enchants.size() != 0)
		{
			for (ItemEnchantment enchant : enchants)
			{
				int weight = 0;
				for (EnchantEnums enumEnchant : EnchantEnums.values())
					if (enchant.getEnchantment() == enumEnchant.getEnchant())
						weight = enumEnchant.getWeight();
				
				enchantCost += (11 - (10 * weight / 10)) * enchant.getLevel();
			}
		}
		
		if (!blacksmithHandler.isPlayerTransacting(uuid))
		{
			double cost = costPerUnit * numUnits + enchantCost + experienceCost + nameCost;				
			player.sendMessage(Text.join(Utils.goldText("The tool will cost: "), Text.builder("$" + cost).color(TextColors.AQUA).build(), Utils.goldText(". If you would like to reforge it, right click me again.")));
			blacksmithHandler.newTransaction(cost, uuid, itemStack);
            Task.builder().execute(new BlacksmithTask(blacksmithHandler, uuid)).delayTicks(100).submit(EconomyNPC.instance());
		}
		else
		{
			if (blacksmithHandler.doesItemMatch(uuid, itemStack))
			{
                EconomyService econ = Utils.getEconomyService().get();
				if (econ.getOrCreateAccount(uuid).get().withdraw(econ.getDefaultCurrency(), new BigDecimal(blacksmithHandler.getCost(uuid)), Cause.of(NamedCause.source(EconomyNPC.instance()))).getResult() == ResultType.SUCCESS)
				{
					player.getItemInHand().get().offer(Keys.ITEM_DURABILITY, 0);
					player.sendMessage(Utils.goldText("Transaction successful!"));
				}
				else
					player.sendMessage(Utils.darkRedText("You don't have enough money for that."));
			}
			else
				player.sendMessage(Utils.darkRedText("Something's not right with that!"));
			
			blacksmithHandler.removeTransaction(uuid);
		}
	}
	
	private void parseMaps()
	{
		ItemType wood = ItemTypes.PLANKS;
		ItemType carrot = ItemTypes.CARROT;
		ItemType flint = ItemTypes.FLINT;
		ItemType stick = ItemTypes.STICK;
		ItemType leather = ItemTypes.LEATHER;
		ItemType cobble = ItemTypes.COBBLESTONE;
		ItemType iron = ItemTypes.IRON_INGOT;
		ItemType gold = ItemTypes.GOLD_INGOT;
		ItemType diamond = ItemTypes.DIAMOND;
		repairables.put(ItemTypes.IRON_SHOVEL, iron);
		repairables.put(ItemTypes.IRON_PICKAXE, iron);
		repairables.put(ItemTypes.IRON_AXE, iron);
		repairables.put(ItemTypes.WOODEN_SHOVEL, wood);
		repairables.put(ItemTypes.WOODEN_PICKAXE, wood);
		repairables.put(ItemTypes.WOODEN_AXE, wood);
		repairables.put(ItemTypes.STONE_SHOVEL, cobble);
		repairables.put(ItemTypes.STONE_PICKAXE, cobble);
		repairables.put(ItemTypes.STONE_AXE, cobble);
		repairables.put(ItemTypes.DIAMOND_SHOVEL, diamond);
		repairables.put(ItemTypes.DIAMOND_PICKAXE, diamond);
		repairables.put(ItemTypes.DIAMOND_AXE, diamond);
		repairables.put(ItemTypes.GOLDEN_SHOVEL, gold);
		repairables.put(ItemTypes.GOLDEN_PICKAXE, gold);
		repairables.put(ItemTypes.GOLDEN_AXE, gold);
		repairables.put(ItemTypes.WOODEN_HOE, wood);
		repairables.put(ItemTypes.IRON_HOE, iron);
		repairables.put(ItemTypes.STONE_HOE, cobble);
		repairables.put(ItemTypes.DIAMOND_HOE, diamond);
		repairables.put(ItemTypes.GOLDEN_HOE, gold);
		repairables.put(ItemTypes.WOODEN_SWORD, wood);
		repairables.put(ItemTypes.IRON_SWORD, iron);
		repairables.put(ItemTypes.STONE_SWORD, cobble);
		repairables.put(ItemTypes.DIAMOND_SWORD, diamond);
		repairables.put(ItemTypes.GOLDEN_SWORD, gold);
		repairables.put(ItemTypes.LEATHER_BOOTS, leather);
		repairables.put(ItemTypes.CHAINMAIL_BOOTS, iron);
		repairables.put(ItemTypes.IRON_BOOTS, iron);
		repairables.put(ItemTypes.DIAMOND_BOOTS, diamond);
		repairables.put(ItemTypes.GOLDEN_BOOTS, gold);
		repairables.put(ItemTypes.LEATHER_CHESTPLATE, leather);
		repairables.put(ItemTypes.CHAINMAIL_CHESTPLATE, iron);
		repairables.put(ItemTypes.IRON_CHESTPLATE, iron);
		repairables.put(ItemTypes.DIAMOND_CHESTPLATE, diamond);
		repairables.put(ItemTypes.GOLDEN_CHESTPLATE, gold);
		repairables.put(ItemTypes.LEATHER_HELMET, leather);
		repairables.put(ItemTypes.CHAINMAIL_HELMET, iron);
		repairables.put(ItemTypes.IRON_HELMET, iron);
		repairables.put(ItemTypes.DIAMOND_HELMET, diamond);
		repairables.put(ItemTypes.GOLDEN_HELMET, gold);
		repairables.put(ItemTypes.LEATHER_LEGGINGS, leather);
		repairables.put(ItemTypes.CHAINMAIL_LEGGINGS, iron);
		repairables.put(ItemTypes.IRON_LEGGINGS, iron);
		repairables.put(ItemTypes.DIAMOND_LEGGINGS, diamond);
		repairables.put(ItemTypes.GOLDEN_LEGGINGS, gold);
		repairables.put(ItemTypes.BOW, stick);
		repairables.put(ItemTypes.FLINT_AND_STEEL, flint);
		repairables.put(ItemTypes.FISHING_ROD, stick);
		repairables.put(ItemTypes.SHEARS, iron);
		repairables.put(ItemTypes.CARROT_ON_A_STICK, carrot);
        
        int diamondTool = 1562;
        int goldTool = 33;
        int ironTool = 251;
        int stoneTool = 132;
        int woodTool = 60;
        int ironHelmet = 166;
        int ironChestplate = 241;
        int ironLeggings = 226;
        int ironBoots = 196;
        maxDurabilities.put(ItemTypes.IRON_SHOVEL, ironTool);
        maxDurabilities.put(ItemTypes.IRON_PICKAXE, ironTool);
        maxDurabilities.put(ItemTypes.IRON_AXE, ironTool);
        maxDurabilities.put(ItemTypes.WOODEN_SHOVEL, woodTool);
        maxDurabilities.put(ItemTypes.WOODEN_PICKAXE, woodTool);
        maxDurabilities.put(ItemTypes.WOODEN_AXE, woodTool);
        maxDurabilities.put(ItemTypes.STONE_SHOVEL, stoneTool);
        maxDurabilities.put(ItemTypes.STONE_PICKAXE, stoneTool);
        maxDurabilities.put(ItemTypes.STONE_AXE, stoneTool);
        maxDurabilities.put(ItemTypes.DIAMOND_SHOVEL, diamondTool);
        maxDurabilities.put(ItemTypes.DIAMOND_PICKAXE, diamondTool);
        maxDurabilities.put(ItemTypes.DIAMOND_AXE, diamondTool);
        maxDurabilities.put(ItemTypes.GOLDEN_SHOVEL, goldTool);
        maxDurabilities.put(ItemTypes.GOLDEN_PICKAXE, goldTool);
        maxDurabilities.put(ItemTypes.GOLDEN_AXE, goldTool);
        maxDurabilities.put(ItemTypes.WOODEN_HOE, woodTool);
        maxDurabilities.put(ItemTypes.IRON_HOE, ironTool);
        maxDurabilities.put(ItemTypes.STONE_HOE, stoneTool);
        maxDurabilities.put(ItemTypes.DIAMOND_HOE, diamondTool);
        maxDurabilities.put(ItemTypes.GOLDEN_HOE, goldTool);
        maxDurabilities.put(ItemTypes.WOODEN_SWORD, woodTool);
        maxDurabilities.put(ItemTypes.IRON_SWORD, ironTool);
        maxDurabilities.put(ItemTypes.STONE_SWORD, stoneTool);
        maxDurabilities.put(ItemTypes.DIAMOND_SWORD, diamondTool);
        maxDurabilities.put(ItemTypes.GOLDEN_SWORD, goldTool);
        maxDurabilities.put(ItemTypes.LEATHER_BOOTS, 66);
        maxDurabilities.put(ItemTypes.CHAINMAIL_BOOTS, ironBoots);
        maxDurabilities.put(ItemTypes.IRON_BOOTS, ironBoots);
        maxDurabilities.put(ItemTypes.DIAMOND_BOOTS, 430);
        maxDurabilities.put(ItemTypes.GOLDEN_BOOTS, 92);
        maxDurabilities.put(ItemTypes.LEATHER_CHESTPLATE, 81);
        maxDurabilities.put(ItemTypes.CHAINMAIL_CHESTPLATE, ironChestplate);
        maxDurabilities.put(ItemTypes.IRON_CHESTPLATE, ironChestplate);
        maxDurabilities.put(ItemTypes.DIAMOND_CHESTPLATE, 529);
        maxDurabilities.put(ItemTypes.GOLDEN_CHESTPLATE, 113);
        maxDurabilities.put(ItemTypes.LEATHER_HELMET, 56);
        maxDurabilities.put(ItemTypes.CHAINMAIL_HELMET, ironHelmet);
        maxDurabilities.put(ItemTypes.IRON_HELMET, ironHelmet);
        maxDurabilities.put(ItemTypes.DIAMOND_HELMET, 364);
        maxDurabilities.put(ItemTypes.GOLDEN_HELMET, 78);
        maxDurabilities.put(ItemTypes.LEATHER_LEGGINGS, 76);
        maxDurabilities.put(ItemTypes.CHAINMAIL_LEGGINGS, ironLeggings);
        maxDurabilities.put(ItemTypes.IRON_LEGGINGS, ironLeggings);
        maxDurabilities.put(ItemTypes.DIAMOND_LEGGINGS, 496);
        maxDurabilities.put(ItemTypes.GOLDEN_LEGGINGS, 106);
        maxDurabilities.put(ItemTypes.BOW, 385);
        maxDurabilities.put(ItemTypes.FLINT_AND_STEEL, 65);
        maxDurabilities.put(ItemTypes.FISHING_ROD, 65);
        maxDurabilities.put(ItemTypes.SHEARS, 238);
        maxDurabilities.put(ItemTypes.CARROT_ON_A_STICK, 26);
		
		expLevels.put(1.0, 7);
		expLevels.put(2.0, 16);
		expLevels.put(3.0, 27);
		expLevels.put(4.0, 40);
	}
	
	private double round(double d)
	{
		if (d > 0 && d <= 1)
			return 1.0;
		else if (d > 1 && d <= 2)
			return 2.0;
		else if (d > 2 && d <= 3)
			return 3.0;
		else if (d > 3 && d <= 4)
			return 4.0;
		else
			return 0.0;
	}
}
