package me.empirewand;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Confuse
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public Confuse(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    Location loc = player.getTargetBlock(null, 50).getLocation();
    for (Entity entity : this.plugin.GetTargets.getTargetList(loc, 3))
      if ((entity instanceof LivingEntity)) {
        ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 1));
        ((LivingEntity)entity).damage(2);
      }
    try
    {
      this.firework.playFirework(player.getWorld(), loc, FireworkEffect.builder().withColor(Color.fromRGB(3080239)).withFade(Color.fromRGB(4718664)).with(FireworkEffect.Type.BURST).flicker(false).trail(false).build());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    int radius1 = 3;
    int offSetX = loc.getBlockX();
    int offSetY = loc.add(0.0D, 1.0D, 0.0D).getBlockY();
    int offSetZ = loc.getBlockZ();
    World world = loc.getWorld();
    int startX = offSetX - radius1;
    int startY = offSetY - radius1;
    int startZ = offSetZ - radius1;
    int endX = offSetX + radius1;
    int endY = offSetY + radius1;
    int endZ = offSetZ + radius1;

    for (int counterX = startX; counterX <= endX; counterX++)
      for (int counterY = startY; counterY <= endY; counterY++)
        for (int counterZ = startZ; counterZ <= endZ; counterZ++)
          if ((counterX - offSetX ^ 2 + (counterY - offSetY) ^ 2 + (counterZ - offSetZ) ^ 0x2) <= 4) {
            Block block = world.getBlockAt(counterX, counterY, counterZ);
            world.playEffect(block.getLocation(), Effect.SMOKE, BlockFace.UP);
          }
  }
}