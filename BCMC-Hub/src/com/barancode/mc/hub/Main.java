package com.barancode.mc.hub;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.barancode.mc.db.AppDatabase;
import com.barancode.mc.db.AppObject;
import com.barancode.mc.db.TimeDatabase;
import com.barancode.mc.db.UUIDDatabase;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class Main extends JavaPlugin implements Listener, PluginMessageListener{
	ItemStack i = new ItemStack(Material.FEATHER);
	ItemStack punch = new ItemStack(Material.ARROW);
	UUIDDatabase uuidDatabase;
	AppDatabase appDatabase;
	TimeDatabase timeDatabase;
	Menu menu = null;
	Menu punchMenu = null;
	HashMap<String, String> menuItems = new HashMap<String, String>();
	BukkitScheduler scheduler = null;
	HashMap<String, Integer> players = new HashMap<String, Integer>();
	HashSet<String> doubleJumpCooldown = new HashSet<String>();
	HashSet<String> used = new HashSet<String>();
	HashSet<String> flying = new HashSet<String>();
	
	HashSet<String> punchOff = new HashSet<String>();
	HashSet<String> superPunch = new HashSet<String>();
	
	HashSet<String> listeningAppType = new HashSet<String>();
	
	HashMap<String, String> reviewing = new HashMap<String, String>();
	HashSet<String> newPlayers = new HashSet<String>();
	
	ItemStack book = new ItemStack(Material.BOOK);
	InfoBook infobook = new InfoBook(this);
	
	static Main plugin;
	{
		plugin = this;
	}
	
	@SuppressWarnings("deprecation")
	public void onEnable(){
		uuidDatabase = new UUIDDatabase();
		appDatabase = new AppDatabase();
		timeDatabase = new TimeDatabase();
		
		scheduler = Bukkit.getScheduler();
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Server Selector");
		meta.setLore(Arrays.asList(ChatColor.DARK_GREEN + "Choose a server", ChatColor.DARK_GREEN + "to play on"));
		i.setItemMeta(meta);
		
		meta = punch.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Punching Options");
		meta.setLore(Arrays.asList(ChatColor.BLUE + "Change the punching", ChatColor.BLUE + "settings"));
		punch.setItemMeta(meta);
		
		book = infobook.getBook();
		
		
		
		players.put("experia", 0);
		players.put("mh1", 0);
		players.put("mh2", 0);
		players.put("survival", 0);
		players.put("factions", 0);
		players.put("creative", 0);
		
		menu = new Menu(ChatColor.translateAlternateColorCodes('&', "&4Choose a server"), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	event.setWillClose(true);
            	String name = menuItems.get(event.getName());
            	if (name.equalsIgnoreCase("minigames")){
            		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + event.getPlayer().getName() + " -875.5 53 -646.5");
            		return;
            	}
            	connectToServer(event.getPlayer(), name);
            }
        }, this);
		setMenuItem("experia");
		setMenuItem("minigames");
		setMenuItem("mh1");
		setMenuItem("mh2");
		setMenuItem("survival"); 
		setMenuItem("factions");
		setMenuItem("creative");
		
		punchMenu = new Menu(ChatColor.translateAlternateColorCodes('&', "&1Punching Options"), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	event.setWillClose(true);
            	String name = menuItems.get(event.getName());
            	String username = event.getPlayer().getName();
            	if (name.equals("toggle")){
            		if (punchOff.contains(username)){
            			punchOff.remove(username);
            			event.getPlayer().sendMessage(ChatColor.GOLD + "You have turned punching on");
            			return;
            		} else {
            			punchOff.add(username);
            			event.getPlayer().sendMessage(ChatColor.GOLD + "You have turned punching off");
            			return;
            		}
            	} else if (name.equals("regular")){
            		if (superPunch.contains(username)) superPunch.remove(username);
            		event.getPlayer().sendMessage(ChatColor.GOLD + "You are now using the regular punch");
            		return;
            	} else if (name.equals("super")){
		    		if (event.getPlayer().hasPermission("bcmc.donator.hubpunch")){
	            		superPunch.add(username);
	            		event.getPlayer().sendMessage(ChatColor.GOLD + "You are now using the " + ChatColor.BOLD + "super-punch");
	            		return;
					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "Only VIPs can use the super-punch. Donate at: " + ChatColor.GRAY + "shop.bcmcnetwork.com");
						return;
					}
            	}
            }
        }, this);
		
		setPunchMenuItem("toggle");
		setPunchMenuItem("regular");
		setPunchMenuItem("super");
		
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				for (String s : players.keySet()){
					getPlayerCount(s);
				}
			}
		}, 0L, 5 * 20L);
		
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		
		
		scheduler.scheduleAsyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				for (Player p : Bukkit.getOnlinePlayers()){
					updateTime(p);
				}
			}
		}, 0, 15 * 60 * 20);
	}
	
    public boolean connectToServer(Player p, String server){
      try{
        Messenger messenger = Bukkit.getMessenger();
        
        if (!messenger.isOutgoingChannelRegistered(this, "BungeeCord")) {
          messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        }

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        
        DataOutputStream out = new DataOutputStream(byteArray);

        out.writeUTF("Connect"); out.writeUTF(server);

        p.sendPluginMessage(this, "BungeeCord", byteArray.toByteArray());
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }

      return true;
    }
    
    public boolean getPlayerCount(String server){
        try{
          Messenger messenger = Bukkit.getMessenger();
          
          if (!messenger.isOutgoingChannelRegistered(this, "BungeeCord")) {
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
          }

          ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
          
          DataOutputStream out = new DataOutputStream(byteArray);

          out.writeUTF("PlayerCount");
          out.writeUTF(server);

          if (Bukkit.getOnlinePlayers().length > 0) Bukkit.getOnlinePlayers()[0].sendPluginMessage(this, "BungeeCord", byteArray.toByteArray());
        }
        catch (Exception ex) {
          ex.printStackTrace();
          return false;
        }

        return true;
      }
    

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (!channel.equals("BungeeCord")) {
        return;
      }
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      in.readUTF();
      String server = in.readUTF().toLowerCase();
      int playercount = in.readInt();
      players.put(server, playercount);
      updateMenu();
    }
    
    public void updateMenu(){
    	menu.removeOptions();
		setMenuItem("experia");
		setMenuItem("minigames");
		setMenuItem("mh1");
		setMenuItem("mh2");
		setMenuItem("survival"); 
		setMenuItem("factions");
		setMenuItem("creative");
    }
	
	@SuppressWarnings("deprecation")
	public void setMenuItem(String item){
		String displayName = ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".NAME"));
		menuItems.put(displayName, item);
		String idstring = getConfig().getString(item + ".ID");
		String[] idparts = idstring.split(":");
		if (idparts.length == 1){
			if (isInteger(idstring)) menu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(Integer.parseInt(idstring))), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).replaceAll("<count>", players.get(item) + "").split("--"));
			else menu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(idstring)), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).replaceAll("<count>", players.get(item) + "").split("--"));
		} else if (idparts.length == 2){
			if (isInteger(idparts[0])) menu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (short)Integer.parseInt(idparts[1])), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).replaceAll("<count>", players.get(item) + "").split("--"));
			else menu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(idparts[0]), 1, (short)Integer.parseInt(idparts[1])), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).replaceAll("<count>", players.get(item) + "").split("--"));
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setPunchMenuItem(String item){
		String displayName = ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".NAME"));
		menuItems.put(displayName, item);
		String idstring = getConfig().getString(item + ".ID");
		String[] idparts = idstring.split(":");
		if (idparts.length == 1){
			if (isInteger(idstring)) punchMenu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(Integer.parseInt(idstring))), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).split("--"));
			else punchMenu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(idstring)), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).split("--"));
		} else if (idparts.length == 2){
			if (isInteger(idparts[0])) punchMenu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (short)Integer.parseInt(idparts[1])), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).split("--"));
			else punchMenu.setOption(getConfig().getInt(item + ".SLOT"), new ItemStack(Material.getMaterial(idparts[0]), 1, (short)Integer.parseInt(idparts[1])), displayName, ChatColor.translateAlternateColorCodes('&', getConfig().getString(item + ".LORE")).split("--"));
		}
	}
	
	public boolean isInteger(String s){
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public void updateTime(Player p){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL("http://freegeoip.net/xml/" + p.getAddress().getHostString()).openStream());
			doc.getDocumentElement().normalize();
			float longitude = Float.parseFloat(getNodeValue("Longitude", doc.getDocumentElement().getChildNodes()));
			int timezone = (int)(longitude / 15);
			DateFormat dateFormat = new SimpleDateFormat("HH");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String UTCtime = dateFormat.format(new Date());
			int time = Integer.parseInt(UTCtime) + timezone;
			long mctime = (time - 6) * 1000;
			if (mctime < 0) mctime = 24000 + mctime;
			p.setPlayerTime(mctime, false);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public String getNodeValue(String tagName, NodeList nodes ) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            NodeList childNodes = node.getChildNodes();
	            for (int y = 0; y < childNodes.getLength(); y++ ) {
	                Node data = childNodes.item(y);
	                if ( data.getNodeType() == Node.TEXT_NODE )
	                    return data.getNodeValue();
	            }
	        }
	    }
	    return "";
	}

	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(final PlayerJoinEvent e){
		for (ItemStack i : e.getPlayer().getInventory().getContents()){
			if (i == null) continue;
			if (i.getType() == Material.AIR) continue;
			if (i.getType() == Material.BOOK_AND_QUILL) continue;
			e.getPlayer().getInventory().remove(i);
		}
		e.getPlayer().getInventory().addItem(i);
		e.getPlayer().getInventory().addItem(punch);
		e.getPlayer().getInventory().setItem(8, book);
		String username = uuidDatabase.getUsername(e.getPlayer().getUniqueId());
		final boolean firstJoin = (username.equals("")) ? true : false;
		uuidDatabase.setPair(e.getPlayer().getUniqueId(), e.getPlayer().getName());
		
		e.getPlayer().resetPlayerTime();
		
		if (Bukkit.getOnlinePlayers().length == 1){
			for (String s : players.keySet()){
				getPlayerCount(s);
			}
		}
		
		e.getPlayer().setWalkSpeed(0.4F);
		e.getPlayer().setFlySpeed(0.3F);
		
		scheduler.scheduleAsyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				updateTime(e.getPlayer());
			}
		});
		
		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run(){
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2000000000, 1, true));
				if (e.getPlayer().hasPermission("bcmc.review")) e.getPlayer().sendMessage(ChatColor.AQUA + "Don't forget to check the applications with /review!");
				if (firstJoin) e.getPlayer().sendMessage(ChatColor.AQUA + "You have joined BCMC for the first time. If a friend has told you about BCMC, make sure to do \"/thank <player>\" so that they'll get a reward");
			}
		}, 1L);
		
		if (e.getPlayer().getUniqueId().toString().equals("395fe01a-7cf1-4003-89b7-1cf5de2460ca")){
			List<String> promotions = getConfig().getStringList("promoted");
			if (promotions == null || promotions.size() == 0) return;
			e.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "---===" + ChatColor.GOLD + ChatColor.BOLD + " The following players have 4 votes " + ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "===---");
			for (String s : promotions){
				e.getPlayer().sendMessage(ChatColor.YELLOW + uuidDatabase.getUsername(UUID.fromString(s)));
			}
			getConfig().set("promoted", null);
			saveConfig();
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if (event.getCause() != DamageCause.CUSTOM && event.getCause() != DamageCause.ENTITY_ATTACK) event.setCancelled(true);
	}
	
	@EventHandler
	public void onPVP(EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			Player p = (Player)e.getDamager();
			Player entity = (Player)e.getEntity();
			if (punchOff.contains(p.getName())){
				p.sendMessage(ChatColor.RED + "You have turned punching off");
				e.setCancelled(true);
				return;
			}
			if (punchOff.contains(entity.getName())){
				p.sendMessage(ChatColor.RED + "That player has turned punching off");
				e.setCancelled(true);
				return;
			}
			e.setDamage(0);
			e.setCancelled(false);
			if (superPunch.contains(p.getName())){
				entity.setVelocity(e.getEntity().getVelocity().add(e.getEntity().getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(20)).setY(1.5));
				entity.sendMessage(ChatColor.YELLOW + "You got super-punched by " + p.getName() + "!");
				return;
			}
			entity.setVelocity(e.getEntity().getVelocity().add(e.getEntity().getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(4)).setY(1.2));
		}
	}
	
	@EventHandler
	public void toggleFly(final PlayerToggleFlightEvent e){
		if (flying.contains(e.getPlayer().getName())) return;
		
		e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), 1.2, e.getPlayer().getVelocity().getZ()).multiply(2));
		e.setCancelled(true);
		e.getPlayer().setAllowFlight(false);
		doubleJumpCooldown.add(e.getPlayer().getName());
		scheduler.scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				doubleJumpCooldown.remove(e.getPlayer().getName());
			}
		}, 30L);
		
		ParticleEffect.HUGE_EXPLOSION.display(e.getPlayer().getLocation(), 0.75F, 0.75F, 0.75F, 0.0F, 1);
	}
	
	@EventHandler
	public void playerMove(final PlayerMoveEvent e){		
		if (e.getTo().getY() > e.getFrom().getY()
				&& !doubleJumpCooldown.contains(e.getPlayer().getName())) e.getPlayer().setAllowFlight(true);
		Location locFrom = e.getFrom();
		Location locTo = e.getTo();
        if (locFrom.getBlockX() != locTo.getBlockX() || locFrom.getBlockY() != locTo.getBlockY() || locFrom.getBlockZ() != locTo.getBlockZ()){        	
        	ParticleEffect.MOB_SPELL.display(e.getPlayer().getLocation(), 0.3F, 0.3F, 0.3F, 1F, 4);
        }
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.PHYSICAL) return;
		if (e.getItem() == null) return;
		if (e.getItem().isSimilar(i)) menu.open(e.getPlayer());
		if (e.getItem().isSimilar(punch)) punchMenu.open(e.getPlayer());
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		if (e.getItemDrop().getItemStack().isSimilar(book)){
			e.getPlayer().getInventory().setItem(8, book);
			e.setCancelled(true);
		}
		else e.setCancelled(true);
	}
	
	@EventHandler
	public void onRespawn(final PlayerRespawnEvent e){
		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run(){
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, true));
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, true));
			}
		}, 1L);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		 if (cmd.getName().equalsIgnoreCase("apply")){
			sender.sendMessage(ChatColor.GOLD + "Please type which of the following ranks you are applying for:");
			sender.sendMessage(ChatColor.YELLOW + "Moderator, YouTuber, Builder, Developer");
			listeningAppType.add(sender.getName());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("fly")){
			if (flying.contains(sender.getName())){
				sender.sendMessage(ChatColor.GOLD + "You have turned fly off");
				flying.remove(sender.getName());
				((Player)sender).setFlying(false);
			} else {
				sender.sendMessage(ChatColor.GOLD + "You have turned fly on");
				flying.add(sender.getName());
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hat") && args.length == 1){
			String[] parts = args[0].split(":");
			boolean isInteger = isInteger(parts[0]);
			try {
				if (isInteger){
					Material m = Material.getMaterial(Integer.parseInt(parts[0]));
					if (parts.length == 1) ((Player)sender).getInventory().setHelmet(new ItemStack(m));
					else if (parts.length == 2) ((Player)sender).getInventory().setHelmet(new ItemStack(m, 1, (byte)Integer.parseInt(parts[1])));
					else return false;
				} else {
					Material m = Material.getMaterial(parts[0].toUpperCase());
					if (parts.length == 1) ((Player)sender).getInventory().setHelmet(new ItemStack(m));
					else if (parts.length == 2) ((Player)sender).getInventory().setHelmet(new ItemStack(m, 1, (byte)Integer.parseInt(parts[1])));
					else return false;
				}
				sender.sendMessage(ChatColor.GREEN + "Your hat has been updated");
			} catch (Exception e){
				sender.sendMessage(ChatColor.RED + "Invalid block type");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("review")){
			Menu reviewMenu = new Menu(ChatColor.translateAlternateColorCodes('&', "&6Apps"), 54, new Menu.OptionClickEventHandler() {
	            @Override
	            public void onOptionClick(Menu.OptionClickEvent event) {
	            	event.setWillClose(true);
	            	event.setWillDestroy(true);
	            	String name = ChatColor.stripColor(event.getName());
	            	UUID uuid = uuidDatabase.getUUID(name);
	            	AppObject app = appDatabase.getApp(uuid);
	            	
	            	if ((app.text.contains(event.getPlayer().getName() + " -1") || app.text.contains(event.getPlayer().getName() + " +1")) && !event.getPlayer().getUniqueId().toString().equals("395fe01a-7cf1-4003-89b7-1cf5de2460ca")){
	            		event.getPlayer().sendMessage(ChatColor.RED + "You have already reviewed for that app!");
	            		return;
	            	}
	            	
					ItemStack i = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta meta = (BookMeta)i.getItemMeta();
					meta.setDisplayName(ChatColor.YELLOW + name + "'s App");
					meta.addPage(app.text.split("--newpage--"));
					i.setItemMeta(meta);
					event.getPlayer().getInventory().addItem(i);
					event.getPlayer().sendMessage(ChatColor.GOLD + "That app has been added to your inventory");
					event.getPlayer().sendMessage(ChatColor.AQUA + "Type 'up' or 'down' to vote for this app");
					reviewing.put(event.getPlayer().getName(), uuid.toString());
	            }
	        }, this);
			
    		int i = 0;
			for (AppObject app : appDatabase.getApps()){
				if (i == 54) continue;
				String name = uuidDatabase.getUsername(app.uuid);
				
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
    			SkullMeta meta = (SkullMeta)item.getItemMeta(); meta.setOwner(name);
    			List<String> lore = new LinkedList<String>();
    			lore.add(ChatColor.GOLD + "" + ((((System.currentTimeMillis() - app.time)/1000)/60)/60) + " hours ago");
    			lore.add(ChatColor.YELLOW + "Score: " + appDatabase.getVotes(app.uuid));
    			lore.add(ChatColor.GREEN + "Time spent on BCMC: " + timeDatabase.getTime(app.uuid) + " minutes");
    			meta.setLore(lore);
    			item.setItemMeta(meta);
    			reviewMenu.setOption(i, item, name);
    			
    			i++;
			}
			
			reviewMenu.open((Player)sender);
    		return true;
		} else if (cmd.getName().equalsIgnoreCase("deleteapp") && args.length == 1){
			appDatabase.deleteApp(uuidDatabase.getUUID(args[0]));
			sender.sendMessage(ChatColor.BLUE + "That app has been deleted");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("savebook")){
			infobook.save(((Player)sender).getItemInHand());
			book = infobook.getBook();
			sender.sendMessage("Saved");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void bookEvent(PlayerEditBookEvent e){
		if (!e.isSigning()) return;
		if (appDatabase.appExists(e.getPlayer().getUniqueId())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You already have a pending application! Please wait for it to be reviewed");
			return;
		}
		BookMeta meta = e.getNewBookMeta();
		String text = "";
		for (int i = 1; i <= meta.getPageCount(); i++){
			text += meta.getPage(i) + "--newpage--";
		}
		appDatabase.submitApp(e.getPlayer().getUniqueId(), text, System.currentTimeMillis());
		e.getPlayer().sendMessage(ChatColor.GREEN + "Your app has been submitted. It can take a while (usually a few days) for the staff team to come to a decision. If you are accepted, you will either be given the rank, or added on Skype for further info.");
	}
	
	@EventHandler
	public void onChat(final AsyncPlayerChatEvent e){
		if (listeningAppType.contains(e.getPlayer().getName())){
			e.setCancelled(true);
			listeningAppType.remove(e.getPlayer().getName());
			
			scheduler.scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run(){
					String message = e.getMessage();
					final Player p = e.getPlayer();
					if (message.equalsIgnoreCase("moderator")){
						if (timeDatabase.getTime(p.getUniqueId()) < (600)){
							p.sendMessage(ChatColor.RED + "To be able to apply for Moderator, you need to play for 10 hours on BCMC, first");
							return;
						}
						
						p.sendMessage(ChatColor.GOLD + "Please read the following, first: " + ChatColor.GRAY + "http://bit.ly/bcmcmod" + "\n" + ChatColor.GOLD + "Come back when you have " + ChatColor.UNDERLINE + "read it all, very carefully");
						
						scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
							@Override
							public void run(){
								p.sendMessage(ChatColor.YELLOW + "If you have fully read the article, open the book in your inventory");
								ItemStack i = new ItemStack(Material.BOOK_AND_QUILL);
								BookMeta meta = (BookMeta)i.getItemMeta();
								meta.setDisplayName(ChatColor.YELLOW + "Apply for Moderator");
								meta.setLore(Arrays.asList(ChatColor.BLUE + "You will write your", ChatColor.BLUE + "application for the mod", ChatColor.BLUE + "rank, in here"));
								meta.addPage(ChatColor.DARK_GREEN + "Please read the following form, and start writing after it. The more you write, the better. Be sure to leave a good impression!\n\n" + ChatColor.BLUE + "Sign the book when you're finished\n\n" + ChatColor.GRAY + "Note: All the questions are mandatory", "1. Name\n2. Skype Username\n3. Age\n4. Maturity (1-10), with description\n5. Amount of experience as a staff member; give proof!\n6. Commands you know (that you will use to moderate on the network)", "7. Reason for wanting to be a moderator\n8. Amount of time you will spend on BCMC. Be specific!\n9. How will you punish players? (list numerous scenarios)\n10. Anything you want to add");
								i.setItemMeta(meta);
								p.getInventory().addItem(i);
							}
						}, 59 * 20);
					} else if (message.equalsIgnoreCase("youtuber")){
						ItemStack i = new ItemStack(Material.BOOK_AND_QUILL);
						BookMeta meta = (BookMeta)i.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + "Apply for Youtuber");
						meta.setLore(Arrays.asList(ChatColor.BLUE + "You will write your", ChatColor.BLUE + "application for the youtuber", ChatColor.BLUE + "rank, in here"));
						meta.addPage(ChatColor.RED + "At least 500 subscribers are necessary!\n\n" + ChatColor.BLUE + "Answer the questions on the next page, and sign the book when you're finished\n\n", ChatColor.DARK_GREEN + "1. Please give a link to your channel\n2. You must either:\n- already have an uploaded video of BCMC (if so, skip this question)\n- if not, enter your skype username here so that we can discuss the video\n\nThat is all.");
						i.setItemMeta(meta);
						p.getInventory().addItem(i);
						p.sendMessage(ChatColor.GOLD + "Please read the book in your inventory");
					} else if (message.equalsIgnoreCase("builder")){
						ItemStack i = new ItemStack(Material.BOOK_AND_QUILL);
						BookMeta meta = (BookMeta)i.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + "Apply for Builder");
						meta.setLore(Arrays.asList(ChatColor.BLUE + "You will write your", ChatColor.BLUE + "application for the builder", ChatColor.BLUE + "rank, in here"));
						meta.addPage(ChatColor.DARK_GREEN + "Please read the following form, and start writing after it.\n\n" + ChatColor.BLUE + "Sign the book when you're finished\n\n" + ChatColor.GRAY + "Note: All the questions are mandatory\nNote: Being a builder means being a part of the official BCMC build team", "1. Name\n2. Skype Username\n3. Link to your portfolio (have many pictures, of your best builds)\n4. Can you work in a team?\nIf we like your app, we will ask you to build something infront of us.");
						i.setItemMeta(meta);
						p.getInventory().addItem(i);
						p.sendMessage(ChatColor.GOLD + "Please read the book in your inventory");
					} else if (message.equalsIgnoreCase("developer")){
						ItemStack i = new ItemStack(Material.BOOK_AND_QUILL);
						BookMeta meta = (BookMeta)i.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + "Apply for Developer");
						meta.setLore(Arrays.asList(ChatColor.BLUE + "You will write your", ChatColor.BLUE + "application for the developer", ChatColor.BLUE + "rank, in here"));
						meta.addPage(ChatColor.DARK_GREEN + "Please read the following form, and start writing after it.\n\n" + ChatColor.BLUE + "Sign the book when you're finished", "1. Name\n2. Skype Username\n3. Age (we are okay with young developers)\n4. BukkitDev account, if you have one\n5. GitHub account, if you have one\n6. Other servers you have worked for", "If we like your application, BaranCODE will send you a contact request on Skype, and give you a detailed quiz as your final test.");
						i.setItemMeta(meta);
						p.getInventory().addItem(i);
						p.sendMessage(ChatColor.GOLD + "Please read the book in your inventory");
					} else {
						p.sendMessage(ChatColor.RED + "That is not a valid rank!");
						return;
					}
				}
			});
		} else if (reviewing.containsKey(e.getPlayer().getName())){
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("up")){
				UUID reviewed = UUID.fromString(reviewing.get(e.getPlayer().getName()));
				appDatabase.vote(reviewed, true);
				AppObject app = appDatabase.getApp(reviewed);
				appDatabase.updateText(reviewed, app.text + e.getPlayer().getName() + " +1\n");
				reviewing.remove(e.getPlayer().getName());
				if (appDatabase.getVotes(reviewed) == 4){
					List<String> promoted = getConfig().getStringList("promoted");
					promoted.add(reviewed.toString());
					getConfig().set("promoted", promoted);
					saveConfig();
				}
				e.getPlayer().sendMessage(ChatColor.GOLD + "You have voted up for that app");
			} else if (e.getMessage().equalsIgnoreCase("down")){
				UUID reviewed = UUID.fromString(reviewing.get(e.getPlayer().getName()));
				appDatabase.vote(reviewed, false);
				AppObject app = appDatabase.getApp(reviewed);
				appDatabase.updateText(reviewed, app.text + e.getPlayer().getName() + " -1\n");
				reviewing.remove(e.getPlayer().getName());
				if (appDatabase.getVotes(reviewed) == -3){
					appDatabase.deleteApp(reviewed);
				}
				e.getPlayer().sendMessage(ChatColor.GOLD + "You have voted down for that app");
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + "Please type 'up' or 'down'");
			}
		}
	}
}
