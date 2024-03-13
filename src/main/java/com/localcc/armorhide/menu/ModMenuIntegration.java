package com.localcc.armorhide.menu;

import com.localcc.armorhide.ClientMod;
import com.localcc.armorhide.Mod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

    private static AbstractConfigListEntry<Boolean> createVanillaEntry(ConfigEntryBuilder entryBuilder, String translatableName, String name) {
        var configValue = ClientMod.getHiddenItems().contains(name);
        return entryBuilder.startBooleanToggle(Component.translatable("gui.armorhide.hide").append(" ").append(Component.translatable(translatableName)), configValue)
                .setSaveConsumer(value -> {
                    if(value) {
                        ClientMod.addHiddenItem(name);
                    } else {
                        ClientMod.removeHiddenItem(name);
                    }
                })
                .setDefaultValue(false).build();
    }
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("Armor Hide"));
            var entryBuilder = builder.entryBuilder();

            var vanillaItems = builder.getOrCreateCategory(Component.translatable("gui.armorhide.vanilla"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.head", "head"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.chest", "chest"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.legs", "legs"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.feet", "feet"));

            var trinketGroups = Mod.TRINKET_INFO_PROVIDER.getGroups(Minecraft.getInstance().player);
            if(!trinketGroups.isEmpty()) {
                var trinketsItems = builder.getOrCreateCategory(Component.translatable("gui.armorhide.trinkets"));
                trinketGroups.forEach((groupName, trinkets) -> {
                    var subCategory = entryBuilder.startSubCategory(Component.literal(groupName));
                    for (var trinket : trinkets) {
                        var itemName = trinket.groupName() + "/" + trinket.slotName() + "/" + trinket.groupOrder();
                        var shortName = trinket.slotName() + "/" + trinket.groupOrder();
                        subCategory.add(entryBuilder.startBooleanToggle(
                                        Component.translatable("gui.armorhide.hide").append(" " + shortName),
                                        ClientMod.getHiddenItems().contains(itemName))
                                .setSaveConsumer(value -> {
                                    if (value) {
                                        ClientMod.addHiddenItem(itemName);
                                    } else {
                                        ClientMod.removeHiddenItem(itemName);
                                    }
                                })
                                .setDefaultValue(false)
                                .build());
                    }
                    trinketsItems.addEntry(subCategory.build());
                });
            }

            builder.setSavingRunnable(() -> {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.armorhide.reconnect"), false);
                ClientMod.sendSettings();
            });
            return builder.build();
        };
    }
}
