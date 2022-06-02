package com.localcc.armorhide;

import com.localcc.armorhide.command.ArmorHideCommand;
import com.localcc.armorhide.event.SaveEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

public class Mod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("armorhide");

	@Override
	public void onInitialize() {
	}
}
