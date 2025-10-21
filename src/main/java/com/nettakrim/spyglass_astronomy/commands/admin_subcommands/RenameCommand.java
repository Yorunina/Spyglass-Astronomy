package com.nettakrim.spyglass_astronomy.commands.admin_subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nettakrim.spyglass_astronomy.commands.NameCommand;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;

public class RenameCommand {
    public static LiteralCommandNode<CommandSourceStack> getCommandNode() {
        LiteralCommandNode<CommandSourceStack> renameNode = Commands
            .literal("rename")
            .build();

        LiteralCommandNode<CommandSourceStack> constellationNameNode = Commands
            .literal("constellation")
            .then(
                Commands.argument("index", IntegerArgumentType.integer(0))
                    .then(
                        Commands.argument("name", MessageArgument.message())
                            .executes(NameCommand::nameConstellation)
                    )
            )
            .build();

        LiteralCommandNode<CommandSourceStack> starNameNode = Commands
            .literal("star")
            .then(
                Commands.argument("index", IntegerArgumentType.integer(0))
                    .then(
                        Commands.argument("name", MessageArgument.message())
                            .executes(NameCommand::nameStar)
                    )
            )
            .build();

        LiteralCommandNode<CommandSourceStack> orbitingBodyNameNode = Commands
            .literal("planet")
            .then(
                Commands.argument("index", IntegerArgumentType.integer(0))
                    .then(
                        Commands.argument("name", MessageArgument.message())
                            .executes(NameCommand::nameOrbitingBody)
                    )
            )
            .build();

        renameNode.addChild(constellationNameNode);
        renameNode.addChild(starNameNode);
        renameNode.addChild(orbitingBodyNameNode);
        return renameNode;
    }
}
