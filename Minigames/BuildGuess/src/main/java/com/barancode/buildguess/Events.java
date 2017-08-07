package main.java.com.barancode.buildguess;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class Events implements Listener{
	Main plugin;
	
	public Events(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (!e.getPlayer().getName().equals(Main.vars.builder)) return;
		HashMap<String, Integer> region = Main.vars.buildRegion;
		Location loc = e.getTo();
		if (loc.getBlockX() <= region.get("maxx") && loc.getBlockX() >= region.get("minx") && loc.getBlockY() <= region.get("maxy") && loc.getBlockY() >= region.get("miny") && loc.getBlockZ() <= region.get("maxz") && loc.getBlockZ() >= region.get("minz")){
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void asyncLogin(AsyncPlayerPreLoginEvent e){
		if (Main.vars.gg){
			e.setLoginResult(Result.KICK_OTHER);
			e.setKickMessage(ConfigValues.getEndingMessage());
			return;
		} else if (!Main.vars.ingame){
			if (e.getLoginResult() != Result.KICK_FULL){
				if (e.getLoginResult() == Result.ALLOWED && Bukkit.getOnlinePlayers().length == ConfigValues.getMaxPlayers()){
					if (!Permissions.hasVIPJoin(e.getName())) e.setLoginResult(Result.KICK_FULL);
				}
				return;
			}
			if (Permissions.hasVIPJoin(e.getName())) e.setLoginResult(Result.ALLOWED);
		} else {
			if (Permissions.hasVIPJoin(e.getName())) e.setLoginResult(Result.ALLOWED);
			else {
				e.setLoginResult(Result.KICK_OTHER);
				e.setKickMessage(ConfigValues.getIngameMessage());
			}
		}
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e){
		e.setJoinMessage(ConfigValues.getJoinMessage(e.getPlayer().getName()));
		e.getPlayer().sendMessage(ConfigValues.getJoinMotd());
		
		if (!Main.vars.coins.containsKey(e.getPlayer().getName())){
			Main.vars.coins.put(e.getPlayer().getName(), Permissions.getStartCoins(e.getPlayer()));
		}
		
		if (!Main.vars.hintsLeft.containsKey(e.getPlayer().getName())){
			int max;
			if (e.getPlayer().hasPermission("tips.vip")) max = ConfigValues.getVIPTipAmount();
			else max = ConfigValues.getTipAmount();
			Main.vars.hintsLeft.put(e.getPlayer().getName(), max);
		}
		
		if (Bukkit.getOnlinePlayers().length == ConfigValues.getMinPlayers() && !Main.vars.ingame && !Main.vars.countdown){
			plugin.matchman.startCountdown();
		}
		
		Main.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				e.getPlayer().teleport(ConfigValues.getSpawn());
				e.getPlayer().getInventory().clear();
				e.getPlayer().getInventory().setItem(ConfigValues.getHubSlot(), plugin.hubItem);
				e.getPlayer().getInventory().setItem(ConfigValues.getBookSlot(), plugin.bookItem);
				if (Main.vars.ingame) e.getPlayer().getInventory().setItem(ConfigValues.getHintSlot(), plugin.hintItem);
				for (Player p : Bukkit.getOnlinePlayers()){
					if (!Main.vars.ingame) Board.coinlessBoard(p);
					else Board.gameBoard(p);
				}
				e.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
		}, 1L);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		// The last player is leaving, time to initialize.
		// The match might not have ended, but even so, if
		// all the players have left the server, we should reset
		if (Bukkit.getOnlinePlayers().length == 1){
			Main.scheduler.cancelAllTasks();
			plugin.initialize();
			return;
		}
		
		if (Bukkit.getOnlinePlayers().length == 2 && Main.vars.ingame && !Main.vars.gg){
			Main.scheduler.cancelAllTasks();
			Bukkit.broadcastMessage(ConfigValues.getMatchEnd());
			plugin.matchman.endBuilder();
			Main.vars.ingame = false;
			Main.vars.gg = true;
			
			Main.vars.countdownTime = ConfigValues.getFinishedTime();
			
			plugin.matchman.getWinner();
			plugin.matchman.rollback();
			plugin.matchman.startGG();
			return;
		}
		
		if (!Main.vars.kicking) e.setQuitMessage(ConfigValues.getLeaveMessage(e.getPlayer().getName()));
		else e.setQuitMessage("");
		
		// Update scoreboard (for the player count)
		Main.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				for (Player p : Bukkit.getOnlinePlayers()){
					if (!Main.vars.ingame) Board.coinlessBoard(p);
					else Board.gameBoard(p);
				}
			}
		}, 1L);
		
		// If builder left, move to next round
		if (Main.vars.builder.equals(e.getPlayer().getName())){
			if (Main.vars.ingame) {
				Main.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
					@Override
					public void run(){
						plugin.matchman.newRound(true);
					}
				}, 1L);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getItem() != null){
			if (e.getItem().isSimilar(plugin.hubItem)){
				e.setCancelled(true);
				Utils.connectToHub(e.getPlayer(), plugin);
				return;
			} else if (e.getItem().isSimilar(plugin.hintItem)){
				e.setCancelled(true);
				if (Main.vars.ingame && !Main.vars.word.equals("")){
					String name = e.getPlayer().getName();
					int left = Main.vars.hintsLeft.get(name);
					if (left == 0){
						e.getPlayer().sendMessage(ConfigValues.getMaxTipsMessage());
						return;
					}
					
					left--;
					Main.vars.hintsLeft.put(name, left);
					e.getPlayer().sendMessage(ConfigValues.getTipsLeftMessage().replaceAll("<amount>", left + ""));
					e.getPlayer().sendMessage(ConfigValues.getHintMessage().replaceAll("<hint>", ConfigValues.getHint(Main.vars.word)));
				}
				return;
			} else if (e.getItem().isSimilar(plugin.bookItem)){
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) return;
			} else if ((e.getItem().getType() == Material.MINECART || e.getItem().getType() == Material.BOAT) && e.getAction() == Action.RIGHT_CLICK_BLOCK){
				e.setCancelled(true);
				return;
			} else if ((e.getItem().getType() == Material.ENDER_PEARL || e.getItem().getType() == Material.EYE_OF_ENDER) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
				e.setCancelled(true);
				return;
			}
		}
		
		if (!e.getPlayer().getName().equals(Main.vars.builder)){
			e.setCancelled(true);
			return;
		}
		
		Location loc = null;
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) loc = e.getClickedBlock().getLocation();
		else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) loc = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
		else return;
		HashMap<String, Integer> region = Main.vars.buildRegion;
		if (loc.getBlockX() <= region.get("maxx") && loc.getBlockX() >= region.get("minx") && loc.getBlockY() <= region.get("maxy") && loc.getBlockY() >= region.get("miny") && loc.getBlockZ() <= region.get("maxz") && loc.getBlockZ() >= region.get("minz")){
			e.setCancelled(false);
			return;
		}
		e.getPlayer().sendMessage(ConfigValues.getBuilderOutside());
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onChat(final AsyncPlayerChatEvent e){
		if (e.getPlayer().getName().equals(Main.vars.builder)){
			e.setCancelled(true);
			// Sync for thread safety
			Main.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
				@Override
				public void run(){
					e.getPlayer().sendMessage(ConfigValues.getBuilderTalk());
				}
			});
			return;
		}
		
		if (Main.vars.ingame){
			String name = e.getPlayer().getName();
			if (Main.vars.lastChat.containsKey(name)){
				long last = Main.vars.lastChat.get(name);
				int cooldown = ConfigValues.getChatCooldown();
				if ((System.currentTimeMillis() - last) / 1000 < cooldown){
					e.getPlayer().sendMessage(ConfigValues.getChatCooldownMessage().replaceAll("<time>", cooldown + ""));
					e.setCancelled(true);
					return;
				}
			}
			Main.vars.lastChat.put(name, System.currentTimeMillis());
			
			if (e.getMessage().trim().equalsIgnoreCase(Main.vars.word)){
				e.setCancelled(true);
				// Sync for thread safety
				Main.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
					@Override
					public void run(){
						Bukkit.broadcastMessage(ConfigValues.getGuessed().replaceAll("<player>", e.getPlayer().getName()).replaceAll("<word>", Main.vars.word));
						int guesserReward = ConfigValues.getGuesserReward();
						int builderReward = ConfigValues.getBuilderReward();
						if (Main.vars.coins.containsKey(Main.vars.builder)) Main.vars.coins.put(Main.vars.builder, Main.vars.coins.get(Main.vars.builder) + builderReward);
						else Main.vars.coins.put(Main.vars.builder, builderReward);
						if (Main.vars.coins.containsKey(e.getPlayer().getName())) Main.vars.coins.put(e.getPlayer().getName(), Main.vars.coins.get(e.getPlayer().getName()) + guesserReward);
						else Main.vars.coins.put(e.getPlayer().getName(), guesserReward);
						Bukkit.getPlayer(Main.vars.builder).sendMessage(ConfigValues.getCoinsMessage().replaceAll("<amount>", builderReward + ""));
						e.getPlayer().sendMessage(ConfigValues.getCoinsMessage().replaceAll("<amount>", guesserReward + ""));
						plugin.matchman.endBuilder();
						plugin.matchman.newRound(true);
					}
				});
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		if (e.getItemDrop().getItemStack().isSimilar(plugin.hintItem)) e.getPlayer().getInventory().setItem(ConfigValues.getHintSlot(), plugin.hintItem);
		else if (e.getItemDrop().getItemStack().isSimilar(plugin.hubItem)) e.getPlayer().getInventory().setItem(ConfigValues.getHubSlot(), plugin.hubItem);
		else if (e.getItemDrop().getItemStack().isSimilar(plugin.bookItem)) e.getPlayer().getInventory().setItem(ConfigValues.getBookSlot(), plugin.bookItem);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if (e.getCurrentItem() != null && (e.getCurrentItem().isSimilar(plugin.hubItem)
				|| e.getCurrentItem().isSimilar(plugin.hintItem)
				|| e.getCurrentItem().isSimilar(plugin.bookItem))){
			e.setCancelled(true);
			return;
		}
		if (e.getCursor() != null && (e.getCursor().isSimilar(plugin.hubItem)
				|| e.getCursor().isSimilar(plugin.hintItem)
				|| e.getCursor().isSimilar(plugin.bookItem))){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		List<String> commands = ConfigValues.getCommands();
		String command = e.getMessage().replaceFirst("/", "");
		String[] commandparts = command.split(" ");
		command = "";
		for (int i = 0; i < commandparts.length; i++){
			  command += " " + commandparts[i];
			  command = command.trim();
			  if (commands.contains(command.toLowerCase())){
					e.getPlayer().sendMessage(ConfigValues.getCommandMessage());
					e.setCancelled(true);
					return;
			  }
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e){
		if (Main.vars.gg){
			e.setMotd(ConfigValues.getFinishingMotd());
		} else if (Main.vars.ingame){
			e.setMotd(ConfigValues.getIngameMotd());
		} else if (Main.vars.countdown){
			e.setMotd(ConfigValues.getLobbyingMotd());
		} else {
			e.setMotd(ConfigValues.getWaitingMotd());
		}
	}
	
	@EventHandler
	public void onBurn(BlockBurnEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onSpread(BlockSpreadEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFade(BlockFadeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void weatherChange(WeatherChangeEvent e){
		if (e.toWeatherState()) e.setCancelled(true);
	}
	
	@EventHandler
	public void onForm(BlockFormEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		e.setRespawnLocation(ConfigValues.getSpawn());
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if (e.getEntityType() == EntityType.PLAYER) e.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e){
		e.setFoodLevel(20);
	}
	
	@EventHandler
	public void onFlow(BlockFromToEvent e){
		HashMap<String, Integer> region = Main.vars.buildRegion;
		Location loc = e.getToBlock().getLocation();
		if (loc.getBlockX() <= region.get("maxx") && loc.getBlockX() >= region.get("minx") && loc.getBlockY() <= region.get("maxy") && loc.getBlockY() >= region.get("miny") && loc.getBlockZ() <= region.get("maxz") && loc.getBlockZ() >= region.get("minz")){
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onGrow(StructureGrowEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBow(EntityShootBowEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDestroyFrame(HangingBreakEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlaceFrame(HangingPlaceEvent e){
		e.setCancelled(true);
	}
}
