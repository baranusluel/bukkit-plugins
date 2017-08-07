package nl.dhd.milan;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import nl.dhd.milan.utils.ExperienceManager;
import nl.dhd.milan.utils.ParticleEffects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ExperienceFly extends JavaPlugin
  implements Listener
{
	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	List<String> playersHittingTheGround = new LinkedList<String>();
  public void onEnable()
  {
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerMoveEvent(PlayerMoveEvent event)
  {
    if (!event.getPlayer().isFlying()) {
    	if (playersHittingTheGround.contains(event.getPlayer().getName())) return;
    	if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
    	if (event.getTo().getY() < event.getFrom().getY()){
    		Location loc = event.getTo();
    		loc.setY(loc.getY() - 1);
    		if (loc.getBlock().getType() == Material.AIR) return;
    		playersHittingTheGround.add(event.getPlayer().getName());
    		final String name = event.getPlayer().getName();            
            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    playersHittingTheGround.remove(name);
                }
            }, 5L);
            double threshold = 0.6D;	
            double multiplier = 11.0D;
            double exponent = 1.1D;

            double vel = event.getPlayer().getVelocity().getY();
            if (vel > -threshold) return;

            double damage = Math.pow((vel + threshold) * -multiplier, exponent);
            event.getPlayer().setHealth(event.getPlayer().getHealth() - damage);
    	}
      return;
    }

    if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
      return;
    }

    Player player = event.getPlayer();
    ExperienceManager expMan = new ExperienceManager(player);
    double distance = event.getFrom().distance(event.getTo());
    if (expMan.getCurrentExp() > 0.5D) {
      if (distance > 0.0D) {
        if (player.getLevel() < 5) {
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5F, 1.1F);
          player.getLocation().getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
        }
        player.playSound(player.getLocation(), Sound.BLAZE_BREATH, 2.0F, 0.1F);
        ParticleEffects.sendToLocation(ParticleEffects.EXPLODE, player.getLocation(), 1.0F, 1.0F, 1.0F, 0.0F, 20);
        expMan.changeExp(-10);
        return;
      }
      return;
    }
    player.sendMessage(ChatColor.RED + "Helaas je Experience is te laag om door te vliegen");
    player.setFlying(false);
    for (int i = 0; i < 30; i++)
      player.getLocation().getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
  }

  @EventHandler
  public void OnPlayerFlightToggle(PlayerToggleFlightEvent event)
  {
    ExperienceManager expMan = new ExperienceManager(event.getPlayer());

    if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
      return;
    }

    if (event.getPlayer().getLevel() < 20) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "Helaas je hebt te weinig levels om te vliegen, je hebt nog " + ChatColor.GOLD + expMan.getXpForLevel(20) + ChatColor.RED + " XP nodig om te kunnen vliegen");
      Location loc = event.getPlayer().getLocation();
      if (loc.getBlock().getType() == Material.STATIONARY_WATER || loc.getBlock().getType() == Material.STATIONARY_LAVA) return;
      while (loc.getBlock().getType() == Material.AIR)
    	  loc.setY(loc.getY() - 1);
      loc.setY(loc.getY() + 1);
      event.getPlayer().teleport(loc);
      return;
    }
  }

  @EventHandler
  public void OnPlayerJoinEvent(PlayerJoinEvent event)
  {
    event.getPlayer().setAllowFlight(true);
  }
  
  @EventHandler
  public void onDamage(EntityDamageEvent event){
	  if (event.getEntity() instanceof Player){
		  if (event.getCause() == DamageCause.FALL) event.setCancelled(true);
	  }
  }
}