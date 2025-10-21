package com.nettakrim.spyglass_astronomy.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nettakrim.spyglass_astronomy.SpaceRenderingManager;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class HideCommand implements Command<CommandSourceStack> {
    public static LiteralCommandNode<CommandSourceStack> getCommandNode() {
        LiteralCommandNode<CommandSourceStack> hideNode = Commands
            .literal("sga:hide")
            .executes(new HideCommand())
            .build();

        LiteralCommandNode<CommandSourceStack> constellationsHideNode = Commands
            .literal("constellations")
            .executes(HideCommand::hideConstellations)
            .build();

        LiteralCommandNode<CommandSourceStack> starsHideNode = Commands
            .literal("stars")
            .executes(HideCommand::hideStars)
            .build();

        LiteralCommandNode<CommandSourceStack> orbitingBodiesHideNode = Commands
            .literal("planets")
            .executes(HideCommand::hideOrbitingBodies)
            .build();

        LiteralCommandNode<CommandSourceStack> oldStarsHideNode = Commands
            .literal("vanillastars")
            .executes(HideCommand::hideOldStars)
            .build();

        LiteralCommandNode<CommandSourceStack> dayTimeHideNode = Commands
            .literal("daytime")
            .executes(HideCommand::hideDaytime)
            .build();

        hideNode.addChild(constellationsHideNode);
        hideNode.addChild(starsHideNode);
        hideNode.addChild(orbitingBodiesHideNode);
        hideNode.addChild(oldStarsHideNode);
        hideNode.addChild(dayTimeHideNode);

        return hideNode;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        boolean active = !(SpaceRenderingManager.constellationsVisible || SpaceRenderingManager.starsVisible || SpaceRenderingManager.orbitingBodiesVisible || SpaceRenderingManager.oldStarsVisible);
        SpaceRenderingManager.constellationsVisible = active;
        SpaceRenderingManager.starsVisible = active;
        SpaceRenderingManager.orbitingBodiesVisible = active;
        SpaceRenderingManager.oldStarsVisible = false;
        sayHideUpdate("all", active);
        return 1;
	}

    private static int hideConstellations(CommandContext<CommandSourceStack> context) {
        SpaceRenderingManager.constellationsVisible = !SpaceRenderingManager.constellationsVisible;
        sayHideUpdate("constellations", SpaceRenderingManager.constellationsVisible);
        return 1;
    }

    private static int hideStars(CommandContext<CommandSourceStack> context) {
        SpaceRenderingManager.starsVisible = !SpaceRenderingManager.starsVisible;
        sayHideUpdate("stars", SpaceRenderingManager.starsVisible);
        return 1;
    }

    private static int hideOrbitingBodies(CommandContext<CommandSourceStack> context) {
        SpaceRenderingManager.orbitingBodiesVisible = !SpaceRenderingManager.orbitingBodiesVisible;
        sayHideUpdate("planets", SpaceRenderingManager.orbitingBodiesVisible);
        return 1;
    }

    private static int hideOldStars(CommandContext<CommandSourceStack> context) {
        SpaceRenderingManager.oldStarsVisible = !SpaceRenderingManager.oldStarsVisible;
        sayHideUpdate("vanillastars", SpaceRenderingManager.oldStarsVisible);
        return 1;
    }

    private static int hideDaytime(CommandContext<CommandSourceStack> context) {
        SpaceRenderingManager.starsAlwaysVisible = !SpaceRenderingManager.starsAlwaysVisible;
        sayHideUpdate("daytime", SpaceRenderingManager.starsAlwaysVisible);
        return 1;
    }


    private static void sayHideUpdate(String base, boolean active) {
        SpyglassAstronomyClient.say("commands.hide."+base+(active ? ".show" :".hide"));
    }
}
