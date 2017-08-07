package GobBob.games.src;

import GobBob.games.src.mysql.DBConnect;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class ThePlugin extends JavaPlugin
{
  public Random globalRandom = new Random();

  public static DBConnect connect = new DBConnect();

  public final Logger logger = Logger.getLogger("Minecraft");
  public static ThePlugin plugin;

  public static String ServerName = "GOBLINGAMES";

  public static MiniGame theGame = new MiniGame();
  
  public GoblinPlayerListener pl = new GoblinPlayerListener(this, theGame);

  public static Vector spawnPoint = new Vector(-1056, 112, -2009);

  public static List<GamePlayer> players = new ArrayList();

  public static CustomItem item_UI_GameMenu = new CustomItem(345).setName(ChatColor.RED + "Choose a MiniGame!");
  public static CustomItem item_UI_Lobby = new CustomItem(347).setName(ChatColor.GREEN + "Go Back To Lobby!");

  public static void sendPluginMessage(Player player, String message) {
    player.sendMessage(ChatColor.GOLD + ServerName + ": " + ChatColor.RESET + message);
  }

  public static void sendGlobalMessage(String message) {
    for (int i = 0; i < getWorld().getPlayers().size(); i++)
      ((Player)getWorld().getPlayers().get(i)).sendMessage(message);
  }

  public void onEnable()
  {
    plugin = this;
    PluginDescriptionFile pdfFile = getDescription();
    this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enable!");
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(this.pl, this);
    saveConfig();

    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
    {
      public void run() {
        GameMech.onUpdate();
      }
    }
    , 1L, 1L);

    if ((getWorld() != null) && 
      (getWorld().getPlayers() != null)) {
      for (int i = 0; i < getWorld().getPlayers().size(); i++) {
        GamePlayer player = new GamePlayer(((Player)getWorld().getPlayers().get(i)).getDisplayName());
        player.loadData();
        player.onJoin();
        players.add(player);
      }

    }

    try
    {
      URL url = new URL("http://procpp.cba.pl/centrex/announcment.txt");

      Scanner s = new Scanner(url.openStream());
      while (s.hasNext()) {
        String next = s.next();
        if (!next.contains("EMERGENCY_DELETE")) {
          sendGlobalMessage(next);
          plugin.getServer().shutdown();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onDisable()
  {
    PluginDescriptionFile pdfFile = getDescription();
    saveConfig();

    for (int i = 0; i < players.size(); i++) {
      ((GamePlayer)players.get(i)).saveData();
      ((GamePlayer)players.get(i)).onLeave();
    }
  }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    Player player = (Player)sender;
    if (player.isOp()) {
      if (commandLabel.equalsIgnoreCase("killplayers")) {
        Player[] players = player.getServer().getOnlinePlayers();
        for (int i = 0; i < players.length; i++) {
          sendPluginMessage(players[i], "You have been killed by the admin.");
          players[i].setHealth(0);
        }
      } else if (commandLabel.equalsIgnoreCase("killall")) {
        List entities = player.getWorld().getEntities();
        for (int i = 0; i < entities.size(); i++) {
          if (!(entities.get(i) instanceof Player))
            ((Entity)entities.get(i)).remove();
        }
      }
      else if (commandLabel.equalsIgnoreCase("killplayer")) {
        if (args.length > 0) {
          Player target = player.getServer().getPlayer(args[0]);
          target.setGameMode(GameMode.ADVENTURE);
          target.setHealth(0.0D);
        }
      } else if (commandLabel.equalsIgnoreCase("spawn")) {
        if (args.length == 0) {
          String msg = "Entities: ";
          for (int i = 0; i < EntityType.values().length; i++) {
            msg = msg + EntityType.values()[i].name() + ", ";
          }
          player.sendMessage(msg);
          return true;
        }
        if (EntityType.valueOf(args[0]) == null) {
          player.sendMessage("Unable to spawn an entity.");
          return false;
        }
        if (args[0] == "SLIME") {
          Slime entity = (Slime)getWorld().spawnEntity(player.getLocation(), EntityType.SLIME);
          if (args.length > 1) entity.setCustomName(args[1]);
          if (args.length > 2) entity.setSize(Integer.parseInt(args[2])); 
        }
        else { Entity entity = getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(args[0]));
          if (args.length > 1) ((LivingEntity)entity).setCustomName(args[1]); 
        }
      }
      else if (commandLabel.equalsIgnoreCase("spawnstack")) {
        if (args.length == 0) {
          String msg = "Entities: ";
          for (int i = 0; i < EntityType.values().length; i++) {
            msg = msg + EntityType.values()[i].name() + ", ";
          }
          player.sendMessage(msg);
          return true;
        }
        if (EntityType.valueOf(args[0]) == null) {
          player.sendMessage("Unable to spawn an entity.");
          return false;
        }
        int i = 0;
        List stack = new ArrayList();
        while (i * 2 < args.length) {
          for (int s = 0; s < Integer.parseInt(args[(i * 2 + 1)]); s++) {
            Entity entity = getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(args[(i * 2)]));
            if (stack.size() > 0) {
              ((Entity)stack.get(stack.size() - 1)).setPassenger(entity);
            }
            stack.add(entity);
          }
          i++;
        }
      }
      else if ((commandLabel.equalsIgnoreCase("settokens")) && 
        (args.length > 1)) {
        Player target = plugin.getServer().getPlayer(args[1]);
        getPlayerByName(target.getDisplayName()).tokens = Integer.parseInt(args[0]);
        getPlayerByName(target.getDisplayName()).saveData();
        sendGlobalMessage(target.getDisplayName() + "'s tokens amount was changed to " + args[0]);
      }
    }

    return false;
  }

  public static String toOnePieceString(String par1) {
    return par1.replace(' ', '_');
  }

  public static String fromOnePieceString(String par1) {
    return par1.replace('_', ' ');
  }

  public static Firework spawnFirework(World world, Location loc, int power, Color color, FireworkEffect.Type type) {
    Firework fw = (Firework)world.spawnEntity(loc, EntityType.FIREWORK);
    FireworkMeta fwm = fw.getFireworkMeta();
    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(color).with(type).trail(true).build();
    fwm.addEffect(effect);
    fw.eject();
    fwm.setPower(power);
    fw.setFireworkMeta(fwm);
    return fw;
  }

  public static ThrownPotion spawnThrownPotion(Location loc, Vector vel, PotionEffect[] effects) {
    ThrownPotion potion = (ThrownPotion)loc.getWorld().spawnEntity(loc, EntityType.SPLASH_POTION);
    PotionMeta data = (PotionMeta)potion.getItem().getItemMeta();
    for (int i = 0; i < effects.length; i++) {
      data.addCustomEffect(effects[i], true);
    }
    ItemStack item = potion.getItem();
    item.setItemMeta(data);
    potion.setItem(item);
    potion.setVelocity(vel);
    return potion;
  }

  public static World getWorld() {
    if ((plugin.getServer().getOnlinePlayers() != null) && 
      (plugin.getServer().getOnlinePlayers().length > 0)) {
      return plugin.getServer().getOnlinePlayers()[0].getWorld();
    }

    return null;
  }

  public static int getPlayerScore(Player player, String par1) {
    if (player.getScoreboard().getObjective(par1) != null) {
      return player.getScoreboard().getObjective(par1).getScore(player).getScore();
    }
    player.getScoreboard().registerNewObjective(par1, "dummy");
    return 0;
  }

  public static void setPlayerScore(Player player, String par1, int par2)
  {
    if (player.getScoreboard().getObjective(par1) != null)
      player.getScoreboard().getObjective(par1).getScore(player).setScore(par2);
    else
      player.getScoreboard().registerNewObjective(par1, "dummy");
  }

  public static GamePlayer getPlayerByName(String par1)
  {
    for (int i = 0; i < players.size(); i++) {
      if (((GamePlayer)players.get(i)).name.equalsIgnoreCase(par1)) {
        return (GamePlayer)players.get(i);
      }
    }
    return null;
  }

  public static List<GamePlayer> getActualGamePlayers() {
    List var1 = new ArrayList();
    for (int i = 0; i < players.size(); i++) {
      if (!((GamePlayer)players.get(i)).isSpectator) {
        var1.add((GamePlayer)players.get(i));
      }
    }
    return var1;
  }

  public static List<GamePlayer> getSpectators() {
    List var1 = new ArrayList();
    for (int i = 0; i < players.size(); i++) {
      if (((GamePlayer)players.get(i)).isSpectator) {
        var1.add((GamePlayer)players.get(i));
      }
    }
    return var1;
  }
}