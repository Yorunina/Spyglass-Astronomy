package com.nettakrim.spyglass_astronomy.commands.admin_subcommands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nettakrim.spyglass_astronomy.SpyglassAstronomyClient;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class BypassCommand {
    public static LiteralCommandNode<CommandSourceStack> getCommandNode() {
        LiteralCommandNode<CommandSourceStack> bypassNode = Commands
            .literal("bypassknowledge")
            .executes(BypassCommand::bypassKnowledge)
            .build();

        return bypassNode;
    }

    public static int bypassKnowledge(CommandContext<CommandSourceStack> context) {
        if (SpyglassAstronomyClient.knowledge.bypassKnowledge()) {
            SpyglassAstronomyClient.say("commands.admin.bypass.on");
        } else {
            SpyglassAstronomyClient.say("commands.admin.bypass.off");
        }
        return 1;
    }
}
