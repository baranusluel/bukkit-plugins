package com.barancode.mc.bcmcall;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.barancode.mc.db.ColorDatabase;
import com.barancode.mc.db.TimeDatabase;
import com.barancode.mc.db.UUIDDatabase;

public class Main extends JavaPlugin implements Listener{
	List<String> deniedCommands = new LinkedList<String>();
	HashMap<String, String> tpaRequests = new HashMap<String, String>();
	BukkitScheduler scheduler = null;
	public HashMap<String, String> lastMessage = new HashMap<String, String>();
	String senderformat;
	String receiverformat;
	ColorDatabase cd = new ColorDatabase();
	List<String> colors;
	ItemStack stick = new ItemStack(Material.STICK);
	ItemStack rod = new ItemStack(Material.BLAZE_ROD);
	ItemStack bone = new ItemStack(Material.BONE);
	HashSet<UUID> entities = new HashSet<UUID>();
	HashMap<UUID, Integer> particleTasks = new HashMap<UUID, Integer>();
	HashMap<UUID, String> pearlThrower = new HashMap<UUID, String>();
	Random random = new Random();
	TimeDatabase timedb = new TimeDatabase();
	UUIDDatabase uuiddb = new UUIDDatabase();
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		deniedCommands.add("help");
		deniedCommands.add("?");
		deniedCommands.add("plugins");
		deniedCommands.add("pl");
		deniedCommands.add("me");
		deniedCommands.add("minecraft:help");
		deniedCommands.add("stop");
		saveDefaultConfig();
		scheduler = Bukkit.getScheduler();
		senderformat = getConfig().getString("senderformat");
		receiverformat = getConfig().getString("receiverformat");
		
		colors = Arrays.asList("Aqua", "Black", "Blue", "Dark Aqua", "Dark Blue", "Dark Gray", "Dark Green", "Dark Purple", "Dark Red", "Gold", "Gray", "Green", "Light Purple", "Red", "White", "Yellow");
		
		ItemMeta meta = stick.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Snow Splash");
		stick.setItemMeta(meta);
		
		meta = rod.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Music Fire");
		rod.setItemMeta(meta);
		
