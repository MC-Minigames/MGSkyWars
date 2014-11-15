package com.comze_instancelabs.mgskywars;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ChestsConfig {

	private FileConfiguration arenaConfig = null;
	private File arenaFile = null;
	private JavaPlugin plugin = null;

	public ChestsConfig(JavaPlugin plugin) {
		this.plugin = plugin;
		this.getConfig().options().header("Just copy paste if you want more chests. The percentages must add up to 100!");

		this.getConfig().addDefault("config.enabled", false);

		this.getConfig().addDefault("config.chests.chest1.items", "5*64;5*64;5*64;5*64;262*64;278*1;5*64%30");
		this.getConfig().addDefault("config.chests.chest1.percentage", 5);

		this.getConfig().addDefault("config.chests.chest2.items", "5*64;262*64;267*1");
		this.getConfig().addDefault("config.chests.chest2.percentage", 20);

		this.getConfig().addDefault("config.chests.chest3.items", "5*64;262*64");
		this.getConfig().addDefault("config.chests.chest3.percentage", 25);

		this.getConfig().addDefault("config.chests.chest4.items", "5*64");
		this.getConfig().addDefault("config.chests.chest4.percentage", 50);

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}

	public FileConfiguration getConfig() {
		if (arenaConfig == null) {
			reloadConfig();
		}
		return arenaConfig;
	}

	public void saveConfig() {
		if (arenaConfig == null || arenaFile == null) {
			return;
		}
		try {
			getConfig().save(arenaFile);
		} catch (IOException ex) {

		}
	}

	public void reloadConfig() {
		if (arenaFile == null) {
			arenaFile = new File(plugin.getDataFolder(), "chests.yml");
		}
		arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

		InputStream defConfigStream = plugin.getResource("chests.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			arenaConfig.setDefaults(defConfig);
		}
	}

}
