package com.localcc.armorhide;

import com.localcc.armorhide.command.ArmorHideCommand;
import com.localcc.armorhide.event.SaveEvent;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;

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
                    PLAYER_DATA = NbtIo.readCompressed(path.toFile());
                } catch(Exception e) {
                    LOGGER.error("Failed to load persistent data", e);
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
                var buf = PacketByteBufs.create();
                buf.writeNbt(PLAYER_DATA.getCompound(handler.player.getStringUUID()));
                ServerPlayNetworking.send(handler.player, ArmorHideNetwork.SETTINGS_PACKET, buf);
            }
        });

        SaveEvent.EVENT.register(level -> {
            if(PLAYER_DATA != null) {
                Util.ioPool().execute(() -> {
                    try {
                        NbtIo.writeCompressed(PLAYER_DATA, level.getServer().getWorldPath(PERSISTENT_DATA).toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        CommandRegistrationCallback.EVENT.register(ArmorHideCommand::register);

        ServerPlayNetworking.registerGlobalReceiver(ArmorHideNetwork.SETTINGS_PACKET, (server, player, handler, buf, responseSender) -> {
            var nbt = buf.readNbt();
            if(nbt != null) {
                if(ServerMod.PLAYER_DATA.contains(player.getStringUUID())) {
                    ServerMod.PLAYER_DATA.remove(player.getStringUUID());
                }
                ServerMod.PLAYER_DATA.put(player.getStringUUID(), nbt);
            }
        });
    }
}
