package com.barancode.mc.experia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

public class Main extends JavaPlugin{
	
	Random random = new Random();
	HashMap<String, String> giveKit = new HashMap<String, String>();
	Menu menu;
	BukkitScheduler scheduler;
	Announcements announcements;
	ScoreboardManager manager;
	Main plugin = this;
	ConcurrentHashMap<UUID, Scoreboard> scoreboards = new ConcurrentHashMap<UUID, Scoreboard>();
	ExperiaListener listener = new ExperiaListener(this);
	Items itemmanager = new Items(this);
	Utils utils = new Utils(this);
	ExperiaBook book = new ExperiaBook(this);
	DataFile customconfig = new DataFile(this);
	ExperiaCommands commands = new ExperiaCommands(this);
	MyItemsCommands micommands = new MyItemsCommands(this);
	MyItemsListener milistener = new MyItemsListener(this);
	ZombiesListener zlistener = new ZombiesListener(this);
	ItemFile ItemFile;
	HashMap<ItemMeta, String> MyItemsItems = new HashMap<ItemMeta, String>();
	List<String> events = new LinkedList<String>();
	HashMap<UUID, List<String>> teamInvites = new HashMap<UUID, List<String>>();
	List<ItemStack> kitItems = new LinkedList<ItemStack>();
	HashMap<UUID, String> teamMembers = new HashMap<UUID, String>();
	HashMap<UUID, List<String>> teamMessages = new HashMap<UUID, List<String>>();
	List<String> invincible = new LinkedList<String>();
	List<GiantHit> giantHits = new LinkedList<GiantHit>();
	
	//HashMap<Location, Runnable> pendingChests = new HashMap<Location, Runnable>();
	
	ConcurrentHashMap<String, Location> loggingOut = new ConcurrentHashMap<String, Location>();
	ConcurrentHashMap<String, List<Integer>> loggingOutSchedulers = new ConcurrentHashMap<String, List<Integer>>();
	List<String> kicking = new LinkedList<String>();
	HashMap<String, ItemStack[]> NPCInventories = new HashMap<String, ItemStack[]>();
	List<UUID> deleteInventories = new LinkedList<UUID>();
	HashMap<String, LivingEntity> NPCs = new HashMap<String, LivingEntity>();
	List<String> PVPPlayers = new LinkedList<String>();
	ConcurrentHashMap<String, Integer> PVPSchedulers = new ConcurrentHashMap<String, Integer>();
	List<String> loggingOutCooldown = new LinkedList<String>();
	
	ConcurrentHashMap<UUID, Integer> playerKills = new ConcurrentHashMap<UUID, Integer>();
	ConcurrentHashMap<UUID, Integer> zombieKills = new ConcurrentHashMap<UUID, Integer>();
	ConcurrentHashMap<UUID, Integer> deaths = new ConcurrentHashMap<UUID, Integer>();
	
	HashMap<Integer, Location> spawns = new HashMap<Integer, Location>();
	
	int giantTicks = 1;
	
	static UUIDDatabase uuidDatabase;
	
