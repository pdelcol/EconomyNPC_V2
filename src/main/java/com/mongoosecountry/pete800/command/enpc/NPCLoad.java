package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import com.mongoosecountry.pete800.npc.NPCStorage;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;

public class NPCLoad extends AbstractCommand
{
    public NPCLoad()
    {
        super("load", "Manually load all NPC data from the config files.", Arrays.asList(new CommandArgument("/enpc"), new CommandArgument("load")), 0, "npc.load", false);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        if (!testPermission(source))
            return CommandResult.empty();

        EconomyNPC.npcStorage.getNPCs().forEach(AbstractNPC::despawnNPC);
        EconomyNPC.npcStorage = new NPCStorage(new File("config", "ENPC"));
        if (Sponge.getServer().getOnlinePlayers().size() > 0)
            EconomyNPC.npcStorage.getNPCs().forEach(AbstractNPC::respawnNPC);

        source.sendMessage(Text.builder("The command was successful, but please check your console for any errors that may have occurred.").color(TextColors.GREEN).build());
        return CommandResult.success();
    }
}
