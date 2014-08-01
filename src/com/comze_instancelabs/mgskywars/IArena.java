package com.comze_instancelabs.mgskywars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaType;
import com.comze_instancelabs.minigamesapi.util.Util;

public class IArena extends Arena {

	public static Main m;

	public IArena(Main m, String arena_id) {
		super(m, arena_id, ArenaType.REGENERATION);
		this.m = m;
	}

	@Override
	public void spectate(String playername) {
		Util.clearInv(Bukkit.getPlayer(playername));
		super.spectate(playername);
	}
	
	@Override
	public void started(){
		for(Location t : this.getSpawns()){
			Location temp = t.clone().add(0D, -2D, 0D);
			temp.getBlock().setType(Material.AIR);
		}
	}



}
