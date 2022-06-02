package com.localcc.armorhide.mixin;

import com.localcc.armorhide.ClientMod;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.TrinketFeatureRenderer;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TrinketFeatureRenderer.class)
public class TrinketFeatureRendererMixin {
    @Inject(method = "lambda$render$1(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFFLdev/emi/trinkets/api/SlotReference;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void lambda(PoseStack arg0, MultiBufferSource arg1, int arg2, LivingEntity player, float arg4, float arg5, float arg6, float arg7, float arg8, float arg9, SlotReference slotReference, ItemStack stack, CallbackInfo ci) {
        var slot = slotReference.inventory().getSlotType();
        var name = slot.getGroup() + "/" + slot.getName() + "/" + slotReference.index();
        if(player.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
            if(ClientMod.getHiddenItems().contains(name)) {
                ci.cancel();
            }
        }
    }

}
