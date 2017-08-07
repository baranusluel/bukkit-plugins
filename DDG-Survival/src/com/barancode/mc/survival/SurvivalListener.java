package com.barancode.mc.survival;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class SurvivalListener implements Listener{
	
	Main plugin;
	List<String> toBeAnnounced = new LinkedList<String>();
	Random random = new Random();
	HashMap<Player, Integer> powerIncreasingSchedulers = new HashMap<Player, Integer>();
	List<String> doubleJumpCooldown = new LinkedList<String>();
	
	public SurvivalListener(Main plugin){
		this.plugin = plugin;
	}
	
	/*
	The following function should only be used if everyone has access to double jumping
	*/
	@EventHandler
	public void entityDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			if (e.getCause() == DamageCause.FALL) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent e) {
		List<String> commands = plugin.getConfig().getStringList("command-blacklist");
		String command = e.getMessage();
		command = command.replaceFirst("/", "");
		String[] commandparts = command.split(" ");
		if (commands.contains(commandparts[0])){
			e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("cannot-use")));
			e.setCancelled(true);
		}
		
		if (commandparts[0].equalsIgnoreCase("help")){
			e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("help")));
			e.setCancelled(true);
		}
		
		if (plugin.db.isMuted(e.getPlayer().getLocation().getChunk(), e.getPlayer().getUniqueId())){
			e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("muted")));
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerTalk(AsyncPlayerChatEvent e){
		if (plugin.db.isMuted(e.getPlayer().getLocation().getChunk(), e.getPlayer().getUniqueId())){
			e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("muted")));
			e.setCancelled(true);
		} else if (plugin.tpaNameInput.contains(e.getPlayer().getName())){
			e.setCancelled(true);
			plugin.tpaNameInput.remove(e.getPlayer().getName());
			e.getPlayer().performCommand("tpa " + e.getMessage());
		} else if (plugin.pointNameInput.contains(e.getPlayer().getName())){
			e.setCancelled(true);
			plugin.pointNameInput.remove(e.getPlayer().getName());
			plugin.db.setArrowPoint(e.getPlayer().getUniqueId(), e.getMessage(), e.getPlayer().getLocation());
			e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("menu.arrow.setpoint.entered")).replaceAll("<point>", e.getMessage()));
			plugin.activearrows.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
		}
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e){
		if (powerIncreasingSchedulers.containsKey(e.getPlayer())){
			plugin.scheduler.cancelTask(powerIncreasingSchedulers.get(e.getPlayer()));
			powerIncreasingSchedulers.remove(e.getPlayer());
		}
		e.setQuitMessage(Utils.replace(plugin.getConfig().getString("leave-message")).replaceAll("<player>", e.getPlayer().getName()));
	    for (Iterator<DeathInventory> it = plugin.inventories.iterator(); it.hasNext(); ){
	        DeathInventory di = it.next();
			if (di.player.equals(e.getPlayer().getName())) it.remove();
	    }
	    if (plugin.afklocations.containsKey(e.getPlayer().getName())) plugin.afklocations.remove(e.getPlayer().getName());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void interact(final PlayerInteractEvent e){
		ItemStack item = e.getItem();
		if (plugin.isEmerald(item)){
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
				plugin.menuFirst.open(e.getPlayer());
				e.setCancelled(true);
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK){
			if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
				Sign sign = (Sign)e.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[Survival]")){
					e.setCancelled(true);
					
					int power = plugin.db.getPower(e.getPlayer().getUniqueId());
					if (power < 10){
				        e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("not-enough-power")));
				        return;
					}
					
					if (plugin.chunkLoading.contains(e.getPlayer().getName())){
						e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("sign-spam")));
						return;
					}
					e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("wait-chunk")));
        			plugin.locfinder.findAndLoad(e.getPlayer());
        			plugin.chunkLoading.add(e.getPlayer().getName());
                    int id = plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                        @Override
                        public void run(){
                        	if (!plugin.randomLocationReady.containsKey(e.getPlayer().getName())){
                        		return;
                        	}
                        	e.getPlayer().teleport(plugin.randomLocationReady.get(e.getPlayer().getName()));
                        	plugin.randomLocationReady.remove(e.getPlayer().getName());
                        	plugin.scheduler.cancelTask(plugin.firstTeleportSchedulers.get(e.getPlayer().getName()));
                        	plugin.firstTeleportSchedulers.remove(e.getPlayer().getName());
                        	plugin.db.claimChunk(e.getPlayer(), e.getPlayer().getLocation().getChunk().getX(), e.getPlayer().getLocation().getChunk().getZ());
                        	Location loc = e.getPlayer().getLocation();
                        	plugin.db.setHome(e.getPlayer().getUniqueId(), (int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
                        	plugin.chunkLoading.remove(e.getPlayer().getName());
                        	plugin.db.chunkClaimPrice(e.getPlayer());
                        }
                    }, 0L, 20L);
                    plugin.firstTeleportSchedulers.put(e.getPlayer().getName(), id);
                    return;
				}
			}
			
			UUID owner = plugin.db.getOwner(e.getClickedBlock().getChunk());
			if (owner != null && !owner.equals(e.getPlayer().getUniqueId())){
				if (!plugin.db.isFriend(e.getClickedBlock().getChunk(), e.getPlayer().getUniqueId())){
					e.setCancelled(true);
					e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("can-not-interact")));
				} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.OBSIDIAN && e.getItem().getType() == Material.FLINT_AND_STEEL){
					if (!e.getPlayer().hasPermission("survival.nether")){
						e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("portal-permission")));
						e.setCancelled(true);
					}
				}
			} else {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.OBSIDIAN && e.getItem().getType() == Material.FLINT_AND_STEEL){
					if (!e.getPlayer().hasPermission("survival.nether")){
						e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("portal-permission")));
						e.setCancelled(true);
					}
				}
			}
		}
		
		if (e.getAction() == Action.PHYSICAL){
			UUID owner = plugin.db.getOwner(e.getPlayer().getLocation().getChunk());
			if (owner != null && !owner.equals(e.getPlayer().getUniqueId()) && !plugin.db.isFriend(e.getPlayer().getLocation().getChunk(), e.getPlayer().getUniqueId())) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entityExplode(EntityExplodeEvent e){
	    for (Iterator<Block> it = e.blockList().iterator(); it.hasNext(); )
	    	if (plugin.db.getOwner(it.next().getChunk()) != null) it.remove();
	}
	
	@EventHandler
	public void playerMove(final PlayerMoveEvent e){
		if (e.getTo().getChunk() != e.getFrom().getChunk()){
			final Chunk c = e.getTo().getChunk();
			boolean b = plugin.db.isBanned(c, e.getPlayer().getUniqueId());
			
			if (b){			
				UUID owner = plugin.db.getOwner(c);
				String name = Utils.getName(owner);
				e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("can-not-enter")).replaceAll("<player>", name));
				e.setCancelled(true);
				e.getPlayer().teleport(e.getFrom());
				return;
			} else {
				final UUID owner = plugin.db.getOwner(c);
				final UUID oldowner = plugin.db.getOwner(e.getFrom().getChunk());
				if (oldowner != null && !oldowner.equals(owner)){
					if (!oldowner.equals(e.getPlayer().getUniqueId())){
		            	String name = Utils.getName(oldowner);
						e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("chunk-leave-message")).replaceAll("<player>", name));
					}
					else e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("chunk-own-leave-message")));
				}
				if (owner != null && !owner.equals(oldowner)){
					if (!owner.equals(e.getPlayer().getUniqueId())){
		            	String name = Utils.getName(owner);
						e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("chunk-welcome-message")).replaceAll("<player>", name));
					}
					else e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("chunk-own-welcome-message")));
				}
			}
		}
		
		if (e.getTo().getY() > e.getFrom().getY()
				&& !doubleJumpCooldown.contains(e.getPlayer().getName())
				&& e.getPlayer().hasPermission("survival.doublejump")) e.getPlayer().setAllowFlight(true);
	}
	
	@EventHandler
	public void toggleFly(final PlayerToggleFlightEvent e){
		if (plugin.flying.contains(e.getPlayer().getName())) return;
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		/*Location newloc = e.getPlayer().getLocation().clone();
		newloc.setY(newloc.getY() - 2);
		
		if (newloc.getBlock().getType() == Material.AIR || newloc.getBlock().getType() == Material.WATER || newloc.getBlock().getType() == Material.LAVA || newloc.getBlock().getType() == Material.STATIONARY_WATER || newloc.getBlock().getType() == Material.STATIONARY_LAVA){
			e.setCancelled(true);
			e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), -0.3, e.getPlayer().getVelocity().getZ()));
			return;
		}*/
		
		e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), 1, e.getPlayer().getVelocity().getZ()));
		e.setCancelled(true);
		e.getPlayer().setAllowFlight(false);
		doubleJumpCooldown.add(e.getPlayer().getName());
		plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				doubleJumpCooldown.remove(e.getPlayer().getName());
			}
		}, 2 * 20L);
	}
	
	@EventHandler
	public void signEvent(SignChangeEvent e){
		if (e.getLine(0).equalsIgnoreCase("[survival]") && e.getPlayer().hasPermission("survival.admin")){
			e.setLine(0, ChatColor.GREEN + "[Survival]");
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void placeBlock(BlockPlaceEvent e){
		UUID owner = plugin.db.getOwner(e.getBlock().getChunk());
		if (owner == null || owner.equals(e.getPlayer().getUniqueId())){
		
			if (e.getBlock().getType() != Material.DIRT && e.getBlock().getType() != Material.COBBLESTONE) return;
			Location loc = e.getBlock().getLocation();
			int exception = 2;
			boolean notdirt = false;
			for (int i = 1; i < 11; i++){
				loc.setY(loc.getY() - 1);
				if (loc.getBlock().getType() != Material.DIRT && loc.getBlock().getType() != Material.COBBLESTONE) notdirt = true;
				
				loc.setX(loc.getX() + 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setZ(loc.getZ() + 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setX(loc.getX() - 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setX(loc.getX() - 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setZ(loc.getZ() - 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setZ(loc.getZ() - 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setX(loc.getX() + 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setX(loc.getX() + 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				loc.setZ(loc.getZ() + 1);
				if (loc.getBlock().getType() != Material.AIR) exception--;
				
				loc.setX(loc.getX() - 1);
			}
			if (!notdirt && exception > -1){
				e.setCancelled(true);
				e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("tower")));
			}
			
		} else {
			if (!plugin.db.isFriend(e.getBlock().getChunk(), e.getPlayer().getUniqueId())){
				e.setCancelled(true);
				e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("can-not-interact")));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakBlock(BlockBreakEvent e){
		UUID owner = plugin.db.getOwner(e.getBlock().getChunk());
		if (owner != null && !owner.equals(e.getPlayer().getUniqueId())){
			if (!plugin.db.isFriend(e.getBlock().getChunk(), e.getPlayer().getUniqueId())){
				e.setCancelled(true);
				e.getPlayer().sendMessage(Utils.replace(plugin.getConfig().getString("can-not-interact")));
			}
		}
	}
	
	public void toggleBan(Player owner, Player banned){
		List<Chunk> chunks = plugin.db.getChunks(owner.getUniqueId());
		boolean b = false;
		for (Chunk chunk : chunks){
			b = plugin.db.banFromChunk(chunk, banned.getUniqueId());
			if (!b){
				plugin.db.unbanFromChunk(chunk, banned.getUniqueId());
			}
		}
		if (b){
			owner.sendMessage(Utils.replace(plugin.getConfig().getString("chunk-ban")).replaceAll("<player>", banned.getName()));
			Chunk chunk = banned.getLocation().getChunk();
			int x = chunk.getX();
			int z = chunk.getZ();
			while (chunks.contains(chunk)){
				x = x - 1;
				chunk = chunk.getWorld().getChunkAt(x, z);
			}
			banned.teleport(chunk.getBlock(7, chunk.getWorld().getHighestBlockYAt(chunk.getBlock(7, 0, 7).getLocation()), 7).getLocation());
		}
		else owner.sendMessage(Utils.replace(plugin.getConfig().getString("chunk-unban")).replaceAll("<player>", banned.getName()));
	}
	
	public void toggleMute(Player owner, Player muted){
		List<Chunk> chunks = plugin.db.getChunks(owner.getUniqueId());
		boolean b = false;
		for (Chunk chunk : chunks){
			b = plugin.db.muteInChunk(chunk, muted.getUniqueId());
			if (!b){
				plugin.db.unmuteInChunk(chunk, muted.getUniqueId());
			}
		}
		if (b) owner.sendMessage(Utils.replace(plugin.getConfig().getString("chunk-mute")).replaceAll("<player>", muted.getName()));
		else owner.sendMessage(Utils.replace(plugin.getConfig().getString("chunk-unmute")).replaceAll("<player>", muted.getName()));
	}
	
	public void toggleFriend(Player owner, Player friend){
		List<Chunk> chunks = plugin.db.getChunks(owner.getUniqueId());
		boolean b = false;
		for (Chunk chunk : chunks){
			b = plugin.db.addFriend(chunk, friend.getUniqueId());
			if (!b){
				plugin.db.removeFriend(chunk, friend.getUniqueId());
			}
		}
		if (b) owner.sendMessage(Utils.replace(plugin.getConfig().getString("add-friend")).replaceAll("<player>", friend.getName()));
		else owner.sendMessage(Utils.replace(plugin.getConfig().getString("remove-friend")).replaceAll("<player>", friend.getName()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void entityDamageEntity(EntityDamageEvent e){
		if (e.getEntityType() == EntityType.PLAYER){
			final Chunk c = e.getEntity().getLocation().getChunk();
			UUID owner = plugin.db.getOwner(c);
			if (owner != null && owner.toString().equals("2c14c815-e6f3-4b37-b7af-359e502d5e8a")){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void entityDamageEntity(EntityDamageByEntityEvent e){
		if (e.getDamager() instanceof Player){
			if (e.getEntity() instanceof Player){
				e.setCancelled(true);
				Player p = (Player)e.getDamager();
				if (plugin.isEmerald(p.getItemInHand())){
					if (!plugin.binds.containsKey(p.getName())) return;
					String bind = plugin.binds.get(p.getName());
					if (bind.equals("ban")){
						if (((Player)e.getEntity()).hasPermission("survival.admin")) {
							p.sendMessage(Utils.replace(plugin.getConfig().getString("ban-admin")));
							return;
						}
						toggleBan(p, (Player)e.getEntity());
					} else if (bind.equals("mute")){
						if (((Player)e.getEntity()).hasPermission("survival.admin")) {
							p.sendMessage(Utils.replace(plugin.getConfig().getString("mute-admin")));
							return;
						}
						toggleMute(p, (Player)e.getEntity());
					} else if (bind.equals("friend")){
						toggleFriend(p, (Player)e.getEntity());
					}
				}
			} else {
				Chunk c = e.getEntity().getLocation().getChunk();
				UUID owner = plugin.db.getOwner(c);
				if (owner != null && !owner.equals(((Player)e.getDamager()).getUniqueId()) && !plugin.db.isFriend(c, ((Player)e.getDamager()).getUniqueId())){
					e.setCancelled(true);
					((Player)e.getDamager()).sendMessage(Utils.replace(plugin.getConfig().getString("can-not-hit")));
				}
			}
		} else if (e.getCause() == DamageCause.PROJECTILE){
			ProjectileSource shooter = ((Projectile)e.getDamager()).getShooter();
			if (!(shooter instanceof Player)) return;
			Player p = (Player)shooter;
			if (e.getEntity() instanceof Player) e.setCancelled(true);
			else {
				Chunk c = e.getEntity().getLocation().getChunk();
				UUID owner = plugin.db.getOwner(c);
				if (owner != null && !owner.equals(p.getUniqueId()) && !plugin.db.isFriend(c, p.getUniqueId())){
					e.setCancelled(true);
					p.sendMessage(Utils.replace(plugin.getConfig().getString("can-not-hit")));
				}
			}
		}
	}
	
	@EventHandler
	public void playerRespawn(final PlayerRespawnEvent e){
		Location loc = plugin.db.getHome(e.getPlayer().getUniqueId());
		if (loc != null) e.setRespawnLocation(loc);
		e.getPlayer().getInventory().setItem(8, plugin.emerald);
		for (DeathInventory di : plugin.inventories){
			if (di.player.equals(e.getPlayer().getName())){
    	        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    	            @Override
    	            public void run(){
    	    			plugin.menuKeep.open(e.getPlayer());
    	            }
    	        }, 1L);
			}
		}
	}
	
	public void dropItems(DeathInventory di){
		ItemStack[] inv = di.inv;
		ItemStack[] armorinv = di.armorinv;
		Location loc = di.loc;
		for (ItemStack item : inv){
			if (item == null) continue;
			if (plugin.isEmerald(item)) continue;
			loc.getWorld().dropItemNaturally(loc, item);
		}
		for (ItemStack item : armorinv){
			if (item == null) continue;
			if (plugin.isEmerald(item)) continue;
			loc.getWorld().dropItemNaturally(loc, item);
		}
	}
	
	@EventHandler
	public void playerDropItems(PlayerDropItemEvent e){
		if (plugin.isEmerald(e.getItemDrop().getItemStack())){
			// I do this instead of cancelling, because when I cancel, the emerald gets moved to slot 0
			e.getItemDrop().remove();
			e.getPlayer().getInventory().setItem(8, plugin.emerald);
		}
	}
	
	@EventHandler
	public void playerDie(PlayerDeathEvent e){
		e.getDrops().remove(plugin.emerald);
		if (!e.getEntity().hasPermission("survival.keepitems")) {
			return;
		}
		if (plugin.db.getPower(((Player)e.getEntity()).getUniqueId()) < 100){
			e.getEntity().sendMessage(Utils.replace(plugin.getConfig().getString("keepitems-power")));
			return;
		}
		DeathInventory di = new DeathInventory(e.getEntity().getName(), e.getEntity().getInventory().getContents(), e.getEntity().getInventory().getArmorContents(), e.getEntity().getLocation());
		plugin.inventories.add(di);
	    for (Iterator<ItemStack> it = e.getDrops().iterator(); it.hasNext(); ){
	        it.next(); it.remove();
	    }
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent e){
		if (plugin.isEmerald(e.getCursor()) || plugin.isEmerald(e.getCurrentItem())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLiquidSpread(BlockFromToEvent e){
		if (e.getBlock().getChunk() == e.getToBlock().getChunk()) return;
		if (e.getBlock().getType() == Material.LAVA || e.getBlock().getType() == Material.STATIONARY_LAVA || e.getBlock().getType() == Material.WATER || e.getBlock().getType() == Material.STATIONARY_WATER){
			if (plugin.db.getOwner(e.getToBlock().getChunk()) != null) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entityChangeBlock(EntityChangeBlockEvent e){
		if (e.getEntity() instanceof Enderman) e.setCancelled(true);
	}
		
	@EventHandler
	public void playerJoin(final PlayerJoinEvent e){
		
		e.setJoinMessage("");
		
        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
        		Player player = e.getPlayer();
        		if (toBeAnnounced.contains(player.getName())){
        			player.sendMessage(Utils.replace(plugin.getConfig().getString("first-join-motd")).replaceAll("<player>", player.getName()).replaceAll("&&", "\n"));
        			toBeAnnounced.remove(player.getName());
        			
        			player.teleport(new Location(Bukkit.getWorld("world"), plugin.getConfig().getDouble("spawn.x"), plugin.getConfig().getDouble("spawn.y"), plugin.getConfig().getDouble("spawn.z")));
        			
        			plugin.db.increasePower(player);
        			
        			Scoreboard board = plugin.manager.getNewScoreboard();
        		    Objective objective = board.registerNewObjective("scoreboard", "dummy");
        		    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        		    objective.setDisplayName(Utils.replace(plugin.getConfig().getString("scoreboard")));
        		    Score power = objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power"))));
        		    power.setScore(10);
        		    Score points = objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-points"))));
        		    points.setScore(0);
        		    player.setScoreboard(board);
        			plugin.scoreboards.put(player.getUniqueId(), board);
        			player.getInventory().addItem(plugin.bookitem);
        			
        		} else {
        			Scoreboard board = plugin.scoreboards.get(player.getUniqueId());
        			if (board != null){
        				Objective objective = board.getObjective("scoreboard");
        				objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(plugin.db.getPower(player.getUniqueId()));
        				player.setScoreboard(board);
        			} else {
        				Scoreboard board2 = plugin.manager.getNewScoreboard();
        			    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
        			    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        			    objective.setDisplayName(Utils.replace(plugin.getConfig().getString("scoreboard")));
        			    Score power = objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power"))));
        			    power.setScore(plugin.db.getPower(player.getUniqueId()));
        			    player.setScoreboard(board2);
        				plugin.scoreboards.put(player.getUniqueId(), board2);
        			}
        			player.sendMessage(Utils.replace(plugin.getConfig().getString("motd")).replaceAll("<player>", player.getName()).replaceAll("&&", "\n"));
        		}
        		
        		player.getInventory().setItem(8, plugin.emerald);
        		
        		// This is for double jump
        		/*if (player.hasPermission("survival.doublejump")) player.setAllowFlight(true);
        		else player.setAllowFlight(false);*/
        		
        		final Player finalplayer = player;
        		
        		// I use a separate scheduled task for each player instead of just using one
        		// scheduler for all of the players, because if it was like that, the server
        		// could lag while increasing the power for all the players at the same time
        		// (saving it to the database)
        		int id = plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                    	plugin.db.increasePower(finalplayer);
                    }
                }, 2 * 60 * 20L, 2 * 60 * 20L);
                powerIncreasingSchedulers.put(finalplayer, id);
            }
        }, 1L);
	}
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent e){
		if (!plugin.playerlist.players.contains(e.getPlayer().getUniqueId())){
			Bukkit.broadcastMessage(Utils.replace(plugin.getConfig().getString("first-join-broadcast")).replaceAll("<player>", e.getPlayer().getName()));
			toBeAnnounced.add(e.getPlayer().getName());
			plugin.playerlist.write(e.getPlayer().getUniqueId());
		} else Bukkit.broadcastMessage(Utils.replace(plugin.getConfig().getString("join-message")).replaceAll("<player>", e.getPlayer().getName()));
	}
	
	/*@EventHandler
	public void asyncPreLogin(AsyncPlayerPreLoginEvent e){
		UUID uuid = Utils.getUUID(e.getName());
		if (plugin.banfile.getCustomConfig().getBoolean("players." + uuid + ".banned")){
			Date date = new Date();
			if (plugin.banfile.getCustomConfig().getInt("players." + uuid + ".time") == 0){
				String bannedmessage = plugin.banfile.getCustomConfig().getString("permanent-bannedmessage");
				bannedmessage = bannedmessage.replaceAll("<banner>", plugin.banfile.getCustomConfig().getString("players." + uuid + ".banner"));
				bannedmessage = bannedmessage.replaceAll("<reason>", plugin.banfile.getCustomConfig().getString("players." + uuid + ".reason"));
				e.disallow(Result.KICK_BANNED, Utils.replace(bannedmessage));
				return;
			} else if (date.getTime() < plugin.banfile.getCustomConfig().getLong("players." + uuid + ".time")){
				String bannedmessage = plugin.banfile.getCustomConfig().getString("temp-bannedmessage");
				bannedmessage = bannedmessage.replaceAll("<banner>", plugin.banfile.getCustomConfig().getString("players." + uuid + ".banner"));
				bannedmessage = bannedmessage.replaceAll("<reason>", plugin.banfile.getCustomConfig().getString("players." + uuid + ".reason"));
				long left = plugin.banfile.getCustomConfig().getLong("players." + uuid + ".time") - date.getTime(); left = left / (1000 * 60);
				int minutes = (int) (left % 60);
				left /= 60;
				int hours = (int) (left % 24);
				left /= 24;
				int days = (int) left;
				String time = "";
				if (days == 1) time += "1 " + plugin.banfile.getCustomConfig().getString("day") + " ";
				else if (days > 1) time += days + " " + plugin.banfile.getCustomConfig().getString("days") + " ";
				if (hours == 1) time += "1 " + plugin.banfile.getCustomConfig().getString("hour") + " ";
				else if (hours > 1) time += hours + " " + plugin.banfile.getCustomConfig().getString("hours") + " ";
				if (minutes == 1) time += "1 " + plugin.banfile.getCustomConfig().getString("minute") + " ";
				else if (minutes > 1) time += minutes + " " + plugin.banfile.getCustomConfig().getString("minutes") + " ";
				
				if (time.equals("")) time = "< 1 " + plugin.banfile.getCustomConfig().getString("minute") + " ";
				bannedmessage = bannedmessage.replaceAll("<time>", time);
				e.disallow(Result.KICK_BANNED, Utils.replace(bannedmessage));
				return;
			} else {
				plugin.banfile.getCustomConfig().set("players." + uuid, "");
			}
		}
	}*/
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e){
		double x = e.getLocation().getX();
		double z = e.getLocation().getZ();
		if (x > 0 && x < 105 && z < 5 && z > -100) e.setCancelled(true);
	}
}
