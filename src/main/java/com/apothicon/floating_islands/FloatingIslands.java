package com.apothicon.floating_islands;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloatingIslands implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Floating Islands");

	public static boolean isFloatingIslandsRegistered = false;

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Floating Islands Initialized!");
	}
}

