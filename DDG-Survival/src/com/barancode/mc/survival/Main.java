package com.barancode.mc.survival;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.barancode.mc.db.UUIDDatabase;

public class Main extends JavaPlugin{
	
	SurvivalCommands commands = new SurvivalCommands(this);
	SurvivalListener listener = new SurvivalListener(this);
	//BanFile banfile = new BanFile(this);
	PlayerList playerlist = new PlayerList(this);
	public SurvivalDatabase db = new SurvivalDatabase(this);
	BoundaryEffect effect = new BoundaryEffect(this);
	SurvivalBook book = new SurvivalBook(this);
	AFKChecker afk;
	Announcements announcements;
	LocationFinder locfinder = new LocationFinder(this);
	
	Main plugin = this;
	
	ItemStack bookitem;
	
	BukkitScheduler scheduler;
	ScoreboardManager manager;
	
	ItemStack emerald = new ItemStack(Material.EMERALD);
	
	List<String> flying = new LinkedList<String>();
	
	List<SurvivalChunk> dbchunks = new LinkedList<SurvivalChunk>();
	List<SurvivalPlayer> dbplayers = new LinkedList<SurvivalPlayer>();
	List<SurvivalChunkBan> dbbans = new LinkedList<SurvivalChunkBan>();
	List<SurvivalHome> dbhomes = new LinkedList<SurvivalHome>();
	List<SurvivalChunkMute> dbmutes = new LinkedList<SurvivalChunkMute>();
	List<SurvivalFriend> dbfriends = new LinkedList<SurvivalFriend>();
	List<DeathInventory> inventories = new LinkedList<DeathInventory>();
	
	Arrow arrow = new Arrow(this);
	List<ArrowPoint> dbarrowpoints = new LinkedList<ArrowPoint>();
	ConcurrentHashMap<UUID, Location> activearrows = new ConcurrentHashMap<UUID, Location>();
	ConcurrentHashMap<UUID, Location> oldarrows = new ConcurrentHashMap<UUID, Location>();
	
	List<SurvivalVoter> voters = new LinkedList<SurvivalVoter>();
	
	ConcurrentHashMap<UUID, Scoreboard> scoreboards = new ConcurrentHashMap<UUID, Scoreboard>();
	ConcurrentHashMap<String, String> binds = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, Location> afklocations = new ConcurrentHashMap<String, Location>();
	ConcurrentHashMap<String, String> friendMenuViewing = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> banMenuViewing = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, ArrowPoint> pointMenuViewing = new ConcurrentHashMap<String, ArrowPoint>();
	ConcurrentHashMap<String, Location> randomLocationReady = new ConcurrentHashMap<String, Location>();
	
	Menu menuOwn;
	ConcurrentHashMap<String, String> menuItem = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> pointDNs = new ConcurrentHashMap<String, String>();
	Menu menuNotClaimed;
	Menu menuOther;
	Menu menuTPA;
	Menu menuKeep;
	Menu menuFirst;
	Menu menuFriendList;
	Menu menuBannedList;
	Menu menuUnfriend;
	Menu menuFriend;
	Menu menuUnban;
	Menu menuArrow;
	Menu menuArrowPointsList;
	Menu menuPoint;
	
	List<String> tpaNameInput = new LinkedList<String>();
	List<String> pointNameInput = new LinkedList<String>();
	
	ConcurrentHashMap<String, Integer> firstTeleportSchedulers = new ConcurrentHashMap<String, Integer>();
	
	List<String> chunkLoading = new LinkedList<String>();
	
	static UUIDDatabase uuidDatabase;
	static Main instance = null;
	{
		instance = this;
	}
	
	public static Main getPlugin(){
		return instance;
	}
	
