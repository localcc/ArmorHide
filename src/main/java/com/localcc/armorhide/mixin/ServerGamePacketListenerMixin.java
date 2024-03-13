package com.localcc.armorhide.mixin;

import com.localcc.armorhide.ServerMod;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, CallbackInfo ci) {
        var self = (ServerGamePacketListenerImpl) (Object) this;
        hidePacket(self.player.level(), packet);
    }

    @Unique
    void hideEquipment(Level level, ClientboundSetEquipmentPacket equipmentPacket) {
        var entity = level.getEntity(equipmentPacket.getEntity());
        if (entity instanceof ServerPlayer && ServerMod.PLAYER_DATA.contains(entity.getStringUUID())) {
            var tag = ServerMod.PLAYER_DATA.getCompound(entity.getStringUUID());
            if (tag != null) {
                var slots = equipmentPacket.getSlots();
                var hiddenSlots = tag.getAllKeys();
                for (int i = 0; i < slots.size(); i++) {
                    if (hiddenSlots.contains(slots.get(i).getFirst().getName())) {
                        var slot = slots.get(i).getFirst();
                        slots.set(i, Pair.of(slot, ItemStack.EMPTY));
                    }
                }
            }
        }
    }

    @Unique
    void hidePacket(Level level, Packet<?> packet) {
        if (packet instanceof ClientboundSetEquipmentPacket equipmentPacket) {
            hideEquipment(level, equipmentPacket);
        } else if (packet instanceof ClientboundBundlePacket bundlePacket) {
            for (Packet<?> subPacket : bundlePacket.subPackets()) {
                hidePacket(level, subPacket);
            }
        }
    }
}
