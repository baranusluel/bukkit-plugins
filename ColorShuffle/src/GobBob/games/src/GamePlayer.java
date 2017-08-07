package GobBob.games.src;

import GobBob.games.src.mysql.DBConnect;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class GamePlayer
{
  public String name;
  public int tokens;
  public boolean isSpectator;

  public GamePlayer(String par1)
  {
    this.name = par1;
  }

  public Player getPlayer() {
    return ThePlugin.plugin.getServer().getPlayer(this.name);
  }

  public void setTokens(int par1) {
    this.tokens = par1;
  }

  public void addTokens(int par1) {
    this.tokens += par1;
  }

  public void saveData() {
    ThePlugin.connect.savePlayerData(this);
  }

  public boolean loadData() {
    if (ThePlugin.connect.loadPlayerData(this, this.name)) {
      ThePlugin.connect.createNewPlayerInDatabase(this.name);
    }
    return false;
  }

  public void onUpdate() {
    getPlayer().setFallDistance(0.0F);
    getPlayer().setFoodLevel(20);

    if (ThePlugin.theGame.inLobbyState) {
      if (ThePlugin.theGame.countingDown) {
        getPlayer().setScoreboard(GameMech.getCustomScoreboard("preGameLobby", ChatColor.GREEN + "Starting in " + ChatColor.BOLD + ThePlugin.theGame.timeUntilStart / 20 + " seconds", new String[] { 
          "Players: " + ChatColor.GREEN + ChatColor.BOLD + ThePlugin.players.size(), 
          ChatColor.GOLD + "Tokens: ", 
          " " + this.tokens }));
      }
      else
        getPlayer().setScoreboard(GameMech.getCustomScoreboard("preGameLobby", ChatColor.GREEN + "Waiting for players...", new String[] { 
          "Players: " + ChatColor.GREEN + ChatColor.BOLD + ThePlugin.players.size(), 
          ChatColor.GOLD + "Tokens: ", 
          " " + this.tokens }));
    }
    else
    {
      getPlayer().setScoreboard(GameMech.getCustomScoreboard("preGameLobby", ChatColor.GOLD + "-=COLORSHUFFLE=-", new String[] { 
        ChatColor.GREEN + "Alive: " + ThePlugin.getActualGamePlayers().size(), 
        ChatColor.RED + "Dead: " + ThePlugin.getSpectators().size() }));

      if (getLocation().getY() < 20.0D) {
        teleport(new Location(getWorld(), ThePlugin.spawnPoint.getX(), ThePlugin.spawnPoint.getY(), ThePlugin.spawnPoint.getZ()));
      }
    }

    if (this.isSpectator) {
      getPlayer().addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(100000000, 1));
      getPlayer().setAllowFlight(true);
      getPlayer().setFlying(true);
    } else {
      getPlayer().setAllowFlight(false);
      getPlayer().setFlying(false);
    }
  }

  public void onInvClick(ItemStack item)
  {
  }

  public void onItemUse(ItemStack item)
  {
  }

  public void onJoin() {
    teleport(new Location(getWorld(), ThePlugin.spawnPoint.getX(), ThePlugin.spawnPoint.getY(), ThePlugin.spawnPoint.getZ()));
    getPlayer().setGameMode(GameMode.SURVIVAL);
    removePotionEffects();
    if (!ThePlugin.theGame.inLobbyState) {
      this.isSpectator = true;
      lose();
      ThePlugin.theGame.getClass(); ThePlugin.theGame.getClass(); teleport(new Location(ThePlugin.getWorld(), ThePlugin.theGame.boardPosition.getX() + 10 / 2, ThePlugin.theGame.boardPosition.getY() + 10.0D, ThePlugin.theGame.boardPosition.getZ() + 10 / 2));
    }
  }

  public void onLeave() {
    getPlayer().setGameMode(GameMode.SURVIVAL);
    removePotionEffects();
    despawnHat();
  }

  public void onDeath() {
    this.isSpectator = true;
    removePotionEffects();
    getPlayer().setGameMode(GameMode.SURVIVAL);
    spawnHat();
  }

  public void lose() {
    this.isSpectator = true;
    removePotionEffects();
    ThePlugin.sendGlobalMessage(ChatColor.AQUA + this.name + ChatColor.RED + " has lost!");
    getPlayer().setGameMode(GameMode.SURVIVAL);
    despawnHat();
  }

  public void win() {
    saveData();
    spawnHat();
  }

  public void respawnAtGameLobby()
  {
    getPlayer().setGameMode(GameMode.SURVIVAL);
    removePotionEffects();

    teleport(new Location(getWorld(), ThePlugin.spawnPoint.getX(), ThePlugin.spawnPoint.getY(), ThePlugin.spawnPoint.getZ()));

    getPlayer().getInventory().clear();
    getPlayer().getInventory().setItem(0, ThePlugin.item_UI_Lobby.getItem());
    getPlayer().updateInventory();

    this.isSpectator = false;
  }

  public void removePotionEffects() {
    getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
    getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
    getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
    getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
    getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
    getPlayer().removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
    getPlayer().removePotionEffect(PotionEffectType.HARM);
    getPlayer().removePotionEffect(PotionEffectType.HEALTH_BOOST);
    getPlayer().removePotionEffect(PotionEffectType.HUNGER);
    getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    getPlayer().removePotionEffect(PotionEffectType.JUMP);
    getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
    getPlayer().removePotionEffect(PotionEffectType.POISON);
    getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
    getPlayer().removePotionEffect(PotionEffectType.SATURATION);
    getPlayer().removePotionEffect(PotionEffectType.SLOW);
    getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
    getPlayer().removePotionEffect(PotionEffectType.SPEED);
    getPlayer().removePotionEffect(PotionEffectType.WATER_BREATHING);
    getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);
    getPlayer().removePotionEffect(PotionEffectType.WITHER);
  }

  public Location getLocation() {
    return getPlayer().getLocation();
  }

  public void teleport(Location location) {
    getPlayer().teleport(location);
  }

  public World getWorld() {
    return getPlayer().getWorld();
  }

  public void spawnHat()
  {
  }

  public void despawnHat()
  {
    if (getPlayer().getPassenger() != null)
      getPlayer().getPassenger().remove();
  }
}