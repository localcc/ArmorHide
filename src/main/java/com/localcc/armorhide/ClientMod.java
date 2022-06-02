package com.localcc.armorhide;

import com.localcc.armorhide.menu.trinkets.IModMenuTrinkets;
import com.localcc.armorhide.menu.trinkets.ModMenuTrinkets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClientMod implements ClientModInitializer {
    public static IModMenuTrinkets MOD_MENU_TRINKETS = () -> new HashMap<String, java.util.List<com.localcc.armorhide.menu.trinkets.ModMenuTrinket>>();

    private static Set<String> HIDDEN_ITEMS = new HashSet<>();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ArmorHideNetwork.SETTINGS_PACKET, (client, handler, buf, responseSender) -> {
            var nbt = buf.readNbt();
            if(nbt != null) {
                HIDDEN_ITEMS = new HashSet<>(nbt.getAllKeys());
            }
        });

        if(FabricLoader.getInstance().isModLoaded("trinkets")) {
            MOD_MENU_TRINKETS = new ModMenuTrinkets();
        }
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
