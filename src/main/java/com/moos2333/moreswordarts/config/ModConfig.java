package com.moos2333.moreswordarts.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = "moreswordarts", category = "aerial_slash")
public class ModConfig {

    public static AerialSlashConfig aerialSlash = new AerialSlashConfig();

    public static class AerialSlashConfig {
        public float baseMultiplier = 0.0F;
        public float perLevelMultiplier = 0.5F;
        public float baseFlatDamage = 0.0F;
        public float perLevelFlatDamage = 3.0F;
        public float exhaustion = 4.0F;
        public int hoverDuration = 20;
        public float beamSpeed = 1.5F;

        @Config.Comment("Air movement factor during hover. Vanilla default is 0.02F. Try 0.1F for fast, 0.15F for extremely fast.")
        public float hoverMoveFactor = 0.1F;
    }

    @Mod.EventBusSubscriber(modid = "moreswordarts")
    public static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals("moreswordarts")) {
                ConfigManager.sync("moreswordarts", Config.Type.INSTANCE);
            }
        }
    }
}