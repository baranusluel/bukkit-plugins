package me.empirewand;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class FlameWave
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public FlameWave(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    final BlockIterator blocknext = new BlockIterator(player);
    new BukkitRunnable() {
      public int timer = 0;

      public void run() {
        blocknext.next();
        if (this.timer++ > 60) {
          cancel();
        }
        if (!blocknext.hasNext()) {
          cancel();
        }
        if (this.timer % 5 != 0) {
          return;
        }
        Block next = blocknext.next();
        try {
          Location loc = next.getLocation();
          int rad = 1;
          int offSetX = loc.getBlockX();
          int offSetY = loc.add(0.0D, 1.0D, 0.0D).getBlockY();
          int offSetZ = loc.getBlockZ();
          World world = loc.getWorld();

          int startX = offSetX - rad;
          int startY = offSetY - rad;
          int startZ = offSetZ - rad;

          int endX = offSetX + rad;
          int endY = offSetY + rad;
          int endZ = offSetZ + rad;
          boolean doFire = false;
          for (int counterX = startX; counterX <= endX; counterX++) {
            for (int counterY = startY; counterY <= endY; counterY++)
              for (int counterZ = startZ; counterZ <= endZ; counterZ++)
                if ((counterX - offSetX ^ 2 + (counterY - offSetY) ^ 2 + (counterZ - offSetZ) ^ 0x2) <= 4) {
                  if (doFire) {
                    Block block = world.getBlockAt(counterX, counterY, counterZ);
                    block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                    if (block.getLocation().getBlock().getType() == Material.AIR) {
                      block.setType(Material.FIRE);
                    }
                  }
                  doFire = !doFire;
                }
          }
        }
        catch (IllegalArgumentException e)
        {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    .runTaskTimer(this.plugin, 1L, 1L);
  }
}