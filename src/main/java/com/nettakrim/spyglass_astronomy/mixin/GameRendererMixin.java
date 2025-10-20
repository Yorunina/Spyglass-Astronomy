package com.nettakrim.spyglass_astronomy.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.player.AbstractClientPlayer;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//https://github.com/Nova-Committee/AbsolutelyNotAZoomMod/blob/fabric/universal/src/main/java/committee/nova/anazm/mixin/GameRendererMixin.java

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private float fov;
    @Shadow
    private float oldFov;
    @Inject(
        method = "tickFov",
        at = @At("HEAD"),
        cancellable = true
    )
    private void updateFovMultiplier(CallbackInfo ci) {
        if (SpyglassAstronomyClient.zoom == 0) return;
        LocalPlayer player = SpyglassAstronomyClient.client.player;
        if (player == null) return;
        boolean spyGlassing = player.isUsingItem() && player.getUseItem().is(Items.SPYGLASS);
        if (!(spyGlassing && SpyglassAstronomyClient.client.options.getCameraType().isFirstPerson())) SpyglassAstronomyClient.zoom = 0;

        float f = 1.0f;
        if (SpyglassAstronomyClient.client.getCameraEntity() instanceof AbstractClientPlayer abstractClientPlayerEntity) {
            f = abstractClientPlayerEntity.getFieldOfViewModifier();
        }
        //1.25892541179 would be more accurate, but it doesnt really matter
        f *= (float)Math.pow(1.25d, SpyglassAstronomyClient.zoom);
        this.oldFov = this.fov;
        this.fov += (f - this.fov) * 0.5f;
        if (this.fov > 1.5f) {
            this.fov = 1.5f;
        }
        if (this.fov < 0.01f) {
            this.fov = 0.01f;
        }

        ci.cancel();
    }
}
