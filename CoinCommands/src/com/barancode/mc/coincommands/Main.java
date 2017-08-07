package com.barancode.mc.coincommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.barancode.mc.survival.Announcements;

public class Main extends JavaPlugin{
	Database database = new Database(this);
	BukkitScheduler scheduler;
	
	public void onEnable(){
		saveDefaultConfig();
		scheduler = Bukkit.getScheduler();
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args)
	{
		if (args.length == 4){
			if (args[0].equalsIgnoreCase("coin") && args[1].equalsIgnoreCase("add")){
				if (!sender.hasPermission("coincommands.addcoins")){
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.permission")));
				}
				getConfig().set("data." + args[2], getConfig().getInt("data." + args[2]) + Integer.parseInt(args[3]));
				saveConfig();
				String message = getConfig().getString("messages.received-coins");
				if (message.equals("")) return true;
				message = ChatColor.translateAlternateColorCodes('&', message);
				message = message.replaceAll("<amount>", args[3]);
				sender.sendMessage(message);
				return true;
			}
		}
		if (args.length == 3){
			if (args[0].equalsIgnoreCase("setcoins")){
				if (!sender.hasPermission("coincommands.setcoins")){
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.permission")));
				}
				getConfig().set("data." + args[2], Integer.parseInt(args[3]));
				saveConfig();
				String message = getConfig().getString("messages.received-coins");
				if (message.equals("")) return true;
				message = ChatColor.translateAlternateColorCodes('&', message);
				message = message.replaceAll("<amount>", args[3]);
				sender.sendMessage(message);
				return true;
			}
		}
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("coins")){
				if (!sender.hasPermission("coincommands.balance")){
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.permission")));
				}
				int amount = database.getCoins(((Player)sender).getUniqueId());
				String message = getConfig().getString("messages.balance");
				message = ChatColor.translateAlternateColorCodes('&', message);
				message = message.replaceAll("<amount>", amount + "");
				sender.sendMessage(message);
				return true;
			}
		}
		if (args.length == 2){
			if (args[0].equalsIgnoreCase("coins")){
				if (!sender.hasPermission("coincommands.balance.others")){
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.permission")));
				}
		        scheduler.scheduleAsyncDelayedTask(this, new Runnable() {
		            @Override
		            public void run(){
						int amount = database.getCoins(Utils.getUUID(((Player)sender).getName()));
						String message = getConfig().getString("messages.balance");
						message = ChatColor.translateAlternateColorCodes('&', message);
						message = message.replaceAll("<amount>", amount + "");
						message = message.replaceAll("<player>", args[1]);
						sender.sendMessage(message);
		            }
		        }, 0L);
				return true;
			}
		}
		if (args.length == 0){
			String message = getConfig().getString("messages.help");
			message = ChatColor.translateAlternateColorCodes('&', message);
			sender.sendMessage(message);
			return true;
		}
		
		if (!sender.hasPermission("coincommands.command")){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.permission")));
			return true;
		}
		
		if (!getConfig().getConfigurationSection("commands").getKeys(false).contains(args[0])){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.invalid-command")));
			return true;
		}
		
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.player")));
			return true;
		}
		
		  String finalString = "";
		  for (int i = 0; i < args.length; i++) {
			  finalString += args[i] + ' ';
		  }
		  finalString = finalString.trim();
		  if (finalString.equals("")) return false;
		  
		  int balance = getConfig().getInt("data." + ((Player)sender).getName());
		  int price = getConfig().getInt("commands." + args[0] + ".cost");
		  
		  if (balance < price){
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.enough-coins")));
				return true;
		  }
		  
		  getConfig().set("data." + ((Player)sender).getName(), balance - price);
		  saveConfig();
		  ((Player)sender).sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("commands." + args[0] + ".message")));
		  
		  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalString);
		  return true;
	}
}
