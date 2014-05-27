package com.mongoosecountry.pete800.listeners;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC;
import com.mongoosecountry.pete800.npc.PlayerNPC.NPCType;
import com.mongoosecountry.pete800.util.packet.WrapperPlayClientUseEntity;

public class NPCInteractListener extends PacketAdapter
{
	EconomyNPC plugin;
	
	public NPCInteractListener(EconomyNPC plugin, PacketType type)
	{
		super(plugin, type);
		this.plugin = plugin;
	}
	
	@Override
	public void onPacketReceiving(PacketEvent event)
	{
		Player player = event.getPlayer();
		if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY)
		{
			WrapperPlayClientUseEntity use = new WrapperPlayClientUseEntity(event.getPacket());
			for (PlayerNPC npc : plugin.storage.getEntities())
			{
				if (npc.getEntityData().getEntityID() == use.getTargetID())
				{
					if(npc.getType() == NPCType.BLACKSMITH || npc.getType() == NPCType.XP)
					{
						npc.handleNonInventoryNPC(player, plugin.econ);
					}
					else
					{
						player.openInventory(npc.getInventory(player));
					}
				}
			}
		}
	}
}
