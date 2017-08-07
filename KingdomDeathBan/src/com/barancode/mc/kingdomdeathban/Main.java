package com.barancode.mc.kingdomdeathban;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener{
	
	List<String> hiding = new LinkedList<String>();
	
	public void onEnable(){
	    getServer().getPluginManager().registerEvents(this, this);
	    saveDefaultConfig();
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event){
		hiding.add(event.getPlayer().getName());
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	hiding.remove(event.getPlayer().getName());
            }
        }, 5 * 20L);
	}
	
	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent event){
		if (hiding.contains(event.getPlayer().getName())) event.setCancelled(true);
		for (Player p : Bukkit.getOnlinePlayers()){
			if (hiding.contains(p.getName())) event.getRecipients().remove(p);
		}
	}
	
	@EventHandler
	public void deathEvent(PlayerDeathEvent event){
		if (event.getEntity() instanceof Player){
			Player p = (Player)event.getEntity();
			p.kickPlayer(replaceColors(getConfig().getString("banmessage")));
			p.setBanned(true);
			final Player fp = p;
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
	            @Override
	            public void run(){
	            	fp.setBanned(false);
	            }
	        }, 2 * 20L);
		}
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
