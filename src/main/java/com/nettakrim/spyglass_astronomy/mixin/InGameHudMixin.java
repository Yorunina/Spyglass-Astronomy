package com.nettakrim.spyglass_astronomy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {
    @Unique
    private static final ResourceLocation CONSTELLATION_SPYGLASS_SCOPE = new ResourceLocation(SpyglassAstronomyClient.MODID,"textures/constellation_spyglass_scope.png");
    @Unique
    private static final ResourceLocation STAR_SPYGLASS_SCOPE = new ResourceLocation(SpyglassAstronomyClient.MODID,"textures/star_spyglass_scope.png");

    @Inject(method = "renderSpyglassOverlay",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIIII)V",ordinal = 0))
    public void renderSpyglassMode(GuiGraphics context, float scale, CallbackInfo ci, @Local(ordinal = 2) int k, @Local(ordinal = 3) int l, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j){
        if (SpyglassAstronomyClient.editMode != 0) {
            context.blit(SpyglassAstronomyClient.editMode == 1 ? CONSTELLATION_SPYGLASS_SCOPE : STAR_SPYGLASS_SCOPE, k, l, -90, 0.0F, 0.0F, i, j, i, j);
        }
    }
}
