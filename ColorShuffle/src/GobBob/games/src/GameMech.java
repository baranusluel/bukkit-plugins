package GobBob.games.src;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class GameMech
{
  public static final String PLAYER_GAME_ID = "GameID";

  public static void onUpdate()
  {
    ThePlugin.theGame.onUpdate();
    for (int i = 0; i < ThePlugin.players.size(); i++)
      ((GamePlayer)ThePlugin.players.get(i)).onUpdate();
  }

  public static Scoreboard getCustomScoreboard(String par1, String par2, String[] par3)
  {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getNewScoreboard();
    Objective objective = board.registerNewObjective(par1, "dummy");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    objective.setDisplayName(par2);
    for (int i = 0; i < par3.length; i++) {
      Score score = objective.getScore(Bukkit.getOfflinePlayer(par3[i]));
      score.setScore(par3.length - i);
    }
    return board;
  }

  public static Scoreboard getCustomScoreboard(String par1, String par2, List<String> par3) {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getNewScoreboard();
    Objective objective = board.registerNewObjective(par1, "dummy");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    objective.setDisplayName(par2);
    for (int i = 0; i < par3.size(); i++) {
      Score score = objective.getScore(Bukkit.getOfflinePlayer((String)par3.get(i)));
      score.setScore(par3.size() - i);
    }
    return board;
  }
}