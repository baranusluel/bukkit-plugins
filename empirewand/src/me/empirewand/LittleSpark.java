package me.empirewand;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LittleSpark
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public LittleSpark(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    Block block = player.getTargetBlock(null, 50);
    Location location = block.getLocation();
    try {
      this.firework.playFirework(player.getWorld(), location, FireworkEffect.builder().withColor(Color.WHITE).withFade(Color.RED).with(FireworkEffect.Type.BURST).trail(false).flicker(true).build());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (Entity entity : this.plugin.GetTargets.getTargetList(location, 3))
      if ((entity instanceof LivingEntity))
        ((LivingEntity)entity).damage(5);
  }
}