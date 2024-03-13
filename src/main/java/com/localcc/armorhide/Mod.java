package com.localcc.armorhide;

import com.localcc.armorhide.trinkets.DummyTrinketInformationProvider;
import com.localcc.armorhide.trinkets.ITrinketInformationProvider;
import com.localcc.armorhide.trinkets.TrinketInformationProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("armorhide");

    public static ITrinketInformationProvider TRINKET_INFO_PROVIDER;

    public static void initializeTrinketInfoProvider() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TRINKET_INFO_PROVIDER = new TrinketInformationProvider();
        } else {
            TRINKET_INFO_PROVIDER = new DummyTrinketInformationProvider();
        }
    }

    @Override
    public void onInitialize() {
    }
}
