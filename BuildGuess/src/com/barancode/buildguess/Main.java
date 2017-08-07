package com.barancode.buildguess;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

public class Main extends JavaPlugin{
	Events events = new Events(this);
	Commands commands = new Commands(this);
	static FileConfiguration config;
	static Variables vars;
	static BukkitScheduler scheduler;
	static ScoreboardManager boardman;
	MatchManager matchman = new MatchManager(this);
	InfoBook book = new InfoBook(this);
	
	Random random = new Random();
	
	ItemStack hubItem;
	ItemStack hintItem;
	ItemStack bookItem;
	static Main plugin;
	{
		plugin = this;
	}
	
	@Override
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(events, this);
		saveDefaultConfig();
		config = this.getConfig();
		scheduler = Bukkit.getScheduler();
		
		initialize();
		setupCommands();
		setupItems();
		Bukkit.getWorld("world").setGameRuleValue("doDaylightCycle", "false");
		Bukkit.getWorld("world").setTime(6000);
		
		scheduler.scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				boardman = Bukkit.getScoreboardManager();
			}
		});
	}
	
	public void setupCommands(){
		getCommand("setspawn").setExecutor(commands);
		getCommand("setbuildspawn").setExecutor(commands);
		getCommand("setpos").setExecutor(commands);
		getCommand("setbook").setExecutor(commands);
	}
	
	public void setupItems(){
		hubItem = new ItemStack(Material.getMaterial(ConfigValues.getItemType("hub-item")));
		ItemMeta meta = hubItem.getItemMeta();
		meta.setDisplayName(ConfigValues.getItemName("hub-item"));
		meta.setLore(ConfigValues.getItemLore("hub-item"));
		hubItem.setItemMeta(meta);
		
		hintItem = new ItemStack(Material.getMaterial(ConfigValues.getItemType("hint-item")));
		meta = hintItem.getItemMeta();
		meta.setDisplayName(ConfigValues.getItemName("hint-item"));
		meta.setLore(ConfigValues.getItemLore("hint-item"));
		hintItem.setItemMeta(meta);
		
		bookItem = book.getBook();
	}
	
	public void initialize(){
		vars = new Variables();
	}
}
