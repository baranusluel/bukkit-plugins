package com.barancode.mc.checkpoints;


import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Main extends JavaPlugin implements Listener{
	
	BukkitScheduler scheduler = null;
	ScoreboardManager manager;
	HashMap<UUID, Scoreboard> scoreboards = new HashMap<UUID, Scoreboard>();
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
        		manager = Bukkit.getScoreboardManager();
            }
        }, 0L);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("setcheckpoint")){
			Player player = Bukkit.getPlayer(args[0]);
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("back")){
					if (getConfig().getBoolean("spawns." + player.getUniqueId() + ".exists")){
				          World w = player.getWorld();
				          double x = getConfig().getDouble("spawns." + player.getUniqueId() + ".x");
				          double y = getConfig().getDouble("spawns." + player.getUniqueId() + ".y");
				          double z = getConfig().getDouble("spawns." + player.getUniqueId() + ".z");
				          float yaw = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".yaw");
				          float pitch = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".pitch");
				          Location loc = new Location(w, x, y, z, yaw, pitch);
				          player.teleport(loc);
			        	  player.sendMessage(ChatColor.GREEN + "You have been teleported to your last checkpoint");
			        	  return true;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have a checkpoint");
						return true;
					}
				}/* else if (args[1].equalsIgnoreCase("first")){
					getConfig().set("spawns." + player.getUniqueId() + ".x", player.getLocation().getX());
					getConfig().set("spawns." + player.getUniqueId() + ".y", player.getLocation().getY());
					getConfig().set("spawns." + player.getUniqueId() + ".z", player.getLocation().getZ());
					getConfig().set("spawns." + player.getUniqueId() + ".yaw", player.getLocation().getYaw());
					getConfig().set("spawns." + player.getUniqueId() + ".pitch", player.getLocation().getPitch());
					getConfig().set("spawns." + player.getUniqueId() + ".exists", true);
					getConfig().set("spawns." + player.getUniqueId() + ".count", 0);
					getConfig().set("spawns." + player.getUniqueId() + ".totalcount", 0);
					return true;
				}*/
			}
			getConfig().set("spawns." + player.getUniqueId() + ".x", player.getLocation().getX());
			getConfig().set("spawns." + player.getUniqueId() + ".y", player.getLocation().getY());
			getConfig().set("spawns." + player.getUniqueId() + ".z", player.getLocation().getZ());
			getConfig().set("spawns." + player.getUniqueId() + ".yaw", player.getLocation().getYaw());
			getConfig().set("spawns." + player.getUniqueId() + ".pitch", player.getLocation().getPitch());
			getConfig().set("spawns." + player.getUniqueId() + ".exists", true);
			//getConfig().set("spawns." + player.getUniqueId() + ".count", 0);
			player.sendMessage(ChatColor.GREEN + "You have set your spawnpoint");
			saveConfig();
			return true;
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void death(PlayerDeathEvent event){
		event.setDroppedExp(0);
		event.getDrops().clear();
	}	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void respawn(final PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if (getConfig().getBoolean("spawns." + player.getUniqueId() + ".exists")){
	          World w = player.getWorld();
	          double x = getConfig().getDouble("spawns." + player.getUniqueId() + ".x");
	          double y = getConfig().getDouble("spawns." + player.getUniqueId() + ".y");
	          double z = getConfig().getDouble("spawns." + player.getUniqueId() + ".z");
	          float yaw = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".yaw");
	          float pitch = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".pitch");
	          Location loc = new Location(w, x, y, z, yaw, pitch);
	          event.setRespawnLocation(loc);
	          /*int count = getConfig().getInt("spawns." + player.getUniqueId() + ".count");
	          int totalcount = getConfig().getInt("spawns." + player.getUniqueId() + ".totalcount");
	          count++;
	          totalcount++;
	          getConfig().set("spawns." + player.getUniqueId() + ".count", count);
	          getConfig().set("spawns." + player.getUniqueId() + ".totalcount", totalcount);*/
	          saveConfig();
	          /*if (count != 1){
	        	  player.sendMessage(ChatColor.GREEN + "Teleporting you back to the last checkpoint.");
	        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have died " + count + " times in this level.");
	        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have died " + totalcount + " times in total.");
	          } else {
	        	  player.sendMessage(ChatColor.GREEN + "Teleporting you back to the last checkpoint.");
	        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have only died 1 time in this level.");
	        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have died " + totalcount + " times in total.");
	          }*/
		}
		
		// THE FOLLOWING IS ONLY FOR MH
		
  	/*	scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run(){
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, true));
			}
		}, 1L);*/
	}
	
	/*@EventHandler
	public void onJoin(final PlayerJoinEvent e){
  		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run(){
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, true));
			}
		}, 1L);
	}*/
}
