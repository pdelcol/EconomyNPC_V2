package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class NPCSave extends AbstractCommand
{
    public NPCSave()
    {
        super("save", "Manually save all NPC data to the config files.", Arrays.asList(new CommandArgument("/enpc"), new CommandArgument("save")), 0, "npc.save", false);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        if (!testPermission(source))
            return CommandResult.empty();

        EconomyNPC.npcStorage.save(false);
        source.sendMessage(Text.builder("The command was successful, but please check your console for any errors that may have occurred.").color(TextColors.GREEN).build());
        return CommandResult.success();
    }
}
