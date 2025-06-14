package com.localcc.armorhide;

import com.localcc.armorhide.network.SettingsPayload;
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

        ClientPlayNetworking.registerGlobalReceiver(SettingsPayload.TYPE, (payload, context) -> {
            HIDDEN_ITEMS = payload.hiddenItems();
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
        ClientPlayNetworking.send(new SettingsPayload(new HashSet<>(HIDDEN_ITEMS)));
    }
}
