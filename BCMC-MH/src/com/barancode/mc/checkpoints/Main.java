package com.barancode.mc.checkpoints;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.barancode.mc.db.UUIDDatabase;

public class Main extends JavaPlugin implements Listener{
	
	BukkitScheduler scheduler = null;
	HashSet<String> currentPlayers = new HashSet<String>();
	ItemMessage im;
	LinkedHashMap<String, Integer> leaderboard = new LinkedHashMap<String, Integer>();
	UUIDDatabase uuiddb = new UUIDDatabase();
	ScoreboardManager boardman;
	Scoreboard board;
	
	public void onEnable(){
		im = new ItemMessage(this);
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		scheduler = Bukkit.getServer().getScheduler();
		
		scheduler.scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				boardman = Bukkit.getScoreboardManager();
				orderLeaderboard();
				updateScoreboard();
			}
		});
        
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
        	@Override
        	public void run(){
        		for (Player p : Bukkit.getOnlinePlayers()){
        			if (currentPlayers.contains(p.getName()) && !p.isDead()){
        				int time = getConfig().getInt("times." + p.getUniqueId().toString());
        				time = time + 5;
        				getConfig().set("times." + p.getUniqueId().toString(), time);
        				im.sendMessage(p, ChatColor.GOLD + "[" + ChatColor.YELLOW + (int)(time / 60) + ChatColor.GOLD + ":" + ChatColor.YELLOW + (int)(time % 60) + ChatColor.GOLD + "]" + ChatColor.AQUA + " (In seconds: " + time + ")");
        			}
        		}
        	}
        }, 0L, 5 * 20L);
        
        scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
        	@Override
        	public void run(){
        		saveConfig();
        	}
        }, 30 * 20L, 30 * 20L);
	}
	
	public void orderLeaderboard(){
		leaderboard.clear();
		HashMap<String, Integer> unordered = new HashMap<String, Integer>();
		if (getConfig().getConfigurationSection("leaderboard") == null) return;
		for (String s : getConfig().getConfigurationSection("leaderboard").getKeys(false)){
			int time = getConfig().getInt("leaderboard." + s);
			unordered.put(s, time);
		}
		for (int i = 0; i < 10; i++){
			if (unordered.size() == 0) break;
			int smallest = -1;
			String smallestString = "";
			for (String s : unordered.keySet()){
				int time = unordered.get(s);
				if (smallest == -1){
					smallest = time;
					smallestString = s;
				} else if (time < smallest){
					smallest = time;
					smallestString = s;
				}
			}
			leaderboard.put(smallestString, smallest);
			unordered.remove(smallestString);
		}
	} // &6These values--are in seconds
	
	public void updateScoreboard(){
		board = boardman.getNewScoreboard();
		Objective objective = board.registerNewObjective("scoreboard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Best Times");
	    int rank = 1;
	    for (String uuid : leaderboard.keySet()){
	    	int time = leaderboard.get(uuid);
	    	String name = rank + ". " + uuiddb.getUsername(UUID.fromString(uuid));
	    	name = name.substring(0, Math.min(name.length(), 16));
	    	Score score = objective.getScore(Bukkit.getOfflinePlayer(name));
		    score.setScore(time);
		    rank++;
	    }
	    Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "These values"));
	    score.setScore(0);
	    Score score2 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "are in seconds"));
	    score2.setScore(0);
	    for (Player p : Bukkit.getOnlinePlayers()){
	    	p.setScoreboard(board);
	    }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("setcheckpoint")){
			Player player = Bukkit.getPlayer(args[0]);
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("back")){
					if (getConfig().getBoolean("spawns." + player.getUniqueId() + ".exists")){
				          World w = player.getWorld();
				          double x = getConfig().getDouble("spawns." + player.getUniqueId() + ".x");
				          double y = getConfig().getDouble("spawns." + player.getUniqueId() + ".y");
				          double z = getConfig().getDouble("spawns." + player.getUniqueId() + ".z");
				          float yaw = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".yaw");
				          float pitch = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".pitch");
				          Location loc = new Location(w, x, y, z, yaw, pitch);
				          player.teleport(loc);
			        	  player.sendMessage(ChatColor.GREEN + "You have been teleported to your last checkpoint");
			      		  currentPlayers.add(player.getName());
			        	  return true;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have a checkpoint");
						return true;
					}
				} else if (args[1].equalsIgnoreCase("first")){
					getConfig().set("spawns." + player.getUniqueId() + ".x", player.getLocation().getX());
					getConfig().set("spawns." + player.getUniqueId() + ".y", player.getLocation().getY());
					getConfig().set("spawns." + player.getUniqueId() + ".z", player.getLocation().getZ());
					getConfig().set("spawns." + player.getUniqueId() + ".yaw", player.getLocation().getYaw());
					getConfig().set("spawns." + player.getUniqueId() + ".pitch", player.getLocation().getPitch());
					getConfig().set("spawns." + player.getUniqueId() + ".exists", true);
					getConfig().set("times." + player.getUniqueId(), 0);
					saveConfig();
					currentPlayers.add(player.getName());
					return true;
				} else if (args[1].equalsIgnoreCase("end")){
					UUID uuid = player.getUniqueId();
					getConfig().set("spawns." + player.getUniqueId(), null);
					int time = getConfig().getInt("times." + player.getUniqueId());
					player.sendMessage(ChatColor.GOLD + "You have finished this map, with a time of: " + ChatColor.YELLOW + (int)(time / 60) + ":" + (time - (((int)(time / 60)) * 60)));
					
					if (getConfig().getConfigurationSection("leaderboard") == null){
						getConfig().set("leaderboard." + uuid.toString(), time);
						orderLeaderboard();
						updateScoreboard();
					} else {
						HashMap<String, Integer> unordered = new HashMap<String, Integer>();
						for (String s : getConfig().getConfigurationSection("leaderboard").getKeys(false)){
							int toptime = getConfig().getInt("leaderboard." + s);
							unordered.put(s,  toptime);
						}
						
						if (unordered.containsKey(uuid.toString())){
							if (unordered.get(uuid.toString()) > time){
								getConfig().set("leaderboard." + uuid.toString(), time);
								orderLeaderboard();
								updateScoreboard();
							}
						} else if (unordered.size() < 10){
							getConfig().set("leaderboard." + uuid.toString(), time);
							orderLeaderboard();
							updateScoreboard();
						} else {
							int biggestInt = -1;
							String biggestString = "";
							for (String s : unordered.keySet()){
								int get = unordered.get(s);
								if (biggestInt == -1){
									biggestInt = get;
									biggestString = s;
								} else if (get > biggestInt){
									biggestInt = get;
									biggestString = s;
								}
							}
							if (time < biggestInt){
								getConfig().set("leaderboard." + biggestString, null);
								getConfig().set("leaderboard." + uuid.toString(), time);
								orderLeaderboard();
								updateScoreboard();
							}
						}
					}
					
					saveConfig();
					currentPlayers.remove(player.getName());
					return true;
				}
			}
			getConfig().set("spawns." + player.getUniqueId() + ".x", player.getLocation().getX());
			getConfig().set("spawns." + player.getUniqueId() + ".y", player.getLocation().getY());
			getConfig().set("spawns." + player.getUniqueId() + ".z", player.getLocation().getZ());
			getConfig().set("spawns." + player.getUniqueId() + ".yaw", player.getLocation().getYaw());
			getConfig().set("spawns." + player.getUniqueId() + ".pitch", player.getLocation().getPitch());
			getConfig().set("spawns." + player.getUniqueId() + ".exists", true);
			player.sendMessage(ChatColor.GREEN + "You have set your spawnpoint");
			saveConfig();
			return true;
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void death(PlayerDeathEvent event){
		event.setDroppedExp(0);
		event.getDrops().clear();
		event.setDeathMessage("");
	}	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void respawn(final PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if (getConfig().getBoolean("spawns." + player.getUniqueId() + ".exists")){
	          World w = player.getWorld();
	          double x = getConfig().getDouble("spawns." + player.getUniqueId() + ".x");
	          double y = getConfig().getDouble("spawns." + player.getUniqueId() + ".y");
	          double z = getConfig().getDouble("spawns." + player.getUniqueId() + ".z");
	          float yaw = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".yaw");
	          float pitch = (float)getConfig().getDouble("spawns." + player.getUniqueId() + ".pitch");
	          final Location loc = new Location(w, x, y, z, yaw, pitch);
	          event.setRespawnLocation(loc);
        	  player.sendMessage(ChatColor.GREEN + "Teleporting you back to the last checkpoint.");
        	  player.sendMessage(ChatColor.RED + "15 seconds have been added to your timer.");
        	  getConfig().set("times." + player.getUniqueId(), getConfig().getInt("times." + player.getUniqueId()) + 15);
        	  
      		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
    			@Override
    			public void run(){
    				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, true));
    				event.getPlayer().teleport(loc);
    				event.getPlayer().setScoreboard(board);
    			}
    		}, 1L);
		} else {
      		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
    			@Override
    			public void run(){
    				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, true));
    				event.getPlayer().setScoreboard(board);
    			}
    		}, 1L);
		}
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event){
		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run(){
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, true));
				event.getPlayer().setGameMode(GameMode.SURVIVAL);
				event.getPlayer().setScoreboard(board);
			}
		}, 1L);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		currentPlayers.remove(event.getPlayer().getName());
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent e){
		if (e.getMessage().replaceAll("/", "").toLowerCase().contains("gamemode")){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You can not do that on the Mysterious House servers!");
		}
	}
	
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryOpenEvent(InventoryOpenEvent e){
    	if (e.getView().getType() == InventoryType.DISPENSER || e.getView().getType() == InventoryType.DROPPER){
        	if (!e.getPlayer().hasPermission("bcmc.dispenser")) e.setCancelled(true);
        }
    }
}
