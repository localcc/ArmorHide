package com.localcc.armorhide.mixin;

import com.localcc.armorhide.ClientMod;
import com.localcc.armorhide.IPlayerRenderStateUUID;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> {

    @Shadow protected abstract A getArmorModel(S humanoidRenderState, EquipmentSlot equipmentSlot);

    @Shadow protected abstract void renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, ItemStack itemStack, EquipmentSlot equipmentSlot, int i, A humanoidModel);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, S humanoidRenderState, float f, float g, CallbackInfo ci) {
        if (humanoidRenderState instanceof PlayerRenderState playerRenderState) {
            var playerId = ((IPlayerRenderStateUUID)playerRenderState).getPlayerUUID();

            if (playerId.equals(Minecraft.getInstance().player.getUUID())) {
                var slots = new Tuple[]{
                        new Tuple<>(EquipmentSlot.CHEST, humanoidRenderState.chestEquipment),
                        new Tuple<>(EquipmentSlot.LEGS, humanoidRenderState.legsEquipment),
                        new Tuple<>(EquipmentSlot.FEET, humanoidRenderState.feetEquipment),
                        new Tuple<>(EquipmentSlot.HEAD, humanoidRenderState.headEquipment)
                };

                for (var s : slots) {
                    var slot = (Tuple<EquipmentSlot, ItemStack>)s;
                    if (!ClientMod.getHiddenItems().contains(slot.getA().getName()))
                        this.renderArmorPiece(poseStack, multiBufferSource, slot.getB(), slot.getA(), i, this.getArmorModel(humanoidRenderState, slot.getA()));
                }

                ci.cancel();
            }
        }
    }
}
