package me.empirewand;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Escape
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public Escape(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player) {
    final Location Explosion = player.getLocation();
    Vector vector = new Vector();
    double rotX = Explosion.getYaw();
    double rotY = -40.0D;
    vector.setY(-Math.sin(Math.toRadians(rotY)));
    double h = Math.cos(Math.toRadians(rotY));
    vector.setX(-h * Math.sin(Math.toRadians(rotX)));
    vector.setZ(h * Math.cos(Math.toRadians(rotX)));
    player.setVelocity(vector.multiply(3));
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
      public void run() {
        Vector vector = new Vector();
        double rotX = Explosion.getYaw();
        double rotY = 80.0D;
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double h = Math.cos(Math.toRadians(rotY));
        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));
        player.setVelocity(vector.multiply(3));
        player.setFallDistance(-10000000.0F);
      }
    }
    , 25L);
    new BukkitRunnable() {
      public int timer = 0;

      public void run() {
        if (this.timer++ > 10) {
          cancel();
        }
        if (!player.getLocation().add(0.0D, -0.5D, 0.0D).getBlock().isEmpty()) {
          cancel();
        }
        Location loc = player.getLocation();
        int id = 51;
        int radius = 1;
        int X = loc.getBlockX();
        int Y = loc.getBlockY();
        int Z = loc.getBlockZ();
        World world = loc.getWorld();
        int startX = X - radius;
        int startY = Y - radius;
        int startZ = Z - radius;
        int endX = X + radius;
        int endY = Y + radius;
        int endZ = Z + radius;

        for (int counterX = startX; counterX <= endX; counterX++) {
          for (int counterY = startY; counterY <= endY; counterY++) {
            for (int counterZ = startZ; counterZ <= endZ; counterZ++)
              if ((counterX - X ^ 2 + (counterY - Y) ^ 2 + (counterZ - Z) ^ 0x2) <= 4)
              {
                Block block = world.getBlockAt(counterX, counterY, counterZ);
                world.playEffect(block.getLocation(), Effect.STEP_SOUND, id);
              }
          }
        }
      }
    }
    .runTaskTimer(this.plugin, 8L, 5L);
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
      public void run() {
        Explosion.getWorld().createExplosion(Explosion.getX(), Explosion.getY() + 1.0D, Explosion.getZ(), 5.0F, true, false);
      }
    }
    , 40L);
  }
}