package com.comze_instancelabs.mgskywars;

import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.config.ClassesConfig;

public class IClassesConfig extends ClassesConfig {

	public IClassesConfig(JavaPlugin plugin) {
		super(plugin, true);
    	this.getConfig().options().header("Used for saving classes. Default class:");
    	this.getConfig().addDefault("config.kits.default.name", "default");
    	this.getConfig().addDefault("config.kits.default.items", "261:0#ARROW_INFINITE:1#KNOCKBACK:2*1;280:0#KNOCKBACK:5*1;262:0*1");
    	this.getConfig().addDefault("config.kits.default.lore", "The default class.");
    	this.getConfig().addDefault("config.kits.default.requires_money", false);
    	this.getConfig().addDefault("config.kits.default.requires_permission", false);
    	this.getConfig().addDefault("config.kits.default.money_amount", 100);
    	this.getConfig().addDefault("config.kits.default.permission_node", "minigames.kits.default");
    	this.getConfig().options().copyDefaults(true);
    	this.saveConfig();
	}

}
