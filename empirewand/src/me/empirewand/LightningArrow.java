package me.empirewand;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LightningArrow
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public LightningArrow(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    final Arrow arrow = (Arrow)player.launchProjectile(Arrow.class);
    new BukkitRunnable()
    {
      private Location firstLoc;

      public final void run() {
        if (!arrow.isValid()) {
          try {
            LightningArrow.this.firework.playFirework(arrow.getWorld(), arrow.getLocation(), FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(Color.AQUA).withFade(Color.WHITE).build());
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (Exception e) {
            e.printStackTrace();
          }
          arrow.remove();
          cancel();
        } else if (this.firstLoc == null) {
          this.firstLoc = arrow.getLocation();
        } else {
          Location secondLoc = arrow.getLocation();
          if (this.firstLoc.toVector().equals(secondLoc.toVector())) {
            secondLoc.getWorld().strikeLightning(secondLoc);
            arrow.remove();
            cancel();
          }
          this.firstLoc = secondLoc;
        }

        if (this.firstLoc != null)
          try {
            LightningArrow.this.firework.playFirework(arrow.getWorld(), arrow.getLocation(), FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(Color.AQUA).withFade(Color.WHITE).build());
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (Exception e) {
            e.printStackTrace();
          }
      }
    }
    .runTaskTimer(this.plugin, 2L, 4L);
  }
}