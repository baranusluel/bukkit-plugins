package com.barancode.mc.tntrun;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.barancode.mc.db.TokenDatabase;

public class Main extends JavaPlugin implements Listener{
	
	boolean playing = false;
	int countdown = 30;
	boolean countdownStarted = false;
	BukkitScheduler scheduler;
	Scoreboard scoreboard;
	ScoreboardManager manager;
	String prefix = ChatColor.RED + "[" + ChatColor.LIGHT_PURPLE + "TNT Run" + ChatColor.RED + "] " + ChatColor.RESET;
	List<String> kicking = new LinkedList<String>();
	Main plugin = this;
	Location loc;
	
	boolean restarting = false;
	
	Runnable runnable = null;
	
	TokenDatabase tokendb;
	
	/*@SuppressWarnings("deprecation")
	public void reinitializeVariables(){
		playing = false;
		countdown = 30;
		countdownStarted = false;
		
		scoreboard = manager.getNewScoreboard();
	    Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    objective.setDisplayName(ChatColor.GREEN + "Info");
	    Score time = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Countdown"));
	    time.setScore(countdown);
	    Score count = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players"));
	    count.setScore(0);
	    
	    kicking = new LinkedList<String>();
	    
	    scheduler.cancelAllTasks();
	    
    	World world = Bukkit.getWorld("world");
    	World arena = Bukkit.getWorld("arena");
	    for (int x = -30; x <= 30; x++){
	    	for (int z = -30; z <= 30; z++){
    			Block block = world.getBlockAt(x, 5, z);
    			Block newblock = arena.getBlockAt(x, 5, z);
    			newblock.setType(block.getType());
    			newblock.getState().setData(block.getState().getData());
    			if (block.getType() == Material.CHEST){
    				Chest chest = (Chest)block.getState();
    				Chest newchest = (Chest)newblock.getState();
    				newchest.getInventory().setContents(chest.getInventory().getContents());
    			}
	    	}
	    }
	    
	    restarting = false;
	}*/
	
