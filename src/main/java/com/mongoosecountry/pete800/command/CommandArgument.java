package com.mongoosecountry.pete800.command;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Arrays;
import java.util.List;

public class CommandArgument
{
    private final List<Syntax> syntaxList;
    private final String name;

    public CommandArgument(String name)
    {
        this(name, Syntax.LITERAL);
    }

    public CommandArgument(String name, Syntax... syntaxes)
    {
        this.name = name;
        this.syntaxList = Arrays.asList(syntaxes);
    }

    public Text format()
    {
        Text name = Text.of(this.name);
        if (syntaxList.contains(Syntax.MULTIPLE))
            name = Text.builder().append(name, Text.of("...")).build();

        if (syntaxList.contains(Syntax.REPLACE))
            name = Text.builder().append(name).style(TextStyles.ITALIC).build();

        if (syntaxList.contains(Syntax.OPTIONAL))
            name = Text.builder().append(Text.of("["), name, Text.of("]")).build();

        if (syntaxList.contains(Syntax.REQUIRED))
            name = Text.builder().append(Text.of("<"), name, Text.of(">")).build();

        return name;
    }

    public enum Syntax
    {
        LITERAL,
        MULTIPLE,
        REPLACE,
        REQUIRED,
        OPTIONAL
    }
}