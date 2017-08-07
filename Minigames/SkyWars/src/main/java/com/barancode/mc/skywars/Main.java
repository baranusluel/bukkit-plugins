package main.java.com.barancode.mc.skywars;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
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

public class Main extends JavaPlugin implements Listener{
	
	boolean playing = false;
	int countdown = 30;
	boolean countdownStarted = false;
	BukkitScheduler scheduler;
	Scoreboard scoreboard;
	ScoreboardManager manager;
	String prefix = ChatColor.RED + "[" + ChatColor.LIGHT_PURPLE + "SkyWars" + ChatColor.RED + "] " + ChatColor.RESET;
	List<String> kicking = new LinkedList<String>();
	Main plugin = this;
	HashMap<String, Integer> teams = new HashMap<String, Integer>();
	Location firstloc;
	
	boolean restarting = false;
	
	Runnable runnable = null;
	
	/*public void reinitializeVariables(){
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
	    teams = new HashMap<String, Integer>();
	    
	    scheduler.cancelAllTasks();
	    
    	World world = Bukkit.getWorld("world");
    	World arena = Bukkit.getWorld("arena");
	    for (int x = -50; x <= 50; x++){
	    	for (int z = -50; z <= 50; z++){
	    		for (int y = 40; y <= 100; y++){
	    			Block block = world.getBlockAt(x, y, z);
	    			Block newblock = arena.getBlockAt(x, y, z);
	    			newblock.setType(block.getType());
	    			newblock.getState().setData((MaterialData)(block.getState().getData()));
	    			if (block.getType() == Material.CHEST){
	    				Chest chest = (Chest)block.getState();
	    				Chest newchest = (Chest)newblock.getState();
	    				newchest.getInventory().setContents(chest.getInventory().getContents());
	    			}
	    		}
	    	}
	    }
	    
	    restarting = false;
	}*/
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		scheduler = Bukkit.getServer().getScheduler();
		
		File firstWorld = new File("./world");
		for (File f : new File(firstWorld + "/playerdata").listFiles()){
			f.delete();
		}
		File secondWorld = new File("./arena");
		try {
    		delete(secondWorld);
			copyFolder(firstWorld, secondWorld);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
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
        		int x = getConfig().getInt("first-join-spawn.x");
        		int y = getConfig().getInt("first-join-spawn.y");
        		int z = getConfig().getInt("first-join-spawn.z");
        		firstloc = new Location(world, x, y, z);
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
		e.setJoinMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has joined SkyWars");
		
		e.getPlayer().teleport(firstloc);
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run(){
        		e.getPlayer().setScoreboard(scoreboard);
        		
        		e.getPlayer().teleport(firstloc);
        		clearArmor(e.getPlayer());
        		e.getPlayer().getInventory().clear();
        		e.getPlayer().setHealth(20.0);
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
        		        			
        		        			int tempCount = 0;
        		        			for (Player player : Bukkit.getOnlinePlayers()){
        		        				player.setScoreboard(scoreboard);
        		        				
        		        				tempCount++;
        		        				if ((tempCount - 1) % 4 == 0 && tempCount != 1){
        		        					tempCount = 1;
        		        				}
        	        					World world = Bukkit.getWorld("arena");
        	        					int x = getConfig().getInt("spawns." + tempCount + ".x");
        	        					int y = getConfig().getInt("spawns." + tempCount + ".y");
        	        					int z = getConfig().getInt("spawns." + tempCount + ".z");
        	        					Location loc = new Location(world, x, y, z);
        	        					player.teleport(loc);
        	        					
        	        					teams.put(player.getName(), tempCount);
        		        			}
        		            		return;
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
					ProcessBuilder builder = new ProcessBuilder("bash", "restart.sh");
					try {
						builder.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			Bukkit.shutdown();
		} if (!kicking.contains(e.getPlayer().getName())){
			e.setQuitMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has left SkyWars");
		} else {
			e.setQuitMessage("");
			kicking.remove(e.getPlayer().getName());
		}
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run(){
            	if (!playing){
	        		Objective objective = scoreboard.getObjective("scoreboard");
	        		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players")).setScore(Bukkit.getOnlinePlayers().length);
            	} else {
	        		Objective objective = scoreboard.getObjective("scoreboard");
	        		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players Left")).setScore(Bukkit.getOnlinePlayers().length);
            	}
            	
            	boolean sameTeam = true;
            	int initialTeam = -1;
            	for (Player p : Bukkit.getOnlinePlayers()){
            		if (initialTeam == -1) initialTeam = teams.get(p.getName());
            		else if (initialTeam != teams.get(p.getName())){
            			sameTeam = false;
            			break;
            		}
            	}
        		
        		if (playing && sameTeam){
        			restarting = true;
        			
        			for (Player p : Bukkit.getOnlinePlayers()){
            			p.sendMessage(prefix + ChatColor.YELLOW + "=-=-= You have won! =-=-=");
            	    	connectToHub(p);
        			}
        		}
            }
        }, 1L);
	}
	
	@SuppressWarnings("deprecation")
	public void updateScoreboardCountdown(){
		Objective objective = scoreboard.getObjective("scoreboard");
		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Countdown")).setScore(countdown);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if (playing && !restarting){
			kicking.add(e.getEntity().getName());
			connectToHub(e.getEntity());
			e.setDeathMessage(prefix + ChatColor.BLUE + e.getEntity().getName() + " has died");
		} else e.setDeathMessage("");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void entityDamageByEntity(EntityDamageByEntityEvent e){
		if (!playing){
			e.setCancelled(true);
			return;
		}
		
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player damager = (Player)e.getDamager();
			Player damaged = (Player)e.getEntity();
			if (teams.get(damager.getName()) == teams.get(damaged.getName()) && teams.containsKey(damager.getName())){
				e.setCancelled(true);
				damager.sendMessage(ChatColor.RED + "You can not hit that player, you are on the same team!");
				return;
			}
		}
		
		e.setCancelled(false);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		e.setRespawnLocation(firstloc);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if (e.getBlock().getLocation().getY() > getConfig().getInt("lobby-altitude")) e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if (e.getBlock().getLocation().getY() > getConfig().getInt("lobby-altitude")) e.setCancelled(true);
	}
	
	@EventHandler
	public void serverListPingEvent(ServerListPingEvent e){
		if (restarting) e.setMotd(ChatColor.RED + "Restarting");
		else if (playing) e.setMotd(ChatColor.RED + "In Game");
		else e.setMotd(ChatColor.GREEN + "Waiting");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e){
		if (e.getCause() == DamageCause.ENTITY_ATTACK) return;
		
		if (e.getCause() == DamageCause.VOID){
			e.setDamage(10.0);
		}
		e.setCancelled(false);
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e){
		e.setCancelled(true);
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
    
    public void delete(File f) throws IOException {
	  if (f.isDirectory()) {
	    for (File c : f.listFiles())
	      delete(c);
	  }
	  if (!f.delete())
	    throw new FileNotFoundException("Failed to delete file: " + f);
	}
    
    public void clearArmor(Player player){
    	player.getInventory().setHelmet(null);
    	player.getInventory().setChestplate(null);
    	player.getInventory().setLeggings(null);
    	player.getInventory().setBoots(null);
	}
}