	public void onEnable(){
		tokendb = new TokenDatabase();
		
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		scheduler = Bukkit.getServer().getScheduler();
		
		File firstWorld = new File("./world");
		for (File f : new File(firstWorld + "/playerdata").listFiles()){
			f.delete();
		}
		File secondWorld = new File("./arena");
		try {
    		FileUtils.deleteDirectory(secondWorld);
			copyFolder(firstWorld, secondWorld);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
        		manager = Bukkit.getScoreboardManager();
        		scoreboard = manager.getNewScoreboard();
        	    Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");
        	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        	    objective.setDisplayName(ChatColor.GREEN + "Info");
        	    Score time = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Countdown"));
        	    time.setScore(countdown);
        	    Score count = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players"));
        	    count.setScore(0);
        		
                WorldCreator wc = new WorldCreator("arena");
                wc.generateStructures(false);
                wc.type(WorldType.FLAT);
                wc.createWorld();
                
        		World world = Bukkit.getWorld("arena");
        		int x = getConfig().getInt("spawn.x");
        		int y = getConfig().getInt("spawn.y");
        		int z = getConfig().getInt("spawn.z");
        		loc = new Location(world, x, y, z);
            }
        }, 0L);
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e){
		if (restarting){
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage(prefix + ChatColor.RED + "This arena is restarting");
		} else if (playing){
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage(prefix + ChatColor.RED + "This arena is currently in a game");
		}
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e){
		e.setJoinMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has joined TNT Run");
		
		e.getPlayer().teleport(loc);
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
        		e.getPlayer().setScoreboard(scoreboard);
        		
        		e.getPlayer().teleport(loc);
        		clearArmor(e.getPlayer());
        		e.getPlayer().getInventory().clear();
        		e.getPlayer().setHealth(20);
        		e.getPlayer().setFoodLevel(20);
        		
        		int playerCount = Bukkit.getOnlinePlayers().length;
        		if (playerCount < 2){
        			Bukkit.broadcastMessage(prefix + ChatColor.GOLD + "You need " + ChatColor.GRAY + (2 - playerCount) + ChatColor.GOLD + " more players to start");
        		} else {
        			if (!countdownStarted){
        				countdownStarted = true;
        				Bukkit.broadcastMessage(prefix + ChatColor.GOLD + "The match will start in 30 seconds");
        		        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
        		            @Override
        		            public void run(){
        		            	if (countdown == 0){
        		            		int playerCount = Bukkit.getOnlinePlayers().length;
        		            		if (playerCount < 2){
        		            			Bukkit.broadcastMessage(prefix + ChatColor.RED + "There aren't enough players to start");
        		            			countdown = 30;
        		            			return;
        		            		}
        		            		playing = true;
        		            		Bukkit.broadcastMessage(prefix + ChatColor.GREEN + "The match started!");
        		            		scheduler.cancelAllTasks();
        		            		scoreboard = manager.getNewScoreboard();
        		            	    Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");
        		            	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        		            	    objective.setDisplayName(ChatColor.GREEN + "Info");
        		            	    Score count = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players Left"));
        		            	    count.setScore(playerCount);
        		        			
        		        			for (Player player : Bukkit.getOnlinePlayers()){
        		        				player.setScoreboard(scoreboard);
        		        			}
        		            	}
        		        		updateScoreboardCountdown();
        		        		countdown--;
        		            }
        		        }, 0L, 20L);
        			}
        		}
        		
        		Objective objective = scoreboard.getObjective("scoreboard");
        		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players")).setScore(playerCount);
            }
        }, 3L);
	}
	
	public void onDisable(){
		if (runnable != null) runnable.run();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (restarting){
			e.setQuitMessage("");
			//reinitializeVariables();
			runnable = new Runnable(){
				@Override
				public void run(){
					ProcessBuilder builder = new ProcessBuilder("./start.sh");
					try {
						builder.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			Bukkit.shutdown();
		} if (!kicking.contains(e.getPlayer().getName())){
			e.setQuitMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has left TNT Run");
		} else {
			e.setQuitMessage("");
			kicking.remove(e.getPlayer().getName());
		}
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	if (!playing){
	        		Objective objective = scoreboard.getObjective("scoreboard");
	        		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players")).setScore(Bukkit.getOnlinePlayers().length);
            	} else {
	        		Objective objective = scoreboard.getObjective("scoreboard");
	        		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players Left")).setScore(Bukkit.getOnlinePlayers().length);
            	}
        		
        		if (playing && Bukkit.getOnlinePlayers().length == 1){
        			restarting = true;
        			
        			Player p = Bukkit.getOnlinePlayers()[0];
        			p.sendMessage(prefix + ChatColor.YELLOW + "=-=-= You have won! =-=-=");
        			tokendb.addAmount(p.getUniqueId(), 5);
        			p.sendMessage(ChatColor.BLUE + "You have gained 5 tokens for winning!");
        	    	connectToHub(p);
        		}
            }
        }, 1L);
	}
	
	public void updateScoreboardCountdown(){
		Objective objective = scoreboard.getObjective("scoreboard");
		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Countdown")).setScore(countdown);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if (playing && !restarting){
			kicking.add(e.getEntity().getName());
			tokendb.addAmount(e.getEntity().getUniqueId(), 1);
			e.getEntity().sendMessage(ChatColor.BLUE + "You have gained 1 token for playing!");
			connectToHub(e.getEntity());
			e.setDeathMessage(prefix + ChatColor.BLUE + e.getEntity().getName() + " has died");
		} else e.setDeathMessage("");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void entityDamageByEntity(EntityDamageByEntityEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		e.setRespawnLocation(loc);
	}
	
	@EventHandler
	public void serverListPingEvent(ServerListPingEvent e){
		if (restarting) e.setMotd(ChatColor.RED + "Restarting");
		else if (playing) e.setMotd(ChatColor.RED + "In Game");
		else e.setMotd(ChatColor.GREEN + "Waiting");
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (!playing || restarting) return;
		Location loc = e.getFrom();
		loc.setY(getConfig().getInt("floor-altitude"));
		final Location finalloc = loc;
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	finalloc.getWorld().getBlockAt(finalloc).setType(Material.AIR);
            }
        }, 5L);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e){
		if (e.getCause() == DamageCause.ENTITY_ATTACK) return;
		
		if (e.getCause() == DamageCause.VOID){
			e.setDamage(10);
		}
		e.setCancelled(false);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
	  if (e.getFoodLevel() < 20) {
	     ((Player)e.getEntity()).setFoodLevel(20);
	  }
	}
	
    public boolean connectToHub(Player player)
    {
      try
      {
        Messenger messenger = Bukkit.getMessenger();
        
        if (!messenger.isOutgoingChannelRegistered(plugin, "BungeeCord")) {
          messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
        }

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        
        DataOutputStream out = new DataOutputStream(byteArray);

        out.writeUTF("Connect"); out.writeUTF("Hub");

        player.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }

      return true;
    }
    
    public static void copyFolder(File src, File dest)
        	throws IOException{
     
        	if(src.isDirectory()){
     
        		//if directory not exists, create it
        		if(!dest.exists()){
        		   dest.mkdir();
        		}
     
        		//list all the directory contents
        		String files[] = src.list();
     
        		for (String file : files) {
        		   //construct the src and dest file structure
        		   File srcFile = new File(src, file);
        		   File destFile = new File(dest, file);
        		   //recursive copy
        		   copyFolder(srcFile,destFile);
        		}
     
        	}else if (!src.getName().contains("uid.dat")){
        		//if file, then copy it
        		//Use bytes stream to support all file types
        		InputStream in = new FileInputStream(src);
        	        OutputStream out = new FileOutputStream(dest); 
     
        	        byte[] buffer = new byte[1024];
     
        	        int length;
        	        //copy the file content in bytes 
        	        while ((length = in.read(buffer)) > 0){
        	    	   out.write(buffer, 0, length);
        	        }
     
        	        in.close();
        	        out.close();
        	}
        }
    
    public void clearArmor(Player player){
    	player.getInventory().setHelmet(null);
    	player.getInventory().setChestplate(null);
    	player.getInventory().setLeggings(null);
    	player.getInventory().setBoots(null);
	}
}
