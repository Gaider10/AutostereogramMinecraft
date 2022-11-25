package me.andrew.eyething;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class EyeThingMod implements ModInitializer {
    public static final String MOD_ID = "eyething";

    public static final Identifier SHADER = new Identifier("shaders/post/etm_main.json");;

    public static boolean enabled = false;
    public static int maxDistance = 64;

    @Override
    public void onInitialize() {
        ModKeyBindings.init();

        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
    }
}
