package GobBob.games.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class MiniGame
{
  public static final int TILECOLOR_RED = 14;
  public static final int TILECOLOR_GREEN = 13;
  public static final int TILECOLOR_LIME = 5;
  public static final int TILECOLOR_ORANGE = 1;
  public static final int TILECOLOR_YELLOW = 4;
  public static final int TILECOLOR_BLACK = 15;
  public static final int TILECOLOR_GREY = 7;
  public static final int TILECOLOR_BLUE = 11;
  public static final int TILECOLOR_LIGHTBLUE = 3;
  public static final int TILECOLOR_LIGHTGREY = 8;
  public static final int TILECOLOR_WHITE = 0;
  public static final int TILECOLOR_PINK = 6;
  public static final int TILECOLOR_PURPLE = 10;
  public static int[] availableTileColorList = { 
    14, 
    6, 
    13, 
    5, 
    11, 
    3, 
    0, 15, 
    10, 
    1, 
    4, 
    7, 
    8 };

  public List<Integer> usedTileColorList = null;

  public Vector boardPosition = new Vector(-1286, 113, -2009);

  public final int boardSize = 10;

  public List<GameData> gameData = new ArrayList();
  public int timeUntilStart;
  public boolean inLobbyState = true;
  public boolean countingDown = false;
  public int timeUntilShuffle;
  public float speedMultiplier = 1.0F;
  public int currTileColor;
  
  public boolean playing = false;

  public String getID()
  {
    return "colorShuffle";
  }

  public String getDisplayName() {
    return "Color Shuffle";
  }

  public ItemStack getIconItem() {
    return new CustomItem(357).setName(ChatColor.DARK_BLUE + "S" + ChatColor.BLUE + "h" + ChatColor.DARK_AQUA + "u" + ChatColor.AQUA + "f" + ChatColor.LIGHT_PURPLE + "f" + ChatColor.DARK_GREEN + "l" + ChatColor.GREEN + "e").getItem();
  }

  public void onUpdate()
  {
    if (this.inLobbyState) {
      if (ThePlugin.players.size() >= getMinPlayers()) {
        if (!this.countingDown) {
          this.timeUntilStart = 400;
          ThePlugin.sendGlobalMessage("The game is about to start! " + ChatColor.BOLD + ChatColor.GOLD + "20 seconds" + ChatColor.RESET + " remaining!");
        }
        this.countingDown = true;
        this.timeUntilStart -= 1;
        if (this.timeUntilStart == 1) {
          this.countingDown = false;
          this.inLobbyState = false;
          onStart();
        }
      } else {
        this.countingDown = false;
        this.timeUntilStart = 0;
      }
    }
    else {
      if (this.timeUntilShuffle == (int)(100.0F / this.speedMultiplier)) {
        this.currTileColor = availableTileColorList[ThePlugin.plugin.globalRandom.nextInt(availableTileColorList.length)];
        for (int i = 0; i < ThePlugin.getActualGamePlayers().size(); i++) {
          ((GamePlayer)ThePlugin.getActualGamePlayers().get(i)).getPlayer().getInventory().clear();
          for (int s = 0; s < 9; s++) {
            ((GamePlayer)ThePlugin.getActualGamePlayers().get(i)).getPlayer().getInventory().setItem(s, new CustomItem(35).setDurability(this.currTileColor).getItem());
          }
        }
        playSound(false, new Vector(), Sound.ORB_PICKUP, 2.0F, 1.0F);
      }
      if (this.timeUntilShuffle == (int)(50.0F / this.speedMultiplier)) {
        Location theLocation = new Location(ThePlugin.getWorld(), this.boardPosition.getX(), this.boardPosition.getY(), this.boardPosition.getZ());
        breakTheBoardButTheColor(theLocation, this.currTileColor);
        playSound(false, new Vector(), Sound.ITEM_BREAK, 2.0F, 1.0F);
      }
      if (this.timeUntilShuffle == 0) {
        Location theLocation = new Location(ThePlugin.getWorld(), this.boardPosition.getX(), this.boardPosition.getY(), this.boardPosition.getZ());

        spawnTheBoard(theLocation, ThePlugin.plugin.globalRandom);
        this.speedMultiplier += 0.1F;
        this.timeUntilShuffle = ((int)(200.0F / this.speedMultiplier));
      }
      for (int i = 0; i < ThePlugin.getActualGamePlayers().size(); i++) {
        if (((GamePlayer)ThePlugin.getActualGamePlayers().get(i)).getLocation().getY() < this.boardPosition.getY() - 5.0D) {
          ((GamePlayer)ThePlugin.getActualGamePlayers().get(i)).teleport(new Location(((GamePlayer)ThePlugin.players.get(i)).getWorld(), this.boardPosition.getX() + 5.0D, this.boardPosition.getY() + 10.0D, this.boardPosition.getZ() + 5.0D));

          ((GamePlayer)ThePlugin.getActualGamePlayers().get(i)).getPlayer().sendMessage("You are a spectator now!");
          ((GamePlayer)ThePlugin.getActualGamePlayers().get(i)).getPlayer().getInventory().clear();
          ((GamePlayer)ThePlugin.getActualGamePlayers().get(i)).lose();
        }
      }
      if (ThePlugin.getActualGamePlayers().size() == 1) {
        ThePlugin.sendGlobalMessage(ChatColor.GREEN + ((GamePlayer)ThePlugin.getActualGamePlayers().get(0)).name + " just won in " + getDisplayName() + "!");
        ((GamePlayer)ThePlugin.getActualGamePlayers().get(0)).tokens += 10;
        ((GamePlayer)ThePlugin.getActualGamePlayers().get(0)).win();
        for (int i = 0; i < ThePlugin.players.size(); i++) {
          ((GamePlayer)ThePlugin.players.get(i)).respawnAtGameLobby();
          ((GamePlayer)ThePlugin.players.get(i)).getPlayer().setGameMode(GameMode.SURVIVAL);
        }
        onEnd();
      }
      this.timeUntilShuffle -= 1;
    }
  }

  public void onStart() {
    this.timeUntilShuffle = 200;
    this.speedMultiplier = 1.0F;
    this.usedTileColorList = new ArrayList();
    Location theLocation = new Location(ThePlugin.getWorld(), this.boardPosition.getX(), this.boardPosition.getY(), this.boardPosition.getZ());
    spawnTheBoard(theLocation, ThePlugin.plugin.globalRandom);

    for (int i = 0; i < ThePlugin.players.size(); i++) {
      ((GamePlayer)ThePlugin.players.get(i)).teleport(new Location(ThePlugin.getWorld(), this.boardPosition.getX() + ThePlugin.plugin.globalRandom.nextInt(30), this.boardPosition.getY() + 1.0D, this.boardPosition.getZ() + ThePlugin.plugin.globalRandom.nextInt(30)));
      ((GamePlayer)ThePlugin.players.get(i)).getPlayer().setGameMode(GameMode.SURVIVAL);
    }
    
    playing = true;
  }

  public void spawnTheBoard(Location theLocation, Random rand)
  {
    theLocation.setY(this.boardPosition.getY());
    for (int x = 0; x < 10; x++)
      for (int z = 0; z < 10; z++) {
        int tileColor = availableTileColorList[rand.nextInt(availableTileColorList.length)];
        if (!isColorOnBoard(tileColor)) this.usedTileColorList.add(Integer.valueOf(tileColor));
        for (int i = 0; i < 3; i++)
          for (int j = 0; j < 3; j++) {
            theLocation.setX(this.boardPosition.getX() + x * 3 + i);
            theLocation.setZ(this.boardPosition.getZ() + z * 3 + j);
            if (!playing || theLocation.getBlock().getType() != Material.AIR){
            	theLocation.getBlock().setTypeId(35);
                theLocation.getBlock().setData((byte)tileColor);
            }
          }
      }
  }

  public void breakTheBoard(Location theLocation)
  {
    theLocation.setY(this.boardPosition.getY());
    for (int x = 0; x < 10; x++)
      for (int z = 0; z < 10; z++)
        for (int i = 0; i < 3; i++)
          for (int j = 0; j < 3; j++) {
            theLocation.setX(this.boardPosition.getX() + x * 3 + i);
            theLocation.setZ(this.boardPosition.getZ() + z * 3 + j);
            theLocation.getBlock().setTypeId(0);
          }
  }

  public void breakTheBoardButTheColor(Location theLocation, int color)
  {
    theLocation.setY(this.boardPosition.getY());
    for (int x = 0; x < 30; x++)
      for (int z = 0; z < 30; z++) {
        theLocation.setX(this.boardPosition.getX() + x);
        theLocation.setZ(this.boardPosition.getZ() + z);
        if ((theLocation.getBlock().getType() == Material.WOOL) && (theLocation.getBlock().getData() != color))
          theLocation.getBlock().setTypeId(30);
      }
  }

  public boolean isColorOnBoard(int par1)
  {
    for (int i = 0; i < this.usedTileColorList.size(); i++) {
      if (((Integer)this.usedTileColorList.get(i)).intValue() == par1) {
        return true;
      }
    }
    return false;
  }

  public void onEnd() {
    this.inLobbyState = true;
    this.countingDown = false;
    this.timeUntilStart = 400;
    breakTheBoard(new Location(ThePlugin.getWorld(), this.boardPosition.getX(), this.boardPosition.getY(), this.boardPosition.getZ()));
    
    playing = false;
  }

  public int getMinPlayers() {
    return 2;
  }

  public int getMaxPlayers() {
    return 20;
  }

  public void playSound(boolean ThreeDSound, Vector par2, Sound par3, float volume, float pitch) {
    for (int i = 0; i < ThePlugin.players.size(); i++) {
      Vector pos = par2;
      if (!ThreeDSound) {
        pos.setX(((GamePlayer)ThePlugin.players.get(i)).getLocation().getX());
        pos.setY(((GamePlayer)ThePlugin.players.get(i)).getLocation().getY());
        pos.setZ(((GamePlayer)ThePlugin.players.get(i)).getLocation().getZ());
      }
      ((GamePlayer)ThePlugin.players.get(i)).getPlayer().playSound(new Location(ThePlugin.getWorld(), pos.getX(), pos.getY(), pos.getZ()), par3, volume, pitch);
    }
  }
}