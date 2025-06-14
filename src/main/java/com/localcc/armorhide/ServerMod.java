package com.localcc.armorhide;

import com.localcc.armorhide.command.ArmorHideCommand;
import com.localcc.armorhide.event.SaveEvent;
import com.localcc.armorhide.network.SettingsPayload;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

import static com.localcc.armorhide.Mod.LOGGER;

public class ServerMod implements DedicatedServerModInitializer {
    public static CompoundTag PLAYER_DATA;
    private static final LevelResource PERSISTENT_DATA = new LevelResource("armorhide");
    private static final int ACCOUNTER_LIMIT = 1024;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            var path = server.getWorldPath(PERSISTENT_DATA);
            if(Files.exists(path)) {
                try {
                    PLAYER_DATA = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
                } catch(Exception e) {
                    LOGGER.error("[ArmorHide] Failed to load persistent data", e);
                }
            } else {
                PLAYER_DATA = new CompoundTag();
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Mod.initializeTrinketInfoProvider();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if(ServerMod.PLAYER_DATA.contains(handler.player.getStringUUID())) {
                var tag = PLAYER_DATA.getCompound(handler.player.getStringUUID());
                tag.ifPresent(compoundTag -> ServerPlayNetworking.send(handler.player, SettingsPayload.fromTag(compoundTag)));
            }
        });

        SaveEvent.EVENT.register(level -> {
            if(PLAYER_DATA != null) {
                Util.ioPool().execute(() -> {
                    try {
                        NbtIo.writeCompressed(PLAYER_DATA, level.getServer().getWorldPath(PERSISTENT_DATA));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        CommandRegistrationCallback.EVENT.register(ArmorHideCommand::register);

        ServerPlayNetworking.registerGlobalReceiver(SettingsPayload.TYPE, (payload, context) -> {
            if(ServerMod.PLAYER_DATA.contains(context.player().getStringUUID())) {
                ServerMod.PLAYER_DATA.remove(context.player().getStringUUID());
            }
            ServerMod.PLAYER_DATA.put(context.player().getStringUUID(), payload.toTag());
        });
    }
}
