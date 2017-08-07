package me.empirewand;

import java.util.Iterator;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Capture
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public Capture(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player)
  {
    if (player.getPassenger() != null) {
      player.getPassenger().setVelocity(player.getLocation().getDirection().multiply(3));
      Location loc = player.getPassenger().getLocation();
      player.playEffect(loc, Effect.ENDER_SIGNAL, 50);
      player.playEffect(loc, Effect.EXTINGUISH, 0);
      player.playSound(loc, Sound.ENDERMAN_TELEPORT, 2.0F, 1.0F);
      player.eject();
      return;
    }
    BlockIterator iterator = new BlockIterator(player.getWorld(), player.getLocation().toVector(), player.getEyeLocation().getDirection(), 0.0D, 100);
    Iterator localIterator;
    for (; iterator.hasNext(); 
      localIterator.hasNext())
    {
      Block item = iterator.next();
      Iterable nearbyLoc = player.getNearbyEntities(10.0D, 10.0D, 10.0D);
      int acc = 2;
      localIterator = nearbyLoc.iterator(); continue; Entity entity = (Entity)localIterator.next();
      for (int x = -acc; x < acc; x++)
        for (int z = -acc; z < acc; z++)
          for (int y = -acc; y < acc; y++)
            if (entity.getLocation().getBlock().getRelative(x, y, z).equals(item)) {
              if ((entity instanceof LivingEntity)) {
                player.setPassenger(entity);
                Location loc = player.getPassenger().getLocation();
                player.playEffect(loc, Effect.ENDER_SIGNAL, 50);
                player.playEffect(loc, Effect.EXTINGUISH, 0);
                player.playSound(loc, Sound.ENDERMAN_TELEPORT, 2.0F, 1.0F);
                new BukkitRunnable()
                {
                  public void run() {
                    if ((player.getPassenger() == null) || (player.getPassenger().isDead()) || (!player.getPassenger().isValid())) {
                      cancel();
                      return;
                    }
                    player.getWorld().playEffect(player.getEyeLocation().add(0.0D, 2.0D, 0.0D), Effect.ENDER_SIGNAL, 0);
                  }
                }
                .runTaskTimer(this.plugin, 1L, 3L);
              }
              return;
            }
    }
  }
}