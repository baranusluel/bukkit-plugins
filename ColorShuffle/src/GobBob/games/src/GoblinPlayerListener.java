package GobBob.games.src;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class GoblinPlayerListener
  implements Listener
{
  ThePlugin plugin;
  public Vector boardPosition;

  public GoblinPlayerListener(ThePlugin instance, MiniGame mginstance)
  {
    this.plugin = instance;
    boardPosition = mginstance.boardPosition;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    GamePlayer player = new GamePlayer(event.getPlayer().getDisplayName());
    player.loadData();
    player.onJoin();
    ThePlugin.players.add(player);

    event.setJoinMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "JOIN> " + ChatColor.RESET + Group.getPlayerGroup(player.name).getPrefix() + " " + player.name);

    player.spawnHat();
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    ThePlugin.getPlayerByName(event.getPlayer().getDisplayName()).onLeave();
    ThePlugin.getPlayerByName(event.getPlayer().getDisplayName()).saveData();
    ThePlugin.players.remove(ThePlugin.getPlayerByName(event.getPlayer().getDisplayName()));
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
  }

  @EventHandler
  public void onEntityRespawn(CreatureSpawnEvent event) {
    event.getEntityType();
  }

  @EventHandler
  public void onPlayerItemDrop(PlayerDropItemEvent event)
  {
    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerDie(PlayerDeathEvent event)
  {
    GamePlayer player = ThePlugin.getPlayerByName(event.getEntity().getDisplayName());
    player.onDeath();
    event.setDeathMessage("");
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    event.setRespawnLocation(new Location(event.getPlayer().getWorld(), ThePlugin.spawnPoint.getX(), ThePlugin.spawnPoint.getY(), ThePlugin.spawnPoint.getZ()));
    event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), ThePlugin.spawnPoint.getX(), ThePlugin.spawnPoint.getY(), ThePlugin.spawnPoint.getZ()));
  }

  @EventHandler
  public void onPlayerAttack(EntityDamageByEntityEvent event) {
    if ((event.getDamager() instanceof Player)) {
      Player player = (Player)event.getEntity();
      if (player.getGameMode() != GameMode.CREATIVE)
        event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerInvClick(InventoryClickEvent event)
  {
    if ((event.getWhoClicked() instanceof Player)) {
      Player player = (Player)event.getWhoClicked();
      if ((event.getCurrentItem() != null) && 
        (event.getCurrentItem().hasItemMeta()) && 
        (event.getCurrentItem().getItemMeta().hasDisplayName())) {
        GamePlayer gamePlayer = ThePlugin.getPlayerByName(player.getDisplayName());
        gamePlayer.onInvClick(event.getCurrentItem());
      }

    }

    if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE) event.setCancelled(true); 
  }

  @EventHandler
  public void onPlayerInvDrag(InventoryDragEvent event)
  {
    if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerEntityInteract(PlayerInteractEntityEvent event)
  {
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    if (event.getItem() != null) {
      if (event.getItem().getItemMeta().hasDisplayName()) {
        GamePlayer gamePlayer = ThePlugin.getPlayerByName(player.getDisplayName());
        gamePlayer.onItemUse(event.getItem());
      }
      event.getPlayer().updateInventory();
    }
    if ((event.getPlayer().getGameMode() != GameMode.CREATIVE) && 
      (event.getItem() != null)) {
      if (event.getItem().getType().isBlock()) {
        event.setCancelled(true);
      }
      event.setCancelled(true);
    }
    
    if (event.getAction() == Action.LEFT_CLICK_BLOCK){
    	Location loc = event.getClickedBlock().getLocation();
    	GamePlayer gp = ThePlugin.getPlayerByName(event.getPlayer().getDisplayName());
		if (!gp.isSpectator && loc.getBlockY() == boardPosition.getBlockY() && loc.getBlockX() >= boardPosition.getX() && loc.getBlockX() <= (boardPosition.getX() + 29) && loc.getBlockZ() >= boardPosition.getZ() && loc.getBlockZ() <= (boardPosition.getZ() + 29)){
			event.getClickedBlock().setType(Material.AIR);
		}
    }
  }

  @EventHandler
  public void onProjectileLanded(ProjectileHitEvent event)
  {
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event)
  {
    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to break that block.");
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockPlaced(BlockPlaceEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.setCancelled(true);
      event.getPlayer().updateInventory();
    }
  }

  @EventHandler
  public void onPlayerChat(PlayerChatEvent event) {
    event.setFormat(Group.getPlayerGroup(event.getPlayer().getDisplayName()).getPrefix() + ChatColor.AQUA + " " + event.getPlayer().getDisplayName() + ChatColor.RESET + "> " + event.getMessage());
  }
}