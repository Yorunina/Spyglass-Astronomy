package com.nettakrim.spyglass_astronomy.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MouseHandler.class)
public class MouseMixin {

    @Inject(at = @At("TAIL"), method = "turnPlayer")
    public void updateMouse(CallbackInfo ci) {
        if (SpyglassAstronomyClient.isDrawingConstellation) {
            SpyglassAstronomyClient.updateDrawingConstellation();
        }
    }
    @WrapWithCondition(
            method = "onScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V"
            )
    )
    private boolean onMouseScroll(Inventory inventory, double scroll){
        LocalPlayer player = SpyglassAstronomyClient.client.player;
        if (player == null) return true;
        boolean spyGlassing = player.isUsingItem() && player.getUseItem().is(Items.SPYGLASS);
        if(spyGlassing){
            SpyglassAstronomyClient.zoom = Mth.clamp(SpyglassAstronomyClient.zoom - (float)scroll, -10, 10);
            return false;
        }
        return true;
    }

    @ModifyArgs(
            method = "turnPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
            )
    )
    private void onTurn(Args args) {
        LocalPlayer player = SpyglassAstronomyClient.client.player;
        if (player != null && player.isUsingItem() && player.getUseItem().is(Items.SPYGLASS) && SpyglassAstronomyClient.client.options.getCameraType().isFirstPerson()) {
            double sensitivityScale = Math.pow(1.25d, SpyglassAstronomyClient.zoom);
            float cosAngle = Mth.cos(player.getXRot() / 180 * Mth.PI);
            if (cosAngle < 0) cosAngle *= -1;
            cosAngle = Math.max(cosAngle, (Math.max(SpyglassAstronomyClient.zoom,0)+1)/11f);
            double angleScale = 1/cosAngle;

            args.set(0, (double)args.get(0) * sensitivityScale * angleScale);
            args.set(1, (double)args.get(1) * sensitivityScale);
        }
    }
}
