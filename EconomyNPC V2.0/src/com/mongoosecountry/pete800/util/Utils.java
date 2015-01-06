package com.mongoosecountry.pete800.util;

import java.lang.reflect.InvocationTargetException;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

public class Utils
{
	public static void broadcastPacket(PacketContainer packet)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			try
			{
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
			}
			catch (InvocationTargetException e)
			{
				player.sendMessage(ChatColor.RED + "A packet sending error has occurred. NPC's might be hiding from you :c");
			}
		}
	}
}
