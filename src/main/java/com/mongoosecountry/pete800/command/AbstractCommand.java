package com.mongoosecountry.pete800.command;

import com.mongoosecountry.pete800.util.Utils;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCommand implements CommandCallable
{
	private final boolean isPlayerOnly;
	private final int minArgs;
	private final List<AbstractCommand> subCommands;
	private final String name;
	private final String permission;
	private final Text description;
	private final Text usage;

	public AbstractCommand(String name, String description, List<CommandArgument> arguments, int minArgs, String permission, boolean isPlayerOnly)
	{
		this(name, description, arguments, minArgs, permission, isPlayerOnly, new ArrayList<>());
	}

	public AbstractCommand(String name, String description, List<CommandArgument> arguments, int minArgs, String permission, boolean isPlayerOnly, List<AbstractCommand> subCommands)
	{
		this.name = name;
		this.description = Text.of(description);
		this.usage = parseUsage(arguments);
		this.minArgs = minArgs;
		this.permission = permission;
		this.isPlayerOnly = isPlayerOnly;
		this.subCommands = subCommands;
	}

	private Text parseUsage(List<CommandArgument> arguments)
	{
		List<Text> textList = new ArrayList<>();
		arguments.forEach(argument -> textList.add(argument.format()));
		return Text.joinWith(Text.of(" "), textList);
	}

	@Nonnull
	@Override
	public List<String> getSuggestions(@Nonnull CommandSource source, @Nonnull String arguments)
	{
		String[] args = splitArgs(arguments);
		List<String> list = new ArrayList<>();
		for (AbstractCommand command : subCommands)
		{
			if (args.length == 0)
				list.add(command.getName());
			if (command.getName().startsWith(args[args.length - 1]))
				list.add(command.getName());
		}

		return list;
	}

	@Override
	public boolean testPermission(@Nonnull CommandSource source)
	{
		if (isPlayerOnly && !(source instanceof Player))
		{
			source.sendMessage(Text.builder("[ENPC] This is a player only command.").color(TextColors.RED).build());
			return false;
		}

		if (!source.hasPermission(permission))
		{
			source.sendMessage(Text.builder("[ENPC] You do not have permission to do that.").color(TextColors.RED).build());
			return false;
		}

		return true;
	}

	@Nonnull
	@Override
	public Optional<? extends Text> getShortDescription(@Nonnull CommandSource source)
	{
		return Optional.of(description);
	}

	@Nonnull
	@Override
	public Optional<? extends Text> getHelp(@Nonnull CommandSource source)
	{
		return Optional.of(Text.joinWith(Text.of(" "), usage, description));
	}

	@Nonnull
	@Override
	public Text getUsage(@Nonnull CommandSource source)
	{
		return usage;
	}

	public int getMinArgs()
	{
		return minArgs;
	}

	public List<AbstractCommand> getSubCommands()
	{
		return subCommands;
	}

	public String getName()
	{
		return name;
	}

	public String getPermission()
	{
		return permission;
	}

	protected boolean minArgsMet(CommandSource source, int args)
	{
		if (args >= getMinArgs())
			return true;

		source.sendMessage(Text.joinWith(Utils.darkRedText("Not enough arguments: "), getUsage(source)));
		return false;
	}

	protected String[] splitArgs(String arguments)
	{
		return arguments.split("\\s");
	}

	protected String moveArguments(String[] arguments)
	{
		StringBuilder sb = new StringBuilder();
		for (String arg : arguments)
		{
			if (sb.length() == 0)
				sb.append(arg);
			else
				sb.append(" ").append(arg);
		}

		return sb.toString();
	}
}
