package me.empirewand;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

public class Lightningstorm
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public Lightningstorm(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player)
  {
    Location loc = player.getLocation();
    List circle1 = circle(player, loc, Integer.valueOf(13).intValue(), Integer.valueOf(1).intValue(), Boolean.valueOf(true).booleanValue(), Boolean.valueOf(false).booleanValue(), 15);
    int time = 0;
    final World world = player.getWorld();
    final Location loc2 = loc.add(0.0D, 5.0D, 0.0D);
    final List entity = player.getNearbyEntities(10.0D, 10.0D, 10.0D);
    for (time = 0; time < circle1.size(); time++) {
      final Location j = (Location)circle1.get(time);
      Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
        public void run() {
          try {
            Lightningstorm.this.firework.playFirework(player.getWorld(), j, FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.AQUA).withFade(Color.WHITE).build());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      , time);
    }
    Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
      public void run() {
        for (int i = 0; i < entity.size(); i++)
          world.strikeLightning(((Entity)entity.get(i)).getLocation());
        try
        {
          Lightningstorm.this.firework.playFirework(world, loc2, FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(Color.AQUA).build());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    , time);
  }

  public List<Location> circle(Player player, Location loc, int r, int h, boolean hollow, boolean sphere, int plus_y) {
    List circleblocks = new ArrayList();
    int cx = loc.getBlockX();
    int cy = loc.getBlockY();
    int cz = loc.getBlockZ();
    for (int x = cx - r; x <= cx + r; x++) {
      for (int z = cz - r; z <= cz + r; z++) {
        for (int y = sphere ? cy - r : cy; y < (sphere ? cy + r : cy + h); y++) {
          double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
          if ((dist < r * r) && ((!hollow) || (dist >= (r - 1) * (r - 1)))) {
            Location l = new Location(loc.getWorld(), x, y + plus_y, z);
            circleblocks.add(l);
          }
        }
      }
    }
    return circleblocks;
  }
}