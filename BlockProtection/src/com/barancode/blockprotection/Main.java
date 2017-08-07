package com.barancode.blockprotection;

import java.util.HashMap;
import java.util.HashSet;

import net.coreprotect.CoreProtectAPI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.barancode.blockprotection.io.CustomConfig;
import com.barancode.blockprotection.io.PlayerList;
import com.barancode.blockprotection.managers.BoardManager;
import com.barancode.blockprotection.managers.CoreProtectManager;
import com.barancode.blockprotection.managers.DBManager;
import com.barancode.blockprotection.managers.PermissionManager;

public class Main extends JavaPlugin{
	
	Main plugin = this;
	public CoreProtectManager cpman = new CoreProtectManager(this);
	public Listeners listeners = new Listeners(this);
	public PlayerList players = new PlayerList(this);
	public Commands commands = new Commands(this);
	public PermissionManager pman = new PermissionManager(this);
	public DBManager dbman = new DBManager(this);
	public BoardManager boardman = new BoardManager(this);
	AFKChecker afk;
	
	public CustomConfig ruleConfig = new CustomConfig(this, "rules.yml");
	public CustomConfig blockConfig = new CustomConfig(this, "blocks.yml");
	public CustomConfig stringConfig = new CustomConfig(this, "messages.yml");
	public CustomConfig boardConfig = new CustomConfig(this, "scoreboard.yml");
	public CustomConfig chestConfig = new CustomConfig(this, "chests.yml");
	
	public HashSet<String> pendingRules = new HashSet<String>();
	public ScoreboardManager manager;
	public HashMap<String, Integer> blockPrices = new HashMap<String, Integer>();
	public HashMap<String, Integer> chestPrices = new HashMap<String, Integer>();
	public int defaultPrice = 0;
	public int defaultChestPrice = 0;
	public HashSet<String> onlinePlayers = new HashSet<String>();
	public BukkitScheduler scheduler;
	public HashMap<String, Scoreboard> scoreboards = new HashMap<String, Scoreboard>();
	public HashMap<String, Location> afklocations = new HashMap<String, Location>();
	public HashSet<String> override = new HashSet<String>();
	public HashMap<Integer, Integer> groupPlayers = new HashMap<Integer, Integer>();
	public HashSet<String> scoreboardOff = new HashSet<String>();
	
	public void onEnable(){
		scheduler = Bukkit.getScheduler();
		saveDefaultConfig();
		ruleConfig.saveDefaultConfig();
		blockConfig.saveDefaultConfig();
		stringConfig.saveDefaultConfig();
		boardConfig.saveDefaultConfig();
		chestConfig.saveDefaultConfig();
		
		players.populate();
		dbman.initialize();
		afk = new AFKChecker(this);
		
		getCommand("accept").setExecutor(commands);
		getCommand("getinfluence").setExecutor(commands);
		getCommand("setinfluence").setExecutor(commands);
		getCommand("override").setExecutor(commands);
		getCommand("score").setExecutor(commands);
		
		CoreProtectAPI CoreProtect = cpman.getCoreProtect();
		if (CoreProtect != null){
			CoreProtect.testAPI();
		}
		
		getServer().getPluginManager().registerEvents(listeners, this);
		
		for (String s : blockConfig.getCustomConfig().getKeys(false)){
			if (!s.equals("default")) blockPrices.put(s, blockConfig.getCustomConfig().getInt(s));
		}
		defaultPrice = blockConfig.getCustomConfig().getInt("default");
		
		for (String s : chestConfig.getCustomConfig().getKeys(false)){
			if (!s.equals("default")) chestPrices.put(s, chestConfig.getCustomConfig().getInt(s));
		}
		defaultChestPrice = chestConfig.getCustomConfig().getInt("default");
		
		scheduler.scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				manager = Bukkit.getScoreboardManager();
				
				for (Player p : Bukkit.getOnlinePlayers()){
					int group = plugin.pman.getGroup(p.getName());
					if (plugin.groupPlayers.containsKey(group)) plugin.groupPlayers.put(group, plugin.groupPlayers.get(group) + 1);
					else plugin.groupPlayers.put(group, 1);
				}
			}
		});
		
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@SuppressWarnings("unchecked")
			@Override
			public void run(){
				HashSet<String> temp = (HashSet<String>)onlinePlayers.clone();
				onlinePlayers.clear();
				Player[] players = Bukkit.getOnlinePlayers();
				for (int i = 0; i < players.length; i++){
					Player p = players[i];
					if (temp.contains(p.getName())){
						dbman.addAmount(p.getName(), 1);
					}
					onlinePlayers.add(p.getName());
				}
			}
		}, 0L, 60 * 20L);
	}
	
	public void onDisable(){
		dbman.quit();
	}
}