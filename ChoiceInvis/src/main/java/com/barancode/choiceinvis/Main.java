package main.java.com.barancode.choiceinvis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;
import org.dynmap.DynmapAPI;

public class Main extends JavaPlugin implements Listener{
	BukkitScheduler scheduler = null;
	int time = 0;
	LinkedHashMap<UUID, PendingInvis> readdingTasks = new LinkedHashMap<UUID, PendingInvis>();
	DynmapAPI livemap = null;
	Main instance = this;
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		scheduler = Bukkit.getScheduler();
		saveDefaultConfig();
		time = getConfig().getInt("time-seconds") * 20;
		if (getServer().getPluginManager().getPlugin("dynmap") != null) livemap = (DynmapAPI)getServer().getPluginManager().getPlugin("dynmap");
		
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				while (true){
					if (readdingTasks.size() == 0) return;
					UUID firstKey = readdingTasks.keySet().iterator().next();
					PendingInvis pi = readdingTasks.get(firstKey);
					long executeTime = pi.start + ((time / 20) * 1000);
					if (System.currentTimeMillis() > executeTime){
						readdingTasks.remove(firstKey);
						Player entity = null;
						if (isOnline(firstKey)) entity = Bukkit.getPlayer(firstKey);
						else continue;
						entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, pi.timeLeft, 0));
						if (livemap != null) livemap.assertPlayerInvisibility(entity, false, instance);
					} else {
						break;
					}
				}
			}
		}, 5L, 5L);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		UUID uuid = e.getEntity().getUniqueId();
		if (readdingTasks.containsKey(uuid)) readdingTasks.remove(uuid);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (livemap != null) livemap.assertPlayerInvisibility(e.getPlayer(), false, instance);
	}
	
	@EventHandler
	public void onPVP(EntityDamageByEntityEvent e){
		if (!(e.getEntity() instanceof Player && (e.getDamager() instanceof Player || e.getDamager() instanceof Projectile))) return;
		if (e.getDamager() instanceof Projectile){
			 ProjectileSource source = ((Projectile)e.getDamager()).getShooter();
			 if (!(source instanceof Player)) return;
		}
		final Player entity = (Player)e.getEntity();
		final UUID uuid = entity.getUniqueId();
		if (readdingTasks.containsKey(uuid)){
			PendingInvis pi = readdingTasks.get(uuid);
			//scheduler.cancelTask(pi.id);
			/*pi.id = scheduler.scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run(){
					if (!readdingTasks.containsKey(uuid)) return;
					PendingInvis pi = readdingTasks.get(uuid);
					readdingTasks.remove(uuid);
					entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, pi.timeLeft, 0));
					if (livemap != null) livemap.assertPlayerInvisibility(entity, false, instance);
				}
			}, time);*/
			long timeSinceStart = System.currentTimeMillis() - pi.start;
			timeSinceStart = (timeSinceStart / 1000) * 20;
			pi.timeLeft = pi.timeLeft - (int)timeSinceStart;
			pi.start = System.currentTimeMillis();
			readdingTasks.put(uuid, pi);
			
			Collection<PotionEffect> effects = entity.getActivePotionEffects();
			PotionEffect effect = null;
			for (PotionEffect ef : effects){
				if (!ef.getType().equals(PotionEffectType.INVISIBILITY)) continue;
				effect = ef;
			}
			if (effect != null) entity.removePotionEffect(PotionEffectType.INVISIBILITY);
			return;
		}
		if (!entity.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
		Collection<PotionEffect> effects = entity.getActivePotionEffects();
		PotionEffect effect = null;
		for (PotionEffect ef : effects){
			if (!ef.getType().equals(PotionEffectType.INVISIBILITY)) continue;
			effect = ef;
		}
		if (effect == null) return;
		if (effect.getDuration() > time){
			/*int id = scheduler.scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run(){
					if (!readdingTasks.containsKey(uuid)) return;
					PendingInvis pi = readdingTasks.get(uuid);
					readdingTasks.remove(uuid);
					entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, pi.timeLeft, 0));
					if (livemap != null) livemap.assertPlayerInvisibility(entity, false, instance);
				}
			}, time);*/
			readdingTasks.put(uuid, new PendingInvis(effect.getDuration() - time, System.currentTimeMillis(), uuid));
		}
		entity.removePotionEffect(PotionEffectType.INVISIBILITY);
		if (livemap != null) livemap.assertPlayerInvisibility(entity, true, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		reloadConfig();
		time = getConfig().getInt("time-seconds") * 20;
		sender.sendMessage("Reloaded.");
		return true;
	}
	
	public static boolean isOnline(UUID uuid){
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.getUniqueId().equals(uuid)) return true;
		}
		return false;
	}
}