	public void onEnable(){
		uuidDatabase = new UUIDDatabase();
		
		saveDefaultConfig();
		//banfile.saveDefaultConfig();
		
		scheduler = Bukkit.getServer().getScheduler();
        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run(){
        		manager = Bukkit.getScoreboardManager();
        		db.initialize();
        	    effect.start();
        		announcements = new Announcements(plugin);
            }
        }, 0L);
		
		
		getServer().getPluginManager().registerEvents(listener, this);
		playerlist.populate();
		
		setupCommands();
		setupMenus();
	    
	    ItemMeta meta = emerald.getItemMeta();
	    meta.setDisplayName(Utils.replace(getConfig().getString("emerald-name")));
	    meta.setLore(Arrays.asList(Utils.replace(getConfig().getString("emerald-lore")).split("&&")));
	    emerald.setItemMeta(meta);
	    
	    bookitem = book.createDefaultBook("book");
	    
	    startFlyEffect();
	    
	    afk = new AFKChecker(this);
	    
		arrow.start();
	}
	
	public void onDisable(){
		scheduler.cancelAllTasks();
	}
	
	public void startFlyEffect(){
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
            	for (String s : flying){
            		if (!Bukkit.getOfflinePlayer(s).isOnline()) continue;
            		Player player = Bukkit.getPlayer(s);
            		if (!player.isFlying()) continue;
                	Location loc = player.getLocation(); loc.setY(loc.getY() - 1);
                	player.playSound(player.getLocation(), Sound.BLAZE_BREATH, 0.03F, 0.1F);
                    ParticleEffect.EXPLODE.display(loc, 0.2F, 0.05F, 0.2F, 0.0F, 20);
                }
            }
        }, 0L, 2L);
	}
	
	public void setupCommands(){
	    getCommand("sethome").setExecutor(commands);
	    getCommand("home").setExecutor(commands);
	    getCommand("tpa").setExecutor(commands);
	    getCommand("tpaaccept").setExecutor(commands);
	    getCommand("tpdeny").setExecutor(commands);
	    getCommand("chunks").setExecutor(commands);
	    getCommand("chunk").setExecutor(commands);
	    getCommand("setpoints").setExecutor(commands);
	    getCommand("setpower").setExecutor(commands);
	    getCommand("points").setExecutor(commands);
	    getCommand("power").setExecutor(commands);
	    getCommand("broadcast").setExecutor(commands);
	    getCommand("fly").setExecutor(commands);
	    getCommand("chunkban").setExecutor(commands);
	    getCommand("chunkunban").setExecutor(commands);
	    getCommand("chunkmute").setExecutor(commands);
	    getCommand("chunkunmute").setExecutor(commands);
	    getCommand("friend").setExecutor(commands);
	    getCommand("vote").setExecutor(commands);
	    getCommand("setbook").setExecutor(commands);
	    getCommand("togglearrow").setExecutor(commands);
	    getCommand("setspawn").setExecutor(commands);
	}
	
	@SuppressWarnings("deprecation")
	public void setMenuItem(Menu menu, String path, String item){
		String displayName = Utils.replace(getConfig().getString("menu." + path + "." + item + ".name"));
		menuItem.put(displayName, item);
		String idstring = getConfig().getString("menu." + path + "." + item + ".id"); String[] idparts = idstring.split(":");
		if (idparts.length == 1) menu.setOption(getConfig().getInt("menu." + path + "." + item + ".slot"), new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1), displayName, Utils.replace(getConfig().getString("menu." + path + "." + item + ".lore")).split("&&"));
		else if (idparts.length == 2) menu.setOption(getConfig().getInt("menu." + path + "." + item + ".slot"), new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1])), displayName, Utils.replace(getConfig().getString("menu." + path + "." + item + ".lore")).split("&&"));
	}
	
	public void setupMenus(){
		menuOwn = new Menu(Utils.replace(getConfig().getString("menu.own.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("ban")){
            		binds.put(event.getPlayer().getName(), "ban");
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.own.ban.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(Utils.replace(getConfig().getString("menu.own.ban.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("mute")){
            		binds.put(event.getPlayer().getName(), "mute");
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.own.mute.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(Utils.replace(getConfig().getString("menu.own.mute.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("friend")){
            		binds.put(event.getPlayer().getName(), "friend");
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.own.friend.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(Utils.replace(getConfig().getString("menu.own.friend.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("fly")){
            		event.getPlayer().performCommand("fly");
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("tpa")){
            		tpaNameInput.add(event.getPlayer().getName());
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.own.tpa.selected")));
            	} else if (name.equals("home")){
            		event.getPlayer().performCommand("home");
            	} else if (name.equals("declaim")){
            		event.getPlayer().performCommand("chunk declaim");
            	} else if (name.equals("sethome")){
            		event.getPlayer().performCommand("sethome");
            	} else if (name.equals("pvp")){
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.not-claimed.pvp.selected")));
            		event.getPlayer().setItemInHand(emerald);
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuNotClaimed = new Menu(Utils.replace(getConfig().getString("menu.not-claimed.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("friend")){
            		binds.put(event.getPlayer().getName(), "friend");
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.not-claimed.friend.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(Utils.replace(getConfig().getString("menu.not-claimed.friend.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("fly")){
            		event.getPlayer().performCommand("fly");
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("pvp")){
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.not-claimed.pvp.selected")));
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("claim")){
            		event.getPlayer().performCommand("chunk claim");
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("tpa")){
            		tpaNameInput.add(event.getPlayer().getName());
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.not-claimed.tpa.selected")));
            	} else if (name.equals("home")){
            		event.getPlayer().performCommand("home");
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuOther = new Menu(Utils.replace(getConfig().getString("menu.other.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("friend")){
            		binds.put(event.getPlayer().getName(), "friend");
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.other.friend.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(Utils.replace(getConfig().getString("menu.other.friend.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
                    event.setWillClose(true);
            	} else if (name.equals("fly")){
            		event.getPlayer().performCommand("fly");
            		event.getPlayer().setItemInHand(emerald);
                    event.setWillClose(true);
            	} else if (name.equals("tpa")){
            		tpaNameInput.add(event.getPlayer().getName());
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.other.tpa.selected")));
            	} else if (name.equals("home")){
            		event.getPlayer().performCommand("home");
            	} else if (name.equals("pvp")){
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.not-claimed.pvp.selected")));
            		event.getPlayer().setItemInHand(emerald);
            	}
            	event.setWillClose(true);
            }
        }, this);
		
		menuTPA = new Menu(Utils.replace(getConfig().getString("menu.tpa.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("tpaaccept")){
            		event.getPlayer().performCommand("tpaccept");
            	} else if (name.equals("tpadeny")){
            		event.getPlayer().performCommand("tpdeny");
            	}
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		menuKeep = new Menu(Utils.replace(getConfig().getString("menu.keepitems.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	DeathInventory di = null;
        	    for (Iterator<DeathInventory> it = inventories.iterator(); it.hasNext(); ){
        	    	DeathInventory tempdi = it.next();
            		if (event.getPlayer().getName().equals(tempdi.player)){
            			di = new DeathInventory(tempdi.player, tempdi.inv, tempdi.armorinv, tempdi.loc);
            			it.remove();
            		}
        	    }
            	if (name.equals("keepaccept")){
            		event.getPlayer().getInventory().setContents(di.inv);
            		event.getPlayer().getInventory().setArmorContents(di.armorinv);
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("keepitems-success")));
            		db.setPower(event.getPlayer().getUniqueId(), db.getPower(event.getPlayer().getUniqueId()) - 100);
            	} else if (name.equals("keepdecline")){
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("keepitems-decline")));
            		listener.dropItems(di);
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuFirst = new Menu(Utils.replace(getConfig().getString("menu.firstmenu.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent e) {
            	String name = menuItem.get(e.getName());
            	if (name.equals("friends")){
            		e.getPlayer().sendMessage(Utils.replace(getConfig().getString("loading")));
			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run(){
			            	showFriendList(e.getPlayer());
			            }
			        }, 2L);
            	} else if (name.equals("mainmenu")){
    				UUID owner = db.getOwner(e.getPlayer().getLocation().getChunk());
    				if (owner == null){
    			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    			            @Override
    			            public void run(){
    			            	menuNotClaimed.open(e.getPlayer());
    			            }
    			        }, 2L);
    				} else if (owner.equals(e.getPlayer().getUniqueId())){
    			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    			            @Override
    			            public void run(){
    			            	menuOwn.open(e.getPlayer());
    			            }
    			        }, 2L);
    				} else {
    			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    			            @Override
    			            public void run(){
    			            	menuOther.open(e.getPlayer());
    			            }
    			        }, 2L);
    				}
            	} else if (name.equals("bannedplayers")){
            		e.getPlayer().sendMessage(Utils.replace(getConfig().getString("loading")));
			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run(){
			            	showBannedList(e.getPlayer());
			            }
			        }, 2L);
            	} else if (name.equals("arrow")){
			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run(){
			            	menuArrow.open(e.getPlayer());
			            }
			        }, 2L);
            	}
                e.setWillClose(true);
            }
        }, this);
		
		menuUnfriend = new Menu(Utils.replace(getConfig().getString("menu.unfriend.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("unfriend")){
            		event.getPlayer().performCommand("friend remove " + friendMenuViewing.get(event.getPlayer().getName()));
            	}
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		menuFriend = new Menu(Utils.replace(getConfig().getString("menu.friend.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("unfriend")){
    		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    		            @Override
    		            public void run(){
    						Menu menu;
    						menu = menuUnfriend.clone();
    						menu.setName(Utils.replace(plugin.getConfig().getString("menu.friend.name")).replaceAll("<player>", friendMenuViewing.get(event.getPlayer().getName())));
    		            	menu.open(event.getPlayer());
    		            }
    		        }, 2L);
            	} else if (name.equals("tpa")){
            		event.getPlayer().performCommand("tpa " + friendMenuViewing.get(event.getPlayer().getName()));
            	}
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		menuUnban = new Menu(Utils.replace(getConfig().getString("menu.unban.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("unban")){
            		event.getPlayer().performCommand("chunkunban " + banMenuViewing.get(event.getPlayer().getName()));
            	}
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		menuArrow = new Menu(Utils.replace(getConfig().getString("menu.arrow.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("toggle")){
            		event.getPlayer().performCommand("togglearrow");
            	} else if (name.equals("points")){
    		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    		            @Override
    		            public void run(){
    	            		showPointsList(event.getPlayer());
    		            }
    		        }, 2L);
            	} else if (name.equals("setpoint")){
					List<ArrowPoint> list = plugin.db.getArrowPoints(event.getPlayer().getUniqueId());
					int size = list.size();
					if (event.getPlayer().hasPermission("survival.max")){
						if (size > 53){
							event.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("menu.arrow.setpoint.too-many")).replaceAll("<number>", "54"));
							return;
						}
					} else {
						if (size > 9){
							event.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("menu.arrow.setpoint.too-many")).replaceAll("<number>", "10"));
							return;
						}
					}
            		pointNameInput.add(event.getPlayer().getName());
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.arrow.setpoint.selected")));
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuPoint = new Menu(Utils.replace(getConfig().getString("menu.point.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	ArrowPoint ap = pointMenuViewing.get(event.getPlayer().getName());
            	if (name.equals("choose")){
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.point.choose.selected")).replaceAll("<point>", ap.name));
            		activearrows.put(event.getPlayer().getUniqueId(), ap.loc);
            	} else if (name.equals("remove")){
            		event.getPlayer().sendMessage(Utils.replace(getConfig().getString("menu.point.remove.selected")).replaceAll("<point>", ap.name));
            		plugin.db.deleteArrowPoint(event.getPlayer().getUniqueId(), ap.name);
            	}
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		setMenuItem(menuOwn, "own", "ban");
		setMenuItem(menuOwn, "own", "mute");
		setMenuItem(menuOwn, "own", "friend");
		setMenuItem(menuOwn, "own", "fly");
		setMenuItem(menuOwn, "own", "tpa");
		setMenuItem(menuOwn, "own", "home");
		setMenuItem(menuOwn, "own", "declaim");
		setMenuItem(menuOwn, "own", "sethome");
		
		setMenuItem(menuNotClaimed, "not-claimed", "friend");
		setMenuItem(menuNotClaimed, "not-claimed", "fly");
		setMenuItem(menuNotClaimed, "not-claimed", "claim");
		setMenuItem(menuNotClaimed, "not-claimed", "tpa");
		setMenuItem(menuNotClaimed, "not-claimed", "home");
		
		setMenuItem(menuOther, "other", "friend");
		setMenuItem(menuOther, "other", "fly");
		setMenuItem(menuOther, "other", "tpa");
		setMenuItem(menuOther, "other", "home");
		
		setMenuItem(menuTPA, "tpa", "tpaaccept");
		setMenuItem(menuTPA, "tpa", "tpadeny");
		
		setMenuItem(menuKeep, "keepitems", "keepaccept");
		setMenuItem(menuKeep, "keepitems", "keepdecline");
		
		setMenuItem(menuFirst, "firstmenu", "mainmenu");
		setMenuItem(menuFirst, "firstmenu", "friends");
		setMenuItem(menuFirst, "firstmenu", "bannedplayers");
		setMenuItem(menuFirst, "firstmenu", "arrow");
		
		setMenuItem(menuUnfriend, "unfriend", "unfriend");
		setMenuItem(menuUnfriend, "unfriend", "cancel");
		
		setMenuItem(menuFriend, "friend", "unfriend");
		setMenuItem(menuFriend, "friend", "tpa");
		
		setMenuItem(menuUnban, "unban", "unban");
		setMenuItem(menuUnban, "unban", "cancel");
		
		setMenuItem(menuArrow, "arrow", "toggle");
		setMenuItem(menuArrow, "arrow", "setpoint");
		setMenuItem(menuArrow, "arrow", "points");
		
		setMenuItem(menuPoint, "point", "choose");
		setMenuItem(menuPoint, "point", "remove");
	}
	
	@SuppressWarnings("deprecation")
	public void showFriendList(final Player p){
		menuFriendList = new Menu(Utils.replace(getConfig().getString("menu.friendlist.name")), 54, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
            	friendMenuViewing.put(event.getPlayer().getName(), event.getName());
		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Menu menu;
						menu = menuFriend.clone();
						menu.setName(Utils.replace(plugin.getConfig().getString("menu.friend.name")).replaceAll("<player>", event.getName()));
		            	menu.open(p);
		            }
		        }, 2L);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		List<Chunk> chunks = db.getChunks(p.getUniqueId());
		if (chunks.size() != 0){ 
    		int i = 0;
    		for (UUID s : db.getFriends(chunks.get(0))){
    			String name = Utils.getName(s);
    			
    			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
    			SkullMeta meta = (SkullMeta)item.getItemMeta(); meta.setOwner(name);
    			List<String> lore = new LinkedList<String>();
    			if (Bukkit.getOfflinePlayer(name).isOnline()) lore.add(ChatColor.GREEN + getConfig().getString("online"));
    			else lore.add(ChatColor.RED + getConfig().getString("offline"));
    			meta.setLore(lore);
    			item.setItemMeta(meta);
    			menuFriendList.setOption(i, item, name);
    			i++;
    		}
		}
		
		menuFriendList.open(p);
	}
	
	@SuppressWarnings("deprecation")
	public void showBannedList(final Player p){
		menuBannedList = new Menu(Utils.replace(getConfig().getString("menu.bannedlist.name")), 54, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
            	banMenuViewing.put(event.getPlayer().getName(), event.getName());
		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Menu menu;
						menu = menuUnban.clone();
						menu.setName(Utils.replace(plugin.getConfig().getString("menu.unban.name")).replaceAll("<player>", event.getName()));
		            	menu.open(p);
		            }
		        }, 2L);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		List<Chunk> chunks = db.getChunks(p.getUniqueId());
		int i = 0;
		if (chunks.size() != 0){
    		for (UUID s : db.getBannedPlayers(chunks.get(0))){
    			String name = Utils.getName(s);
    			
    			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
    			SkullMeta meta = (SkullMeta)item.getItemMeta(); meta.setOwner(name);
    			List<String> lore = new LinkedList<String>();
    			if (Bukkit.getOfflinePlayer(name).isOnline()) lore.add(ChatColor.GREEN + getConfig().getString("online"));
    			else lore.add(ChatColor.RED + getConfig().getString("offline"));
    			meta.setLore(lore);
    			item.setItemMeta(meta);
    			menuBannedList.setOption(i, item, name);
    			i++;
    		}
		}
		
		menuBannedList.open(p);
	}
	

	public void showPointsList(final Player p){
		menuArrowPointsList = new Menu(Utils.replace(getConfig().getString("menu.points.name")), 54, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Menu menu;
						menu = menuPoint.clone();
						String dn = Utils.replace(plugin.getConfig().getString("menu.point.name")).replaceAll("<point>", event.getName());
						menu.setName(dn);
		            	menu.open(event.getPlayer());
		            	
		            	for (ArrowPoint ap : dbarrowpoints){
		            		if (ap.name.equals(event.getName()) && ap.player.equals(event.getPlayer().getUniqueId())){
		            			pointMenuViewing.put(event.getPlayer().getName(), ap);
		            		}
		            	}
		            }
		        }, 2L);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		List<ArrowPoint> list = db.getArrowPoints(p.getUniqueId());
		int i = 0;
		for (ArrowPoint ap : list){
			@SuppressWarnings("deprecation")
			ItemStack item = new ItemStack(Material.getMaterial(getConfig().getInt("menu.points.id")), 1);
			menuArrowPointsList.setOption(i, item, ap.name);
			i++;
		}
		
		menuArrowPointsList.open(p);
	}
	
	
	
	
    
    public boolean isEmerald(ItemStack item){
    	if (item == null) return false;
    	ItemMeta im = item.getItemMeta();
    	if (im == null) return false;
    	List<String> lore = im.getLore();
    	if (lore == null) return false;
    	
    	ItemStack emerald = plugin.emerald;
    	ItemMeta imsecond = emerald.getItemMeta();
    	if (lore.equals(imsecond.getLore()) && item.getType().equals(emerald.getType())) return true;
    	else return false;
    }
}
