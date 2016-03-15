package com.mongoosecountry.pete800.handler.exchange;

import java.util.UUID;

public class ExchangeTask implements Runnable
{
    private final ExchangeHandler handler;
    private final UUID player;

    public ExchangeTask(ExchangeHandler handler, UUID player)
    {
        this.handler = handler;
        this.player = player;
    }

    @Override
    public void run()
    {
        handler.endTransaction(player);
    }
}
