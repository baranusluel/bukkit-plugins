package com.barancode.mc.joinfull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
	}
	@EventHandler
	public void login(PlayerLoginEvent e){
		if (e.getResult() == Result.KICK_FULL){
			if (Bukkit.getOnlinePlayers().length < getConfig().getInt("max-players")) e.allow();
			else {
				if (e.getPlayer().hasPermission("bcmc.joinfull")){
					e.allow();
				} else {
					e.setKickMessage(ChatColor.RED + "That server is full!\n" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "[Tip]" + ChatColor.DARK_PURPLE + " Donators can join full servers");
				}
			}
		}
	}
}
