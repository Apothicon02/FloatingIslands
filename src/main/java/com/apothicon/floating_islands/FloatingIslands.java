package com.apothicon.floating_islands;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import dev.crmodders.flux.api.generators.BlockGenerator;
import dev.crmodders.flux.registry.FluxRegistries;
import dev.crmodders.flux.tags.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloatingIslands implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Floating Islands");
	public static final String modId = "floating_islands";
	public static final String[] blocks = {
			"cherry_leaves",
			"dark_oak_leaves",
			"palm_leaves",
			"cactus",
			"stripped_tree_log"
	};
	public static boolean isFloatingIslandsRegistered = false;

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Floating Islands Initialized!");
		for (String block:blocks) {
			FluxRegistries.BLOCKS.register(
					new Identifier(modId, block), BlockGenerator::createGenerator
			);
		}
	}
}