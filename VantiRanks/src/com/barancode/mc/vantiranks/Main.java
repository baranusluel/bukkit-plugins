package com.barancode.mc.vantiranks;

import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	public void onEnable(){
	    saveDefaultConfig();
	    
	    List<String> ranks = getConfig().getStringList("ranks");
	    for (String s : ranks)
	    	getLogger().info(s);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 2 && cmd.getName().equalsIgnoreCase("rank")){
			if (!sender.hasPermission("rank." + args[1])){
				sender.sendMessage(ChatColor.RED + "You do not have permission!");
				return true;
			}
			List<String> ranks = getConfig().getStringList("ranks");
			if (ranks.contains(args[1])){
				List<String> commands = getConfig().getStringList("rank." + args[1] + ".commands");
				for (String command : commands){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("<name>", args[0]));
				}
				Bukkit.getPlayer(args[0]).sendMessage(replaceColors(getConfig().getString("rank." + args[1] + ".message")));
			} else {
				sender.sendMessage(ChatColor.RED + "That rank doesn't exist!");
			}
			return true;
		} else if (args.length == 0 && cmd.getName().equalsIgnoreCase("reloadranks")){
			reloadConfig();
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
