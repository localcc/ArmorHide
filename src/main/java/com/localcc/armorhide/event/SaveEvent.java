package com.localcc.armorhide.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

public interface SaveEvent {
    Event<SaveEvent> EVENT = EventFactory.createArrayBacked(SaveEvent.class, (listeners) -> (level) -> {
        for (SaveEvent event : listeners) {
            event.save(level);
        }
    });

    void save(ServerLevel level);
}
