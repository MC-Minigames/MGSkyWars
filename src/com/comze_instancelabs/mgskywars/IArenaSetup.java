package com.comze_instancelabs.mgskywars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaSetup;
import com.comze_instancelabs.minigamesapi.ArenaType;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class IArenaSetup extends ArenaSetup {

	@Override
	public Arena saveArena(JavaPlugin plugin, String arenaname) {
		if (!Validator.isArenaValid(plugin, arenaname)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Arena " + arenaname + " appears to be invalid.");
			return null;
		}
		PluginInstance pli = MinigamesAPI.getAPI().pinstances.get(plugin);
		if (pli.getArenaByName(arenaname) != null) {
			pli.removeArenaByName(arenaname);
		}
		IArena a = Main.initArena(arenaname);
		if(a.getArenaType() == ArenaType.REGENERATION){
			if(Util.isComponentForArenaValid(plugin, arenaname, "bounds")){
				Util.saveArenaToFile(plugin, arenaname);
			}else{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not save arena to file because boundaries were not set up.");
			}
		}
		this.setArenaVIP(plugin, arenaname, false);
		pli.addArena(a);
		return a;
	}
	
}
