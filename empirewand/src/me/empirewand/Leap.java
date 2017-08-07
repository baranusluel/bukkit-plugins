package me.empirewand;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Leap
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public Leap(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player) {
    Location loc = player.getLocation();
    player.setVelocity(loc.getDirection().multiply(3));
    player.setFallDistance(-10000000.0F);
    loc = loc.add(0.0D, 1.0D, 0.0D);
    new BukkitRunnable() {
      public int timer = 0;

      public void run()
      {
        player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
        if (player.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getType() != Material.AIR) {
          cancel();
          return;
        }
        if (this.timer++ > 150)
          cancel();
      }
    }
    .runTaskTimer(this.plugin, 8L, 1L);
  }
}