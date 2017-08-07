package com.barancode.mc.kingdomwarnings;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener{
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e){
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
				  List<String> warnings = getConfig().getStringList("warnings." + e.getPlayer().getName());
				  if (warnings.size() == 0) e.getPlayer().sendMessage(ChatColor.GOLD + "You have no warnings");
				  else if (warnings.size() == 1) e.getPlayer().sendMessage(ChatColor.GOLD + "You have " + ChatColor.RED + "1" + ChatColor.GOLD + " warning!");
				  else e.getPlayer().sendMessage(ChatColor.GOLD + "You have " + ChatColor.RED + warnings.size() + ChatColor.GOLD + " warnings!");
            }
        }, 2 * 20L);
	}
	
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (cmd.getName().equalsIgnoreCase("waarschuwing")){
			  if (args.length == 0){
				  List<String> warnings = getConfig().getStringList("warnings." + sender.getName());
				  if (warnings.size() == 0) sender.sendMessage(ChatColor.GOLD + "You have no warnings");
				  else if (warnings.size() == 1) sender.sendMessage(ChatColor.GOLD + "You have " + ChatColor.RED + "1" + ChatColor.GOLD + " warning!");
				  else sender.sendMessage(ChatColor.GOLD + "You have " + ChatColor.RED + warnings.size() + ChatColor.GOLD + " warnings!");
				  for (String s : warnings){
					  sender.sendMessage(ChatColor.GOLD + "- " + replaceColors(s));
				  }
				  return true;
			  } else if (args.length == 1){
				  if (!sender.hasPermission("waarschuwing.zie")){
					  sender.sendMessage(ChatColor.RED + "You do not have permission!");
					  return true;
				  }
				  List<String> warnings = getConfig().getStringList("warnings." + args[0]);
				  if (warnings.size() == 0) sender.sendMessage(ChatColor.GOLD + args[0] + " has no warnings");
				  else if (warnings.size() == 1) sender.sendMessage(ChatColor.GOLD + args[0] + " has " + ChatColor.RED + "1" + ChatColor.GOLD + " warning!");
				  else sender.sendMessage(ChatColor.GOLD + args[0] + " has " + ChatColor.RED + warnings.size() + ChatColor.GOLD + " warnings!");
				  for (String s : warnings){
					  sender.sendMessage(ChatColor.GOLD + "- " + replaceColors(s));
				  }
				  return true;
			  } else {
				  if (!sender.hasPermission("waarschuwing.geef")){
					  sender.sendMessage(ChatColor.RED + "You do not have permission!");
					  return true;
				  }
				  List<String> warnings = getConfig().getStringList("warnings." + args[0]);
				  
					String finalString = "";
	    			for (int i = 1; i < args.length; i++) {
	    			    finalString += args[i] + ' ';
	    			}
	    			finalString = finalString.trim();
	    			
	    			warnings.add(finalString);
	    			getConfig().set("warnings." + args[0], warnings);
	    			
	    			sender.sendMessage(ChatColor.GOLD + "You have given " + args[0] + " a warning for: " + finalString);
				  
				  if (warnings.size() == 1) sender.sendMessage(ChatColor.GOLD + args[0] + " now has " + ChatColor.RED + "1" + ChatColor.GOLD + " warning!");
				  else sender.sendMessage(ChatColor.GOLD + args[0] + " now has " + ChatColor.RED + warnings.size() + ChatColor.GOLD + " warnings!");
				  for (String s : warnings){
					  sender.sendMessage(ChatColor.GOLD + "- " + replaceColors(s));
				  }
				  saveConfig();
				  return true;
			  }
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
