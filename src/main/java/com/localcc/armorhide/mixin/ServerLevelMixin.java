package com.localcc.armorhide.mixin;

import com.localcc.armorhide.event.SaveEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;save(Z)V"))
    private void save(ProgressListener progressListener, boolean bl, boolean bl2, CallbackInfo ci) {
        SaveEvent.EVENT.invoker().save((ServerLevel) (Object) this);
    }
}
