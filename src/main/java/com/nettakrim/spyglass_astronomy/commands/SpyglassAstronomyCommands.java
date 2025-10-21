package com.nettakrim.spyglass_astronomy.commands;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.RootCommandNode;
import com.nettakrim.spyglass_astronomy.Constellation;
import com.nettakrim.spyglass_astronomy.OrbitingBody;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomy;
import com.nettakrim.spyglass_astronomy.Star;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpyglassAstronomy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpyglassAstronomyCommands {
    public static final SuggestionProvider<CommandSourceStack> constellations = (context, builder) -> {
        for (Constellation constellation : SpyglassAstronomyClient.constellations) {
            builder.suggest(constellation.name);
        }
        return CompletableFuture.completedFuture(builder.build());
    };

    public static final SuggestionProvider<CommandSourceStack> stars = (context, builder) -> {
        for (Star star : SpyglassAstronomyClient.stars) {
            if (!star.isUnnamed()) builder.suggest(star.name);
        }
        return CompletableFuture.completedFuture(builder.build());
    };

    public static final SuggestionProvider<CommandSourceStack> orbitingBodies = (context, builder) -> {
        for (OrbitingBody orbitingBody : SpyglassAstronomyClient.orbitingBodies) {
            if (!orbitingBody.isUnnamed()) builder.suggest(orbitingBody.name);
        }
        return CompletableFuture.completedFuture(builder.build());
    };

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        RootCommandNode<CommandSourceStack> root = event.getDispatcher().getRoot();

        root.addChild(AdminCommand.getCommandNode());
        root.addChild(HideCommand.getCommandNode());
        root.addChild(InfoCommand.getCommandNode());
        root.addChild(NameCommand.getCommandNode());
        root.addChild(SelectCommand.getCommandNode());
        root.addChild(ShareCommand.getCommandNode());
    }

    public static Constellation getConstellation(CommandContext<CommandSourceStack> context) {
        String name = getMessageText(context);
        for (Constellation constellation : SpyglassAstronomyClient.constellations) {
            if (constellation.name.equals(name)) {
                return constellation;
            }
        }
        SpyglassAstronomyClient.say("commands.find.constellation.fail", name);
        return null;
    }

    public static Star getStar(CommandContext<CommandSourceStack> context) {
        String name = getMessageText(context);
        for (Star star : SpyglassAstronomyClient.stars) {
            if (star.name != null && star.name.equals(name)) {
                return star;
            }
        }
        SpyglassAstronomyClient.say("commands.find.star.fail", name);
        return null;        
    }

    public static OrbitingBody getOrbitingBody(CommandContext<CommandSourceStack> context) {
        String name = getMessageText(context);
        for (OrbitingBody orbitingBody : SpyglassAstronomyClient.orbitingBodies) {
            if (orbitingBody.name != null && orbitingBody.name.equals(name)) {
                return orbitingBody;
            }
        }
        SpyglassAstronomyClient.say("commands.find.planet.fail", name);
        return null;        
    }

    public static String getMessageText(CommandContext<CommandSourceStack> context) {
        return getMessageText(context, "name");
    }

    public static String getMessageText(CommandContext<CommandSourceStack> context, String name) {
        try {
            return MessageArgument.getMessage(context, name).getString();
        } catch (Exception e) {
            return null;
        }
    }

    public static Component getClickHere(String actionKey, String command, boolean run, Object... formatting) {
        return Component.translatable(SpyglassAstronomy.MODID+".commands.share.click").setStyle(Style.EMPTY
        .withClickEvent(
            new ClickEvent(run ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND, command)
        )
        .withColor(SpyglassAstronomyClient.buttonTextColor))
        .append(Component.translatable(SpyglassAstronomy.MODID+"."+actionKey, formatting).setStyle(Style.EMPTY.withColor(SpyglassAstronomyClient.textColor)));
    }
}
