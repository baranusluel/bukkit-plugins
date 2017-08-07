package com.barancode.mc.durindir;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin{
	
	Player player;
	
	public void onEnable(){
		saveDefaultConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player) {
	        player = (Player) sender;
	    } else {
	        sender.sendMessage(ChatColor.RED + "You must be a player!");
	        return true;
	    }
		  
		if (cmd.getName().equalsIgnoreCase("durindir")){
			if (player.hasPermission("kingdom.durindir")){
				player.sendMessage(replaceColors(getConfig().getString("beforeteleport")));
				BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
		            @Override
		            public void run(){
		                World w = Bukkit.getWorld(getConfig().getString("spawn.world"));
		                double x = getConfig().getDouble("spawn.x");
		                double y = getConfig().getDouble("spawn.y");
		                double z = getConfig().getDouble("spawn.z");
		                float yaw = (float)getConfig().getDouble("spawn.yaw");
		                float pitch = (float)getConfig().getDouble("spawn.pitch");
		                Location location = new Location(w, x, y, z, yaw, pitch);

		                player.teleport(location);
		            	player.sendMessage(replaceColors(getConfig().getString("onteleport")));
		            }
		        }, 10 * 20L);
			} else player.sendMessage(replaceColors(getConfig().getString("nopermission")));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setspawndurindir")){
			if (player.hasPermission("kingdom.durindirsetspawn")){
			    Location loc = player.getLocation();
			    getConfig().set("spawn.world", loc.getWorld().getName().toString());
			    getConfig().set("spawn.x", Double.valueOf(loc.getX()));
			    getConfig().set("spawn.y", Double.valueOf(loc.getY()));
			    getConfig().set("spawn.z", Double.valueOf(loc.getZ()));
			    getConfig().set("spawn.yaw", Float.valueOf(loc.getYaw()));
			    getConfig().set("spawn.pitch", Float.valueOf(loc.getPitch()));
			    saveConfig();
				sender.sendMessage("You have set the durindir spawn");
			} else player.sendMessage(replaceColors(getConfig().getString("nopermission")));
			return true;
		}
		return false;
	}
	
	  public String replaceColors(String message){
		    message = message.replaceAll("&0", ChatColor.BLACK + "");
		    message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
		    message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
		    message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
		    message = message.replaceAll("&4", ChatColor.DARK_RED + "");
		    message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		    message = message.replaceAll("&6", ChatColor.GOLD + "");
		    message = message.replaceAll("&7", ChatColor.GRAY + "");
		    message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
		    message = message.replaceAll("&9", ChatColor.BLUE + "");
		    message = message.replaceAll("(?i)&a", ChatColor.GREEN + "");
		    message = message.replaceAll("(?i)&b", ChatColor.AQUA + "");
		    message = message.replaceAll("(?i)&c", ChatColor.RED + "");
		    message = message.replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE + "");
		    message = message.replaceAll("(?i)&e", ChatColor.YELLOW + "");
		    message = message.replaceAll("(?i)&f", ChatColor.WHITE + "");
		    message = message.replaceAll("(?i)&l", ChatColor.BOLD + "");
		    message = message.replaceAll("(?i)&o", ChatColor.ITALIC + "");
		    message = message.replaceAll("(?i)&m", ChatColor.STRIKETHROUGH + "");
		    message = message.replaceAll("(?i)&n", ChatColor.UNDERLINE + "");
		    message = message.replaceAll("(?i)&k", ChatColor.MAGIC + "");
		    message = message.replaceAll("(?i)&r", ChatColor.RESET + "");
		    return message;
	  }
}
