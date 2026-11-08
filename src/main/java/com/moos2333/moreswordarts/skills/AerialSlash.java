package com.moos2333.moreswordarts.skills;

import com.moos2333.moreswordarts.config.ModConfig;
import com.moos2333.moreswordarts.entity.EntityAerialSlashBeam;
import dynamicswordskills.api.SkillGroup;
import dynamicswordskills.entity.DSSPlayerInfo;
import dynamicswordskills.ref.ModSounds;
import dynamicswordskills.skills.SkillActive;
import dynamicswordskills.skills.Skills;
import dynamicswordskills.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class AerialSlash extends SkillActive {

    private int hoverTimer = 0;
    private int secondBeamTimer = 0;
    private boolean usedInAir = false;
    private static final ResourceLocation MAGIC_DEVICE_ID = new ResourceLocation("casualtinkering", "magicdevice");

    public AerialSlash(String translationKey) {
        super(translationKey);
    }

    private AerialSlash(AerialSlash skill) {
        super(skill);
        this.hoverTimer = skill.hoverTimer;
        this.secondBeamTimer = skill.secondBeamTimer;
        this.usedInAir = skill.usedInAir;
    }

    @Override
    public AerialSlash newInstance() {
        return new AerialSlash(this);
    }

    @Override
    public byte getMaxLevel() {
        return 10;
    }

    @Override
    public boolean displayInGroup(SkillGroup group) {
        return super.displayInGroup(group) || group == Skills.SWORD_GROUP;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean showIconIfUnknown(EntityPlayer player) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(List<String> desc, EntityPlayer player) {
        desc.add(new TextComponentTranslation("skill.dss.aerial_slash.tooltip").getUnformattedText());

        float multiplier = ModConfig.aerialSlash.baseMultiplier + ModConfig.aerialSlash.perLevelMultiplier * this.level;
        float flat = ModConfig.aerialSlash.baseFlatDamage + ModConfig.aerialSlash.perLevelFlatDamage * this.level;
        desc.add(new TextComponentTranslation("skill.dss.aerial_slash.info.damage", (int)(multiplier * 100), (int)flat).getUnformattedText());
        desc.add(this.getExhaustionDisplay(this.getExhaustion()));
    }

    @Override
    public boolean isActive() {
        return this.hoverTimer > 0;
    }

    @Override
    protected float getExhaustion() {
        return ModConfig.aerialSlash.exhaustion;
    }

    @Override
    public boolean canUse(EntityPlayer player) {
        return super.canUse(player)
                && !this.isActive()
                && player.getCooledAttackStrength(0.5F) >= 0.9F
                && PlayerUtils.isSwordOrProvider(player.getHeldItemMainhand(), this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canExecute(EntityPlayer player) {
        return !player.onGround && player.isSneaking() && !this.usedInAir && this.canUse(player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isKeyListener(Minecraft mc, KeyBinding key, boolean isLockedOn) {
        return isLockedOn && key == mc.gameSettings.keyBindUseItem;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean keyPressed(Minecraft mc, KeyBinding key, EntityPlayer player) {
        if (this.canExecute(player)) {
            return this.activate(player);
        }
        return false;
    }

    @Override
    protected boolean onActivated(World world, EntityPlayer player) {
        this.hoverTimer = ModConfig.aerialSlash.hoverDuration;
        this.secondBeamTimer = 0;
        this.usedInAir = true;

        player.motionY = 0;
        player.fallDistance = 0;

        if (!world.isRemote) {
            float multiplier = ModConfig.aerialSlash.baseMultiplier + ModConfig.aerialSlash.perLevelMultiplier * this.level;
            float flat = ModConfig.aerialSlash.baseFlatDamage + ModConfig.aerialSlash.perLevelFlatDamage * this.level;
            float playerDmg = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            float finalDamage = (playerDmg * multiplier) + flat;

            ItemStack offhand = player.getHeldItemOffhand();
            boolean isMagic = !offhand.isEmpty() && offhand.getItem().getRegistryName() != null && offhand.getItem().getRegistryName().equals(MAGIC_DEVICE_ID);
            boolean isShield = !offhand.isEmpty() && offhand.getItem().isShield(offhand, player);
            boolean isDualSword = PlayerUtils.isSword(offhand);

            this.spawnBeam(world, player, finalDamage, isMagic, 0.0F);

            if (isDualSword) {
                this.secondBeamTimer = 3;
            } else if (isShield) {
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, this.hoverTimer, 1, false, false));
            }
        }

        this.triggerVisualFeedback(world, player);
        return true;
    }

    @Override
    protected void onDeactivated(World world, EntityPlayer player) {
        this.hoverTimer = 0;
        this.secondBeamTimer = 0;
        player.jumpMovementFactor = 0.02F;
    }

    @Override
    public void onUpdate(EntityPlayer player) {
        if (player.onGround) {
            this.usedInAir = false;
        }

        if (this.isActive()) {
            --this.hoverTimer;
            player.motionY = 0;
            player.fallDistance = 0;
            player.jumpMovementFactor = ModConfig.aerialSlash.hoverMoveFactor;

            if (this.secondBeamTimer > 0) {
                --this.secondBeamTimer;
                if (this.secondBeamTimer == 0) {
                    World world = player.world;
                    if (!world.isRemote) {
                        float multiplier = ModConfig.aerialSlash.baseMultiplier + ModConfig.aerialSlash.perLevelMultiplier * this.level;
                        float flat = ModConfig.aerialSlash.baseFlatDamage + ModConfig.aerialSlash.perLevelFlatDamage * this.level;
                        float playerDmg = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                        float finalDamage = (playerDmg * multiplier) + flat;

                        this.spawnBeam(world, player, finalDamage, false, 0.0F);
                    }
                    this.triggerVisualFeedback(world, player);
                }
            }

            if (this.hoverTimer <= 0) {
                this.deactivate(player);
            }
        }
    }

    private void triggerVisualFeedback(World world, EntityPlayer player) {
        if (!world.isRemote) {
            PlayerUtils.playSoundAtEntity(world, player, ModSounds.WHOOSH, SoundCategory.PLAYERS, 0.6F, 0.8F);
            DSSPlayerInfo.get(player).setAttackCooldown(10);
        }
        player.swingArm(EnumHand.MAIN_HAND);
        player.resetCooldown();
    }

    private void spawnBeam(World world, EntityPlayer player, float damage, boolean isMagic, float yawOffset) {
        EntityAerialSlashBeam beam = new EntityAerialSlashBeam(world, player, damage, isMagic);

        double yaw = Math.toRadians(player.rotationYaw + yawOffset);
        double pitch = Math.toRadians(player.rotationPitch);
        double offset = 1.0D;

        beam.setPosition(
                player.posX - Math.sin(yaw) * Math.cos(pitch) * offset,
                player.posY + player.getEyeHeight() - Math.sin(pitch) * offset,
                player.posZ + Math.cos(yaw) * Math.cos(pitch) * offset
        );

        beam.shoot(player, player.rotationPitch, player.rotationYaw + yawOffset, 0.0F, ModConfig.aerialSlash.beamSpeed, 1.0F);
        world.spawnEntity(beam);
    }
}