package com.barancode.mc.ddg.survival;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
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

public class Main extends JavaPlugin{
	
	SurvivalCommands commands = new SurvivalCommands(this);
	SurvivalListener listener = new SurvivalListener(this);
	BanFile banfile = new BanFile(this);
	Utils utils = new Utils(this);
	PlayerList playerlist = new PlayerList(this);
	SurvivalDatabase db = new SurvivalDatabase(this);
	BoundaryEffect effect = new BoundaryEffect(this);
	SurvivalBook book = new SurvivalBook(this);
	AFKChecker afk;
	
	Main plugin = this;
	
	ItemStack bookitem;
	
	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	ScoreboardManager manager;
	
	ItemStack emerald = new ItemStack(Material.EMERALD);
	
	List<String> flying = new LinkedList<String>();
	
	List<SurvivalChunk> dbchunks = new LinkedList<SurvivalChunk>();
	List<SurvivalPlayer> dbplayers = new LinkedList<SurvivalPlayer>();
	List<SurvivalChunkBan> dbbans = new LinkedList<SurvivalChunkBan>();
	List<SurvivalHome> dbhomes = new LinkedList<SurvivalHome>();
	List<SurvivalChunkMute> dbmutes = new LinkedList<SurvivalChunkMute>();
	List<SurvivalFriend> dbfriends = new LinkedList<SurvivalFriend>();
	
	List<SurvivalVoter> voters = new LinkedList<SurvivalVoter>();
	
	HashMap<String, Scoreboard> scoreboards = new HashMap<String, Scoreboard>();
	HashMap<String, String> binds = new HashMap<String, String>();
	ConcurrentHashMap<String, Location> locations = new ConcurrentHashMap<String, Location>();
	HashMap<String, String> unfriending = new HashMap<String, String>();
	
	
	List<DeathInventory> inventories = new LinkedList<DeathInventory>();
	
	Menu menuOwn;
	HashMap<String, String> menuItem = new HashMap<String, String>();
	HashMap<String, String> menuFriendItem = new HashMap<String, String>();
	Menu menuNotClaimed;
	Menu menuOther;
	Menu menuVote;
	Menu menuTPA;
	Menu menuKeep;
	Menu menuFirst;
	Menu menuFriendList;
	Menu menuUnfriend;
	
	List<String> tpaNameInput = new LinkedList<String>();
	
	//Random random = new Random();
	
	public void onEnable(){
		saveDefaultConfig();
		banfile.saveDefaultConfig();
		
		manager = Bukkit.getScoreboardManager();
		
		db.initialize();
		
		getServer().getPluginManager().registerEvents(listener, this);
		playerlist.populate();
		
		setupCommands();
		setupMenus();
	    
	    effect.start();
		//randomGenerate();
	    
	    ItemMeta meta = emerald.getItemMeta();
	    meta.setDisplayName(utils.replace(getConfig().getString("emerald-name")));
	    meta.setLore(Arrays.asList(utils.replace(getConfig().getString("emerald-lore")).split("&&")));
	    emerald.setItemMeta(meta);
	    
	    bookitem = book.createDefaultBook("book");
	    
	    startFlyEffect();
	    
	    afk = new AFKChecker(this);
	}
	
	/*public void randomGenerate(){
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run(){
    			int x = random.nextInt(100000);
    			int z = random.nextInt(100000);
    			Location loc = new Location(Bukkit.getWorld("world"), x, 0, z);
    			int y = Bukkit.getWorld("world").getHighestBlockYAt(loc);
    			loc.setY(y + 1);
    			Chunk chunk = Bukkit.getWorld("world").getChunkAt(loc);
    			chunk.load();
            }
        }, 0L, 5L);
	}*/
	
	@SuppressWarnings("deprecation")
	public void startFlyEffect(){
        scheduler.scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()){
                	if (flying.contains(player.getName())){
                		if (!player.isFlying()) continue;
	                	Location loc = player.getLocation(); loc.setY(loc.getY() - 1);
	                	player.playSound(player.getLocation(), Sound.BLAZE_BREATH, 0.03F, 0.1F);
	                    ParticleEffects.sendToLocation(ParticleEffects.EXPLODE, loc, 0.2F, 0.05F, 0.2F, 0.0F, 20);
                	}
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
	    getCommand("ban").setExecutor(commands);
	    getCommand("kick").setExecutor(commands);
	    getCommand("unban").setExecutor(commands);
	    getCommand("broadcast").setExecutor(commands);
	    getCommand("fly").setExecutor(commands);
	    getCommand("chunkban").setExecutor(commands);
	    getCommand("chunkunban").setExecutor(commands);
	    getCommand("chunkmute").setExecutor(commands);
	    getCommand("chunkunmute").setExecutor(commands);
	    getCommand("friend").setExecutor(commands);
	    getCommand("vote").setExecutor(commands);
	    getCommand("setbook").setExecutor(commands);
	}
	
