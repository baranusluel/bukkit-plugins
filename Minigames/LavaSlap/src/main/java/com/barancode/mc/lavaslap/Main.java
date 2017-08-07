package main.java.com.barancode.mc.lavaslap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
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
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener{
	
	boolean playing = false;
	int countdown = 30;
	boolean countdownStarted = false;
	BukkitScheduler scheduler;
	Scoreboard scoreboard;
	ScoreboardManager manager;
	String prefix = ChatColor.RED + "[" + ChatColor.LIGHT_PURPLE + "LavaSlap" + ChatColor.RED + "] " + ChatColor.RESET;
	List<String> kicking = new LinkedList<String>();
	Main plugin = this;
	Location firstloc;
	
	boolean restarting = false;
	
	List<String> doubleJumpCooldown = new LinkedList<String>();
	
	Runnable runnable = null;
	
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
	    for (int x = -51; x <= 51; x++){
	    	for (int z = -51; z <= 51; z++){
	    		for (int y = 55; y <= 203; y++){
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
    	e.setJoinMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has joined LavaSlap");
    	
		e.getPlayer().teleport(firstloc);
    	
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run(){
        		e.getPlayer().setScoreboard(scoreboard);
        		
        		e.getPlayer().teleport(firstloc);
        		clearArmor(e.getPlayer());
        		e.getPlayer().getInventory().clear();
        		e.getPlayer().setAllowFlight(true);
        		e.getPlayer().setHealth(20);
        		e.getPlayer().setFoodLevel(20);
        		e.getPlayer().setFireTicks(0);
        		
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
        		        			
    	        				    ItemStack regular = new ItemStack(Material.LAVA);
    	        				    regular.addUnsafeEnchantment(Enchantment.KNOCKBACK, 4);
    	        				    ItemMeta meta = regular.getItemMeta();
    	        				    meta.setDisplayName(ChatColor.GOLD + "Lava");
    	        				    regular.setItemMeta(meta);
    	        				    
        		        			for (Player player : Bukkit.getOnlinePlayers()){
        		        				player.setScoreboard(scoreboard);
        	        				    player.getInventory().addItem(regular);
        		        			}
        		        			
        		        			for (int x = getConfig().getInt("floor.min-x"); x <= getConfig().getInt("floor.max-x"); x++){
        			        			for (int z = getConfig().getInt("floor.min-z"); z <= getConfig().getInt("floor.max-z"); z++){
        			        				for (int y = getConfig().getInt("floor.min-y"); y <= getConfig().getInt("floor.max-y"); y++){
            			        				Bukkit.getWorld("arena").getBlockAt(x, y, z).setType(Material.AIR);
        			        				}
        			        			}
        		        			}
        		        			
        		        	        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
        		        	            @Override
        		        	            public void run(){
        		        	                FallingBlock block = Bukkit.getWorld("arena").spawnFallingBlock(new Location(Bukkit.getWorld("arena"), getConfig().getInt("volcano.x"), getConfig().getInt("volcano.y"), getConfig().getInt("volcano.z")), Material.LAVA, (byte) 0);
        		        	                float x = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));
        		        	                float y = (float) 1.7;
        		        	                float z = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));
        		        	                block.setVelocity(new Vector(x, y, z));
        		        	                block.setDropItem(false);
        		        	            }
        		        	        }, 120L, 15L);
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
			e.setQuitMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has left LavaSlap");
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
        		
        		if (playing && Bukkit.getOnlinePlayers().length == 1){
        			restarting = true;
        			
        			Player p = Bukkit.getOnlinePlayers()[0];
        			p.sendMessage(prefix + ChatColor.YELLOW + "=-=-= You have won! =-=-=");
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
		if (!playing) e.setCancelled(true);
		else e.setCancelled(false);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player)) return;
		if (event.getCause() == DamageCause.ENTITY_ATTACK) return;
		
		if(event.getCause() == DamageCause.FALL) event.setCancelled(true);
		else event.setCancelled(false);
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
	}
	
	@EventHandler
	public void serverListPingEvent(ServerListPingEvent e){
		if (restarting) e.setMotd(ChatColor.RED + "Restarting");
		else if (playing) e.setMotd(ChatColor.RED + "In Game");
		else e.setMotd(ChatColor.GREEN + "Waiting");
	}
	
	@EventHandler
	public void onLiquidSpread(BlockFromToEvent e){
		if (e.getBlock().getType() == Material.LAVA || e.getBlock().getType() == Material.STATIONARY_LAVA || e.getBlock().getType() == Material.WATER || e.getBlock().getType() == Material.STATIONARY_WATER){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void toggleFly(final PlayerToggleFlightEvent e){
		/*Location newloc = e.getPlayer().getLocation().clone();
		newloc.setY(newloc.getY() - 2);
		
		if (newloc.getBlock().getType() == Material.AIR || newloc.getBlock().getType() == Material.WATER || newloc.getBlock().getType() == Material.LAVA || newloc.getBlock().getType() == Material.STATIONARY_WATER || newloc.getBlock().getType() == Material.STATIONARY_LAVA){
			e.setCancelled(true);
			e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), -1.5, e.getPlayer().getVelocity().getZ()));
			return;
		}*/
		
		e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), 1.6, e.getPlayer().getVelocity().getZ()));
		e.setCancelled(true);
		e.getPlayer().setAllowFlight(false);
		doubleJumpCooldown.add(e.getPlayer().getName());
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				doubleJumpCooldown.remove(e.getPlayer().getName());
			}
		}, 2 * 20L);
		
	}
	
	@EventHandler
	public void playerMove(final PlayerMoveEvent e){		
		if (e.getTo().getY() > e.getFrom().getY()
				&& !doubleJumpCooldown.contains(e.getPlayer().getName())) e.getPlayer().setAllowFlight(true);
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
