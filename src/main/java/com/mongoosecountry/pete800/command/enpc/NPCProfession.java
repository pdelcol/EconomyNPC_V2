package com.mongoosecountry.pete800.command.enpc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.command.CommandArgument;
import com.mongoosecountry.pete800.command.CommandArgument.Syntax;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import com.mongoosecountry.pete800.util.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NPCProfession extends AbstractCommand
{
    public NPCProfession()
    {
        super("profession", "Set the outfit of an NPC.", Arrays.asList(new CommandArgument("/enpc"), new CommandArgument("profession"), new CommandArgument("name", Syntax.REQUIRED, Syntax.REPLACE), new CommandArgument("profession", Syntax.REPLACE, Syntax.REQUIRED)), 2, "npc.profession", false);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        String[] args = splitArgs(arguments);
        if (!testPermission(source))
            return CommandResult.empty();

        if (!minArgsMet(source, args.length))
            return CommandResult.empty();

        AbstractNPC npc = EconomyNPC.npcStorage.getNPC(args[0]);
        if (npc == null)
        {
            source.sendMessage(Utils.darkRedText("This NPC does not exist."));
            return CommandResult.empty();
        }

        Optional<Career> careerOptional = Sponge.getRegistry().getType(Career.class, args[1].toLowerCase());
        Career profession = careerOptional.get();
        if (!careerOptional.isPresent())
        {
            List<Text> careers = Sponge.getRegistry().getAllOf(Career.class).stream().map(c -> Text.of(c.getId())).collect(Collectors.toList());
            source.sendMessage(Text.join(Utils.darkRedText("Invalid profession. Expected: "), Text.joinWith(Text.of(", "), careers)));
            return CommandResult.empty();
        }

        npc.setCareer(profession);
        source.sendMessage(Text.builder("Profession set.").color(TextColors.GREEN).build());
        return CommandResult.success();
    }
}