		meta = bone.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Egg Breaker");
		bone.setItemMeta(meta);
		
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				for (Player p : Bukkit.getOnlinePlayers()){
					timedb.increaseTime(p.getUniqueId());
				}
			}
		}, 60 * 20L, 60 * 20L);
	}
	@EventHandler
	public void command(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage().replaceFirst("/", "");
		String[] parts = command.split(" ");
		if (deniedCommands.contains(parts[0].toLowerCase())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You can not use this command!");
		}
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onJoin(PlayerJoinEvent e){
		if (e.getPlayer().getUniqueId().toString().equals("d0e86017-c591-4ae1-bcfd-5da7be2a8c2c")){
			e.getPlayer().setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "The" + ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Iron" + ChatColor.YELLOW + "" + ChatColor.BOLD + "Mango");
			return;
		}
		
		if (e.getPlayer().hasPermission("bcmc.donator.color")){
			char character = Character.toChars(cd.getColor(e.getPlayer().getUniqueId()))[0];
			e.getPlayer().setDisplayName(ChatColor.getByChar(character) + e.getPlayer().getName());
		} else {
			e.getPlayer().setDisplayName(e.getPlayer().getName());
		}
		e.setJoinMessage(null);
		if (e.getPlayer().hasPermission("bcmc.donator.announce")) e.setJoinMessage(ChatColor.RED + "" + ChatColor.BOLD + "A VIP named " + e.getPlayer().getName() + " has joined the server!");
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("tpa") && args.length == 1){
			if (!getConfig().getBoolean("tpa-enabled")){
				sender.sendMessage(ChatColor.RED + "You aren't allowed to teleport on this server");
				return true;
			}
			
			String targetName = args[0];
			if (!Bukkit.getOfflinePlayer(targetName).isOnline()){
				sender.sendMessage(ChatColor.RED + args[0] + " isn't playing on this server right now");
				return true;
			}
			Player target = Bukkit.getPlayer(targetName);
			target.sendMessage(ChatColor.GREEN + ((Player)sender).getName() + " wants to teleport to you. \nDo " + ChatColor.GRAY + "/tpaccept" + ChatColor.GREEN + " or " + ChatColor.GRAY + "/tpdeny");
			sender.sendMessage(ChatColor.GREEN + "You have sent a teleport request to " + args[0]);
			tpaRequests.put(target.getName(), ((Player)sender).getName());
			return true;
		} else if ((cmd.getName().equalsIgnoreCase("tpaccept") || cmd.getName().equalsIgnoreCase("tpaaccept")) && args.length == 0){
			if (!getConfig().getBoolean("tpa-enabled")){
				sender.sendMessage(ChatColor.RED + "You aren't allowed to teleport on this server");
				return true;
			}
			
			if (!tpaRequests.containsKey(((Player)sender).getName())){
				sender.sendMessage(ChatColor.RED + "You don't have any pending teleport requests");
				return true;
			}
			String requester = tpaRequests.get(((Player)sender).getName());
			if (!Bukkit.getOfflinePlayer(requester).isOnline()){
				sender.sendMessage(ChatColor.RED + requester + " isn't playing on this server right now");
				return true;
			}
			Player requesterP = Bukkit.getPlayer(requester);
			requesterP.teleport(((Player)sender).getLocation());
			requesterP.sendMessage(ChatColor.GREEN + "You have been teleported to " + ((Player)sender).getName());
			((Player)sender).sendMessage(ChatColor.GREEN + requester + " has been teleported to you");
			
			tpaRequests.remove(((Player)sender).getName());
			return true;
		} else if ((cmd.getName().equalsIgnoreCase("tpdeny") || cmd.getName().equalsIgnoreCase("tpadeny")) && args.length == 0){
			if (!getConfig().getBoolean("tpa-enabled")){
				sender.sendMessage(ChatColor.RED + "You aren't allowed to teleport on this server");
				return true;
			}
			
			if (!tpaRequests.containsKey(((Player)sender).getName())){
				sender.sendMessage(ChatColor.RED + "You don't have any pending teleport requests");
				return true;
			}
			String requester = tpaRequests.get(((Player)sender).getName());
			
			((Player)sender).sendMessage(ChatColor.AQUA + "You have denied " + requester + "'s teleport request");
			
			if (Bukkit.getOfflinePlayer(requester).isOnline()){
				Player requesterP = Bukkit.getPlayer(requester);
				requesterP.sendMessage(ChatColor.AQUA + "Your teleport request to " + ((Player)sender).getName() + " has been denied");
			}
			
			tpaRequests.remove(((Player)sender).getName());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("list")){
			Player[] players = Bukkit.getOnlinePlayers();
			sender.sendMessage(ChatColor.AQUA + "There are " + players.length + " out of " + Bukkit.getMaxPlayers() + " players on this server:");
			String message = "";
			for (Player p : players){
				message += ChatColor.RESET + p.getDisplayName() + ChatColor.AQUA + ", ";
			}
			sender.sendMessage(message);
			sender.sendMessage(ChatColor.AQUA + "To list the players on all servers, do /glist");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("tell") || cmd.getName().equalsIgnoreCase("whisper") || cmd.getName().equalsIgnoreCase("message") || cmd.getName().equalsIgnoreCase("msg")){
			if(!(sender instanceof Player)) return false;
			Player p = (Player)sender;
			if(args.length < 2) return false;
			OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
			if (!op.isOnline()){
				p.sendMessage(ChatColor.RED + "That player is not online!");
				return true;
			}
			Player target = op.getPlayer();
			String message = "";
	        for(int i = 1; i < args.length; i++){
	            message += args[i] + " ";
	        }
			String msg = senderformat.replace("%sender%", p.getName()).replace("%receiver%",target.getName()).replace("%message%", message).replace('&', '§');
			String recMsg = receiverformat.replace("%receiver%", target.getName()).replace("%sender%",p.getName()).replace("%message%", message).replace('&', '§');
			target.sendMessage(recMsg);
			p.sendMessage(msg);
			
			lastMessage.put(target.getName(), p.getName());
			lastMessage.put(p.getName(), target.getName());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("r") || cmd.getName().equalsIgnoreCase("reply")){
			if(!(sender instanceof Player)) return false;
			if(args.length == 0) return false;
			Player p = (Player)sender;
			if(!lastMessage.containsKey(p.getName())){
				p.sendMessage(ChatColor.RED + "You have no one to reply to!");
				return true;
			}
			OfflinePlayer op = Bukkit.getOfflinePlayer(lastMessage.get(p.getName()));
			if (!op.isOnline()){
				p.sendMessage(ChatColor.RED + "That player is not online!");
				return true;
			}
			Player target = op.getPlayer();
			String message = "";
	        for(int i = 0; i < args.length; i++){
	        	message += args[i] + " ";
	        }
			String msg = senderformat.replace("%sender%", p.getName()).replace("%receiver%",target.getName()).replace("%message%", message).replace('&', '§');
			String recMsg = receiverformat.replace("%receiver%", target.getName()).replace("%sender%",p.getName()).replace("%message%", message).replace('&', '§');
			target.sendMessage(recMsg);
			p.sendMessage(msg);		
			lastMessage.put(target.getName(), p.getName());
			lastMessage.put(p.getName(), target.getName());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("color") || cmd.getName().equalsIgnoreCase("colour")){
			if (args.length == 0){
				sender.sendMessage(ChatColor.GOLD + "The colors you can use are: ");
				String message = "";
				for (String s : colors){
					if (message == ""){
						message = ChatColor.valueOf(s.toUpperCase().replaceAll(" ", "_")) + s;
					} else {
						message += ChatColor.RESET + ", " + ChatColor.valueOf(s.toUpperCase().replaceAll(" ", "_")) + s;
					}
				}
				sender.sendMessage(message);
				sender.sendMessage(ChatColor.GOLD + "To set your name, do /color <color>");
			} else {
				String message = "";
				for (int i = 0; i < args.length; i++){
					message += args[i] + " ";
				}
				message = message.trim().toUpperCase().replaceAll(" ", "_");
				if (!isColor(message)){
					sender.sendMessage(ChatColor.RED + "That is not a valid color!");
					return true;
				}
				ChatColor color = ChatColor.valueOf(message);
				((Player)sender).setDisplayName(color + sender.getName());
				cd.setColor(((Player)sender).getUniqueId(), (int)color.getChar());
				sender.sendMessage(ChatColor.GOLD + "You have changed your name color");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("cannon") || cmd.getName().equalsIgnoreCase("cannons") || cmd.getName().equalsIgnoreCase("particlecannon") || cmd.getName().equalsIgnoreCase("particlecannons") || cmd.getName().equalsIgnoreCase("particle") || cmd.getName().equalsIgnoreCase("particles")){
			if (!getConfig().getBoolean("tpa-enabled") && !getConfig().getBoolean("is-mh")){
				sender.sendMessage(ChatColor.RED + "You aren't allowed to use particle cannons on this server");
				return true;
			}
			Player p = (Player)sender;
			if (!p.getInventory().contains(stick)) p.getInventory().addItem(stick);
			if (!p.getInventory().contains(rod)) p.getInventory().addItem(rod);
			if (!p.getInventory().contains(bone)) p.getInventory().addItem(bone);
			p.sendMessage(ChatColor.GOLD + "You have been given the particle cannons. Have fun!");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("god")){
			
		} else if (cmd.getName().equalsIgnoreCase("gettime") && args.length == 1){
			if (!sender.hasPermission("bcmc.gettime")){
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				return true;
			}
			UUID uuid = uuiddb.getUUID(args[0]);
			if (uuid == null){
				sender.sendMessage(ChatColor.RED + "A player by that name has not played on BCMC!");
				return true;
			}
			int time = timedb.getTime(uuid);
			sender.sendMessage(ChatColor.AQUA + args[0] + " has played on BCMC for " + time + " minutes");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("gettime") && args.length == 0){
			UUID uuid = ((Player)sender).getUniqueId();
			int time = timedb.getTime(uuid);
			sender.sendMessage(ChatColor.AQUA + "You have played on BCMC for " + time + " minutes");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("vote")){
			sender.sendMessage(ChatColor.RED + "§lBCMC §6§l> §aVote for the server and recieve 20 tokens!"); 
		    sender.sendMessage(ChatColor.GOLD + "http://bcmcnetwork.com/vote.php");
		    return true;
		}
		return false;
	}
	
	public boolean isColor(String s){
		try {
			ChatColor.valueOf(s);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	@EventHandler
	public void weatherChange(WeatherChangeEvent e){
		if (e.toWeatherState()) e.setCancelled(true);
	}
	
	
	 @EventHandler
	  public void onProjectileHitEvent(ProjectileHitEvent event)
	  {
	    if ((event.getEntity() instanceof Snowball))
	    {
	      if (!event.getEntity().hasMetadata("particleeffects")) return;
	      Entity entity = event.getEntity();
	      Location location = entity.getLocation();
	      ParticleEffect.LARGE_SMOKE.display(location, 1.0F, 1.0F, 1.0F, 0.9F, 15);
	      ParticleEffect.ANGRY_VILLAGER.display(location, 1.0F, 1.0F, 1.0F, 0.9F, 10);
	      if (entities.contains(entity.getUniqueId())) entities.remove(entity.getUniqueId());
	    }
	    if ((event.getEntity() instanceof Egg))
	    {
	    	if (!event.getEntity().hasMetadata("particleeffects")) return;
	      Entity entity = event.getEntity();
	      Location location = entity.getLocation();
	      ParticleEffect.ENCHANTMENT_TABLE.display(location, 1F, 1F, 1F, 0.0F, 100);
	      ParticleEffect.WITCH_MAGIC.display(location, 1.8F, 1.8F, 1.8F, 0.0F, 100);
	      if (entities.contains(entity.getUniqueId())) entities.remove(entity.getUniqueId());
	    }
	    if ((event.getEntity() instanceof EnderPearl))
	    {
	    	if (!event.getEntity().hasMetadata("particleeffects")) return;
	      final Entity entity = event.getEntity();
	      Location location = entity.getLocation();
	      ParticleEffect.NOTE.display(location, 1.2F, 1.2F, 1.2F, 0.9F, 50);
	      ParticleEffect.FLAME.display(location, 2F, 2F, 2F, 0F, 40);
	      location.getWorld().playSound(location, Sound.NOTE_PIANO, 3, 1);
	      Firework fw = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
	      FireworkMeta meta = fw.getFireworkMeta();
	      meta.setPower(1);
	      meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(Type.BALL_LARGE).withColor(Color.BLUE, Color.RED, Color.YELLOW).build());
	      fw.setFireworkMeta(meta);
	      if (entities.contains(entity.getUniqueId())) entities.remove(entity.getUniqueId());
	      scheduler.scheduleSyncDelayedTask(this, new Runnable(){
	    	  @Override
	    	  public void run(){
	    		  if (pearlThrower.containsKey(entity.getUniqueId())) pearlThrower.remove(entity.getUniqueId());
	    	  }
	      }, 2L);
	    }
	  }
	 
	 @EventHandler
	 public void throwEgg(PlayerEggThrowEvent e){
		 if (entities.contains(e.getEgg().getUniqueId())){
			 e.setHatching(false);
			 entities.remove(e.getEgg().getUniqueId());
		 }
	 }
	 
	 @EventHandler
	 public void teleport(PlayerTeleportEvent e){
		 if (e.getCause() != TeleportCause.ENDER_PEARL) return;
		 if (pearlThrower.containsValue(e.getPlayer().getName())) e.setCancelled(true);
	 }
	 
	 public void spawnSnowball(Player p, Location loc){
		  Snowball ball = (Snowball)p.getWorld().spawn(loc, Snowball.class);
	      ball.setShooter(p);
	      ball.setVelocity(p.getLocation().getDirection().multiply(1.5D));
	      entities.add(ball.getUniqueId());
	      ball.setMetadata("particleeffects", new FixedMetadataValue(this, true));
	 }
	 
	 @EventHandler
	 public void pvp(EntityDamageByEntityEvent e){
		 if (e.getDamager() instanceof Snowball){
			 if (entities.contains(e.getDamager().getUniqueId())){
				 e.setCancelled(true);
				 entities.remove(e.getDamager().getUniqueId());
			 }
		 }
	 }

	  @EventHandler
	  public void clickCannons(PlayerInteractEvent event)
	  {
	    final Player p = event.getPlayer();
	    if (event.getAction() == Action.PHYSICAL) return; 
	    if (p.getItemInHand().isSimilar(stick))
	    {
	    	if (!getConfig().getBoolean("tpa-enabled") && !getConfig().getBoolean("is-mh")){
				p.sendMessage(ChatColor.RED + "You aren't allowed to use particle cannons on this server");
				return;
			}
	      final Location loc = p.getLocation().getBlock().getRelative(BlockFace.UP).getLocation();
		  p.setVelocity(p.getLocation().getDirection().multiply(-0.3D).setY(p.getVelocity().getY()));
		  spawnSnowball(p, loc);
	    }
	    else if (p.getItemInHand().isSimilar(bone))
	    {
	    	if (!getConfig().getBoolean("tpa-enabled") && !getConfig().getBoolean("is-mh")){
				p.sendMessage(ChatColor.RED + "You aren't allowed to use particle cannons on this server");
				return;
			}
	    	if (!p.hasPermission("bcmc.donator.cannon")){
	    		p.sendMessage(ChatColor.DARK_AQUA + "Only VIPs can use that particle cannon!");
	    		return;
	    	}
	      final Egg ball = (Egg)p.launchProjectile(Egg.class);
	      //ball.setShooter(p);
	      //ball.setVelocity(p.getLocation().getDirection().multiply(1.5D));
	      p.setVelocity(p.getLocation().getDirection().multiply(-2.0D).setY(p.getVelocity().getY()));
	      entities.add(ball.getUniqueId());
	      ball.setMetadata("particleeffects", new FixedMetadataValue(this, true));
	      
	      final long time = System.currentTimeMillis();
	      final UUID uuid = p.getUniqueId();
	      if (particleTasks.containsKey(uuid)){
	    	  scheduler.cancelTask(particleTasks.get(uuid));
	      }
	      int id = scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@SuppressWarnings("unused")
			@Override
	    	  public void run(){
	    		  if (ball == null){
	    			  scheduler.cancelTask(particleTasks.get(uuid));
	    			  particleTasks.remove(uuid);
	    		  }
	    		  if (((System.currentTimeMillis() - time)/1000) > 1){
	    			  scheduler.cancelTask(particleTasks.get(uuid));
	    			  particleTasks.remove(uuid);
	    		  }
	    		  ParticleEffect.MAGIC_CRIT.display(ball.getLocation(), 0.1F, 0.1F, 0.1F, 0, 5);
	    	  }
	      }, 0L, 1L);
	      particleTasks.put(uuid, id);
	    }
	    else if (p.getItemInHand().isSimilar(rod))
	    {
	    	if (!getConfig().getBoolean("tpa-enabled") && !getConfig().getBoolean("is-mh")){
				p.sendMessage(ChatColor.RED + "You aren't allowed to use particle cannons on this server");
				return;
			}
	    	if (!p.hasPermission("bcmc.donator.cannon")){
	    		p.sendMessage(ChatColor.DARK_AQUA + "Only VIPs can use that particle cannon!");
	    		return;
	    	}
	      EnderPearl ball = (EnderPearl)p.launchProjectile(EnderPearl.class);
	      //ball.setShooter(p);
	      //ball.setVelocity(p.getLocation().getDirection().multiply(1.5D));
	      entities.add(ball.getUniqueId());
	      pearlThrower.put(ball.getUniqueId(), p.getName());
	      ball.setMetadata("particleeffects", new FixedMetadataValue(this, true));
	    }
	  }
	  
	  
	  @EventHandler
	  public void onCraft(CraftItemEvent e){
		  if (e.getInventory().getMatrix() == null) return;
		  for (ItemStack i : e.getInventory().getMatrix()){
			  if (i != null && i.isSimilar(bone) || i.isSimilar(stick) || i.isSimilar(rod)) e.setCancelled(true);
		  }
	  }
}
