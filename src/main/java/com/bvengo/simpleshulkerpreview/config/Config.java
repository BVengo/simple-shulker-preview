package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public abstract class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	protected abstract File getFile();

	public void save() {
		try (FileWriter writer = new FileWriter(getFile())) {
			GSON.toJson(this, writer);
		} catch (IOException e) {
			SimpleShulkerPreviewMod.LOGGER.error("Failed to save config: {}", e.getMessage());
		}
	}

	public void load() {
		try (FileReader reader = new FileReader(getFile())) {
			Config loaded = GSON.fromJson(reader, this.getClass());
			copyFrom(loaded);
		} catch (IOException e) {
			SimpleShulkerPreviewMod.LOGGER.warn("Failed to load config: {}", e.getMessage());
		}
		save(); // Clean up the file after loading
	}

	/**
	 * Loads the values from the loaded config file into this object.
	 * @param other The config object to copy from.
	 */
	private void copyFrom(Config other) {
		for (var field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				field.set(this, field.get(other));
			} catch (IllegalAccessException e) {
				SimpleShulkerPreviewMod.LOGGER.error("Failed to copy field: {}", field.getName());
			}
		}
	}

}
