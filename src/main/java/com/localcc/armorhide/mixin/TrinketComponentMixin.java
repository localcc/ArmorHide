package com.localcc.armorhide.mixin;

import com.localcc.armorhide.ServerMod;
import dev.emi.trinkets.api.LivingEntityTrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntityTrinketComponent.class)
public abstract class TrinketComponentMixin implements AutoSyncedComponent {

    @Shadow public LivingEntity entity;

    @Shadow private boolean syncing;
    private ServerPlayer syncRecipient;

    @Inject(method = "writeSyncPacket", at = @At("HEAD"))
    private void writeSyncPacketInject(FriendlyByteBuf buf, ServerPlayer recipient, CallbackInfo ci) {
        this.syncRecipient = recipient;
    }

    @Inject(method = "writeToNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void writeToNbtInject(CompoundTag tag, CallbackInfo ci, Iterator iterator, Map.Entry group, CompoundTag groupTag, Iterator iterator2, Map.Entry slot, CompoundTag slotTag, ListTag list, TrinketInventory trinketInventory) {
        if(this.entity instanceof ServerPlayer && !syncRecipient.equals(this.entity) && this.syncing) {
            if(ServerMod.PLAYER_DATA.contains(this.entity.getStringUUID())) {
                var hideTag = ServerMod.PLAYER_DATA.getCompound(this.entity.getStringUUID());
                var itemsToHide = hideTag.getAllKeys();
                for(int i = 0; i < list.size(); i++) {
                    var slotName = group.getKey() + "/" + trinketInventory.getSlotType().getName() + "/" + i;
                    if(itemsToHide.contains(slotName)) {
                        list.set(i, ItemStack.EMPTY.save(new CompoundTag()));
                    }
                }
            }
        }
    }
}
