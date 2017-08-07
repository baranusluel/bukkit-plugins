package com.barancode.mc.bcmcbungeewarnings;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import net.md_5.bungee.api.plugin.Plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.barancode.mc.bcmcwarnings.Database;
import com.barancode.mc.bcmcwarnings.mysql.MySQL;
import com.barancode.mc.bcmcwarnings.sqlite.SQLite;

public class Main extends Plugin implements net.md_5.bungee.api.plugin.Listener{
	
	MySQL MySQL = new MySQL(this, "mcdb.serverbuilds.nl", "3306", "mine_Ste6275", "Ste6275", "ZjrQNTLLaqKif");
	Connection c = null;
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		c = MySQL.openConnection();
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e){
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
				  Statement statement;
				  List<String> warnings = new LinkedList<String>();
				try {
					statement = c.createStatement();
					  ResultSet res = statement.executeQuery("SELECT * FROM warnings WHERE name = '" + e.getPlayer().getName() + "';");
					  					  
					  while (res.next()){
						  if(res.getString("name") != null) {
							  warnings.add(res.getString("warning"));
						  }
					  }
				} catch (SQLException e) {
					e.printStackTrace();
				}  		
				  if (warnings.size() == 1){
					  e.getPlayer().sendMessage(ChatColor.AQUA + "You have " + ChatColor.RED + "1" + ChatColor.AQUA + " warning!");
					  e.getPlayer().sendMessage(ChatColor.AQUA + "To see it, do " + ChatColor.WHITE + "/warnings");
				  } else if (warnings.size() > 1){
					  e.getPlayer().sendMessage(ChatColor.AQUA + "You have " + ChatColor.RED + warnings.size() + ChatColor.AQUA + " warnings!");
					  e.getPlayer().sendMessage(ChatColor.AQUA + "To see your warnings, do " + ChatColor.WHITE + "/warnings");
				  }
            }
        }, 2 * 20L);
	}
	
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (cmd.getName().equalsIgnoreCase("warnings")){
			  if (args.length == 0){
				  Statement statement;
				  List<String> warnings = new LinkedList<String>();
				try {
					statement = c.createStatement();
					  ResultSet res = statement.executeQuery("SELECT * FROM warnings WHERE name = '" + sender.getName() + "';");
					  					  
					  while (res.next()){
						  if(res.getString("name") != null) {
							  warnings.add(res.getString("warning"));
						  }
					  }
				} catch (SQLException e) {
					e.printStackTrace();
				}  				  

				  if (warnings.size() == 0) sender.sendMessage(ChatColor.AQUA + "You have no warnings");
				  else if (warnings.size() == 1) sender.sendMessage(ChatColor.AQUA + "You have " + ChatColor.RED + "1" + ChatColor.AQUA + " warning!");
				  else sender.sendMessage(ChatColor.AQUA + "You have " + ChatColor.RED + warnings.size() + ChatColor.AQUA + " warnings!");
				  for (String s : warnings){
					  sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + replaceColors(s));
				  }
				  return true;
			  } else if (args.length == 1){
				  if (!sender.hasPermission("warnings.others")){
					  sender.sendMessage(ChatColor.RED + "You do not have permission!");
					  return true;
				  }
				  Statement statement;
				  List<String> warnings = new LinkedList<String>();
				try {
					statement = c.createStatement();
					  ResultSet res = statement.executeQuery("SELECT * FROM warnings WHERE name = '" + args[0] + "';");
					  					  
					  while (res.next()){
						  if(res.getString("name") != null) {
							  warnings.add(res.getString("warning"));
						  }
					  }
				} catch (SQLException e) {
					e.printStackTrace();
				}  				  

				  if (warnings.size() == 0) sender.sendMessage(ChatColor.AQUA + args[0] + " has no warnings");
				  else if (warnings.size() == 1) sender.sendMessage(ChatColor.AQUA + args[0] + " has " + ChatColor.RED + "1" + ChatColor.AQUA + " warning!");
				  else sender.sendMessage(ChatColor.AQUA + args[0] + " has " + ChatColor.RED + warnings.size() + ChatColor.AQUA + " warnings!");
				  for (String s : warnings){
					  sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + replaceColors(s));
				  }
				  return true;
			  }
		  } else if (cmd.getName().equalsIgnoreCase("warn")) {
				  if (!sender.hasPermission("warnings.give")){
					  sender.sendMessage(ChatColor.RED + "You do not have permission!");
					  return true;
				  }
				  				  
					String finalString = "";
	    			for (int i = 1; i < args.length; i++) {
	    			    finalString += args[i].replaceAll("'", "") + ' ';
	    			}
	    			finalString = finalString.trim();
	    			
					  try {
						  Statement statement = c.createStatement();
							statement.executeUpdate("INSERT INTO warnings (`name`, `warning`) VALUES ('" + args[0] + "', '" + finalString + "');");
						} catch (SQLException e) {
							e.printStackTrace();
						}
	    			
	    			sender.sendMessage(ChatColor.GOLD + "You have given " + args[0] + " a warning for: " + ChatColor.GREEN + finalString);
	    			
	    			
	    			
					  Statement statement;
					  List<String> warnings = new LinkedList<String>();
					try {
						statement = c.createStatement();
						  ResultSet res = statement.executeQuery("SELECT * FROM warnings WHERE name = '" + args[0] + "';");
						  					  
						  while (res.next()){
							  if(res.getString("name") != null) {
								  warnings.add(res.getString("warning"));
							  }
						  }
					} catch (SQLException e) {
						e.printStackTrace();
					}  				  

					  if (warnings.size() == 1) sender.sendMessage(ChatColor.GOLD + args[0] + " now has " + ChatColor.RED + "1" + ChatColor.GOLD + " warning!");
					  else sender.sendMessage(ChatColor.AQUA + args[0] + " now has " + ChatColor.RED + warnings.size() + ChatColor.AQUA + " warnings!");
					  for (String s : warnings){
						  sender.sendMessage(ChatColor.AQUA + "- " + ChatColor.GREEN + replaceColors(s));
					  }
					  
					  
		    			if (Bukkit.getOfflinePlayer(args[0]).isOnline()){
		    				Player p = Bukkit.getPlayer(args[0]);
		    				p.sendMessage(ChatColor.AQUA + "You have been warned by " + sender.getName() + " for: " + ChatColor.GREEN + finalString);
		    				if (warnings.size() > 1) p.sendMessage(ChatColor.AQUA + "To see your other warnings, do " + ChatColor.WHITE + "/warnings");
		    			}
		    			
		    			
				  return true;
		  } else if (cmd.getName().equalsIgnoreCase("clearwarnings") && args.length == 1){
			  if (!sender.hasPermission("warnings.clear")){
				  sender.sendMessage(ChatColor.RED + "You do not have permission!");
				  return true;
			  }
			  try {
				  Statement statement = c.createStatement();
					statement.executeUpdate("DELETE FROM warnings WHERE name = '" + args[0] + "';");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			  sender.sendMessage(ChatColor.GOLD + "You have cleared " + args[0] + "'s warnings");
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
