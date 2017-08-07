package net.choicecraft.ChoiceBossBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ChoiceBossBar extends JavaPlugin implements Listener{
	List<String> announcements = new ArrayList<String>();
	int announcementNumber;
	
	int characterTask = -1;
	String currentMessage = "";
	String currentFormattedMessage = "";
	String currentFormat = "";
	int character = 0;
	
	BukkitScheduler scheduler;
	String movingColor = "";
	String website = "";
	
	@SuppressWarnings("unchecked")
	public void onEnable(){
		saveDefaultConfig();
		int interval = getConfig().getInt("interval");
		announcements = (List<String>)(List<?>)Arrays.asList(getConfig().getConfigurationSection("announcements").getKeys(false).toArray());
		movingColor = getConfig().getString("moving-color");
		website = getConfig().getString("website");
		
		scheduler = Bukkit.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				newMessage();
			}
		}, 0, interval * 20);
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void newMessage(){
		currentMessage = announcements.get(announcementNumber);
		currentFormat = getConfig().getString("announcements." + currentMessage);
		currentMessage = currentMessage.replaceAll("<website>", website);
		currentFormattedMessage = ChatColor.translateAlternateColorCodes('&', currentFormat + currentMessage);
		BarAPI.setMessage(currentFormattedMessage);
		announcementNumber = (announcements.size() - announcementNumber < 2) ? 0 : announcementNumber + 1;
		character = 0;
		if (characterTask != -1) scheduler.cancelTask(characterTask);
		moveColor();
	}
	
	public void moveColor(){
		// We start a new repeating task each time so that it starts at the same time as the new announcement
		characterTask = scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				currentFormattedMessage = ChatColor.translateAlternateColorCodes('&', currentFormat);
				for (int i = 0; i < currentMessage.length(); i++){
					if (i == character){
						currentFormattedMessage += ChatColor.translateAlternateColorCodes('&', movingColor + currentMessage.charAt(i) + currentFormat);
					} else currentFormattedMessage += currentMessage.charAt(i);
				}
				BarAPI.setMessage(currentFormattedMessage);
				character++;
				if (character >= currentMessage.length()) character = 0;
			}
		}, 3, 3);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		BarAPI.setMessage(currentFormattedMessage);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		announcements = (List<String>)(List<?>)Arrays.asList(getConfig().getConfigurationSection("announcements").getKeys(false).toArray());
		announcementNumber = 0;
		newMessage();
		sender.sendMessage("The boss bar messages have been reloaded");
		return true;
	}
}
