package com.barancode.mc.ddg.survival;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
	
	List<String> jumping = new LinkedList<String>();
	
	public SurvivalListener(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent e) {
		List<String> commands = plugin.getConfig().getStringList("command-blacklist");
		String command = e.getMessage();
		command = command.replaceFirst("/", "");
		String[] commandparts = command.split(" ");
		if (commands.contains(commandparts[0])){
			e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("cannot-use")));
			e.setCancelled(true);
		}
		
		if (commandparts[0].equalsIgnoreCase("help")){
			e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("help")));
			e.setCancelled(true);
		}
		
		if (plugin.db.isMuted(e.getPlayer().getLocation().getChunk(), e.getPlayer().getName())){
			e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("muted")));
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerTalk(AsyncPlayerChatEvent e){
		if (plugin.db.isMuted(e.getPlayer().getLocation().getChunk(), e.getPlayer().getName())){
			e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("muted")));
			e.setCancelled(true);
		} else if (plugin.tpaNameInput.contains(e.getPlayer().getName())){
			e.setCancelled(true);
			plugin.tpaNameInput.remove(e.getPlayer().getName());
			e.getPlayer().performCommand("tpa " + e.getMessage());
		} else if (plugin.pointNameInput.contains(e.getPlayer().getName())){
			e.setCancelled(true);
			plugin.pointNameInput.remove(e.getPlayer().getName());
			plugin.db.setArrowPoint(e.getPlayer().getName(), e.getMessage(), e.getPlayer().getLocation());
			e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("menu.arrow.setpoint.entered")).replaceAll("<point>", e.getMessage()));
			plugin.arrows.put(e.getPlayer().getName(), e.getPlayer().getLocation());
		}
	}
	
	@EventHandler
	public void fallingBlockFormBlock(EntityChangeBlockEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e){
		if (powerIncreasingSchedulers.containsKey(e.getPlayer())){
			plugin.scheduler.cancelTask(powerIncreasingSchedulers.get(e.getPlayer()));
			powerIncreasingSchedulers.remove(e.getPlayer());
		}
		e.setQuitMessage(plugin.utils.replace(plugin.getConfig().getString("leave-message")).replaceAll("<player>", e.getPlayer().getName()));
	    for (Iterator<DeathInventory> it = plugin.inventories.iterator(); it.hasNext(); ){
	        DeathInventory di = it.next();
			if (di.player.equals(e.getPlayer().getName())) it.remove();
	    }
	    if (plugin.locations.containsKey(e.getPlayer().getName())) plugin.locations.remove(e.getPlayer().getName());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void interact(PlayerInteractEvent e){
		ItemStack item = e.getItem();
		if (plugin.utils.isEmerald(item)){
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
				plugin.menuFirst.open(e.getPlayer());
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK){
			String owner = plugin.db.getOwner(e.getClickedBlock().getChunk());
			if (!owner.equals("") && !owner.equals(e.getPlayer().getName())){
				if (!plugin.db.isFriend(e.getClickedBlock().getChunk(), e.getPlayer().getName())){
					e.setCancelled(true);
					e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("can-not-interact")));
				}
			} else {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.OBSIDIAN && e.getItem().getType() == Material.FLINT_AND_STEEL){
					if (!e.getPlayer().hasPermission("survival.nether")){
						e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("portal-permission")));
						e.setCancelled(true);
					}
				}
			}
		}
		
		if (e.getAction() == Action.PHYSICAL){
			String owner = plugin.db.getOwner(e.getPlayer().getLocation().getChunk());
			if (!owner.equals(e.getPlayer().getName()) && !owner.equals("") && !plugin.db.isFriend(e.getPlayer().getLocation().getChunk(), e.getPlayer().getName())) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entityExplode(EntityExplodeEvent e){
	    for (Iterator<Block> it = e.blockList().iterator(); it.hasNext(); )
	    	if (!plugin.db.getOwner(it.next().getChunk()).equals("")) it.remove();
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent e){		
		if (e.getTo().getChunk() == e.getFrom().getChunk()) return;
		
		Chunk c = e.getTo().getChunk();
		boolean b = plugin.db.isBanned(c, e.getPlayer().getName());
		
		if (b){
			e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("can-not-enter")).replaceAll("<player>", plugin.db.getOwner(c)));
			e.setCancelled(true);
			e.getPlayer().teleport(e.getFrom());
		} else {
			String owner = plugin.db.getOwner(c);
			String oldowner = plugin.db.getOwner(e.getFrom().getChunk());
			if (!oldowner.equals("") && !oldowner.equals(owner)){
				if (!oldowner.equals(e.getPlayer().getName())) e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-leave-message")).replaceAll("<player>", oldowner));
				else e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-own-leave-message")));
			}
			if (!owner.equals("") && !owner.equals(oldowner)){
				if (!owner.equals(e.getPlayer().getName())) e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-welcome-message")).replaceAll("<player>", owner));
				else e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-own-welcome-message")));
			}
		}
		
		
	}
	
	@EventHandler
	public void toggleFly(PlayerToggleFlightEvent e){
		if (plugin.flying.contains(e.getPlayer().getName())) return;
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		Location newloc = e.getPlayer().getLocation().clone();
		newloc.setY(newloc.getY() - 2);
		
		if (newloc.getBlock().getType() == Material.AIR || newloc.getBlock().getType() == Material.WATER || newloc.getBlock().getType() == Material.LAVA || newloc.getBlock().getType() == Material.STATIONARY_WATER || newloc.getBlock().getType() == Material.STATIONARY_LAVA){
			e.setCancelled(true);
			e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), -0.3, e.getPlayer().getVelocity().getZ()));
			return;
		}
		
		e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), 1, e.getPlayer().getVelocity().getZ()));
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void placeBlock(BlockPlaceEvent e){
		String owner = plugin.db.getOwner(e.getBlock().getChunk());
		if (owner.equals("") || owner.equals(e.getPlayer().getName())){
		
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
				e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("tower")));
			}
			
		} else {
			if (!plugin.db.isFriend(e.getBlock().getChunk(), e.getPlayer().getName())){
				e.setCancelled(true);
				e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("can-not-interact")));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakBlock(BlockBreakEvent e){
		String owner = plugin.db.getOwner(e.getBlock().getChunk());
		if (!owner.equals("") && !owner.equals(e.getPlayer().getName())){
			if (!plugin.db.isFriend(e.getBlock().getChunk(), e.getPlayer().getName())){
				e.setCancelled(true);
				e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("can-not-interact")));
			}
		}
	}
	
	public void toggleBan(Player owner, Player banned){
		List<Chunk> chunks = plugin.db.getChunks(owner.getName());
		boolean b = false;
		for (Chunk chunk : chunks){
			b = plugin.db.banFromChunk(chunk, banned.getName());
			if (!b){
				plugin.db.unbanFromChunk(chunk, banned.getName());
			}
		}
		if (b){
			owner.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-ban")).replaceAll("<player>", banned.getName()));
			Chunk chunk = banned.getLocation().getChunk();
			int x = chunk.getX();
			int z = chunk.getZ();
			do{
				x = x - 1;
				chunk = chunk.getWorld().getChunkAt(x, z);
			} while (chunks.contains(chunk));
			banned.teleport(chunk.getBlock(7, chunk.getWorld().getHighestBlockYAt(chunk.getBlock(7, 0, 7).getLocation()), 7).getLocation());
		}
		else owner.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-unban")).replaceAll("<player>", banned.getName()));
	}
	
	public void toggleMute(Player owner, Player muted){
		List<Chunk> chunks = plugin.db.getChunks(owner.getName());
		boolean b = false;
		for (Chunk chunk : chunks){
			b = plugin.db.muteInChunk(chunk, muted.getName());
			if (!b){
				plugin.db.unmuteInChunk(chunk, muted.getName());
			}
		}
		if (b) owner.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-mute")).replaceAll("<player>", muted.getName()));
		else owner.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-unmute")).replaceAll("<player>", muted.getName()));
	}
	
	public void toggleFriend(Player owner, Player friend){
		List<Chunk> chunks = plugin.db.getChunks(owner.getName());
		boolean b = false;
		for (Chunk chunk : chunks){
			b = plugin.db.addFriend(chunk, friend.getName());
			if (!b){
				plugin.db.removeFriend(chunk, friend.getName());
			}
		}
		if (b) owner.sendMessage(plugin.utils.replace(plugin.getConfig().getString("add-friend")).replaceAll("<player>", friend.getName()));
		else owner.sendMessage(plugin.utils.replace(plugin.getConfig().getString("remove-friend")).replaceAll("<player>", friend.getName()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void entityHitEntity(EntityDamageByEntityEvent e){
		if (e.getDamager() instanceof Player){
			if (e.getEntity() instanceof Player){
				e.setCancelled(true);
				Player p = (Player)e.getDamager();
				if (plugin.utils.isEmerald(p.getItemInHand())){
					if (!plugin.binds.containsKey(p.getName())) return;
					String bind = plugin.binds.get(p.getName());
					if (bind.equals("ban")){
						if (((Player)e.getEntity()).hasPermission("survival.admin")) {
							p.sendMessage(plugin.utils.replace(plugin.getConfig().getString("ban-admin")));
							return;
						}
						toggleBan(p, (Player)e.getEntity());
					} else if (bind.equals("mute")){
						if (((Player)e.getEntity()).hasPermission("survival.admin")) {
							p.sendMessage(plugin.utils.replace(plugin.getConfig().getString("mute-admin")));
							return;
						}
						toggleMute(p, (Player)e.getEntity());
					} else if (bind.equals("friend")){
						List<Chunk> chunks = plugin.db.getChunks(p.getName());
						if (plugin.db.isFriend(chunks.get(0), ((Player)e.getEntity()).getName())){
							toggleFriend(p, (Player)e.getEntity());
						} else {
							int size = plugin.db.getFriends(chunks.get(0)).size();
							if (p.hasPermission("survival.max")){
								if (size > 53){
									p.sendMessage(plugin.utils.replace(plugin.getConfig().getString("too-many-friends")).replaceAll("<number>", "54"));
									return;
								}
							} else {
								if (size > 9){
									 p.sendMessage(plugin.utils.replace(plugin.getConfig().getString("too-many-friends")).replaceAll("<number>", "10"));
									 return;
								}
							}
							toggleFriend(p, (Player)e.getEntity());
						}
					}
				}
			} else {
				Chunk c = e.getEntity().getLocation().getChunk();
				String owner = plugin.db.getOwner(c);
				if (!owner.equals("") && !owner.equals(((Player)e.getDamager()).getName()) && !plugin.db.isFriend(c, ((Player)e.getDamager()).getName())){
					e.setCancelled(true);
					((Player)e.getDamager()).sendMessage(plugin.utils.replace(plugin.getConfig().getString("can-not-hit")));
				}
			}
		}
	}
	
	@EventHandler
	public void playerRespawn(final PlayerRespawnEvent e){
		Location loc = plugin.db.getHome(e.getPlayer().getName());
		if (loc != null) e.setRespawnLocation(loc);
		for (DeathInventory di : plugin.inventories){
			if (di.player.equals(e.getPlayer().getName())){
				if (!e.getPlayer().hasPermission("survival.keepitems")) {
					e.getPlayer().getInventory().setItem(8, plugin.emerald);
					dropItems(di);
					return;
				}
				if (plugin.db.getPower(e.getPlayer().getName()) < 100){
					e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("keepitems-power")));
					e.getPlayer().getInventory().setItem(8, plugin.emerald);
					dropItems(di);
					return;
				}
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
		Location loc = di.loc;
		for (ItemStack item : inv){
			if (item == null) continue;
			if (plugin.utils.isEmerald(item)) continue;
			Bukkit.getWorld("world").dropItemNaturally(loc, item);
		}
	}
	
	@EventHandler
	public void playerDropItems(PlayerDropItemEvent e){
		if (plugin.utils.isEmerald(e.getItemDrop().getItemStack())){
			// I do this instead of cancelling, because when I cancel, the emerald gets moved to slot 0
			e.getItemDrop().remove();
			e.getPlayer().getInventory().setItem(8, plugin.emerald);
		}
	}
	
	@EventHandler
	public void playerDie(PlayerDeathEvent e){
		plugin.inventories.add(new DeathInventory(e.getEntity().getName(), e.getEntity().getInventory().getContents(), e.getEntity().getLocation()));
	    for (Iterator<ItemStack> it = e.getDrops().iterator(); it.hasNext(); ){
	        it.next(); it.remove();
	    }
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent e){
		if (plugin.utils.isEmerald(e.getCursor()) || plugin.utils.isEmerald(e.getCurrentItem())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLiquidSpread(BlockFromToEvent e){
		if (e.getBlock().getChunk() == e.getToBlock().getChunk()) return;
		if (e.getBlock().getType() == Material.LAVA || e.getBlock().getType() == Material.STATIONARY_LAVA || e.getBlock().getType() == Material.WATER || e.getBlock().getType() == Material.STATIONARY_WATER){
			if (!plugin.db.getOwner(e.getToBlock().getChunk()).equals("")) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entityChangeBlock(EntityChangeBlockEvent e){
		if (e.getEntity() instanceof Enderman) e.setCancelled(true);
	}
	
	/*@EventHandler
	public void inventoryDrag(InventoryDragEvent e){
		//if (plugin.utils.sameItem(e.getCursor(), plugin.emerald) || plugin.utils.sameItem(e.getOldCursor(), plugin.emerald)) e.setCancelled(true);
	}*/
	
	public Location getRandomLocation(int error){
		if (error > 1000) return null;
		error++;
		Location loc;
		do {
			int x = random.nextInt(100000) - 50000;
			int z = random.nextInt(100000) - 50000;
			loc = new Location(Bukkit.getWorld("world"), x * 16, 0, z * 16);
			int y = Bukkit.getWorld("world").getHighestBlockYAt(loc);
			loc.setY(y - 1);
		} while (Bukkit.getWorld("world").getBlockAt(loc).getType() == Material.STATIONARY_WATER || Bukkit.getWorld("world").getBlockAt(loc).getType() == Material.STATIONARY_LAVA);
		
		boolean b = false;
		Chunk c = loc.getChunk();
		for (Chunk tempc : plugin.db.getAllChunks()){
			if (c.getX() - tempc.getX() < 20 && c.getZ() - tempc.getZ() < 20) b = true;
		}
		if (b) {
			return getRandomLocation(error);
		}
		else return loc;
	}
		
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerJoin(final PlayerJoinEvent e){
		e.setJoinMessage("");
		
        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
        		Player player = e.getPlayer();
        		if (toBeAnnounced.contains(player.getName())){
        			
        			Location loc = getRandomLocation(0);
        			if (loc == null){
        				e.getPlayer().kickPlayer(plugin.utils.replace(plugin.getConfig().getString("error")));
        				return;
        			}
        			
        			player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("first-join-motd")).replaceAll("<player>", player.getName()).replaceAll("&&", "\n"));
        			toBeAnnounced.remove(player.getName());
        			
        			loc.setY(loc.getY() + 2);
        			player.teleport(loc);
        			
        			plugin.db.increasePower(player);
        			plugin.db.claimChunk(player, loc.getChunk().getX(), loc.getChunk().getZ());
        			
        			Scoreboard board = plugin.manager.getNewScoreboard();
        		    Objective objective = board.registerNewObjective("scoreboard", "dummy");
        		    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        		    objective.setDisplayName(plugin.utils.replace(plugin.getConfig().getString("scoreboard")));
        		    Score power = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power"))));
        		    power.setScore(10);
        		    Score points = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points"))));
        		    points.setScore(10);
        		    player.setScoreboard(board);
        			plugin.scoreboards.put(player.getName(), board);
        			plugin.db.setHome(player.getName(), (int)player.getLocation().getX(), (int)player.getLocation().getY(), (int)player.getLocation().getZ());
        			player.getInventory().addItem(plugin.bookitem);
        			plugin.db.setArrowPoint(e.getPlayer().getName(), "Home", e.getPlayer().getLocation());
        		} else {
        			Scoreboard board = plugin.scoreboards.get(player.getName());
        			if (board != null){
        				Objective objective = board.getObjective("scoreboard");
        				objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(plugin.db.getPower(player.getName()));
        				objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points")))).setScore(plugin.db.getPoints(player.getName()));
        				player.setScoreboard(board);
        			} else {
        				Scoreboard board2 = plugin.manager.getNewScoreboard();
        			    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
        			    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        			    objective.setDisplayName(plugin.utils.replace(plugin.getConfig().getString("scoreboard")));
        			    Score power = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power"))));
        			    power.setScore(plugin.db.getPower(player.getName()));
        			    Score points = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points"))));
        			    points.setScore(plugin.db.getPoints(player.getName()));
        			    player.setScoreboard(board2);
        				plugin.scoreboards.put(player.getName(), board2);
        			}
        			player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("motd")).replaceAll("<player>", player.getName()).replaceAll("&&", "\n"));
        		}
        		
        		player.getInventory().setItem(8, plugin.emerald);
        		
        		// This is for double jump
        		if (player.hasPermission("survival.doublejump")) player.setAllowFlight(true);
        		else player.setAllowFlight(false);
        		
        		final Player finalplayer = player;
        		
        		// I use a separate scheduled task for each player instead of just using one
        		// scheduler for all of the players, because if it was like that, the server
        		// could lag while increasing the power for all the players at the same time
        		// (saving it to the database)
                @SuppressWarnings("deprecation")
        		int id = plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                    	plugin.db.increasePower(finalplayer);
                    }
                }, 2 * 60 * 20L, 2 * 60 * 20L);
                powerIncreasingSchedulers.put(finalplayer, id);
            }
        }, 0L);
	}
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent e){
		if (plugin.banfile.getCustomConfig().getBoolean("players." + e.getPlayer().getName() + ".banned")){
			Date date = new Date();
			if (plugin.banfile.getCustomConfig().getInt("players." + e.getPlayer().getName() + ".time") == 0){
				e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
				String bannedmessage = plugin.banfile.getCustomConfig().getString("permanent-bannedmessage");
				bannedmessage = bannedmessage.replaceAll("<banner>", plugin.banfile.getCustomConfig().getString("players." + e.getPlayer().getName() + ".banner"));
				bannedmessage = bannedmessage.replaceAll("<reason>", plugin.banfile.getCustomConfig().getString("players." + e.getPlayer().getName() + ".reason"));
				e.setKickMessage(plugin.utils.replace(bannedmessage));
				return;
			} else if (date.getTime() < plugin.banfile.getCustomConfig().getLong("players." + e.getPlayer().getName() + ".time")){
				e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
				String bannedmessage = plugin.banfile.getCustomConfig().getString("temp-bannedmessage");
				bannedmessage = bannedmessage.replaceAll("<banner>", plugin.banfile.getCustomConfig().getString("players." + e.getPlayer().getName() + ".banner"));
				bannedmessage = bannedmessage.replaceAll("<reason>", plugin.banfile.getCustomConfig().getString("players." + e.getPlayer().getName() + ".reason"));
				long left = plugin.banfile.getCustomConfig().getLong("players." + e.getPlayer().getName() + ".time") - date.getTime(); left = left / (1000 * 60);
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
				e.setKickMessage(plugin.utils.replace(bannedmessage));
				return;
			} else {
				plugin.banfile.getCustomConfig().set("players." + e.getPlayer().getName(), "");
			}
		}
		
		
		if (!plugin.playerlist.players.contains(e.getPlayer().getName())){
			Bukkit.broadcastMessage(plugin.utils.replace(plugin.getConfig().getString("first-join-broadcast")).replaceAll("<player>", e.getPlayer().getName()));
			toBeAnnounced.add(e.getPlayer().getName());
			plugin.playerlist.write(e.getPlayer().getName());
		} else Bukkit.broadcastMessage(plugin.utils.replace(plugin.getConfig().getString("join-message")).replaceAll("<player>", e.getPlayer().getName()));
	}
}
