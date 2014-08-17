package com.comze_instancelabs.mgskywars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaType;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

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
	public void started() {
		for (Location t : this.getSpawns()) {
			Location temp = t.clone().add(0D, -2D, 0D);
			temp.getBlock().setType(Material.AIR);
		}
		final IArena a = this;
		Bukkit.getScheduler().runTaskLater(m, new Runnable() {
			public void run() {
				for (String p_ : a.getAllPlayers()) {
					if (Validator.isPlayerOnline(p_)) {
						Bukkit.getPlayer(p_).setHealth(20D);
					}
				}
			}
		}, 50L);
	}

	@Override
	public void leavePlayer(String p, boolean fullLeave) {
		/*
		 * List<Entity> t = Bukkit.getPlayer(p).getNearbyEntities(20D, 20D, 20D); for(Entity t_ : t){ t_.remove(); }
		 */
		for (Location t : this.getSpawns()) {
			for (Entity t_ : t.getChunk().getEntities()) {
				if (t_.getType() == EntityType.DROPPED_ITEM) {
					t_.remove();
				}
			}
		}
		super.leavePlayer(p, fullLeave);
	}

}
