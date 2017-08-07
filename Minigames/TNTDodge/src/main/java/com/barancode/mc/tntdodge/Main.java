package main.java.com.barancode.mc.tntdodge;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
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
	String prefix = ChatColor.RED + "[" + ChatColor.LIGHT_PURPLE + "TNT Dodge" + ChatColor.RED + "] " + ChatColor.RESET;
	List<String> kicking = new LinkedList<String>();
	Main plugin = this;
	Location firstloc;
	Location loc;
	Location killerloc;
	Random random = new Random();
	String killer = "";
	
	boolean restarting = false;
	
	Runnable runnable = null;
	
	/*@SuppressWarnings("deprecation")
	public void reinitializeVariables(){
		playing = false;
		countdown = 30;
		countdownStarted = false;
		killer = "";
		
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
    	
    	for (Entity e : arena.getEntities()){
    		e.remove();
    	}
    	
	    for (int x = -40; x <= 40; x++){
	    	for (int z = -40; z <= 40; z++){
	    		for (int y = 50; y <= 100; y++){
	    			Block block = world.getBlockAt(x, y, z);
	    			Block newblock = arena.getBlockAt(x, y, z);
	    			newblock.setType(block.getType());
	    			newblock.getState().setData(block.getState().getData());
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
        		
        		int x2 = getConfig().getInt("spawn.x");
        		int y2 = getConfig().getInt("spawn.y");
        		int z2 = getConfig().getInt("spawn.z");
        		loc = new Location(world, x2, y2, z2);
        		
        		int x3 = getConfig().getInt("killer-spawn.x");
        		int y3 = getConfig().getInt("killer-spawn.y");
        		int z3 = getConfig().getInt("killer-spawn.z");
        		killerloc = new Location(world, x3, y3, z3);
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
		e.setJoinMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has joined TNT Dodge");
		
		e.getPlayer().teleport(firstloc);
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run(){
        		e.getPlayer().setScoreboard(scoreboard);
        		
        		e.getPlayer().teleport(firstloc);
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
        		            	    Score count = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Runners Left"));
        		            	    count.setScore(playerCount - 1);
        		            	    Score time = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Time Left"));
        		            	    time.setScore(90);
        		            	    
        		                    scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
        		                        @Override
        		                        public void run(){
        		                    		Objective objective = scoreboard.getObjective("scoreboard");
        		                    		Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Time Left"));
        		                    		int i = score.getScore() - 1;
        		                    		score.setScore(i);
        		                    		if (i == 0){
        		                    			Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "The runners have won!");
        		                    			restarting = true;
        		                    			for (Player p : Bukkit.getOnlinePlayers()){
        		                    				connectToHub(p);
        		                    			}
        		                    		}
        		                        }
        		                    }, 20L, 20L);
        		            	    
        		            	    int i = random.nextInt(playerCount);
        		            	    Player killer = Bukkit.getOnlinePlayers()[i];
        		            	    plugin.killer = killer.getName();
        		            	    killer.teleport(killerloc);
        		            	    killer.setScoreboard(scoreboard);
        		            	    killer.sendMessage(prefix + ChatColor.RED + "You are the killer! Drop TNT on the runners by clicking the floor");
        		            	    killer.getInventory().addItem(new ItemStack(Material.TNT));
        		        			
        		        			for (Player player : Bukkit.getOnlinePlayers()){
        		        				if (player.equals(killer)) continue;
        		        				player.setScoreboard(scoreboard);
        	        					player.teleport(loc);
        	        					player.sendMessage(prefix + ChatColor.GREEN + "You are a runner! Run away from the TNT that the killer drops");
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
			if (Bukkit.getOnlinePlayers().length == 1){
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
			}
		} if (!kicking.contains(e.getPlayer().getName())){
			e.setQuitMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has left TNT Dodge");
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
	        		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Runners Left")).setScore(Bukkit.getOnlinePlayers().length - 1);
            	}
        		
        		if (playing && Bukkit.getOnlinePlayers().length == 1){
        			restarting = true;
        			
        			Player p = Bukkit.getOnlinePlayers()[0];
        			p.sendMessage(prefix + ChatColor.YELLOW + "=-=-= You, the killer, have won! =-=-=");
        	    	connectToHub(p);
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
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		e.setRespawnLocation(firstloc);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		e.setCancelled(true);
		Location loc = e.getBlockAgainst().getLocation();
		if (loc.getY() == getConfig().getInt("killer-floor")){
			Location newloc = loc; newloc.setY(79);
			loc.getWorld().spawnEntity(newloc, EntityType.PRIMED_TNT);
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e){
		e.setYield(0);
	    for (Iterator<Block> it = e.blockList().iterator(); it.hasNext(); ){
	        Block block = it.next();
	        if ((int)block.getLocation().getY() != getConfig().getInt("runner-floor")) it.remove();
	    }
	    final List<Block> blocks = e.blockList();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	for (Block block : blocks){
            		block.setType(block.getType());
            	}
            }
        }, 80L);
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
			e.setDamage(10);
		}
		e.setCancelled(false);
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
