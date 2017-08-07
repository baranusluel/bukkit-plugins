package com.barancode.mc.ddg.lobby;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	// This hashmap will keep track of all players, and how much they flown. This is used so that
	// we can take a number of xp per a configurable amount of blocks flown.
	HashMap<Player, Double> list = new HashMap<Player, Double>();
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		// Make sure they're flying
		if (!e.getPlayer().isFlying()) return;
		double distance = e.getFrom().distance(e.getTo());
		int blocks = getConfig().getInt("blocks");
		// If they haven't flown the configurable amount, just add the distance to the hashmap
		if (list.get(e.getPlayer()) + distance < blocks){
			if (list.containsKey(e.getPlayer())) list.put(e.getPlayer(), list.get(e.getPlayer()) + distance);
			else list.put(e.getPlayer(), distance);
		}
		else {
			// Take xp
			e.getPlayer().setTotalExperience(e.getPlayer().getTotalExperience() - getConfig().getInt("xp"));
			// Decrease from distance in hashmap
			list.put(e.getPlayer(), list.get(e.getPlayer()) + distance - blocks);
		}
		
		if (e.getPlayer().getTotalExperience() < getConfig().getInt("xp")){
			e.getPlayer().setFlying(false);
			list.remove(e.getPlayer());
			e.getPlayer().sendMessage(replaceColors(getConfig().getString("finished")));
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		// Allow flight
		e.getPlayer().setAllowFlight(true);
	}
	
	@EventHandler
	public void onToggle(PlayerToggleFlightEvent e){
		// Ignore if they're in creative
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		// If they were already flying, and they're disabling it
		if (e.getPlayer().isFlying()){
			// Remove from hashmap to keep it clean
			list.remove(e.getPlayer());
			return;
		}
		// Make sure they have enough xp
		if (e.getPlayer().getTotalExperience() < getConfig().getInt("xp")){
			e.setCancelled(true);
			e.getPlayer().sendMessage(replaceColors(getConfig().getString("error")));
			return;
		}
		// Add player to hashmap
		list.put(e.getPlayer(), 0.0);
	}
	
	// Color codes
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
