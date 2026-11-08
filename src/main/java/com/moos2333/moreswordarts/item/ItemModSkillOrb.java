package com.moos2333.moreswordarts.item;

import dynamicswordskills.api.IMetadataSkillItem;
import dynamicswordskills.api.ItemGrantSkill;
import dynamicswordskills.api.SkillRegistry;
import dynamicswordskills.skills.SkillBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModSkillOrb extends ItemGrantSkill implements IMetadataSkillItem {

    public ItemModSkillOrb() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public SkillBase getSkillToGrant(ItemStack stack) {
        return this.getSkillFromDamage(stack.getMetadata());
    }

    @Override
    public SkillBase getSkillFromDamage(int damage) {
        return SkillRegistry.getSkillById(damage);
    }

    @Override
    public int getItemDamage(SkillBase skill) {
        return skill != null ? skill.getId() : -1;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        SkillBase skill = this.getSkillToGrant(stack);
        if (skill != null) {
            return new TextComponentTranslation("item.moreswordarts.skill_orb.name", skill.getDisplayName()).getUnformattedText();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (SkillBase skill : SkillRegistry.getValues()) {
                if (skill.getRegistryName() != null && skill.getRegistryName().getNamespace().equals("moreswordarts")) {
                    items.add(new ItemStack(this, 1, skill.getId()));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        for (SkillBase skill : SkillRegistry.getValues()) {
            if (skill.getRegistryName() == null || !skill.getRegistryName().getNamespace().equals("moreswordarts")) {
                continue;
            }
            String variant = "moreswordarts:skill_orb_" + skill.getRegistryName().getPath();
            ModelLoader.setCustomModelResourceLocation(
                    this,
                    skill.getId(),
                    new ModelResourceLocation(variant, "inventory")
            );
        }
    }
}