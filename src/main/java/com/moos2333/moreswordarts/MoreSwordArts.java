package com.moos2333.moreswordarts;

import com.moos2333.moreswordarts.client.RenderAerialSlashBeam;
import com.moos2333.moreswordarts.entity.EntityAerialSlashBeam;
import com.moos2333.moreswordarts.item.ItemModSkillOrb;
import com.moos2333.moreswordarts.skills.AerialSlash;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        dependencies = "required-after:dynamicswordskills;required-after:swordskillsapi"
)
public class MoreSwordArts {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    public static AerialSlash aerialSlash;
    public static Item itemModSkillOrb;
    public static CreativeTabs tabMoreSwordArts;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);

        aerialSlash = new AerialSlash("aerial_slash");
        aerialSlash.setIconLocation("moreswordarts:textures/skills/aerial_slash.png").register("aerial_slash");

        itemModSkillOrb = new ItemModSkillOrb();
        itemModSkillOrb.setRegistryName(Tags.MOD_ID, "skill_orb");
        itemModSkillOrb.setTranslationKey(Tags.MOD_ID + ".skill_orb");

        tabMoreSwordArts = new CreativeTabs(Tags.MOD_ID) {
            @SideOnly(Side.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(itemModSkillOrb, 1, MoreSwordArts.aerialSlash.getId());
            }
        };

        itemModSkillOrb.setCreativeTab(tabMoreSwordArts);

        if (event.getSide().isClient()) {
            RenderingRegistry.registerEntityRenderingHandler(
                    EntityAerialSlashBeam.class,
                    new RenderAerialSlashBeam.Factory()
            );
        }
    }

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    public static class RegistryHandler {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().register(itemModSkillOrb);
        }

        @SubscribeEvent
        public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
            event.getRegistry().register(
                    EntityEntryBuilder.create()
                            .entity(EntityAerialSlashBeam.class)
                            .id(new ResourceLocation(Tags.MOD_ID, "aerial_slash_beam"), 0)
                            .name(Tags.MOD_ID + ".aerial_slash_beam")
                            .tracker(64, 10, true)
                            .build()
            );
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = Tags.MOD_ID)
    public static class ClientRegistryHandler {
        @SubscribeEvent
        public static void onModelRegistry(ModelRegistryEvent event) {
            ((ItemModSkillOrb) itemModSkillOrb).registerModels();
        }
    }
}