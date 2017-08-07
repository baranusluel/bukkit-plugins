package me.empirewand;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Comet
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  Accelerator Accelerate = new Accelerator();
  private Start plugin;

  public Comet(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player) {
    final SmallFireball fireball = (SmallFireball)player.launchProjectile(SmallFireball.class);
    new BukkitRunnable()
    {
      public void run() {
        Location loc = fireball.getLocation();
        try {
          Comet.this.Accelerate.accelerateEntity(fireball);
          Comet.this.Accelerate.accelerateEntity(fireball);
          Comet.this.Accelerate.accelerateEntity(fireball);
        } catch (IllegalAccessException e1) {
          e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
          e1.printStackTrace();
        } catch (InvocationTargetException e1) {
          e1.printStackTrace();
        } catch (IllegalStateException e1) {
          e1.printStackTrace();
        }
        if (!fireball.isValid()) {
          cancel();
          try {
            Comet.this.firework.playFirework(player.getWorld(), loc, FireworkEffect.builder().withColor(Color.ORANGE).withFade(Color.RED).with(FireworkEffect.Type.BALL_LARGE).flicker(true).trail(true).build());
            fireball.remove();
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    .runTaskTimer(this.plugin, 0L, 1L);
    new BukkitRunnable()
    {
      public void run()
      {
        Location loc = fireball.getLocation();
        try {
          if (!fireball.isValid()) {
            cancel();
          }
          Comet.this.firework.playFirework(player.getWorld(), loc, FireworkEffect.builder().withColor(Color.ORANGE).withFade(Color.RED).with(FireworkEffect.Type.BURST).flicker(false).trail(false).build());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    .runTaskTimer(this.plugin, 4L, 2L);
  }
}