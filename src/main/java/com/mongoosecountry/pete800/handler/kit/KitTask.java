package com.mongoosecountry.pete800.handler.kit;

import java.util.UUID;

public class KitTask implements Runnable
{
    KitHandler handler;
    UUID player;

    public KitTask(KitHandler handler, UUID player)
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
