package com.localcc.armorhide;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;
import java.util.Set;

public class ClientMod implements ClientModInitializer {
    private static Set<String> HIDDEN_ITEMS = new HashSet<>();

    @Override
    public void onInitializeClient() {
        Mod.initializeTrinketInfoProvider();

        ClientPlayNetworking.registerGlobalReceiver(ArmorHideNetwork.SETTINGS_PACKET, (client, handler, buf, responseSender) -> {
            var nbt = buf.readNbt();
            if(nbt != null) {
                HIDDEN_ITEMS = new HashSet<>(nbt.getAllKeys());
            }
        });
    }

    public static Set<String> getHiddenItems() {
        return HIDDEN_ITEMS;
    }

    public static void addHiddenItem(String string) {
        HIDDEN_ITEMS.add(string);
    }

    public static void removeHiddenItem(String string) {
        HIDDEN_ITEMS.remove(string);
    }

    public static void sendSettings() {
        var buf = PacketByteBufs.create();
        var tag = new CompoundTag();
        for(String s : HIDDEN_ITEMS) {
            tag.putBoolean(s, true);
        }
        buf.writeNbt(tag);
        ClientPlayNetworking.send(ArmorHideNetwork.SETTINGS_PACKET, buf);
    }
}
