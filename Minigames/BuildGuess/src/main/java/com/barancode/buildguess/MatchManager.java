package main.java.com.barancode.buildguess;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MatchManager {
	Main plugin;
	
	public MatchManager(Main plugin){
		this.plugin = plugin;
	}
	
	// Timer for match starting countdown
	public void startCountdown(){
		Main.vars.countdown = true;
		Bukkit.broadcastMessage(ConfigValues.getCountdownStart());
		Main.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run(){
				Main.vars.countdownTime--;
				int time = Main.vars.countdownTime;
				if (time <= 5 && time > 0){
					Bukkit.broadcastMessage(ConfigValues.getCountdownSoon());
					makeSound();
				} else if (time == 0){
					Main.scheduler.cancelAllTasks();
					if (Bukkit.getOnlinePlayers().length < 4) {
						Bukkit.broadcastMessage(ConfigValues.getCountdownNotEnough());
						plugin.initialize();
						for (Player p : Bukkit.getOnlinePlayers()){
							Board.coinlessBoard(p);
						}
						return;
					}
					Bukkit.broadcastMessage(ConfigValues.getCountdownEnd());
					Main.vars.countdown = false;
					Main.vars.ingame = true;
					
					for (Player p : Bukkit.getOnlinePlayers()){
						Board.gameBoard(p);
					}
					
					newRound(false);
					
					startMatch();
					builderAFKCheck();
					return;
				}
				
				for (Player p : Bukkit.getOnlinePlayers()){
					Board.coinlessBoard(p);
				}
			}
		}, 20L, 20L);
	}
	
	// Timer for the match itself (the gameplay)
	public void startMatch(){
		Main.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run(){
				Main.vars.matchTime--;
				int time = Main.vars.matchTime;
				if (time <= 5 && time > 0){
					Bukkit.broadcastMessage(ConfigValues.getMatchCountdown().replaceAll("<time>", time + ""));
					makeSound();
				} else if (time == 0){
					Main.scheduler.cancelAllTasks();
					Bukkit.broadcastMessage(ConfigValues.getMatchEnd());
					endBuilder();
					Main.vars.ingame = false;
					Main.vars.gg = true;
					
					Main.vars.countdownTime = ConfigValues.getFinishedTime();
					
					getWinner();
					rollback();
					startGG();
					return;
				}
				for (Player p : Bukkit.getOnlinePlayers()){
					Board.gameBoard(p);
				}
			}
		}, 20L, 20L);
	}
	
	public void getWinner(){
		String mostPlayer = "";
		int mostAmount = 0;
		for (String s : Main.vars.coins.keySet()){
			int amount = Main.vars.coins.get(s);
			if (amount > mostAmount){
				mostAmount = amount;
				mostPlayer = s;
			}
		}
		Bukkit.broadcastMessage(ConfigValues.getWinnerMessage().replaceAll("<player>", mostPlayer).replaceAll("<coins>", mostAmount + ""));
	}
	
	// Timer for the short time after the match (to say GG)
	public void startGG(){
		for (Player p : Bukkit.getOnlinePlayers()){
			p.getInventory().clear();
			p.getInventory().setItem(ConfigValues.getHubSlot(), plugin.hubItem);
			p.getInventory().setItem(ConfigValues.getBookSlot(), plugin.bookItem);
		}
		Main.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run(){
				Main.vars.countdownTime--;
				int time = Main.vars.countdownTime;
				if (time <= 5 && time > 0){
					Bukkit.broadcastMessage(ConfigValues.getGGCountdown().replaceAll("<time>", time + ""));
					makeSound();
				} else if (time == 0){
					Main.scheduler.cancelAllTasks();
					Main.vars.kicking = true;
					endBuilder();
					if (Bukkit.getOnlinePlayers().length > 0){
						for (Player p : Bukkit.getOnlinePlayers()){
							Utils.connectToHub(p, plugin);
						}
					} else {
						plugin.initialize();
					}
					return;
				}
				for (Player p : Bukkit.getOnlinePlayers()){
					Board.coinlessBoard(p);
				}
			}
		}, 20L, 20L);
	}
	
	// Save region location to file
	public void saveRegion(Location first, Location second, String type){
		type = type.toLowerCase();
		int maxx, maxy, maxz, minx, miny, minz;
		if (first.getBlockX() > second.getBlockX()){ 
			maxx = first.getBlockX(); minx = second.getBlockX();
		} else {
			maxx = second.getBlockX(); minx = first.getBlockX();
		}
		if (first.getBlockY() > second.getBlockY()){
			maxy = first.getBlockY(); miny = second.getBlockY();
		} else {
			maxy = second.getBlockY(); miny = first.getBlockY();
		}
		if (first.getBlockZ() > second.getBlockZ()){
			maxz = first.getBlockZ(); minz = second.getBlockZ();
		} else {
			maxz = second.getBlockZ(); minz = first.getBlockZ();
		}
		Main.config.set(type + "-region.minx", minx);
		Main.config.set(type + "-region.miny", miny);
		Main.config.set(type + "-region.minz", minz);
		Main.config.set(type + "-region.maxx", maxx);
		Main.config.set(type + "-region.maxy", maxy);
		Main.config.set(type + "-region.maxz", maxz);
		plugin.saveConfig();
		if (type.equals("build")) Main.vars.buildRegion = ConfigValues.getBuildRegion();
		else if (type.equals("rollback")) Main.vars.rollbackRegion = ConfigValues.getRollbackRegion();
	}
	
	public void rollback(){
		HashMap<String, Integer> region = ConfigValues.getRollbackRegion();
		World w = Bukkit.getWorld("world");
		for (int x = region.get("minx"); x <= region.get("maxx"); x++){
			for (int z = region.get("minz"); z <= region.get("maxz"); z++){
				for (int y = region.get("miny"); y <= region.get("maxy"); y++){
					w.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
	}
	
	public void builderAFKCheck(){
		Main.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run(){
				if (Main.vars.builder.equals("")) return;
				Player p = Bukkit.getPlayer(Main.vars.builder);
				if (Main.vars.builderLocation == null){
					Main.vars.builderLocation = p.getLocation();
				} else if (Main.vars.builderLocation.distanceSquared(p.getLocation()) == 0){
					// Distance squared is used instead of distance because it is more optimized
					// (the square root function is heavy), and we only need
					// to know if the distance is equal to 0
					Main.vars.builderAFK++;
					if (Main.vars.builderAFK > ConfigValues.getAFKTime() - 1){
						endBuilder();
						p.kickPlayer(ConfigValues.getAFKKick());
						Bukkit.broadcastMessage(ConfigValues.getAFKKickBroadcast().replaceAll("<player>", p.getName()));
						newRound(true);
					}
				} else {
					Main.vars.builderAFK = 0;
					Main.vars.builderLocation = p.getLocation();
				}
			}
		}, 20L, 20L);
	}
	
	public void makeSound(){
		for (Player p : Bukkit.getOnlinePlayers()){
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
		}
	}
	
	
	public void newBuilder(boolean notFirst){
		final Player builder = Bukkit.getOnlinePlayers()[plugin.random.nextInt(Bukkit.getOnlinePlayers().length)];
		builder.teleport(ConfigValues.getBuildSpawn());
		builder.getInventory().clear();
		builder.setGameMode(GameMode.CREATIVE);
		Bukkit.broadcastMessage(ConfigValues.getBuilderSelected().replaceAll("<builder>", builder.getName()));
		if (!notFirst) Main.vars.word = ConfigValues.getRandomTopic();
		else Main.vars.word = ConfigValues.getRandomTopic(Main.vars.word);
		builder.sendMessage(ConfigValues.getTopicMessage().replaceAll("<topic>", Main.vars.word));
		Main.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				Main.vars.builder = builder.getName();
			}
		}, 1L);
	}
	
	public void endBuilder(){
		if (Main.vars.builder.equals("")) return;
		Player p = Bukkit.getPlayer(Main.vars.builder);
		p.teleport(ConfigValues.getSpawn());
		p.setGameMode(GameMode.ADVENTURE);
		Main.vars.builder = "";
		Main.vars.builderAFK = 0;
		Main.vars.builderLocation = null;
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}
	
	public void newRound(boolean notFirst){
		for (Player p : Bukkit.getOnlinePlayers()){
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.getInventory().setItem(ConfigValues.getHubSlot(), plugin.hubItem);
			p.getInventory().setItem(ConfigValues.getBookSlot(), plugin.bookItem);
			p.getInventory().setItem(ConfigValues.getHintSlot(), plugin.hintItem);
		}
		rollback();
		newBuilder(notFirst);
	}
}
