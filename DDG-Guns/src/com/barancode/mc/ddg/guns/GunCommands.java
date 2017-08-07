package com.barancode.mc.ddg.guns;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GunCommands implements CommandExecutor{
	
	Main plugin;
	
	public GunCommands(Main plugin){
		this.plugin = plugin;
	}
	  
	  @Override
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  Player player;
		  if (sender instanceof Player) {
		      player = (Player) sender;
	      } else {
		      sender.sendMessage(ChatColor.RED + "You must be a player!");
		      return true;
		  }
		  		  
		  if (cmd.getName().equalsIgnoreCase("setjoinspawn")){
		      Location loc = player.getLocation();
		      plugin.df.getCustomConfig().set("joinspawn.world", loc.getWorld().getName().toString());
		      plugin.df.getCustomConfig().set("joinspawn.x", Double.valueOf(loc.getX()));
		      plugin.df.getCustomConfig().set("joinspawn.y", Double.valueOf(loc.getY()));
		      plugin.df.getCustomConfig().set("joinspawn.z", Double.valueOf(loc.getZ()));
		      plugin.df.getCustomConfig().set("joinspawn.yaw", Float.valueOf(loc.getYaw()));
		      plugin.df.getCustomConfig().set("joinspawn.pitch", Float.valueOf(loc.getPitch()));
		      plugin.df.saveCustomConfig();
		      player.sendMessage("You have set the join spawn");
		      return true;
		  } else if (cmd.getName().equalsIgnoreCase("setlobby")){
		      Location loc = player.getLocation();
		      plugin.df.getCustomConfig().set("lobby.world", loc.getWorld().getName().toString());
		      plugin.df.getCustomConfig().set("lobby.x", Double.valueOf(loc.getX()));
		      plugin.df.getCustomConfig().set("lobby.y", Double.valueOf(loc.getY()));
		      plugin.df.getCustomConfig().set("lobby.z", Double.valueOf(loc.getZ()));
		      plugin.df.getCustomConfig().set("lobby.yaw", Float.valueOf(loc.getYaw()));
		      plugin.df.getCustomConfig().set("lobby.pitch", Float.valueOf(loc.getPitch()));
		      plugin.df.saveCustomConfig();
		      player.sendMessage("You have set the lobby spawn");
		      return true;
		  } else if (cmd.getName().equalsIgnoreCase("setspawnteam1")){
		      Location loc = player.getLocation();
		      plugin.df.getCustomConfig().set("team1.world", loc.getWorld().getName().toString());
		      plugin.df.getCustomConfig().set("team1.x", Double.valueOf(loc.getX()));
		      plugin.df.getCustomConfig().set("team1.y", Double.valueOf(loc.getY()));
		      plugin.df.getCustomConfig().set("team1.z", Double.valueOf(loc.getZ()));
		      plugin.df.getCustomConfig().set("team1.yaw", Float.valueOf(loc.getYaw()));
		      plugin.df.getCustomConfig().set("team1.pitch", Float.valueOf(loc.getPitch()));
		      plugin.df.saveCustomConfig();
		      player.sendMessage("You have set the Team 1 (defender) spawn");
		      return true;
		  } else if (cmd.getName().equalsIgnoreCase("setspawnteam2")){
		      Location loc = player.getLocation();
		      plugin.df.getCustomConfig().set("team2.world", loc.getWorld().getName().toString());
		      plugin.df.getCustomConfig().set("team2.x", Double.valueOf(loc.getX()));
		      plugin.df.getCustomConfig().set("team2.y", Double.valueOf(loc.getY()));
		      plugin.df.getCustomConfig().set("team2.z", Double.valueOf(loc.getZ()));
		      plugin.df.getCustomConfig().set("team2.yaw", Float.valueOf(loc.getYaw()));
		      plugin.df.getCustomConfig().set("team2.pitch", Float.valueOf(loc.getPitch()));
		      plugin.df.saveCustomConfig();
		      player.sendMessage("You have set the Team 2 (attacker) spawn");
		      return true;
		  } else if (cmd.getName().equalsIgnoreCase("setobjective") && args.length == 1){
		      Location loc = player.getLocation();
		      plugin.df.getCustomConfig().set("objectives." + args[0] + ".world", loc.getWorld().getName().toString());
		      plugin.df.getCustomConfig().set("objectives." + args[0] + ".x", Double.valueOf(loc.getX()));
		      plugin.df.getCustomConfig().set("objectives." + args[0] + ".y", Double.valueOf(loc.getY()));
		      plugin.df.getCustomConfig().set("objectives." + args[0] + ".z", Double.valueOf(loc.getZ()));
		      plugin.df.getCustomConfig().set("objectives." + args[0] + ".yaw", Float.valueOf(loc.getYaw()));
		      plugin.df.getCustomConfig().set("objectives." + args[0] + ".pitch", Float.valueOf(loc.getPitch()));
		      plugin.df.saveCustomConfig();
		      player.sendMessage("You have set Objective " + args[0]);
		      return true;
		  }
		  return false;
	  }
}
