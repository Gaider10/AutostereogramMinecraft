package me.andrew.eyething.mixin;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import me.andrew.eyething.EyeThingMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private MinecraftClient client;

    private ShaderEffect eyeThingShader = null;

    private void loadEyeThingShader() {
        if (this.eyeThingShader != null) {
            this.eyeThingShader.close();
        }

        Identifier identifier = EyeThingMod.SHADER;
        try {
            this.eyeThingShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier);
            this.eyeThingShader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", identifier, iOException);
            this.eyeThingShader = null;
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to parse shader: {}", identifier, jsonSyntaxException);
            this.eyeThingShader = null;
        }
    }

    @Inject(
            method = "reload",
            at = @At("HEAD")
    )
    private void inject_reload(CallbackInfo ci) {
        this.loadEyeThingShader();
    }

    @Inject(
            method = "onResized",
            at = @At("HEAD")
    )
    private void inject_onResized(int width, int height, CallbackInfo ci) {
        if (this.eyeThingShader != null) {
            this.eyeThingShader.setupDimensions(width, height);
        }
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_renderWord(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        if (EyeThingMod.enabled && this.eyeThingShader != null) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.enableTexture();
            RenderSystem.resetTextureMatrix();
            this.eyeThingShader.render(tickDelta);

            this.client.getFramebuffer().beginWrite(true);
        }
    }
}
