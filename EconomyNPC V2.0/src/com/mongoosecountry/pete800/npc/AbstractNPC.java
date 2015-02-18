package com.mongoosecountry.pete800.npc;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mongoosecountry.pete800.EconomyNPC;

public abstract class AbstractNPC
//public class AbstractNPC
{
	EconomyNPC plugin;
	Villager villager;
	NPCType type;
	String name;
	Location location;
	Profession profession;
	
	public AbstractNPC(EconomyNPC plugin, Location location, NPCType type, String name)
	{
		this.plugin = plugin;
		this.location = location;
		this.type = type;
		this.name = name;
		this.profession = Profession.FARMER;
		if (plugin.getServer().getOnlinePlayers().size() > 0)
			respawnNPC();
	}
	
	public AbstractNPC(EconomyNPC plugin, String name, ConfigurationSection cs)
	{
		this.plugin = plugin;
		this.type = NPCType.fromName(cs.getString("type"));
		this.location = new Location(plugin.getServer().getWorld(cs.getString("world")), cs.getDouble("x"), cs.getDouble("y"), cs.getDouble("z"));
		this.name = name;
		this.profession = Profession.valueOf(cs.getString("profession", "FARMER").toUpperCase());
	}
	
	public void respawnNPC()
	{
		despawnNPC();
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName(name);
		villager.setCustomNameVisible(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 6));
		villager.setRemoveWhenFarAway(false);
		villager.setProfession(profession);
	}
	
	public void despawnNPC()
	{
		if (villager == null)
			return;
		
		villager.remove();
		villager = null;
	}
	
	public abstract void onInteract(Player player);
	
	public NPCType getType()
	{
		return type;
	}
	
	public Villager getVillager()
	{
		return villager;
	}
	
	public String getName()
	{
		if (villager != null)
			return villager.getCustomName();
		
		return name;
	}
	
	public Profession getProfession()
	{
		if (villager != null)
			villager.getProfession();
		
		return profession;
	}
	
	public void setProfession(Profession profession)
	{
		this.profession = profession;
		respawnNPC();
	}
	
	public void setLocation(Location location)
	{
		if (villager != null)
			villager.teleport(location);
		
		this.location = location;
	}
	
	public Location getLocation()
	{
		if (villager != null)
			villager.getLocation();
		
		return location;
	}
	
	public ConfigurationSection save()
	{
		ConfigurationSection cs = new YamlConfiguration();
		cs.set("type", type.toString());
		cs.set("profession", profession.toString());
		cs.set("world", location.getWorld().getName());
		cs.set("x", location.getX());
		cs.set("y", location.getY());
		cs.set("z", location.getZ());
		return cs;
	}
	
	public static enum NPCType
	{
		// Gambling NPC
		BETTING,
		// Repair tools/armor
		BLACKSMITH,
		// Exchange tokens for XP/Money
		EXCHANGE,
		// Buy kits
		KIT,
		// Standard shop
		SHOP,
		// Sell items to the NPC
		SELL;
		
		public static NPCType fromName(String name)
		{
			for (NPCType type : values())
				if (name.toUpperCase().equals(type.toString()))
					return type;
			
			return null;
		}
	}
}
