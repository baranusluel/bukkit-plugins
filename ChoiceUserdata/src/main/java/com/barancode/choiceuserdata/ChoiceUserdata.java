package main.java.com.barancode.choiceuserdata;

import main.java.com.barancode.choiceuserdata.database.DatabaseConnection;
import main.java.com.barancode.choiceuserdata.database.DatabaseIP;
import main.java.com.barancode.choiceuserdata.database.DatabaseUUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ChoiceUserdata extends JavaPlugin implements Listener{
	public static DatabaseConnection db = new DatabaseConnection();
	FileConfiguration config;
	BukkitScheduler scheduler;
	
	public void onEnable(){
		saveDefaultConfig();
		config = getConfig();
		db.initialize(config.getString("host"), config.getString("username"), config.getString("password"), config.getString("database"));
		Bukkit.getPluginManager().registerEvents(this, this);
		scheduler = Bukkit.getScheduler();
	}
	
	public void onDisable(){
		db.quit();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(final PlayerJoinEvent e){
		scheduler.scheduleAsyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				Player p = e.getPlayer();
				DatabaseUUID.setPair(p.getUniqueId(), p.getName());
				DatabaseIP.setPair(p.getUniqueId(), p.getAddress().getHostString());
			}
		});
	}
}
