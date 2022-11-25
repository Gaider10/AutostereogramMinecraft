package me.andrew.eyething;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = EyeThingMod.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.Tooltip
    public float ppi = 92.0F;
    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.Tooltip
    public float eyeSepCm = 6.3F;
    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.Tooltip
    public float eyeDistCm = 60.0F;

    @ConfigEntry.Category("advanced")
    public float maxDepthFact = 3.0F;
    @ConfigEntry.Category("advanced")
    public float sepFact = 0.55F;
}
