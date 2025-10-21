package com.nettakrim.spyglass_astronomy.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nettakrim.spyglass_astronomy.Constellation;
import com.nettakrim.spyglass_astronomy.OrbitingBody;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import com.nettakrim.spyglass_astronomy.Star;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;

public class SelectCommand {
    public static LiteralCommandNode<CommandSourceStack> getCommandNode() {
        LiteralCommandNode<CommandSourceStack> selectNode = Commands
            .literal("sga:select")
            .build();

        LiteralCommandNode<CommandSourceStack> constellationSelectNode = Commands
            .literal("constellation")
            .then(
                Commands.argument("name", MessageArgument.message())
                    .suggests(SpyglassAstronomyCommands.constellations)
                    .executes(SelectCommand::selectConstellation)
            )
            .build();

        LiteralCommandNode<CommandSourceStack> starSelectNode = Commands
            .literal("star")
            .then(
                Commands.argument("name", MessageArgument.message())
                    .suggests(SpyglassAstronomyCommands.stars)
                    .executes(SelectCommand::selectStar)
            )
            .build();

        LiteralCommandNode<CommandSourceStack> orbitingBodySelectNode = Commands
            .literal("planet")
            .then(
                Commands.argument("name", MessageArgument.message())
                    .suggests(SpyglassAstronomyCommands.orbitingBodies)
                    .executes(SelectCommand::selectOrbitingBody)
            )
            .build();

        selectNode.addChild(constellationSelectNode);
        selectNode.addChild(starSelectNode);
        selectNode.addChild(orbitingBodySelectNode);
        return selectNode;
    }

    private static int selectConstellation(CommandContext<CommandSourceStack> context) {
        Constellation constellation = SpyglassAstronomyCommands.getConstellation(context);
        if (constellation == null) {
            return -1;
        }
        if (!SpyglassAstronomyClient.isHoldingSpyglass()) {
            SpyglassAstronomyClient.say("commands.select.constellation.fail");
            return -1;
        }
        constellation.select();
        SpyglassAstronomyClient.say("commands.select.constellation", constellation.name);
        return 1;
    }

    private static int selectStar(CommandContext<CommandSourceStack> context) {
        Star star = SpyglassAstronomyCommands.getStar(context);
        if (star == null) {
            return -1;
        }
        if (!SpyglassAstronomyClient.isHoldingSpyglass()) {
            SpyglassAstronomyClient.say("commands.select.star.fail");
            return -1;
        }
        star.select();
        String starName = (star.isUnnamed() ? "Unnamed" : star.name);
        SpyglassAstronomyClient.say("commands.select.star", starName);
        return 1;
    }

    private static int selectOrbitingBody(CommandContext<CommandSourceStack> context) {
        OrbitingBody orbitingBody = SpyglassAstronomyCommands.getOrbitingBody(context);
        if (orbitingBody == null) {
            return -1;
        }
        if (!SpyglassAstronomyClient.isHoldingSpyglass()) {
            SpyglassAstronomyClient.say("commands.select."+(orbitingBody.isPlanet ? "planet" : "comet")+".fail");
            return -1;
        }
        orbitingBody.select();
        String orbitingBodyName = (orbitingBody.isUnnamed() ? "Unnamed" : orbitingBody.name);
        SpyglassAstronomyClient.say("commands.select."+(orbitingBody.isPlanet ? "planet" : "comet"), orbitingBodyName);
        return 1;
    }
}
