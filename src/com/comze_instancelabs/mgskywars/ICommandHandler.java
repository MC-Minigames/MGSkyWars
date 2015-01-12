package com.comze_instancelabs.mgskywars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.commands.CommandHandler;

public class ICommandHandler extends CommandHandler {

	@Override
	public boolean setSpawn(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p){
		if (!sender.hasPermission(uber_permission + ".setup")) {
			sender.sendMessage(pli.getMessagesConfig().no_perm);
			return true;
		}
		if (args.length > 1) {
			p.sendMessage(ChatColor.AQUA + "Place down the DragonEgg at every spawn you want.");
			ItemStack item = new ItemStack(Material.DRAGON_EGG);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName("mgskywars:" + args[1]);
			item.setItemMeta(im);
			p.getInventory().addItem(item);
			p.updateInventory();
		} else {
			sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena>");
		}
		return true;
	}
	
}
