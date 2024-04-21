package com.apothicon.floating_islands;

import com.badlogic.gdx.Gdx;
import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import finalforeach.cosmicreach.io.SaveLocation;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloatingIslands implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Floating Islands");
	public static boolean isFloatingIslandsRegistered = false;
	public static final String[] blocks = {
			"cherry_leaves",
			"dark_oak_leaves",
			"palm_leaves",
			"cactus"
	};

	public static final String[] logBlocks = {
			"stripped_tree_log"
	};

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Floating Islands Initialized!");
		for (String block:blocks) {
			Gdx.files.classpath("assets/floating_islands/blocks/"+block+".json").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/blocks/"+block+".json"));
			Gdx.files.classpath("assets/floating_islands/models/blocks/"+block+".json").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/models/blocks/"+block+".json"));
			Gdx.files.classpath("assets/floating_islands/textures/blocks/"+block+".png").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/textures/blocks/"+block+".png"));
		}
		for (String block:logBlocks) {
			Gdx.files.classpath("assets/floating_islands/blocks/"+block+".json").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/blocks/"+block+".json"));
			Gdx.files.classpath("assets/floating_islands/models/blocks/"+block+".json").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/models/blocks/"+block+".json"));
			Gdx.files.classpath("assets/floating_islands/models/blocks/"+block+"_bark.json").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/models/blocks/"+block+"_bark.json"));
			Gdx.files.classpath("assets/floating_islands/textures/blocks/"+block+"_top.png").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/textures/blocks/"+block+"_top.png"));
			Gdx.files.classpath("assets/floating_islands/textures/blocks/"+block+"_side.png").copyTo(Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/assets/textures/blocks/"+block+"_side.png"));
		}
	}
}