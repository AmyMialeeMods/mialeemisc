package xyz.amymialee.mialeemisc.entities;

import net.minecraft.entity.LivingEntity;

public interface IPlayerTargeting {
    LivingEntity mialeeMisc$getLastTarget();
    void mialeeMisc$setLastTarget(LivingEntity target);
}