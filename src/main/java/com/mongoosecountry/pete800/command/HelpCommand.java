package com.mongoosecountry.pete800.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class HelpCommand extends AbstractCommand
{
    private final AbstractCommand mainCommand;
    private final Text HEADER = Text.joinWith(Text.of(" "), goldUnderlinedText("EconomyNPC v3.0"), Text.of("by"), goldUnderlinedText("Bruce"), Text.of("&"), Text.of("Pete"));

    public HelpCommand(AbstractCommand mainCommand, CommandSource source)
    {
        super("help", "Display help info for " + mainCommand.getUsage(source), Arrays.asList(new CommandArgument(((LiteralText) mainCommand.getUsage(source)).getContent()), new CommandArgument("help")), 1, "", false, null, null);
        this.mainCommand = mainCommand;
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException
    {
        source.sendMessage(HEADER);
        source.sendMessage(Text.of(mainCommand.getUsage(source), mainCommand.getShortDescription(source)));
        mainCommand.getSubCommands().forEach(command -> source.sendMessage(command.getHelp(source).get()));
        return CommandResult.success();
    }

    private Text goldUnderlinedText(String string)
    {
        return Text.builder(string).color(TextColors.GOLD).style(TextStyles.UNDERLINE).build();
    }
}