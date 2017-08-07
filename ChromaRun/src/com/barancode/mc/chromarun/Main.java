package com.barancode.mc.chromarun;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
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
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener{
	
	boolean playing = false;
	int countdown = 30;
	boolean countdownStarted = false;
	BukkitScheduler scheduler;
	Scoreboard scoreboard;
	ScoreboardManager manager;
	String prefix = ChatColor.RED + "[" + ChatColor.LIGHT_PURPLE + "ChromaRun" + ChatColor.RED + "] " + ChatColor.RESET;
	List<String> kicking = new LinkedList<String>();
	Main plugin = this;
	Location firstloc;
	Location loc;
	Random random = new Random();
	ConcurrentHashMap<String, Integer> playerColors = new ConcurrentHashMap<String, Integer>();
	double speed = 6.5;
	
	boolean restarting = false;
	
	@SuppressWarnings("deprecation")
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
	    
        scheduler.scheduleAsyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	World world = Bukkit.getWorld("world");
            	World arena = Bukkit.getWorld("arena");
        	    for (int x = -50; x <= 50; x++){
        	    	for (int z = -50; z <= 50; z++){
        	    		for (int y = 40; y <= 100; y++){
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
            }
        }, 0L);

	}
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		scheduler = Bukkit.getServer().getScheduler();
		
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
        		
        		File firstWorld = new File("./world");
        		File secondWorld = new File("./arena");
        		try {
            		FileUtils.deleteDirectory(secondWorld);
					copyFolder(firstWorld, secondWorld);
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
                WorldCreator wc = new WorldCreator("arena");
                wc.generateStructures(false);
                wc.type(WorldType.FLAT);
                wc.createWorld();
                
        		World world = Bukkit.getWorld(getConfig().getString("first-join-spawn.world"));
        		int x = getConfig().getInt("first-join-spawn.x");
        		int y = getConfig().getInt("first-join-spawn.y");
        		int z = getConfig().getInt("first-join-spawn.z");
        		firstloc = new Location(world, x, y, z);
        		
        		World world2 = Bukkit.getWorld(getConfig().getString("spawn.world"));
        		int x2 = getConfig().getInt("spawn.x");
        		int y2 = getConfig().getInt("spawn.y");
        		int z2 = getConfig().getInt("spawn.z");
        		loc = new Location(world2, x2, y2, z2);
            }
        }, 0L);
	}
	
 	@SuppressWarnings("deprecation")
	public void doBoard(){
		getLogger().info("doing the board");
		for (Player p : Bukkit.getOnlinePlayers()){
			p.getInventory().clear();
		}
		
		for (int x = 0; x <= 56; x = x + 2){
			for (int z = 0; z >= -56; z = z - 2){
				int data = random.nextInt(15);
				for (int x2 = 0; x2 < 2; x2++){
					for (int z2 = 0; z2 > -2; z2--){
						Block block = Bukkit.getWorld(getConfig().getString("spawn.world")).getBlockAt(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), x + x2, 64, z + z2));
						block.setType(Material.WOOL);
						block.setData((byte)data);
					}
				}
			}
		}
		for (int z = -20; z >= -36; z--){
			for (int y = 74; y <= 90; y++){
				Block block = Bukkit.getWorld(getConfig().getString("spawn.world")).getBlockAt(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), 58, y, z));
				block.setData((byte)15);
				block = Bukkit.getWorld(getConfig().getString("spawn.world")).getBlockAt(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), -2, y, z));
				block.setData((byte)15);
			}
		}
		for (int x = 20; x <= 36; x++){
			for (int y = 74; y <= 90; y++){
				Block block = Bukkit.getWorld(getConfig().getString("spawn.world")).getBlockAt(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), x, y, -58));
				block.setData((byte)15);
				block = Bukkit.getWorld(getConfig().getString("spawn.world")).getBlockAt(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), x, y, 2));
				block.setData((byte)15);
			}
		}
        scheduler.scheduleAsyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	for (Player p : Bukkit.getOnlinePlayers()){
            		int data = random.nextInt(15);
            		playerColors.put(p.getName(), data);
            		for (int z = -20; z >= -36; z--){
            			for (int y = 74; y <= 90; y++){
            				p.sendBlockChange(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), 58, y, z), Material.WOOL, (byte)data);
            				p.sendBlockChange(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), -2, y, z), Material.WOOL, (byte)data);
            			}
            		}
            		for (int x = 20; x <= 36; x++){
            			for (int y = 74; y <= 90; y++){
            				p.sendBlockChange(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), x, y, -58), Material.WOOL, (byte)data);
            				p.sendBlockChange(new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), x, y, 2), Material.WOOL, (byte)data);
            			}
            		}
            		
            		for (int i = 0; i < 9; i++){
            			p.getInventory().setItem(i, new ItemStack(Material.WOOL, 1, (byte)data));
            		}
            	}
            }
        }, 80L);
        
        scheduler.scheduleAsyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	for (Player p : Bukkit.getOnlinePlayers()){
            		int data = playerColors.get(p.getName());
            		for (int x = 0; x <= 56; x++){
            			for (int z = 0; z >= -56; z--){
            				Location loc = new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), x, 64, z);
    						Block block = Bukkit.getWorld(getConfig().getString("spawn.world")).getBlockAt(loc);
    						if (block.getData() != (byte)data) p.sendBlockChange(loc, Material.WEB, (byte)0);
    						
    				        /*scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
    				            @Override
    				            public void run(){
    				            	doBoard();
    				            }
    				        }, 80L);*/
            			}
            		}
            	}
            }
        }, 80L + (int)(20 * speed));
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
	public void onJoin(PlayerJoinEvent e){
		doBoard();
		
		e.setJoinMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has joined ChromaRun");
		e.getPlayer().setScoreboard(scoreboard);
		
		e.getPlayer().teleport(firstloc);
		clearArmor(e.getPlayer());
		e.getPlayer().getInventory().clear();
		
		int playerCount = Bukkit.getOnlinePlayers().length;
		if (playerCount < 2){
			Bukkit.broadcastMessage(prefix + ChatColor.GOLD + "You need " + ChatColor.GRAY + (2 - playerCount) + ChatColor.GOLD + " more players to start");
		} else {
			if (!countdownStarted){
				countdownStarted = true;
				Bukkit.broadcastMessage(prefix + ChatColor.GOLD + "The match will start in 30 seconds");
		        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
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
	        					player.teleport(loc);
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
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (restarting){
			e.setQuitMessage("");
			reinitializeVariables();
		} if (!kicking.contains(e.getPlayer().getName())){
			e.setQuitMessage(prefix + ChatColor.BLUE + e.getPlayer().getName() + " has left ChromaRun");
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
        			
        			Bukkit.getOnlinePlayers()[0].sendMessage(prefix + ChatColor.YELLOW + "=-=-= You have won! =-=-=");
        	    	connectToHub(Bukkit.getOnlinePlayers()[0]);
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
			connectToHub(e.getEntity());
			e.setDeathMessage(prefix + ChatColor.BLUE + e.getEntity().getName() + " has died");
		} else e.setDeathMessage("");
	}
	
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent e){
		if (!playing) e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player)) return;
		if(event.getCause().equals(DamageCause.FALL)) event.setCancelled(true);

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
	public void onMove(PlayerMoveEvent e){
		Location loc = e.getTo(); loc.setY(loc.getY()-1);
		if (loc.getBlock().getType() == Material.WEB) e.getPlayer().setHealth(0);
		e.getPlayer().getB
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

        out.writeUTF("Connect"); out.writeUTF("BCMC1");

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
