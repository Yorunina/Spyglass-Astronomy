package com.nettakrim.spyglass_astronomy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SpyglassAstronomy.MODID)
public class SpyglassAstronomy {
    public static final String MODID = "spyglass_astronomy";

    public SpyglassAstronomy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        SpyglassAstronomyClient.onClientSetup(event);
        MinecraftForge.EVENT_BUS.register(SpyglassAstronomyClient.class);
    }
}
