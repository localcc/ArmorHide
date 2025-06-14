//package com.localcc.armorhide.mixin;
//
//import com.localcc.armorhide.ServerMod;
//import dev.emi.trinkets.TrinketsNetwork;
//import dev.emi.trinkets.payload.SyncInventoryPayload;
//import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.HashMap;
//
//import static net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.createS2CPacket;
//
//@Mixin(ServerPlayNetworking.class)
//public class ServerPlayNetworkingMixin {
//    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
//    private static void send(ServerPlayer player, CustomPacketPayload payload, CallbackInfo ci) {
//        if (payload.type() == TrinketsNetwork.SYNC_INVENTORY) {
//            var inventoryPayload = (SyncInventoryPayload)payload;
//
//            var entity = player.level().getEntity(inventoryPayload.entityId());
//            var contentUpdates = new HashMap<>(inventoryPayload.contentUpdates());
//
//            if (entity != null && !entity.equals(player) && ServerMod.PLAYER_DATA.contains(entity.getStringUUID())) {
//                var buffer = PacketByteBufs.create();
//                var keys = ServerMod.PLAYER_DATA.getCompound(entity.getStringUUID()).getAllKeys();
//                for (String toRemove : keys) {
//                    if (contentUpdates.containsKey(toRemove)) {
//                        contentUpdates.remove(toRemove);
//                        contentUpdates.put(toRemove, ItemStack.EMPTY);
//                    }
//                }
//
//                var newPayload = new SyncInventoryPayload(inventoryPayload.entityId(), contentUpdates, inventoryPayload.inventoryUpdates());
//                player.connection.send(createS2CPacket(newPayload));
//                ci.cancel();
//            }
//        }
//    }
//}