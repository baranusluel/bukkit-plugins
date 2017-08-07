package com.barancode.creative.tokens;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.barancode.mc.db.TokenDatabase;
import com.barancode.mc.db.UUIDDatabase;

public class Main extends JavaPlugin{
	TokenDatabase td = new TokenDatabase();
	UUIDDatabase ud = new UUIDDatabase();
	BukkitScheduler scheduler;
	
	public void onEnable(){
		saveDefaultConfig();
		scheduler = Bukkit.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@SuppressWarnings("deprecation")
			@Override
			public void run(){
				for (String s : getConfig().getKeys(false)){
					long time = getConfig().getLong(s);
					if (time < System.currentTimeMillis()){
						String name = ud.getUsername(UUID.fromString(s));
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + name + " remove worldedit.*");
						getConfig().set(s, null);
						if (Bukkit.getOfflinePlayer(name).isOnline()) Bukkit.getPlayer(name).sendMessage(ChatColor.GOLD + "Your access to WorldEdit has ended");
					}
				}
				saveConfig();
			}
		}, 0L, 60 * 20L);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "Your tokens: " + ChatColor.AQUA + td.getAmount(((Player)sender).getUniqueId()));
			sender.sendMessage(ChatColor.GREEN + "/token worldedit " + ChatColor.DARK_GREEN + "- Buy WorldEdit for 1 hour, with 50 tokens");
			sender.sendMessage(ChatColor.GREEN + "/token send <player> <amount> " + ChatColor.DARK_GREEN + "- Send someone an amount of your tokens");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("send")){
			UUID uuid = ud.getUUID(args[1]);
			if (uuid == null || uuid.toString().equalsIgnoreCase("")){
				sender.sendMessage(ChatColor.RED + "A player by that name hasn't played on BCMC");
				return true;
			}
			int amount;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e){
				sender.sendMessage(ChatColor.RED + "Please enter a valid amount");
				return true;
			}
			if (td.takeAmount(((Player)sender).getUniqueId(), amount)){
				td.addAmount(uuid, amount);
				sender.sendMessage(ChatColor.GOLD + "You have sent " + amount + " tokens to " + args[1]);
				if (Bukkit.getOfflinePlayer(args[1]).isOnline()) Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GOLD + sender.getName() + " has sent you " + amount + " tokens");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have that many tokens!");
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("worldedit")){
			UUID uuid = ((Player)sender).getUniqueId();
			if (td.takeAmount(uuid, 50)){
				getConfig().set(uuid.toString(), System.currentTimeMillis() + getConfig().getLong(uuid.toString()) + (60 * 60 * 1000));
				saveConfig();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + sender.getName() + " add worldedit.*");
				sender.sendMessage(ChatColor.GOLD + "You now have access to WorldEdit for one hour");
			} else {
				sender.sendMessage(ChatColor.RED + "You need 50 tokens!");
			}
			return true;
		}
		return false;
	}
}
