package com.nettakrim.spyglass_astronomy.mixin;

import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomy;
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
    private static final ResourceLocation CONSTELLATION_SPYGLASS_SCOPE = ResourceLocation.fromNamespaceAndPath(SpyglassAstronomy.MODID,"textures/constellation_spyglass_scope.png");
    @Unique
    private static final ResourceLocation STAR_SPYGLASS_SCOPE = ResourceLocation.fromNamespaceAndPath(SpyglassAstronomy.MODID,"textures/star_spyglass_scope.png");

    @Inject(method = "renderSpyglassOverlay",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIIII)V",ordinal = 0))
    public void renderSpyglassMode(GuiGraphics context, float scale, CallbackInfo ci){
        if (SpyglassAstronomyClient.editMode != 0) {
            float screenWidth = (float)context.guiWidth();
            float screenHeight = (float)context.guiHeight();
            float f2 = Math.min(screenWidth, screenHeight);
            float f3 = Math.min(screenWidth / f2, screenHeight / f2) * scale;
            int i = (int)(f2 * f3);
            int j = (int)(f2 * f3);
            int k = (int)((screenWidth - (float)i) / 2.0F);
            int l = (int)((screenHeight - (float)j) / 2.0F);
            context.blit(SpyglassAstronomyClient.editMode == 1 ? CONSTELLATION_SPYGLASS_SCOPE : STAR_SPYGLASS_SCOPE, k, l, -90, 0.0F, 0.0F, i, j, i, j);
        }
    }
}
