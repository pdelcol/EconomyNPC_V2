package com.mongoosecountry.pete800.handler.blacksmith;

import java.util.UUID;

public class BlacksmithTask implements Runnable
{
    private final BlacksmithHandler handler;
    private final UUID player;

    public BlacksmithTask(BlacksmithHandler handler, UUID player)
    {
        this.handler = handler;
        this.player = player;
    }

    @Override
    public void run()
    {
        handler.removeTransaction(player);
    }
}