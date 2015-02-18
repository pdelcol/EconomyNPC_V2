package com.mongoosecountry.pete800.command.enpc;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.command.AbstractCommand;
import com.mongoosecountry.pete800.npc.AbstractNPC;

public class NPCMove extends AbstractCommand
{
	public NPCMove(EconomyNPC plugin)
	{
		super(plugin, true, "move", "Move an NPC to your location.", "npc.move", Arrays.asList("/enpc", "move", "<name>"), null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		Player player = (Player) sender;
		if (args.length > 0)
		{
			AbstractNPC npc = plugin.npcStorage.getNPC(args[0]);
			if (npc == null)
			{
				player.sendMessage(ChatColor.DARK_RED + "This NPC does not exist.");
				return false;
			}
			
			npc.setLocation(player.getLocation());
			player.sendMessage(ChatColor.GREEN + "NPC moved.");
			return true;
		}
		
		return false;
	}
}