	public void onEnable(){
		uuidDatabase = new UUIDDatabase();
		
		setupMyItems();
		saveDefaultConfig();
	    customconfig.saveDefaultConfig();
		getServer().getPluginManager().registerEvents(listener, this);
		getServer().getPluginManager().registerEvents(milistener, this);
		getServer().getPluginManager().registerEvents(zlistener, this);
		setupCommands();
		loadTeams();
		
		kitItems.add(book.createDefaultBook("book"));
		
		itemmanager.fillItemList();
		itemmanager.prepareChestLoot();
		
		scheduler = Bukkit.getServer().getScheduler();
        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run(){
        		manager = Bukkit.getScoreboardManager();
        		announcements = new Announcements(plugin);
        		//Bukkit.getWorld("Experia-game").setTicksPerMonsterSpawns(4);
        		
        		menu = new Menu(utils.replace(getConfig().getString("kit-menu-name")), 9, new Menu.OptionClickEventHandler() {
                    @Override
                    public void onOptionClick(Menu.OptionClickEvent event) {
                    	String olds = event.getName();
                    	String s = "";
                    	char c;
                    	for (int i = 2; i < olds.length(); i++){
                    		c = olds.charAt(i);
                    		s = s + c;
                    	}
                    	Bukkit.getServer().dispatchCommand(event.getPlayer(), "kit " + s);
                        event.setWillClose(true);
                    }
                }, plugin);
        		
        		List<String> list = getConfig().getStringList("kitnames");
        		for (String s : list){
        			String lore = getConfig().getString("kits." + s + ".lore");
        			int id = getConfig().getInt("kits." + s + ".iconid");
        			int loc = getConfig().getInt("kits." + s + ".iconlocation");
        			menu.setOption(loc, new ItemStack(Material.getMaterial(id)), ChatColor.DARK_AQUA + s, utils.replace(lore));
        		}
        		
        		int spawncount = getConfig().getInt("spawncount");
        		for (int i = 1; i <= spawncount; i++){
					double x = plugin.getConfig().getDouble("spawns." + i + ".x");
					double y = plugin.getConfig().getDouble("spawns." + i + ".y");
					double z = plugin.getConfig().getDouble("spawns." + i + ".z");
					float yaw = (float)plugin.getConfig().getDouble("spawns." + i + ".yaw");
					float pitch = (float)plugin.getConfig().getDouble("spawns." + i + ".pitch");
					World world = Bukkit.getWorld(plugin.getConfig().getString("spawns." + i + ".world"));
					Location loc = new Location(world, x, y, z, yaw, pitch);
					spawns.put(i, loc);
        		}
            }
        }, 0L);
		
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override
		    public void run() {
		        World world = Bukkit.getWorld("Experia-game");
		        List<Entity> entities = world.getEntities();
		        for (Entity entity : entities){
		        	if (entity.getType() == EntityType.ZOMBIE){
		        		Zombie zombie = (Zombie) entity;
		    			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1210, 1));
		        	} else if (entity.getType() == EntityType.GIANT){
		        		Giant g = (Giant) entity;
		        		g.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1210, 1));
		        		g.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1210, 1));
		        	}
		        }
		    }
		}, 0, 1200);
		
		/*scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override
		    public void run() {
		        World world = Bukkit.getWorld("Experia-game");
		        Giant g;
		        if (Bukkit.getOnlinePlayers().length > 0){
			        List<Entity> entities = world.getEntities();
			        for (Entity entity : entities){
			        	if (entity.getType() == EntityType.GIANT){
			        		entity.remove();
			        	}
			        }
		        	for (int i = 1; i <= getConfig().getInt("giants-count"); i++){
			        	Entity e = world.spawnEntity(new Location(world, getConfig().getDouble("giants." + i + ".x"), getConfig().getDouble("giants." + i + ".y"), getConfig().getDouble("giants." + i + ".z")), EntityType.GIANT);
			        	g = (Giant) e;
			        	g.setMaxHealth(200);
			        	g.setHealth(200);
			        	g.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1210, 1));
			        	g.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1210, 1));
			        	g.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 1));
		        	}
		        	Bukkit.broadcastMessage(utils.replace(getConfig().getString("giantspawnmessage")));
		        }
		    }
		}, 24000, 24000);*/
        
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override
		    public void run() {
		    	Bukkit.broadcastMessage(ChatColor.GOLD + "Warning: " + ChatColor.YELLOW + "All entities will be removed in 15 seconds");
		    	
		    	scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
		    		@Override
		    		public void run(){
				    	for (World w : Bukkit.getWorlds()){
				    		for (Entity e : w.getEntities()){
				    			if (e.getType() == EntityType.PLAYER) continue;
				    			e.remove();
				    		}
				    	}
				    	Bukkit.broadcastMessage(ChatColor.DARK_RED + "Warning: " + ChatColor.RED + "All entities have been removed");
		    		}
		    	}, 300);
		    }
		}, 72000, 72000);
		
		customZombies();
	}
	
	public void onDisable(){
		/*Collection<Runnable> chests = pendingChests.values();
		for (Runnable task : chests){
			task.run();
		}*/
		scheduler.cancelAllTasks();
	}
	
	public void customZombies(){
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override
		    public void run() {
		    	HashSet<Chunk> zombieChunks = new HashSet<Chunk>();
		    	HashSet<Chunk> playerChunks = new HashSet<Chunk>();
		        for (Player p : Bukkit.getOnlinePlayers()){
		        	if (!p.getWorld().getName().equalsIgnoreCase("Experia-game")) continue;
		        	
	        		if (p.getLevel() >= 40 && giantTicks == 60){
	        			playerChunks.add(p.getLocation().getChunk());
	        			
	        			int xoffset = random.nextInt(20) - 10;
		        		int zoffset = random.nextInt(20) - 10;
		        		Location l = p.getLocation();
		        		if (xoffset < 0)
		        			l.setX(l.getX() - 10 + xoffset);
		        		else
		        			l.setX(l.getX() + 10 + xoffset);
		        		if (zoffset < 0)
		        			l.setZ(l.getZ() - 10 + zoffset);
		        		else
		        			l.setZ(l.getZ() + 10 + zoffset);
		        		l.setY(l.getWorld().getHighestBlockYAt(l) + 1);
		        		
		        		zombieChunks.add(l.getChunk());
		        		
	        			Entity e = p.getWorld().spawnEntity(l, EntityType.GIANT);
			        	Giant g = (Giant) e;
			        	g.setMaxHealth(200);
			        	g.setHealth(200);
			        	g.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1210, 1));
			        	g.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1210, 1));
			        	g.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 1));
			        	p.sendMessage(ChatColor.GOLD + "A giant has spawned near you");
			        	g.setTarget((LivingEntity)p);
	        		} else {
	        			if (playerChunks.contains(p.getLocation().getChunk())) continue;
	        			else playerChunks.add(p.getLocation().getChunk());
	        			
	        			int skeletonChance = random.nextInt(10);
	        			if (skeletonChance == 0){
	        				int zombieLevel = p.getLevel() / 10;
	        				int armor;
	        				if (zombieLevel > 0) armor = random.nextInt(zombieLevel);
	        				else armor = 0;
	        				
	        				ItemStack chestplate = null;
	        				if (armor == 1) chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
	        				else if (armor == 2 || armor == 3) chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
	        				else if (armor == 4) chestplate = new ItemStack(Material.IRON_CHESTPLATE);
	        				else if (armor > 4) chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
	        				
			        		int xoffset = random.nextInt(20) - 10;
			        		int zoffset = random.nextInt(20) - 10;
			        		Location l = p.getLocation();
			        		if (xoffset < 0)
			        			l.setX(l.getX() - 10 + xoffset);
			        		else
			        			l.setX(l.getX() + 10 + xoffset);
			        		if (zoffset < 0)
			        			l.setZ(l.getZ() - 10 + zoffset);
			        		else
			        			l.setZ(l.getZ() + 10 + zoffset);
			        		l.setY(l.getWorld().getHighestBlockYAt(l) + 1);
			        		
			        		if (zombieChunks.contains(l.getChunk())) continue;
			        		else zombieChunks.add(l.getChunk());
			        		
			        		int zombies = 0;
			        		for (Entity e : l.getChunk().getEntities()){
			        			if (e.getType() == EntityType.ZOMBIE || e.getType() == EntityType.SKELETON) zombies++;
			        			if (e.getType() == EntityType.GIANT) zombies = 8;
			        		}
			        		if (zombies > 9) continue;
			        		
			        		Entity e = l.getWorld().spawnEntity(l, EntityType.SKELETON);
			        		LivingEntity le = (LivingEntity)e;
			        		le.getEquipment().setChestplate(chestplate);
			        		le.getEquipment().setChestplateDropChance(0);
			        		le.getEquipment().setHelmet(new ItemStack(Material.SKULL_ITEM, 1, (short)2));
			        		((Skeleton)e).setTarget((LivingEntity)p);
	        			} else {
	        				int zombieLevel = p.getLevel() / 10;
	        				int armor;
	        				if (zombieLevel > 0) armor = random.nextInt(zombieLevel);
	        				else armor = 0;
	        				
	        				ItemStack chestplate = null;
	        				if (armor == 1) chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
	        				else if (armor == 2 || armor == 3) chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
	        				else if (armor == 4) chestplate = new ItemStack(Material.IRON_CHESTPLATE);
	        				else if (armor > 4) chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
	        				
			        		int xoffset = random.nextInt(20) - 10;
			        		int zoffset = random.nextInt(20) - 10;
			        		Location l = p.getLocation();
			        		if (xoffset < 0)
			        			l.setX(l.getX() - 10 + xoffset);
			        		else
			        			l.setX(l.getX() + 10 + xoffset);
			        		if (zoffset < 0)
			        			l.setZ(l.getZ() - 10 + zoffset);
			        		else
			        			l.setZ(l.getZ() + 10 + zoffset);
			        		l.setY(l.getWorld().getHighestBlockYAt(l) + 1);
			        		
			        		if (zombieChunks.contains(l.getChunk())) continue;
			        		else zombieChunks.add(l.getChunk());
			        		
			        		int zombies = 0;
			        		for (Entity e : l.getChunk().getEntities()){
			        			if (e.getType() == EntityType.ZOMBIE || e.getType() == EntityType.SKELETON) zombies++;
			        			if (e.getType() == EntityType.GIANT) zombies = 10;
			        		}
			        		if (zombies > 9) continue;
			        		
			        		Entity e = l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
			        		LivingEntity le = (LivingEntity)e;
			        		le.getEquipment().setChestplate(chestplate);
			        		le.getEquipment().setChestplateDropChance(0);
			        		((Zombie)e).setTarget((LivingEntity)p);
	        			}
	        		}
		        }
		        
		        if (giantTicks < 60) giantTicks++;
		        else giantTicks = 1;
		    }
		}, 200, 200);
	}
	
	public void setupCommands(){
	    getCommand("experia").setExecutor(commands);
	    getCommand("kit").setExecutor(commands);
	    getCommand("setbook").setExecutor(commands);
	    getCommand("bcmckillall").setExecutor(commands);
	    getCommand("myitems").setExecutor(micommands);
	}
	
	public void loadTeams(){
		List<String> teams = customconfig.getCustomConfig().getStringList("teams");
		for (String team : teams){
			List<String> players = customconfig.getCustomConfig().getStringList("team." + team + ".members");
			for (String player : players)
				teamMembers.put(UUID.fromString(player), team);
		}
	}
	
	public void setupMyItems(){
		ItemFile = new ItemFile(this);
		File kititem = new File(getDataFolder(), "kit.txt");
		File spawnitem = new File(getDataFolder(), "spawn.txt");
		File logoutitem = new File (getDataFolder(), "logout.txt");
		
		if (!getDataFolder().exists()){
			try {
				Files.createDirectory(Paths.get(getDataFolder().getAbsolutePath()));
				kititem.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(kititem));
				out.write("displayname: &6Kit Selector");
				out.newLine();
				out.write("id: 388");
				out.newLine();
				out.write("damageentityevent: kit");
				out.newLine();
				out.write("leftclickairevent: kit");
				out.newLine();
				out.write("rightclickentityevent: kit");
				out.newLine();
				out.write("rightclickblockevent: kit");
				out.newLine();
				out.write("rightclickairevent: kit");
				out.newLine();
				out.write("leftclickblockevent: kit");
				out.newLine();
				out.write("lore: &6- &7Kies jouw kit !");
				out.newLine();
				out.write("slot: 0");
				out.newLine();
				out.close();
				
				spawnitem.createNewFile();
				out = new BufferedWriter(new FileWriter(spawnitem));
				out.write("displayname: &CExperia&7-&cSpawn");
				out.newLine();
				out.write("id: 268");
				out.newLine();
				out.write("damageentityevent: experia spawn");
				out.newLine();
				out.write("leftclickblockevent: experia spawn");
				out.newLine();
				out.write("leftclickairevent: experia spawn");
				out.newLine();
				out.write("rightclickentityevent: experia spawn");
				out.newLine();
				out.write("rightclickblockevent: experia spawn");
				out.newLine();
				out.write("rightclickairevent: experia spawn");
				out.newLine();
				out.write("lore: &6- &7Kit gekozen ? Spawn dan !");
				out.newLine();
				out.write("slot: 1");
				out.newLine();
				out.close();
				
				logoutitem.createNewFile();
				out = new BufferedWriter(new FileWriter(logoutitem));
				out.write("displayname: &CLog Out");
				out.newLine();
				out.write("id: 381");
				out.newLine();
				out.write("damageentityevent: experia logout");
				out.newLine();
				out.write("leftclickblockevent: experia logout");
				out.newLine();
				out.write("leftclickairevent: experia logout");
				out.newLine();
				out.write("rightclickentityevent: experia logout");
				out.newLine();
				out.write("rightclickblockevent: experia logout");
				out.newLine();
				out.write("rightclickairevent: experia logout");
				out.newLine();
				out.write("lore: &6Safely log out of survival !");
				out.newLine();
				out.write("slot: 8");
				out.newLine();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (File file : getDataFolder().listFiles()){
			if (!file.getName().equals("config.yml") && !file.getName().equals("data.yml")){
				
				String name = file.getName();
				name = name.replace(".txt", "");
				
				
				String displayname = ItemFile.read(name, "displayname");
				displayname = utils.replace(displayname);
				
				String lore = ItemFile.read(name, "lore");
				String[] loreparts = lore.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(utils.replace(loreparts[i]));
				}
				
				String id = ItemFile.read(name, "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}

				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				MyItemsItems.put(meta, name);
			}
		}
		getLogger().info("Loaded items:");
		for (String name : MyItemsItems.values()){
			getLogger().info("- " + name);
		}
		
		events.add("damageentityevent");
		events.add("rightclickentityevent");
		events.add("leftclickblockevent");
		events.add("rightclickblockevent");
		events.add("leftclickairevent");
		events.add("rightclickairevent");
		events.add("consumeevent");
		events.add("blockplaceevent");
		events.add("holdevent");
		events.add("throwevent");
		events.add("bedenterevent");
	}
	
	
	public void updateScoreboard(Player player){
		int deathscount = 0;
		if (deaths.containsKey(player.getUniqueId())){
			deathscount = deaths.get(player.getUniqueId());
		} else {
			deathscount = customconfig.getCustomConfig().getInt("players." + player.getUniqueId() + ".deaths");
			deaths.put(player.getUniqueId(), deathscount);
		}
		int playerkills = 0;
		if (playerKills.containsKey(player.getUniqueId())){
			playerkills = playerKills.get(player.getUniqueId());
		} else {
			playerkills = customconfig.getCustomConfig().getInt("players." + player.getUniqueId() + ".playerkills");
			playerKills.put(player.getUniqueId(), playerkills);
		}
		int zombiekills = 0;
		if (zombieKills.containsKey(player.getUniqueId())){
			zombiekills = zombieKills.get(player.getUniqueId());
		} else {
			zombiekills = customconfig.getCustomConfig().getInt("players." + player.getUniqueId() + ".zombiekills");
			zombieKills.put(player.getUniqueId(), zombiekills);
		}
		
		Scoreboard board = scoreboards.get(player.getUniqueId());
		if (board != null){
			Objective objective = board.getObjective("scoreboard");
			objective.getScore(Bukkit.getOfflinePlayer(utils.replace(getConfig().getString("scoreboard.zombie-kills")))).setScore(zombiekills);
			objective.getScore(Bukkit.getOfflinePlayer(utils.replace(getConfig().getString("scoreboard.player-kills")))).setScore(playerkills);
			objective.getScore(Bukkit.getOfflinePlayer(utils.replace(getConfig().getString("scoreboard.deaths")))).setScore(deathscount);
			player.setScoreboard(board);
		} else {
			Scoreboard board2 = manager.getNewScoreboard();
		    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
		    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		    objective.setDisplayName(utils.replace(getConfig().getString("scoreboard.name")));
		    Score zombieKills = objective.getScore(Bukkit.getOfflinePlayer(utils.replace(getConfig().getString("scoreboard.zombie-kills"))));
		    zombieKills.setScore(zombiekills);
		    Score playerKills = objective.getScore(Bukkit.getOfflinePlayer(utils.replace(getConfig().getString("scoreboard.player-kills"))));
		    playerKills.setScore(playerkills);
		    Score Deaths = objective.getScore(Bukkit.getOfflinePlayer(utils.replace(getConfig().getString("scoreboard.deaths"))));
		    Deaths.setScore(deathscount);
		    player.setScoreboard(board2);
			scoreboards.put(player.getUniqueId(), board2);
		}
	}
}
