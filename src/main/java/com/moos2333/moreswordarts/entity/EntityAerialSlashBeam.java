package com.moos2333.moreswordarts.entity;

import dynamicswordskills.util.DamageUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityAerialSlashBeam extends EntityThrowable {
    private float damage;
    private boolean isMagic;
    private int lifespan;

    public EntityAerialSlashBeam(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityAerialSlashBeam(World world, EntityPlayer player, float damage, boolean isMagic) {
        super(world, player);
        this.damage = damage;
        this.isMagic = isMagic;
        this.lifespan = isMagic ? 60 : 40;
        this.setSize(0.5F, 0.5F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted > this.lifespan) {
            this.setDead();
        }
    }

    @Override
    public float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) result.entityHit;
                EntityPlayer owner = (EntityPlayer) this.getThrower();

                if (target != owner && owner != null) {
                    target.hurtResistantTime = 0;
                    target.hurtTime = 0;

                    DamageSource source = DamageUtils.causeIndirectComboDamage(this, owner);
                    if (this.isMagic) {
                        source.setMagicDamage();
                    }

                    target.attackEntityFrom(source, this.damage);
                }
            }
            this.setDead();
        }
    }
}