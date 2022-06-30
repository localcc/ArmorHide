package com.localcc.armorhide.command;

import com.localcc.armorhide.ArmorHideNetwork;
import com.localcc.armorhide.Mod;
import com.localcc.armorhide.ServerMod;
import com.localcc.armorhide.ServerPlayerExt;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public class ArmorHideCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("armorhide").then(Commands.literal("hide").then(Commands.argument("slot", StringArgumentType.string()).executes(ctx -> {
                    String slot = ctx.getArgument("slot", String.class);
                    CommandSourceStack source = ctx.getSource();
                    ServerPlayer player = source.getPlayerOrException();
                    CompoundTag tag = ServerMod.PLAYER_DATA.getCompound(player.getStringUUID());
                    if(tag == null) {
                        tag = new CompoundTag();
                    }
                    tag.putBoolean(slot, true);
                    ServerMod.PLAYER_DATA.remove(player.getStringUUID());
                    ServerMod.PLAYER_DATA.put(player.getStringUUID(), tag);

                    var buf = PacketByteBufs.create();
                    buf.writeNbt(tag);
                    ServerPlayNetworking.send(player, ArmorHideNetwork.SETTINGS_PACKET, buf);

                    var message = new PlayerChatMessage(Component.literal("Reconnect to apply changes"), MessageSignature.unsigned(), Optional.empty());
                    player.sendChatMessage(message, ChatSender.system(Component.literal("server")), ChatType.SYSTEM);
                    return 0;
                })))
                .then(Commands.literal("show").then(Commands.argument("slot", StringArgumentType.string()).executes(ctx -> {
                    String slot = ctx.getArgument("slot", String.class);
                    CommandSourceStack source = ctx.getSource();
                    ServerPlayer player = source.getPlayerOrException();
                    CompoundTag tag = ServerMod.PLAYER_DATA.getCompound(player.getStringUUID());
                    if(tag != null) {
                        tag.remove(slot);
                        ServerMod.PLAYER_DATA.remove(player.getStringUUID());
                        ServerMod.PLAYER_DATA.put(player.getStringUUID(), tag);
                    }

                    var buf = PacketByteBufs.create();
                    buf.writeNbt(tag);
                    ServerPlayNetworking.send(player, ArmorHideNetwork.SETTINGS_PACKET, buf);

                    var message = new PlayerChatMessage(Component.literal("Reconnect to apply changes"), MessageSignature.unsigned(), Optional.empty());
                    player.sendChatMessage(message, ChatSender.system(Component.literal("server")), ChatType.SYSTEM);
                    return 0;
                })))
        );
    }
}
