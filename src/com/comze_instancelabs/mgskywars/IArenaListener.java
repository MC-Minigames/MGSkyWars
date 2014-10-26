package com.comze_instancelabs.mgskywars;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.ArenaListener;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.Util;

public class IArenaListener extends ArenaListener {

	Main m;

	public IArenaListener(Main plugin, PluginInstance pinstance) {
		super(plugin, pinstance, "skywars", new ArrayList<String>(Arrays.asList("/sw")));
		this.m = plugin;
	}

	@Override
	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		//
	}



}