	@SuppressWarnings("deprecation")
	public void setMenuItem(Menu menu, String path, String item){
		String displayName = utils.replace(getConfig().getString("menu." + path + "." + item + ".name"));
		menuItem.put(displayName, item);
		String idstring = getConfig().getString("menu." + path + "." + item + ".id"); String[] idparts = idstring.split(":");
		if (idparts.length == 1) menu.setOption(getConfig().getInt("menu." + path + "." + item + ".slot"), new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1), displayName, utils.replace(getConfig().getString("menu." + path + "." + item + ".lore")).split("&&"));
		else if (idparts.length == 2) menu.setOption(getConfig().getInt("menu." + path + "." + item + ".slot"), new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1])), displayName, utils.replace(getConfig().getString("menu." + path + "." + item + ".lore")).split("&&"));
	}
	
	public void setupMenus(){
		menuOwn = new Menu(utils.replace(getConfig().getString("menu.own.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("ban")){
            		binds.put(event.getPlayer().getName(), "ban");
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.own.ban.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(utils.replace(getConfig().getString("menu.own.ban.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("mute")){
            		binds.put(event.getPlayer().getName(), "mute");
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.own.mute.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(utils.replace(getConfig().getString("menu.own.mute.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("friend")){
            		binds.put(event.getPlayer().getName(), "friend");
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.own.friend.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(utils.replace(getConfig().getString("menu.own.friend.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("fly")){
            		event.getPlayer().performCommand("fly");
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("tpa")){
            		tpaNameInput.add(event.getPlayer().getName());
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.own.tpa.selected")));
            	} else if (name.equals("home")){
            		event.getPlayer().performCommand("home");
            	} else if (name.equals("declaim")){
            		event.getPlayer().performCommand("chunk declaim");
            	} else if (name.equals("sethome")){
            		event.getPlayer().performCommand("sethome");
            	} else if (name.equals("pvp")){
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.not-claimed.pvp.selected")));
            		event.getPlayer().setItemInHand(emerald);
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuNotClaimed = new Menu(utils.replace(getConfig().getString("menu.not-claimed.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("friend")){
            		binds.put(event.getPlayer().getName(), "friend");
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.not-claimed.friend.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(utils.replace(getConfig().getString("menu.not-claimed.friend.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
            	} else if (name.equals("fly")){
            		event.getPlayer().performCommand("fly");
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("pvp")){
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.not-claimed.pvp.selected")));
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("claim")){
            		event.getPlayer().performCommand("chunk claim");
            		event.getPlayer().setItemInHand(emerald);
            	} else if (name.equals("tpa")){
            		tpaNameInput.add(event.getPlayer().getName());
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.not-claimed.tpa.selected")));
            	} else if (name.equals("home")){
            		event.getPlayer().performCommand("home");
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuOther = new Menu(utils.replace(getConfig().getString("menu.other.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("friend")){
            		binds.put(event.getPlayer().getName(), "friend");
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.other.friend.selected")));
            		ItemMeta meta = event.getPlayer().getItemInHand().getItemMeta();
            		meta.setDisplayName(utils.replace(getConfig().getString("menu.other.friend.name")));
            		event.getPlayer().getItemInHand().setItemMeta(meta);
                    event.setWillClose(true);
            	} else if (name.equals("fly")){
            		event.getPlayer().performCommand("fly");
            		event.getPlayer().setItemInHand(emerald);
                    event.setWillClose(true);
            	} else if (name.equals("vote")){
            		menuVote.open(event.getPlayer());
            		event.getPlayer().setItemInHand(emerald);
                    event.setWillClose(false);
            	} else if (name.equals("tpa")){
            		tpaNameInput.add(event.getPlayer().getName());
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.other.tpa.selected")));
            	} else if (name.equals("home")){
            		event.getPlayer().performCommand("home");
            	} else if (name.equals("pvp")){
            		event.getPlayer().sendMessage(utils.replace(getConfig().getString("menu.not-claimed.pvp.selected")));
            		event.getPlayer().setItemInHand(emerald);
            	}
            	event.setWillClose(true);
            }
        }, this);
		
		menuVote = new Menu(utils.replace(getConfig().getString("menu.vote.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("voteup")){
            		event.getPlayer().performCommand("vote up");
            	} else if (name.equals("votedown")){
            		event.getPlayer().performCommand("vote down");
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuTPA = new Menu(utils.replace(getConfig().getString("menu.tpa.name")), 9, new Menu.OptionClickEventHandler() {
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
		
		menuKeep = new Menu(utils.replace(getConfig().getString("menu.keepitems.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	DeathInventory di = null;
        	    for (Iterator<DeathInventory> it = inventories.iterator(); it.hasNext(); ){
        	    	DeathInventory tempdi = it.next();
            		if (event.getPlayer().getName().equals(tempdi.player)){
            			di = tempdi;
            			it.remove();
            		}
        	    }
            	if (name.equals("keepaccept")){
            		Bukkit.getPlayer(di.player).getInventory().setContents(di.inv);
            		Bukkit.getPlayer(di.player).sendMessage(utils.replace(getConfig().getString("keepitems-success")));
            		db.setPower(event.getPlayer().getName(), db.getPower(event.getPlayer().getName()) - 100);
            	} else if (name.equals("keepdecline")){
            		Bukkit.getPlayer(di.player).sendMessage(utils.replace(getConfig().getString("keepitems-decline")));
            		event.getPlayer().getInventory().setItem(8, emerald);
            		listener.dropItems(di);
            	}
                event.setWillClose(true);
            }
        }, this);
		
		menuFirst = new Menu(utils.replace(getConfig().getString("menu.firstmenu.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent e) {
            	String name = menuItem.get(e.getName());
            	if (name.equals("friends")){
			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run(){
			            	showFriendList(e.getPlayer());
			            }
			        }, 2L);
            	} else if (name.equals("mainmenu")){
    				String owner = db.getOwner(e.getPlayer().getLocation().getChunk());
    				if (owner.equals(e.getPlayer().getName())){
    			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    			            @Override
    			            public void run(){
    			            	menuOwn.open(e.getPlayer());
    			            }
    			        }, 2L);
    				} else if (owner.equals("")){
    			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    			            @Override
    			            public void run(){
    			            	menuNotClaimed.open(e.getPlayer());
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
            	}
                e.setWillClose(true);
            }
        }, this);
		
		menuUnfriend = new Menu(utils.replace(getConfig().getString("menu.unfriend.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	String name = menuItem.get(event.getName());
            	if (name.equals("unfriend")){
            		event.getPlayer().performCommand("friend remove " + unfriending.get(event.getPlayer().getName()));
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
		setMenuItem(menuOwn, "own", "pvp");
		
		setMenuItem(menuNotClaimed, "not-claimed", "friend");
		setMenuItem(menuNotClaimed, "not-claimed", "fly");
		setMenuItem(menuNotClaimed, "not-claimed", "pvp");
		setMenuItem(menuNotClaimed, "not-claimed", "claim");
		setMenuItem(menuNotClaimed, "not-claimed", "tpa");
		setMenuItem(menuNotClaimed, "not-claimed", "home");
		
		setMenuItem(menuOther, "other", "friend");
		setMenuItem(menuOther, "other", "fly");
		setMenuItem(menuOther, "other", "vote");
		setMenuItem(menuOther, "other", "tpa");
		setMenuItem(menuOther, "other", "home");
		setMenuItem(menuOther, "other", "pvp");
		
		setMenuItem(menuVote, "vote", "voteup");
		setMenuItem(menuVote, "vote", "votedown");
		
		setMenuItem(menuTPA, "tpa", "tpaaccept");
		setMenuItem(menuTPA, "tpa", "tpadeny");
		
		setMenuItem(menuKeep, "keepitems", "keepaccept");
		setMenuItem(menuKeep, "keepitems", "keepdecline");
		
		setMenuItem(menuFirst, "firstmenu", "mainmenu");
		setMenuItem(menuFirst, "firstmenu", "friends");
		
		setMenuItem(menuUnfriend, "unfriend", "unfriend");
		setMenuItem(menuUnfriend, "unfriend", "cancel");
	}
	
	public void showFriendList(final Player p){
		menuFriendList = new Menu(utils.replace(getConfig().getString("menu.friendlist.name")), 54, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
            	unfriending.put(event.getPlayer().getName(), menuFriendItem.get(event.getName()));
		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Menu menu;
						menu = menuUnfriend.clone();
						menu.setName(plugin.utils.replace(plugin.getConfig().getString("menu.unfriend.name")).replaceAll("<player>", event.getName()));
		            	menu.open(p);
		            }
		        }, 2L);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, this);
		
		List<Chunk> chunks = db.getChunks(p.getName());
		int i = 0;
		for (String s : db.getFriends(chunks.get(0))){
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
			SkullMeta meta = (SkullMeta)item.getItemMeta(); meta.setOwner(s);
			if (Bukkit.getOfflinePlayer(s).isOnline()){
				
			}
			item.setItemMeta(meta);
			menuFriendList.setOption(i, item, s);
			menuFriendItem.put(s, s);
			i++;
		}
		
		menuFriendList.open(p);
	}
}
