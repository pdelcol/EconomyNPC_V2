package com.mongoosecountry.pete800.npc;

import com.mongoosecountry.pete800.Config;
import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.handler.exchange.ExchangeHandler;
import com.mongoosecountry.pete800.handler.exchange.ExchangeTask;
import com.mongoosecountry.pete800.util.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class ExchangeNPC extends InventoryNPC
{
    private final ExchangeHandler exchangeHandler = new ExchangeHandler();

    public ExchangeNPC(Location<World> location, String name)
    {
        super(location, NPCType.EXCHANGE, name);
    }

    public ExchangeNPC(String name, ConfigurationNode cn)
    {
        super(name, cn);
    }

    @Override
    public void onInteract(Player player)
    {
        Optional<ItemStack> itemStackOptional = getInventoryEdit().peek(new SlotIndex(0));
        if (!itemStackOptional.isPresent())
        {
            player.sendMessage(Utils.darkRedText("This NPC isn't ready for player interaction."));
            return;
        }

        Config config = EconomyNPC.config;
        double money = config.getTokenForMoney();
        int xp = config.getTokenForExp() + player.get(Keys.CONTAINED_EXPERIENCE).get();
        ItemStack item = itemStackOptional.get();
        ItemType itemType = item.getItem();
        UUID uuid = player.getUniqueId();
        if (exchangeHandler.isTransacting(uuid))
        {
            if (EconomyNPC.tokens.removeTokens(uuid, 1))
            {
                if (itemType == ItemTypes.EMERALD)
                {
                    EconomyService economyService = Utils.getEconomyService().get();
                    if (economyService.getOrCreateAccount(uuid).get().deposit(economyService.getDefaultCurrency(), new BigDecimal(money), Cause.of(NamedCause.source(EconomyNPC.instance()))).getResult() == ResultType.SUCCESS)
                        player.sendMessage(Text.join(Utils.goldText("You have traded 1 token for "), Text.builder().append(EconomyNPC.currencySymbol, Text.of(money)).color(TextColors.AQUA).build()));
                    else
                        player.sendMessage(Utils.darkRedText("You don't have enough money for that."));
                }
                else if (itemType == ItemTypes.EXPERIENCE_BOTTLE)
                {
                    player.offer(Keys.CONTAINED_EXPERIENCE, xp);
                    player.sendMessage(Text.join(Utils.goldText("You have traded 1 token for "), Text.builder(xp + "").color(TextColors.AQUA).build(), Utils.goldText(" XP.")));
                }

                return;
            }

            player.sendMessage(Utils.darkRedText("You do not have enough tokens for that."));
            return;
        }

        if (itemType == ItemTypes.EMERALD)
            player.sendMessage(Text.join(Text.builder("You are about to exchange 1 token for ").color(TextColors.BLUE).build(), Text.builder().append(EconomyNPC.currencySymbol, Text.of(money)).color(TextColors.GOLD).build()));
        else if (itemType == ItemTypes.EXPERIENCE_BOTTLE)
            player.sendMessage(Text.join(Text.builder("You are about to exchange 1 token for ").color(TextColors.BLUE).build(), Utils.goldText(xp + ""), Text.builder(" XP.").color(TextColors.BLUE).build()));

        player.sendMessage(Text.builder("Right click again to continue").color(TextColors.BLUE).build());
        exchangeHandler.newTransaction(uuid);
        Task.builder().execute(new ExchangeTask(exchangeHandler, uuid)).delayTicks(100).submit(EconomyNPC.instance());
    }
}
