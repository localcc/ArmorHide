package com.localcc.armorhide.network;

import com.localcc.armorhide.ClientMod;
import com.mojang.serialization.Codec;
import io.netty.handler.codec.CodecException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;

public record SettingsPayload(HashSet<String> hiddenItems) implements CustomPacketPayload {
    public static final ResourceLocation SETTINGS_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath("armorhide", "settings");
    public static final CustomPacketPayload.Type<SettingsPayload> TYPE = new CustomPacketPayload.Type<>(SETTINGS_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SettingsPayload> CODEC = new StreamCodec<RegistryFriendlyByteBuf, SettingsPayload>() {
        @Override
        public SettingsPayload decode(RegistryFriendlyByteBuf byteBuf) {
            var nbt = FriendlyByteBuf.readNbt(byteBuf);
            if (nbt == null)  {
                throw new CodecException("Invalid settings payload");
            }
            return SettingsPayload.fromTag(nbt);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf byteBuf, SettingsPayload payload) {
            var tag = payload.toTag();
            FriendlyByteBuf.writeNbt(byteBuf, tag);
        }
    };

    public CompoundTag toTag() {
        var tag = new CompoundTag();
        for (String s : hiddenItems) {
            tag.putBoolean(s, true);
        }
        return tag;
    }

    public static SettingsPayload fromTag(CompoundTag tag) {
        var hiddenItems = tag.keySet();
        return new SettingsPayload(new HashSet<>(hiddenItems));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
