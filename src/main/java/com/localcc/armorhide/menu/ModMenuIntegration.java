package com.localcc.armorhide.menu;

import com.localcc.armorhide.ClientMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.UUID;

public class ModMenuIntegration implements ModMenuApi {

    private static AbstractConfigListEntry<Boolean> createVanillaEntry(ConfigEntryBuilder entryBuilder, String translatableName, String name) {
        var configValue = ClientMod.getHiddenItems().contains(name);
        return entryBuilder.startBooleanToggle(new TranslatableComponent("gui.armorhide.hide").append(" ").append(new TranslatableComponent(translatableName)), configValue)
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
                    .setTitle(new TextComponent("Armor Hide"));
            var entryBuilder = builder.entryBuilder();

            var vanillaItems = builder.getOrCreateCategory(new TranslatableComponent("gui.armorhide.vanilla"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.head", "head"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.chest", "chest"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.legs", "legs"));
            vanillaItems.addEntry(createVanillaEntry(entryBuilder, "gui.armorhide.boots", "boots"));

            var trinketGroups = ClientMod.MOD_MENU_TRINKETS.getAllTrinkets();
            if(trinketGroups.size() > 0) {
                var trinketsItems = builder.getOrCreateCategory(new TranslatableComponent("gui.armorhide.trinkets"));
                trinketGroups.forEach((groupName, trinkets) -> {
                    var subCategory = entryBuilder.startSubCategory(new TextComponent(groupName));
                    for (var trinket : trinkets) {
                        var itemName = trinket.groupName() + "/" + trinket.slotName() + "/" + trinket.groupOrder();
                        var shortName = trinket.slotName() + "/" + trinket.groupOrder();
                        subCategory.add(entryBuilder.startBooleanToggle(
                                        new TranslatableComponent("gui.armorhide.hide").append(" " + shortName),
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
                Minecraft.getInstance().player.sendMessage(new TranslatableComponent("message.armorhide.reconnect"), UUID.randomUUID());
                ClientMod.sendSettings();
            });
            return builder.build();
        };
    }
}
