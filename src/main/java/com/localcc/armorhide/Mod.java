package com.localcc.armorhide;

import com.localcc.armorhide.network.SettingsPayload;
import com.localcc.armorhide.trinkets.DummyTrinketInformationProvider;
import com.localcc.armorhide.trinkets.ITrinketInformationProvider;
//import com.localcc.armorhide.trinkets.TrinketInformationProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("armorhide");

    public static ITrinketInformationProvider TRINKET_INFO_PROVIDER;

    public static boolean trinketsSupportEnabled() {
        var loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("trinkets")) {
            return false;
        }

        var version = loader.getModContainer("minecraft").map(mod -> mod.getMetadata().getVersion()).orElse(null);
        if (version == null) {
            LOGGER.warn("[ArmorHide] Failed to determine minecraft version, trinkets support disabled.");
            return false;
        }

        // todo(localcc): remove this version check after trinkets is updated
        try {
            var requiredVersion = Version.parse("1.21.1");
            if (version.compareTo(requiredVersion) <= 0) {
                return true;
            }

            LOGGER.warn("[ArmorHide] Minecraft version is too new, trinkets support disabled.");
            return false;
        } catch (VersionParsingException e) {
            LOGGER.warn("[ArmorHide] Failed to construct required version, trinkets support disabled.");
            return false;
        }
    }

    public static void initializeTrinketInfoProvider() {
        if (trinketsSupportEnabled()) {
//            TRINKET_INFO_PROVIDER = new TrinketInformationProvider();
        } else {
            TRINKET_INFO_PROVIDER = new DummyTrinketInformationProvider();
        }
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(SettingsPayload.TYPE, SettingsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SettingsPayload.TYPE, SettingsPayload.CODEC);
    }
}
