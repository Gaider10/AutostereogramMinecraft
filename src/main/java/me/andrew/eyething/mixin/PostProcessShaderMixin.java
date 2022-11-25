package me.andrew.eyething.mixin;

import me.andrew.eyething.EyeThingMod;
import me.andrew.eyething.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gl.JsonEffectGlShader;
import net.minecraft.client.gl.PostProcessShader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PostProcessShader.class)
public abstract class PostProcessShaderMixin {
    @Shadow @Final private JsonEffectGlShader program;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/Uniform;set(F)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_render(CallbackInfo ci) {
        if (EyeThingMod.enabled) {
            this.program.getUniformByNameOrDummy("ETM_MaxDist").set((float) EyeThingMod.maxDistance);

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

            float ppcm = config.ppi / 2.54F;
            float sepFact = config.sepFact;
            float eyeSep = config.eyeSepCm * ppcm;
            float eyeDist = config.eyeDistCm * ppcm;
            float maxDepth = eyeDist * config.maxDepthFact;
            float minDepth = eyeDist * (sepFact * maxDepth / (maxDepth + eyeDist)) / (1.0F - sepFact * maxDepth / (maxDepth + eyeDist));
            float maxSep = eyeSep * maxDepth / (maxDepth + eyeDist);
            float minSep = eyeSep * minDepth / (minDepth + eyeDist);

            this.program.getUniformByNameOrDummy("ETM_EyeSep").set(eyeSep);
            this.program.getUniformByNameOrDummy("ETM_EyeDist").set(eyeDist);
            this.program.getUniformByNameOrDummy("ETM_MinDepth").set(minDepth);
            this.program.getUniformByNameOrDummy("ETM_MaxDepth").set(maxDepth);
            this.program.getUniformByNameOrDummy("ETM_MinSep").set(minSep);
            this.program.getUniformByNameOrDummy("ETM_MaxSep").set(maxSep);
        }
    }
}
