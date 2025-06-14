package com.localcc.armorhide.mixin;

import com.localcc.armorhide.IPlayerRenderStateUUID;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements IPlayerRenderStateUUID {
    @Unique
    private UUID playerUUID;

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid;
    }
}
