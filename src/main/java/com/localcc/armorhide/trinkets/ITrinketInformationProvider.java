package com.localcc.armorhide.trinkets;

import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public interface ITrinketInformationProvider {
    Map<String, List<TrinketInformation>> getGroups(Player player);
}
