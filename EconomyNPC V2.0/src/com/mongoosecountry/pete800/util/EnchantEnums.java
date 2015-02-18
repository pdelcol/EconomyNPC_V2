package com.mongoosecountry.pete800.util;

import org.bukkit.enchantments.Enchantment;

public enum EnchantEnums
{
	AQUA_AFFINITY(Enchantment.WATER_WORKER, 2),
	BANE_OF_ARTHROPODS(Enchantment.DAMAGE_ARTHROPODS, 5),
	BLAST_PROTECTION(Enchantment.PROTECTION_EXPLOSIONS, 2),
	DEPTH_STRIDER(Enchantment.DEPTH_STRIDER, 2),
	EFFICIENCY(Enchantment.DIG_SPEED, 10),
	FEATHER_FALL(Enchantment.PROTECTION_FALL, 5),
	FIRE_ASPECT(Enchantment.FIRE_ASPECT, 2),
	FIRE_PROTECTION(Enchantment.PROTECTION_FIRE, 5),
	FLAME(Enchantment.ARROW_FIRE, 2),
	FORTUNE(Enchantment.LOOT_BONUS_BLOCKS, 2),
	INFINITY(Enchantment.ARROW_INFINITE, 1),
	KNOCKBACK(Enchantment.KNOCKBACK, 5),
	LOOTING(Enchantment.LOOT_BONUS_MOBS, 2),
	LUCK_OF_THE_SEA(Enchantment.LUCK, 2),
	LURE(Enchantment.LURE, 2),
	POWER(Enchantment.ARROW_DAMAGE, 10),
	PUNCH(Enchantment.ARROW_KNOCKBACK, 2),
	PROJECTILE_PROTECTION(Enchantment.PROTECTION_PROJECTILE, 5),
	PROTECTION(Enchantment.PROTECTION_ENVIRONMENTAL, 10),
	RESPIRATION(Enchantment.OXYGEN, 2),
	SHARPNESS(Enchantment.DAMAGE_ALL, 10),
	SILK_TOUCH(Enchantment.SILK_TOUCH, 1),
	SMITE(Enchantment.DAMAGE_UNDEAD, 5),
	THORNS(Enchantment.THORNS, 1),
	UNBREAKING(Enchantment.DURABILITY, 5);
	
	Enchantment enchant;
	int weight;
	
	private EnchantEnums(Enchantment enchant, int weight)
	{
		this.enchant = enchant;
		this.weight = weight;
	}
	
	public Enchantment getEnchant()
	{
		return enchant;
	}
	
	public int getWeight()
	{
		return weight;
	}
}
