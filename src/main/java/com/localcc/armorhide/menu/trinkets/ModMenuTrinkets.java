package com.localcc.armorhide.menu.trinkets;

import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModMenuTrinkets implements IModMenuTrinkets {
    @Override
    public Map<String, List<ModMenuTrinket>> getAllTrinkets() {
        var map = new HashMap<String, List<ModMenuTrinket>>();
        for(SlotGroup group : TrinketsApi.getPlayerSlots().values()) {
            var list = new ArrayList<ModMenuTrinket>();
            for(var slot : group.getSlots().values()) {
                list.add(new ModMenuTrinket(group.getName(), group.getOrder(), slot.getName()));
            }
            map.put(group.getName(), list);
        }
        return map;
    }
}
