package com.mongoosecountry.pete800.util;

import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.Enchantments;

public enum EnchantEnums
{
    AQUA_AFFINITY(Enchantments.AQUA_AFFINITY, 2),
    BANE_OF_ARTHROPODS(Enchantments.BANE_OF_ARTHROPODS, 5),
    BLAST_PROTECTION(Enchantments.BLAST_PROTECTION, 2),
    DEPTH_STRIDER(Enchantments.DEPTH_STRIDER, 2),
    EFFICIENCY(Enchantments.EFFICIENCY, 10),
    FEATHER_FALL(Enchantments.FEATHER_FALLING, 5),
    FIRE_ASPECT(Enchantments.FIRE_ASPECT, 2),
    FIRE_PROTECTION(Enchantments.FIRE_PROTECTION, 5),
    FLAME(Enchantments.FIRE_ASPECT, 2),
    FORTUNE(Enchantments.FORTUNE, 2),
    INFINITY(Enchantments.INFINITY, 1),
    KNOCKBACK(Enchantments.KNOCKBACK, 5),
    LOOTING(Enchantments.LOOTING, 2),
    LUCK_OF_THE_SEA(Enchantments.LUCK_OF_THE_SEA, 2),
    LURE(Enchantments.LURE, 2),
    POWER(Enchantments.POWER, 10),
    PUNCH(Enchantments.PUNCH, 2),
    PROJECTILE_PROTECTION(Enchantments.PROJECTILE_PROTECTION, 5),
    PROTECTION(Enchantments.PROTECTION, 10),
    RESPIRATION(Enchantments.RESPIRATION, 2),
    SHARPNESS(Enchantments.SHARPNESS, 10),
    SILK_TOUCH(Enchantments.SILK_TOUCH, 1),
    SMITE(Enchantments.SMITE, 5),
    THORNS(Enchantments.THORNS, 1),
    UNBREAKING(Enchantments.UNBREAKING, 5);

    Enchantment enchant;
    int weight;

    EnchantEnums(Enchantment enchant, int weight)
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
