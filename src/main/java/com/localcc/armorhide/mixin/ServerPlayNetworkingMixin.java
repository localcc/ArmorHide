package com.localcc.armorhide.mixin;

import com.localcc.armorhide.ServerMod;
import dev.emi.trinkets.TrinketsNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.createS2CPacket;

@Mixin(ServerPlayNetworking.class)
public class ServerPlayNetworkingMixin {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private static void send(ServerPlayer player, ResourceLocation channelName, FriendlyByteBuf buf, CallbackInfo ci) {
        if(channelName.equals(TrinketsNetwork.SYNC_INVENTORY)) {
            var copy = new FriendlyByteBuf(buf.copy());
            var entityId = copy.readInt();
            var inventories = copy.readNbt();
            var itemsList = copy.readNbt();
            var entity = player.level.getEntity(entityId);

            if(entity != null && !entity.equals(player) && ServerMod.PLAYER_DATA.contains(entity.getStringUUID())) {
                var buffer = PacketByteBufs.create();
                var keys = ServerMod.PLAYER_DATA.getCompound(entity.getStringUUID()).getAllKeys();
                for(String toRemove : keys) {
                    if(itemsList.contains(toRemove)) {
                        itemsList.remove(toRemove);
                        itemsList.put(toRemove, ItemStack.EMPTY.save(new CompoundTag()));
                    }
                }
                buffer.writeInt(entityId);
                buffer.writeNbt(inventories);
                buffer.writeNbt(itemsList);

                player.connection.send(createS2CPacket(channelName, buffer));
                ci.cancel();
            }
        }
    }
}