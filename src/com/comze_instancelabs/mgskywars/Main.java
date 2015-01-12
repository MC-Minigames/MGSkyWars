package com.comze_instancelabs.mgskywars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
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

	LinkedHashMap<String, HashMap<ItemStack, Integer>> chests = new LinkedHashMap<String, HashMap<ItemStack, Integer>>();

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

		final MessagesConfig msgconfig = pli.getMessagesConfig();
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				msgconfig.getConfig().addDefault("messages.extra_life_msg", "&a<player> respawned because he used the extra life kit.");
				msgconfig.getConfig().options().copyDefaults(true);
				msgconfig.saveConfig();
			}
		}, 40L);

		chestsconfig = new ChestsConfig(this);
		custom_chests = chestsconfig.getConfig().getBoolean("config.enabled");

		if (chestsconfig.getConfig().isSet("config.chests.")) {
			for (String c : chestsconfig.getConfig().getConfigurationSection("config.chests.").getKeys(false)) {
				String rawitems = chestsconfig.getConfig().getString("config.chests." + c + ".items");
				HashMap<ItemStack, Integer> items = parseItems(rawitems);
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
			if (!plugin.equalsIgnoreCase("mgskywars")) {
				return;
			}
			String arenaname = args[1];

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
		if (event.getBlock().getType() == Material.CHEST) {
			if (pli.global_players.containsKey(event.getPlayer().getName())) {
				temp.add(event.getBlock().getLocation());
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
							Util.teleportPlayerFixed(Bukkit.getPlayer(playername), m.pli.global_players.get(playername).getPSpawnLocs().get(playername));
							a.used_extra_life.add(playername);
							try {
								String msg = ChatColor.translateAlternateColorCodes('&', pli.getMessagesConfig().getConfig().getString("messages.extra_life_msg"));
								for (String p_ : a.getAllPlayers()) {
									Bukkit.getPlayer(p_).sendMessage(msg.replaceAll("<player>", p.getName()));
								}
							} catch (Exception e) {
								;
							}
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
					System.out.println(dead.getName());
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

	public ArrayList<ItemStack> getChestItems() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		double r = Math.random() * 100;
		int all = 0;
		for (String key : chests.keySet()) {
			all += chestsconfig.getConfig().getInt("config.chests." + key + ".percentage");
			if (all > r) {
				HashMap<ItemStack, Integer> temp = chests.get(key);
				for (ItemStack item : temp.keySet()) {
					int i = (int) (Math.random() * 100);
					if (i <= temp.get(item)) {
						ret.add(item);
					}
				}
				break;
			}
		}
		if (ret.size() > 0) {
			return ret;
		}
		Random r_ = new Random();
		Object[] keys = chests.keySet().toArray();
		HashMap<ItemStack, Integer> randomContents = (HashMap<ItemStack, Integer>) chests.get(keys[r_.nextInt(keys.length)]);
		for (ItemStack item : randomContents.keySet()) {
			if (Math.random() * 100 >= randomContents.get(item)) {
				ret.add(item);
			}
		}
		return ret;
	}

	// example items: 351:6#ALL_DAMAGE:2#KNOCKBACK:2*1=NAME:LORE;267*1;3*64;3*64
	public static HashMap<ItemStack, Integer> parseItems(String rawitems) {
		HashMap<ItemStack, Integer> ret = new HashMap<ItemStack, Integer>();

		try {
			String[] a = rawitems.split(";");

			for (String rawitem : a) {
				int nameindex = rawitem.indexOf("=");
				String[] c = rawitem.split("\\*");
				int optional_armor_color_index = -1;
				int optional_skywars_percentage_index = -1;
				String itemid = c[0];
				String itemdata = "0";
				String[] enchantments_ = itemid.split("#");
				String[] enchantments = new String[enchantments_.length - 1];
				if (enchantments_.length > 1) {
					for (int i = 1; i < enchantments_.length; i++) {
						enchantments[i - 1] = enchantments_[i];
					}
				}
				itemid = enchantments_[0];
				String[] d = itemid.split(":");
				if (d.length > 1) {
					itemid = d[0];
					itemdata = d[1];
				}
				String itemamount = "1";
				String mod = "";
				if (c.length > 1) {
					mod = c[1];
					itemamount = mod;
					optional_armor_color_index = mod.indexOf("#");
					optional_skywars_percentage_index = mod.indexOf("%");
					if (optional_armor_color_index > 0) {
						itemamount = mod.substring(0, optional_armor_color_index);
					} else {
						if (optional_skywars_percentage_index > 0) {
							itemamount = mod.substring(0, optional_skywars_percentage_index);
						}
					}
				} else {
					mod = itemid;
					optional_skywars_percentage_index = mod.indexOf("%");
					if (optional_skywars_percentage_index > 0) {
						itemid = mod.substring(0, optional_skywars_percentage_index);
					}
				}
				
				if (nameindex > -1) {
					itemamount = c[1].substring(0, c[1].indexOf("="));
				}
				if (Integer.parseInt(itemid) < 1) {
					System.out.println("Invalid item id: " + itemid);
					continue;
				}
				ItemStack nitem = new ItemStack(Integer.parseInt(itemid), Integer.parseInt(itemamount), (short) Integer.parseInt(itemdata));
				ItemMeta m = nitem.getItemMeta();
				if (nitem.getType() != Material.ENCHANTED_BOOK) {
					for (String enchant : enchantments) {
						String[] e = enchant.split(":");
						String ench = e[0];
						String lv = "1";
						if (e.length > 1) {
							lv = e[1];
						}
						if (Enchantment.getByName(ench) != null) {
							m.addEnchant(Enchantment.getByName(ench), Integer.parseInt(lv), true);
						}
					}
				}

				if (nameindex > -1) {
					String namelore = rawitem.substring(nameindex + 1);
					String name = "";
					String lore = "";
					int i = namelore.indexOf(":");
					if (i > -1) {
						name = namelore.substring(0, i);
						lore = namelore.substring(i + 1);
					} else {
						name = namelore;
					}
					m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
					m.setLore(Arrays.asList(lore));
				}

				int optional_skywars_percentage = 100;
				if (optional_skywars_percentage_index > -1) {
					optional_skywars_percentage = Integer.parseInt(mod.substring(optional_skywars_percentage_index + 1));
				}

				// RGB Color support for Armor
				if (optional_armor_color_index > -1 && optional_skywars_percentage_index > -1) {
					m.setDisplayName(mod.substring(optional_armor_color_index, optional_skywars_percentage_index));
				}

				nitem.setItemMeta(m);
				if (nitem.getType() == Material.ENCHANTED_BOOK) {
					try {
						EnchantmentStorageMeta meta = (EnchantmentStorageMeta) nitem.getItemMeta();
						for (String enchant : enchantments) {
							String[] e = enchant.split(":");
							String ench = e[0];
							String lv = "1";
							if (e.length > 1) {
								lv = e[1];
							}
							if (Enchantment.getByName(ench) != null) {
								meta.addStoredEnchant(Enchantment.getByName(ench), Integer.parseInt(lv), true);
							}
						}
						nitem.setItemMeta(meta);
					} catch (Exception e) {
						System.out.println("Failed parsing enchanted book. " + e.getMessage());
					}
				}
				ret.put(nitem, optional_skywars_percentage);
			}
			if (ret == null || ret.size() < 1) {
				MinigamesAPI.getAPI().getLogger().severe("Found invalid class in config!");
			}
		} catch (Exception e) {
			if (MinigamesAPI.debug) {
				e.printStackTrace();
			}
			System.out.println("Failed to load class items: " + e.getMessage() + " at [1] " + e.getStackTrace()[1].getLineNumber() + " [0] " + e.getStackTrace()[0].getLineNumber());
			ItemStack rose = new ItemStack(Material.RED_ROSE);
			ItemMeta im = rose.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Sowwy, failed to load class.");
			rose.setItemMeta(im);
			ret.put(rose, 100);
		}
		return ret;
	}

}
