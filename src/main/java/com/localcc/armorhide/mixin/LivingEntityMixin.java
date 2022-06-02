package com.localcc.armorhide.mixin;

import com.localcc.armorhide.Mod;
import com.localcc.armorhide.ServerMod;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(value = LivingEntity.class, priority = 300)
public class LivingEntityMixin {
    @Inject(method = "Lnet/minecraft/world/entity/LivingEntity;handleEquipmentChanges(Ljava/util/Map;)V", at = @At(target = "Lnet/minecraft/network/protocol/game/ClientboundSetEquipmentPacket;<init>(ILjava/util/List;)V", value = "INVOKE"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void handleEquipmentChanges(Map<EquipmentSlot, ItemStack> map, CallbackInfo ci, List<Pair<EquipmentSlot, ItemStack>> list) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayer serverPlayer) {
            CompoundTag tag = ServerMod.PLAYER_DATA.getCompound(entity.getStringUUID());
            if(tag != null) {
                Collection<String> hiddenSlots = tag.getAllKeys();
                for (int i = 0; i < list.size(); i++) {
                    if (hiddenSlots.contains(list.get(i).getFirst().getName())) {
                        EquipmentSlot slot = list.get(i).getFirst();
                        list.set(i, Pair.of(slot, ItemStack.EMPTY));
                    }
                }
            }
        }
    }
}
