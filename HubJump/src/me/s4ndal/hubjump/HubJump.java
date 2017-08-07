package me.s4ndal.hubjump;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


public class HubJump extends JavaPlugin implements Listener{
	
	BukkitScheduler scheduler = null;
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        scheduler = Bukkit.getScheduler();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hjreload")) {
            saveDefaultConfig();
            reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "HubJump has been Reloaded!");
        }
        return false;
    }
    
    // Code for double jumping
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setVelocity(player.getLocation().getDirection().multiply(getConfig().getDouble("Horizontal")).setY(getConfig().getDouble("Vertical")));
    }
    
    // Allow them to double jump again
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE
                && !player.getAllowFlight()
                && event.getTo().getY() <= event.getFrom().getY()
                && player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR){
            player.setAllowFlight(true);
        }
    }
    
    // Disable all damage except void
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() != DamageCause.VOID) event.setCancelled(true);
        }
    }
    
    // Set walk speed on join
    @EventHandler
    public void onJoin(final PlayerJoinEvent event){
    	scheduler.scheduleSyncDelayedTask(this, new Runnable(){
    		@Override
    		public void run(){
    	        event.getPlayer().setWalkSpeed((float)getConfig().getDouble("WalkSpeed"));
    		}
    	}, 1L);
        event.getPlayer().setAllowFlight(true);
        event.getPlayer().setFlying(false);
    }
    
    // Set walk speed on respawn
    @EventHandler
    public void onRespawn(final PlayerRespawnEvent e){
    	scheduler.scheduleSyncDelayedTask(this, new Runnable(){
    		@Override
    		public void run(){
    	        e.getPlayer().setWalkSpeed((float)getConfig().getDouble("WalkSpeed"));
    		}
    	}, 1L);
    }
    
    @EventHandler
    public void onGamemode(PlayerGameModeChangeEvent e){
        if (e.getNewGameMode() != GameMode.CREATIVE){
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(false);
        }
    }
}
    