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
	public void onHunger(FoodLevelChangeEvent event) {
		//
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event) {
		String playername = event.getEntity().getName();

		if (m.pli.global_players.containsKey(playername)) {
			event.getEntity().setHealth(20D);
			Player p = (Player) event.getEntity();
			p.setHealth(20D);
			if (m.pli.getClassesHandler().hasClass(playername)) {
				if (m.pli.getPClasses().get(playername).getInternalName().equalsIgnoreCase("extra_life")) {
					try {
						super.getClass().getDeclaredField("pspawnloc");
						Util.teleportPlayerFixed(Bukkit.getPlayer(playername), m.pli.global_players.get(playername).getPSpawnLocs().get(playername));
					} catch (NoSuchFieldException e) {
						System.out.println("Your MinigamesLib version doesn't support the extra life kit, update please. " + e.getMessage());
					}
					return;
				}
			}
		}
		super.onPlayerDeath(event);
	}

}
