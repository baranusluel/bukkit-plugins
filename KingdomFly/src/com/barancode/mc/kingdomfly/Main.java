package com.barancode.mc.kingdomfly;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener{
	
	List<Player> flying = new LinkedList<Player>();
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()){
                	if (flying.contains(player)){
	                	Location loc = player.getLocation(); loc.setY(loc.getY() - 1);
	                	player.playSound(player.getLocation(), Sound.BLAZE_BREATH, 0.03F, 0.1F);
	                    ParticleEffects.sendToLocation(ParticleEffects.EXPLODE, loc, 0.2F, 0.05F, 0.2F, 0.0F, 20);
                	}
                }
            }
        }, 0L, 1L);
	}
	
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (args.length == 0){
			  Player player;
			  if (sender instanceof Player){
				  player = (Player)sender;
			  } else {
				  sender.sendMessage(ChatColor.RED + "You must be a player!");
				  return true;
			  }
			  if (!sender.hasPermission("survival.fly")){
				  sender.sendMessage(ChatColor.RED + "You do not have permission!");
				  return true;
			  }
			  if (flying.contains(player)){
				  player.setAllowFlight(false);
				  player.setFlying(false);
				  flying.remove(player);
				  player.sendMessage(replaceColors(getConfig().getString("flydisabled")));
				  player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1F);
			  } else {
				  player.setAllowFlight(true);
				  player.setFlying(true);
				  flying.add(player);
				  player.sendMessage(replaceColors(getConfig().getString("flyenabled")));
				  player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1F);
			  }
			  return true;
		  } else if (args.length == 1){
			  Player player = Bukkit.getPlayer(args[0]);
			  if (!sender.hasPermission("survival.flyothers")){
				  sender.sendMessage(ChatColor.RED + "You do not have permission!");
				  return true;
			  }
			  if (flying.contains(player)){
				  player.setAllowFlight(false);
				  player.setFlying(false);
				  flying.remove(player);
				  player.sendMessage(replaceColors(getConfig().getString("flydisabledothers")).replaceAll("<player>", args[0]));
				  player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1F);
			  } else {
				  player.setAllowFlight(true);
				  player.setFlying(true);
				  flying.add(player);
				  player.sendMessage(replaceColors(getConfig().getString("flyenabledothers")).replaceAll("<player>", args[0]));
				  player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1F);
			  }
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
