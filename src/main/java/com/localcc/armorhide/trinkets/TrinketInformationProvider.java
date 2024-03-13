package com.localcc.armorhide.trinkets;

import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrinketInformationProvider implements ITrinketInformationProvider {
    @Override
    public Map<String, List<TrinketInformation>> getGroups(Player player) {
        var map = new HashMap<String, List<TrinketInformation>>();
        for(SlotGroup group : TrinketsApi.getPlayerSlots(player).values()) {
            var list = new ArrayList<TrinketInformation>();
            for(var slot : group.getSlots().values()) {
                list.add(new TrinketInformation(group.getName(), group.getOrder(), slot.getName()));
            }
            map.put(group.getName(), list);
        }
        return map;
    }
}
