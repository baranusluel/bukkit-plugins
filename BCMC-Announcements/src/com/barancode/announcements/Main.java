package com.barancode.announcements;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	int announcement = 0;
	
	public void onEnable(){
		saveDefaultConfig();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				List<String> announcements = getConfig().getStringList("announcements");
				String s = announcements.get(announcement);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
				if (announcement == announcements.size() - 1){
					announcement = 0;
				} else {
					announcement++;
				}
			}
		}, 0L, getConfig().getInt("interval-seconds") * 20L);
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		reloadConfig();
		return true;
	}
}
