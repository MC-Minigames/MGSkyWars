package com.comze_instancelabs.mgskywars;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.ArenaListener;
import com.comze_instancelabs.minigamesapi.PluginInstance;

public class IArenaListener extends ArenaListener {

	public IArenaListener(JavaPlugin plugin, PluginInstance pinstance) {
		super(plugin, pinstance, "skywars", new ArrayList<String>(Arrays.asList("/sw")));
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		//
	}

}
