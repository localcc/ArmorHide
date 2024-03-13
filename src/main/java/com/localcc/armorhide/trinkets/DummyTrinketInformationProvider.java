package com.localcc.armorhide.trinkets;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyTrinketInformationProvider implements ITrinketInformationProvider {
    @Override
    public Map<String, List<TrinketInformation>> getGroups(Player player) {
        return new HashMap<>();
    }
}
