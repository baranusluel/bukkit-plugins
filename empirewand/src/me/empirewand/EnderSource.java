package me.empirewand;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class EnderSource
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public EnderSource(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player) {
    if (player.getVehicle() != null) {
      String str = this.plugin.getConfig().getString("EnderEggTwiceMessage");
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
      return;
    }
    String str = this.plugin.getConfig().getString("EnderEggStartMessage");
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
    FallingBlock bl = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.DRAGON_EGG, (byte)0);
    new EnderEgg(bl, player.getEyeLocation().add(0.0D, 10.0D, 0.0D), 2000, new EnderEgg.Handler() {
      int effect = 0;

      public void onTick(FallingBlock ent, Location target)
      {
        if (this.effect++ > 2) {
          player.playEffect(ent.getLocation().add(0.0D, 1.0D, 0.0D), Effect.ENDER_SIGNAL, 50);
          this.effect = 0;
        }
        if (player.isSneaking())
          target.add(player.getLocation().getDirection());
      }
    }).start(this.plugin);
    bl.setPassenger(player);
  }
}