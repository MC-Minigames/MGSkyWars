package com.comze_instancelabs.mgskywars;

import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.config.ClassesConfig;

public class IClassesConfig extends ClassesConfig {

	public IClassesConfig(JavaPlugin plugin) {
		super(plugin, true);
		this.getConfig().options().header("Used for saving classes. Default class:");
		this.getConfig().addDefault("config.kits.default.name", "default");
		this.getConfig().addDefault("config.kits.default.items", "261:0#ARROW_INFINITE:1#KNOCKBACK:1*1;262:0*1;270*1");
		this.getConfig().addDefault("config.kits.default.lore", "The default class.");
		this.getConfig().addDefault("config.kits.default.requires_money", false);
		this.getConfig().addDefault("config.kits.default.requires_permission", false);
		this.getConfig().addDefault("config.kits.default.money_amount", 100);
		this.getConfig().addDefault("config.kits.default.enabled", true);
		this.getConfig().addDefault("config.kits.default.permission_node", "minigames.kits.default");

		this.getConfig().addDefault("config.kits.pro.name", "pro");
		this.getConfig().addDefault("config.kits.pro.items", "272*1;274*1;261:0#ARROW_INFINITE:1#KNOCKBACK:1*1;262:0*1");
		this.getConfig().addDefault("config.kits.pro.lore", "The Pro class.");
		this.getConfig().addDefault("config.kits.pro.requires_money", false);
		this.getConfig().addDefault("config.kits.pro.requires_permission", false);
		this.getConfig().addDefault("config.kits.pro.money_amount", 100);
		this.getConfig().addDefault("config.kits.pro.enabled", true);
		this.getConfig().addDefault("config.kits.pro.permission_node", "minigames.kits.pro");

		this.getConfig().addDefault("config.kits.extra_life.name", "Extra_Life");
		this.getConfig().addDefault("config.kits.extra_life.items", "351:1*1;272*1;274*1;261:0#ARROW_INFINITE:1#KNOCKBACK:1*1;262:0*1");
		this.getConfig().addDefault("config.kits.extra_life.lore", "The extra life class.");
		this.getConfig().addDefault("config.kits.extra_life.requires_money", false);
		this.getConfig().addDefault("config.kits.extra_life.requires_permission", false);
		this.getConfig().addDefault("config.kits.extra_life.money_amount", 100);
		this.getConfig().addDefault("config.kits.extra_life.enabled", true);
		this.getConfig().addDefault("config.kits.extra_life.permission_node", "minigames.kits.extra_life");

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}

}
