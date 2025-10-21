package com.nettakrim.spyglass_astronomy.commands.admin_subcommands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nettakrim.spyglass_astronomy.SpaceDataManager;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class SeedCommand {
    public static LiteralCommandNode<CommandSourceStack> getCommandNode() {
        LiteralCommandNode<CommandSourceStack> seedNode = Commands
            .literal("seed")
            .build();


        LiteralCommandNode<CommandSourceStack> starNode = Commands
            .literal("star")
            .build();

        LiteralCommandNode<CommandSourceStack> queryStarSeedNode = Commands
            .literal("query")
            .executes(SeedCommand::queryStarSeed)
            .build();

        LiteralCommandNode<CommandSourceStack> resetStarSeedNode = Commands
            .literal("reset")
            .executes(SeedCommand::resetStarSeed)
            .build();

        LiteralCommandNode<CommandSourceStack> setStarSeedNode = Commands
            .literal("set")
            .then(
                Commands.argument("seed", LongArgumentType.longArg())
                    .executes(SeedCommand::setStarSeed)
            )
            .build();


        LiteralCommandNode<CommandSourceStack> planetNode = Commands
            .literal("planet")
            .build();

        LiteralCommandNode<CommandSourceStack> queryPlanetSeedNode = Commands
            .literal("query")
            .executes(SeedCommand::queryPlanetSeed)
            .build();

        LiteralCommandNode<CommandSourceStack> resetPlanetSeedNode = Commands
            .literal("reset")
            .executes(SeedCommand::resetPlanetSeed)
            .build();

        LiteralCommandNode<CommandSourceStack> setPlanetSeedNode = Commands
            .literal("set")
            .then(
                Commands.argument("seed", LongArgumentType.longArg())
                    .executes(SeedCommand::setPlanetSeed)
            )
            .build();

        starNode.addChild(queryStarSeedNode);
        starNode.addChild(resetStarSeedNode);
        starNode.addChild(setStarSeedNode);
        seedNode.addChild(starNode);

        planetNode.addChild(queryPlanetSeedNode);
        planetNode.addChild(resetPlanetSeedNode);
        planetNode.addChild(setPlanetSeedNode);
        seedNode.addChild(planetNode);
        return seedNode;
    }

    private static int setStarSeed(CommandContext<CommandSourceStack> context) {
        return setStarSeed(LongArgumentType.getLong(context, "seed"));
    }

    private static int resetStarSeed(CommandContext<CommandSourceStack> context) {
        return setStarSeed(SpyglassAstronomyClient.world.getBiomeManager().biomeZoomSeed);
    }

    private static int queryStarSeed(CommandContext<CommandSourceStack> context) {
        SpyglassAstronomyClient.say("commands.admin.seed.star.query", Long.toString(SpyglassAstronomyClient.spaceDataManager.getStarSeed()));
        return 1;
    }

    private static int setStarSeed(long seed) {
        SpyglassAstronomyClient.say("commands.admin.seed.star.set", Long.toString(seed), Long.toString(SpyglassAstronomyClient.spaceDataManager.getStarSeed()));
        SpyglassAstronomyClient.spaceDataManager.setStarSeed(seed);
        SpyglassAstronomyClient.generateStars(null, true, true);
        StarCountCommand.invalidatedConstellations.clear();
        SpaceDataManager.makeChange();
        return 1;
    }



    private static int setPlanetSeed(CommandContext<CommandSourceStack> context) {
        return setPlanetSeed(LongArgumentType.getLong(context, "seed"));
    }

    private static int resetPlanetSeed(CommandContext<CommandSourceStack> context) {
        return setPlanetSeed(SpyglassAstronomyClient.world.getBiomeManager().biomeZoomSeed);
    }


    private static int queryPlanetSeed(CommandContext<CommandSourceStack> context) {
        SpyglassAstronomyClient.say("commands.admin.seed.planet.query", Long.toString(SpyglassAstronomyClient.spaceDataManager.getPlanetSeed()));
        return 1;
    }

    private static int setPlanetSeed(long seed) {
        SpyglassAstronomyClient.say("commands.admin.seed.planet.set", Long.toString(seed), Long.toString(SpyglassAstronomyClient.spaceDataManager.getPlanetSeed()));
        SpyglassAstronomyClient.spaceDataManager.setPlanetSeed(seed);
        SpyglassAstronomyClient.generatePlanets(null, true);
        SpaceDataManager.makeChange();
        return 1;
    }
}
