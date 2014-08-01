package com.comze_instancelabs.mgskywars;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaSetup;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.config.DefaultConfig;
import com.comze_instancelabs.minigamesapi.config.MessagesConfig;
import com.comze_instancelabs.minigamesapi.config.StatsConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class Main extends JavaPlugin implements Listener {

	MinigamesAPI api = null;
	PluginInstance pli = null;
	static Main m = null;
	ICommandHandler cmdhandler = new ICommandHandler();

	public void onEnable() {
		m = this;
		api = MinigamesAPI.getAPI().setupAPI(this, "skywars", IArena.class, new ArenasConfig(this), new MessagesConfig(this), new IClassesConfig(this), new StatsConfig(this, false), new DefaultConfig(this, false), false);
		PluginInstance pinstance = api.pinstances.get(this);
		pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
		Bukkit.getPluginManager().registerEvents(this, this);
		pinstance.arenaSetup = new IArenaSetup();
		pinstance.getArenaListener().loseY = 20;
		pli = pinstance;
	}

	public static ArrayList<Arena> loadArenas(JavaPlugin plugin, ArenasConfig cf) {
		ArrayList<Arena> ret = new ArrayList<Arena>();
		FileConfiguration config = cf.getConfig();
		if (!config.isSet("arenas")) {
			return ret;
		}
		for (String arena : config.getConfigurationSection("arenas.").getKeys(false)) {
			if (Validator.isArenaValid(plugin, arena, cf.getConfig())) {
				ret.add(initArena(arena));
			}
		}
		return ret;
	}

	public static IArena initArena(String arena) {
		IArena a = new IArena(m, arena);
		ArenaSetup s = MinigamesAPI.getAPI().pinstances.get(m).arenaSetup;
		a.init(Util.getSignLocationFromArena(m, arena), Util.getAllSpawns(m, arena), Util.getMainLobby(m), Util.getComponentForArena(m, arena, "lobby"), s.getPlayerCount(m, arena, true), s.getPlayerCount(m, arena, false), s.getArenaVIP(m, arena));
		return a;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cmdhandler.handleArgs(this, "mgskywars", "/" + cmd.getName(), sender, args);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (pli.global_players.containsKey(event.getPlayer().getName())) {
			if (pli.global_players.get(event.getPlayer().getName()).getArenaState() != ArenaState.INGAME) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType() == Material.DRAGON_EGG) {
			Player p = event.getPlayer();
			String arenaname = event.getItemInHand().getItemMeta().getDisplayName();

			if (arenaname == null)
				return;

			/*
			 * if (!Validator.isArenaValid(this, arenaname)) { p.sendMessage(ChatColor.RED + "Could not find arena."); return; }
			 */

			int i = pli.arenaSetup.autoSetSpawn(this, arenaname, event.getBlock().getLocation().clone().add(0.5D, 7D, 0.5D));
			event.getPlayer().sendMessage(pli.getMessagesConfig().successfully_set.replaceAll("<component>", "spawn " + Integer.toString(i)));

			Location l = event.getBlock().getLocation();
			l.clone().add(0D, 5D, 0D).getBlock().setType(Material.GLASS);
			l.clone().add(0D, 6D, 1D).getBlock().setType(Material.GLASS);
			l.clone().add(0D, 6D, -1D).getBlock().setType(Material.GLASS);
			l.clone().add(1D, 6D, 0D).getBlock().setType(Material.GLASS);
			l.clone().add(-1D, 6D, 0D).getBlock().setType(Material.GLASS);
			l.clone().add(0D, 7D, 1D).getBlock().setType(Material.GLASS);
			l.clone().add(0D, 7D, -1D).getBlock().setType(Material.GLASS);
			l.clone().add(1D, 7D, 0D).getBlock().setType(Material.GLASS);
			l.clone().add(-1D, 7D, 0D).getBlock().setType(Material.GLASS);
			l.clone().add(0D, 8D, 1D).getBlock().setType(Material.GLASS);
			l.clone().add(0D, 8D, -1D).getBlock().setType(Material.GLASS);
			l.clone().add(1D, 8D, 0D).getBlock().setType(Material.GLASS);
			l.clone().add(-1D, 8D, 0D).getBlock().setType(Material.GLASS);
			l.clone().add(0D, 9D, 0D).getBlock().setType(Material.GLASS);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player dead = event.getEntity();
		if (dead.getKiller() instanceof Player) {
			Player killer = (Player) dead.getKiller();
			if (pli.global_players.containsKey(dead.getName()) && pli.global_players.containsKey(killer.getName())) {
				Arena a = pli.global_players.get(dead.getName());
				if (a != null) {
					a.spectate(dead.getName());
				}
			}
		}
	}

}
