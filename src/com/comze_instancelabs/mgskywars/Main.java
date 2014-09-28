package com.comze_instancelabs.mgskywars;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
	ChestsConfig chestsconfig;

	boolean custom_chests = false;

	HashMap<String, ArrayList<ItemStack>> chests = new HashMap<String, ArrayList<ItemStack>>();

	public void onEnable() {
		m = this;
		api = MinigamesAPI.getAPI().setupAPI(this, "skywars", IArena.class, new ArenasConfig(this), new MessagesConfig(this), new IClassesConfig(this), new StatsConfig(this, false), new DefaultConfig(this, false), true);
		PluginInstance pinstance = api.pinstances.get(this);
		pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
		Bukkit.getPluginManager().registerEvents(this, this);

		IArenaListener t = new IArenaListener(this, pinstance);
		api.registerArenaListenerLater(this, t);
		pinstance.setArenaListener(t);

		pinstance.arenaSetup = new IArenaSetup();
		pinstance.getArenaListener().loseY = 100;
		try {
			pinstance.getClass().getMethod("setAchievementGuiEnabled", boolean.class);
			pinstance.setAchievementGuiEnabled(true);
		} catch (NoSuchMethodException e) {
			System.out.println("Update your MinigamesLib to the latest version to use the Achievement Gui.");
		}
		pli = pinstance;

		this.getConfig().addDefault("config.spawn_glass_blocks", true);

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		chestsconfig = new ChestsConfig(this);
		custom_chests = chestsconfig.getConfig().getBoolean("config.enabled");

		if (chestsconfig.getConfig().isSet("config.chests.")) {
			for (String c : chestsconfig.getConfig().getConfigurationSection("config.chests.").getKeys(false)) {
				String rawitems = chestsconfig.getConfig().getString("config.chests." + c + ".items");
				ArrayList<ItemStack> items = Util.parseItems(rawitems);
				chests.put(c, items);
			}
		}
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
			String arenaname_ = event.getItemInHand().getItemMeta().getDisplayName();

			if (arenaname_ == null)
				return;

			String args[] = arenaname_.split(":");
			String plugin = args[0];
			if (!plugin.equalsIgnoreCase("mgsykwars")) {
				return;
			}
			String arenaname = args[1];

			/*
			 * if (!Validator.isArenaValid(this, arenaname)) { p.sendMessage(ChatColor.RED + "Could not find arena."); return; }
			 */

			int i = pli.arenaSetup.autoSetSpawn(this, arenaname, event.getBlock().getLocation().clone().add(0.5D, 7D, 0.5D));
			event.getPlayer().sendMessage(pli.getMessagesConfig().successfully_set.replaceAll("<component>", "spawn " + Integer.toString(i)));

			if (this.getConfig().getBoolean("config.spawn_glass_blocks")) {
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
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player dead = event.getEntity();
		String playername = dead.getName();
		if (m.pli.global_players.containsKey(playername)) {
			event.getEntity().setHealth(20D);
			Player p = (Player) event.getEntity();
			p.setHealth(20D);
			if (m.pli.getClassesHandler().hasClass(playername)) {
				if (m.pli.getPClasses().get(playername).getInternalName().equalsIgnoreCase("extra_life")) {
					try {
						IArena a = (IArena) pli.global_players.get(playername);
						if (!a.used_extra_life.contains(playername)) {
							System.out.println("testt");
							Util.teleportPlayerFixed(Bukkit.getPlayer(playername), m.pli.global_players.get(playername).getPSpawnLocs().get(playername));
							a.used_extra_life.add(playername);
							return;
						}
					} catch (Exception e) {
						System.out.println("Your MinigamesLib version doesn't support the extra life kit, update please. " + e.getMessage());
					}
				}
			}
		}
		if (dead.getKiller() instanceof Player) {
			Player killer = (Player) dead.getKiller();
			if (pli.global_players.containsKey(dead.getName()) && pli.global_players.containsKey(killer.getName())) {
				Arena a = pli.global_players.get(dead.getName());
				if (a != null) {
					a.spectate(dead.getName());
				}
			}
		}
		pli.getArenaListener().onPlayerDeath(event);
	}

	ArrayList<Location> temp = new ArrayList<Location>();

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!custom_chests) {
			return;
		}
		if (event.hasBlock()) {
			if (event.getClickedBlock().getType() == Material.CHEST) {
				Player p = event.getPlayer();
				if (pli.global_players.containsKey(p.getName())) {
					Arena a = pli.global_players.get(p.getName());
					if (a.getArenaState() == ArenaState.INGAME) {
						if (!temp.contains(event.getClickedBlock().getLocation())) {
							temp.add(event.getClickedBlock().getLocation());
							Block b_ = event.getClickedBlock();
							((Chest) b_.getState()).getBlockInventory().clear();
							((Chest) b_.getState()).update();
							ArrayList<ItemStack> items = getChestItems();
							if (items != null) {
								for (ItemStack i : items) {
									if (i != null) {
										((Chest) b_.getState()).getBlockInventory().addItem(i);
									}
								}
							}
							((Chest) b_.getState()).update();
						}
						a.getSmartReset().addChanged(event.getClickedBlock(), event.getClickedBlock().getType().equals(Material.CHEST));
					}
				}
			}
		}
	}

	// TODO check if this is really percentage randomness
	public ArrayList<ItemStack> getChestItems() {
		double r = Math.random() * 100;
		int all = 0;
		System.out.println(r);
		for (String key : chests.keySet()) {
			all += chestsconfig.getConfig().getInt("config.chests." + key + ".percentage");
			System.out.println(all);
			if (all > r) {
				return chests.get(key);
			}
		}
		System.out.println("");
		return null;
	}

}
