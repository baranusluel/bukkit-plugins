package com.barancode.buildguess;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor{
	Main plugin;
	Location buildFirst = null;
	Location rollbackFirst = null;
	
	public Commands(Main plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("setspawn")){
			if (!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Only players can do that!");
				return true;
			}
			Location loc = ((Player)sender).getLocation();
			Main.config.set("spawn.x", loc.getX());
			Main.config.set("spawn.y", loc.getY());
			Main.config.set("spawn.z", loc.getZ());
			Main.config.set("spawn.pitch", loc.getPitch());
			Main.config.set("spawn.yaw", loc.getYaw());
			plugin.saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have updated the spawn location");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setbuildspawn")){
			if (!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Only players can do that!");
				return true;
			}
			Location loc = ((Player)sender).getLocation();
			Main.config.set("buildspawn.x", loc.getX());
			Main.config.set("buildspawn.y", loc.getY());
			Main.config.set("buildspawn.z", loc.getZ());
			Main.config.set("buildspawn.pitch", loc.getPitch());
			Main.config.set("buildspawn.yaw", loc.getYaw());
			plugin.saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have updated the builder spawn location");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setpos") && args.length == 2){
			if (!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Only players can do that!");
				return true;
			}
			Player p = (Player)sender;
			if (args[0].equalsIgnoreCase("build")){
				if (args[1].equals("1")){
					buildFirst = Utils.getTargetBlock(p, 200).getLocation();
					p.sendMessage(ChatColor.YELLOW + "You have set the first corner");
					return true;
				} else if (args[1].equals("2")){
					if (buildFirst == null){
						sender.sendMessage(ChatColor.RED + "Set the 1st location first!");
						return true;
					}
					Location second = Utils.getTargetBlock(p, 200).getLocation();
					plugin.matchman.saveRegion(buildFirst, second, "build");
					buildFirst = null;
					p.sendMessage(ChatColor.YELLOW + "You have set up the build region");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("rollback")){
				if (args[1].equals("1")){
					rollbackFirst = Utils.getTargetBlock(p, 200).getLocation();
					p.sendMessage(ChatColor.YELLOW + "You have set the first corner");
					return true;
				} else if (args[1].equals("2")){
					if (rollbackFirst == null){
						sender.sendMessage(ChatColor.RED + "Set the 1st location first!");
						return true;
					}
					Location second = Utils.getTargetBlock(p, 200).getLocation();
					plugin.matchman.saveRegion(rollbackFirst, second, "rollback");
					rollbackFirst = null;
					p.sendMessage(ChatColor.YELLOW + "You have set up the rollback region");
					return true;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("setbook")){
			if (!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Only players can do that!");
				return true;
			}
			Player p = (Player)sender;
			plugin.book.save(p.getItemInHand());
			plugin.bookItem = plugin.book.getBook();
			p.sendMessage(ChatColor.GOLD + "You have set the item in your hand as the information book");
		}
		return false;
	}
}
