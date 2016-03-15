package com.mongoosecountry.pete800.npc;

import com.mongoosecountry.pete800.EconomyNPC;
import com.mongoosecountry.pete800.util.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public abstract class AbstractNPC
{
    private Career career;
    private Location<World> location;
    final NPCType type;
    final String name;
    private Villager villager;

    @SuppressWarnings("WeakerAccess")
    public AbstractNPC(Location<World> location, NPCType type, String name)
    {
        this.location = location;
        this.type = type;
        this.name = name;
        this.career = Careers.FARMER;
        if (Sponge.getServer().getOnlinePlayers().size() > 0)
            respawnNPC();
    }

    @SuppressWarnings("WeakerAccess")
    public AbstractNPC(String name, ConfigurationNode cn)
    {
        this.type = NPCType.fromName(cn.getString());
        this.location = Utils.deserialize(cn);
        this.name = name;
        this.career = Sponge.getRegistry().getType(Career.class, cn.getNode("career").getString("farmer").toLowerCase()).get();
    }

    public void respawnNPC()
    {
        despawnNPC();
        villager = (Villager) location.getExtent().createEntity(EntityTypes.VILLAGER, location.getPosition()).get();
        villager.offer(Keys.DISPLAY_NAME, Text.of(name));
        villager.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        villager.offer(Keys.PERSISTS, true);
        villager.offer(Keys.CAREER, career);
        //TODO create a listener that always sets an NPC's velocity to 0
        //villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 6));
        location.getExtent().spawnEntity(villager, Cause.of(NamedCause.source(EconomyNPC.instance())));
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
            return ((LiteralText) villager.get(Keys.DISPLAY_NAME).get()).getContent();

        return name;
    }

    public Career getCareer()
    {
        if (villager != null)
            villager.get(Keys.CAREER).get();

        return career;
    }

    public void setCareer(Career career)
    {
        this.career = career;
        respawnNPC();
    }

    public void setLocation(Location<World> location)
    {
        if (villager != null)
            villager.setLocation(location);

        this.location = location;
    }

    public Location<World> getLocation()
    {
        if (villager != null)
            villager.getLocation();

        return location;
    }

    public ConfigurationNode save()
    {
        ConfigurationNode cn = SimpleConfigurationNode.root();
        cn.getNode("type").setValue(type.toString());
        cn.getNode("career").setValue(career.getId());
        cn.getNode("world").setValue(location.getExtent().getName());
        cn.getNode("x").setValue(location.getX());
        cn.getNode("y").setValue(location.getY());
        cn.getNode("z").setValue(location.getZ());
        return cn;
    }

    public enum NPCType
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
