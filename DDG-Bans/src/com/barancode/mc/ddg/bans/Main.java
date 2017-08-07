package com.barancode.mc.ddg.bans;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	/* I did not use this event, because it can not get the player. I used a PlayerInteractEvent instead
	 * 
	 * @EventHandler(priority=EventPriority.HIGHEST)
	public void portalEvent(PortalCreateEvent e){
		
		e.setCancelled(true);
	}*/
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void interact(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.OBSIDIAN && e.getItem().getType() == Material.FLINT_AND_STEEL){
			if (!e.getPlayer().hasPermission("kingdom.portal")){
				e.getPlayer().sendMessage(replaceColors(getConfig().getString("portal-nopermission")));
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void joinEvent(PlayerLoginEvent e){
		if (getConfig().getBoolean("players." + e.getPlayer().getName() + ".banned")){
			Date date = new Date();
			if (getConfig().getInt("players." + e.getPlayer().getName() + ".time") == 0){
				e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
				String bannedmessage = getConfig().getString("permanent-bannedmessage");
				bannedmessage = bannedmessage.replaceAll("<banner>", getConfig().getString("players." + e.getPlayer().getName() + ".banner"));
				bannedmessage = bannedmessage.replaceAll("<reason>", getConfig().getString("players." + e.getPlayer().getName() + ".reason"));
				e.setKickMessage(replaceColors(bannedmessage));
			} else if (date.getTime() < getConfig().getLong("players." + e.getPlayer().getName() + ".time")){
				e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
				String bannedmessage = getConfig().getString("temp-bannedmessage");
				bannedmessage = bannedmessage.replaceAll("<banner>", getConfig().getString("players." + e.getPlayer().getName() + ".banner"));
				bannedmessage = bannedmessage.replaceAll("<reason>", getConfig().getString("players." + e.getPlayer().getName() + ".reason"));
				long left = getConfig().getLong("players." + e.getPlayer().getName() + ".time") - date.getTime(); left = left / (1000 * 60);
				int minutes = (int) (left % 60);
				left /= 60;
				int hours = (int) (left % 24);
				left /= 24;
				int days = (int) left;
				String time = "";
				if (days == 1) time += "1 " + getConfig().getString("day") + " ";
				else if (days > 1) time += days + " " + getConfig().getString("days") + " ";
				if (hours == 1) time += "1 " + getConfig().getString("hour") + " ";
				else if (hours > 1) time += hours + " " + getConfig().getString("hours") + " ";
				if (minutes == 1) time += "1 " + getConfig().getString("minute") + " ";
				else if (minutes > 1) time += minutes + " " + getConfig().getString("minutes") + " ";
				
				if (time.equals("")) time = "< 1 " + getConfig().getString("minute") + " ";
				bannedmessage = bannedmessage.replaceAll("<time>", time);
				e.setKickMessage(replaceColors(bannedmessage));
			} else {
				getConfig().set("players." + e.getPlayer().getName(), "");
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if ((cmd.getName().equalsIgnoreCase("kingdomban") || cmd.getName().equalsIgnoreCase("kb")) && args.length > 1){
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
			String reason = "";
			boolean time = false;
			String timesentence = "";
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("t:") || args[i].equalsIgnoreCase("time:")){
					time = true;
				}
			    if (!time) reason += args[i] + ' ';
			    else timesentence += args[i] + ' ';
			}
			reason = reason.trim();
			timesentence = timesentence.trim();
			
			if (time){
				String newtimesentence = "";
				String timeparts[] = timesentence.toLowerCase().split(" ");
				int days = 0;
				int hours = 0;
				int minutes = 0;
				for (String s : timeparts){
					if (s.contains("d")){
						s = s.replaceAll("d", "");
						days = Integer.parseInt(s);
						if (days == 1) newtimesentence += "1 " + getConfig().getString("day") + " ";
						else if (days > 1) newtimesentence += days + " " + getConfig().getString("days") + " ";
					} else if (s.contains("h")){
						s = s.replaceAll("h", "");
						hours = Integer.parseInt(s);
						if (hours == 1) newtimesentence += "1 " + getConfig().getString("hour") + " ";
						else if (hours > 1) newtimesentence += hours + " " + getConfig().getString("hours") + " ";
					} else if (s.contains("m")){
						s = s.replaceAll("m", "");
						minutes = Integer.parseInt(s);
						if (minutes == 1) newtimesentence += "1 " + getConfig().getString("minute") + " ";
						else if (minutes > 1) newtimesentence += minutes + " " + getConfig().getString("minutes") + " ";
					}
				}
				newtimesentence = newtimesentence.trim();
				
				if (player.isOnline()){
					String bannedmessage = getConfig().getString("temp-bannedmessage");
					bannedmessage = bannedmessage.replaceAll("<banner>", sender.getName());
					bannedmessage = bannedmessage.replaceAll("<reason>", reason);
					bannedmessage = bannedmessage.replaceAll("<time>", newtimesentence);
					Bukkit.getPlayer(args[0]).kickPlayer(replaceColors(bannedmessage));
				}
				
				String bannermessage = getConfig().getString("temp-bannermessage");
				bannermessage = bannermessage.replaceAll("<banned>", args[0]);
				bannermessage = bannermessage.replaceAll("<reason>", reason);
				bannermessage = bannermessage.replaceAll("<time>", newtimesentence);
				sender.sendMessage(replaceColors(bannermessage));
				
				String broadcast = getConfig().getString("temp-banbroadcast");
				broadcast = broadcast.replaceAll("<banner>", sender.getName());
				broadcast = broadcast.replaceAll("<reason>", reason);
				broadcast = broadcast.replaceAll("<banned>", args[0]);
				broadcast = broadcast.replaceAll("<time>", newtimesentence);
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getName() != sender.getName()){
						p.sendMessage(replaceColors(broadcast));
					}
				}
				
				getConfig().set("players." + args[0] + ".banned", true);
				getConfig().set("players." + args[0] + ".banner", sender.getName());
				getConfig().set("players." + args[0] + ".reason", reason);
				long milli = new Date().getTime();
				milli = milli + (days * 24 * 60 * 60 * 1000) + (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
				getConfig().set("players." + args[0] + ".time", milli);
				saveConfig();
			} else {
				if (player.isOnline()){
					String bannedmessage = getConfig().getString("permanent-bannedmessage");
					bannedmessage = bannedmessage.replaceAll("<banner>", sender.getName());
					bannedmessage = bannedmessage.replaceAll("<reason>", reason);
					Bukkit.getPlayer(args[0]).kickPlayer(replaceColors(bannedmessage));
				}
				
				String bannermessage = getConfig().getString("permanent-bannermessage");
				bannermessage = bannermessage.replaceAll("<banned>", args[0]);
				bannermessage = bannermessage.replaceAll("<reason>", reason);
				sender.sendMessage(replaceColors(bannermessage));
				
				String broadcast = getConfig().getString("permanent-banbroadcast");
				broadcast = broadcast.replaceAll("<banner>", sender.getName());
				broadcast = broadcast.replaceAll("<reason>", reason);
				broadcast = broadcast.replaceAll("<banned>", args[0]);
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getName() != sender.getName()){
						p.sendMessage(replaceColors(broadcast));
					}
				}
				
				getConfig().set("players." + args[0] + ".banned", true);
				getConfig().set("players." + args[0] + ".banner", sender.getName());
				getConfig().set("players." + args[0] + ".reason", reason);
				saveConfig();
			}
			
			return true;
		} else if ((cmd.getName().equalsIgnoreCase("kingdomkick") || cmd.getName().equalsIgnoreCase("kk")) && args.length > 1){
			if (!Bukkit.getOfflinePlayer(args[0]).isOnline()){
				sender.sendMessage(replaceColors(getConfig().getString("notonline")));
				return true;
			}
			String reason = "";
			for (int i = 1; i < args.length; i++) {
			    reason += args[i] + ' ';
			}
			
			String kickedmessage = getConfig().getString("kickedmessage");
			kickedmessage = kickedmessage.replaceAll("<kicker>", sender.getName());
			kickedmessage = kickedmessage.replaceAll("<reason>", reason);
			Bukkit.getPlayer(args[0]).kickPlayer(replaceColors(kickedmessage));
			
			String kickermessage = getConfig().getString("kickermessage");
			kickermessage = kickermessage.replaceAll("<kicked>", args[0]);
			kickermessage = kickermessage.replaceAll("<reason>", reason);
			sender.sendMessage(replaceColors(kickermessage));
			
			String broadcast = getConfig().getString("kickbroadcast");
			broadcast = broadcast.replaceAll("<kicker>", sender.getName());
			broadcast = broadcast.replaceAll("<reason>", reason);
			broadcast = broadcast.replaceAll("<kicked>", args[0]);
			for (Player p : Bukkit.getOnlinePlayers()){
				if (p.getName() != sender.getName()){
					p.sendMessage(replaceColors(broadcast));
				}
			}
			
			return true;
		} else if ((cmd.getName().equalsIgnoreCase("kingdomunban") || cmd.getName().equalsIgnoreCase("kub")) && args.length == 1){
			if (sender.getName().equals(getConfig().getString("players." + args[0] + ".banner"))){
				getConfig().set("players." + args[0], "");
				saveConfig();
				String banner = getConfig().getString("unban");
				banner = banner.replaceAll("<banned>", args[0]);
				sender.sendMessage(replaceColors(banner));
				String broadcast = getConfig().getString("unbanbroadcast");
				broadcast = broadcast.replaceAll("<banned>", args[0]);
				broadcast = broadcast.replaceAll("<banner>", sender.getName());
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getName() != sender.getName()){
						p.sendMessage(replaceColors(broadcast));
					}
				}
			} else {
				String message = getConfig().getString("cannotunban");
				message = message.replaceAll("<banner>", getConfig().getString("players." + args[0] + ".banner"));
				message = message.replaceAll("<banned>", args[0]);
				sender.sendMessage(replaceColors(message));
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
