package com.mongoosecountry.pete800.command.enpc;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.npc.AbstractNPC;
import com.mongoosecountry.pete800.npc.NPCStorage;

public class NPCLoad extends AbstractCommand
{
	public NPCLoad(EconomyNPC plugin)
	{
		super(plugin, false, "load", "Manually load all NPC data from the config files.", "npc.load", Arrays.asList("/enpc", "load"), null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		for (AbstractNPC npc : plugin.npcStorage.getNPCs())
			npc.despawnNPC();
		
		plugin.npcStorage = new NPCStorage(plugin);
		if (plugin.getServer().getOnlinePlayers().size() > 0)
			for (AbstractNPC npc : plugin.npcStorage.getNPCs())
				npc.respawnNPC();
		
		sender.sendMessage(ChatColor.GREEN + "The command was successful, but please check your console for any errors that may have occurred.");
		return true;
	}
}
