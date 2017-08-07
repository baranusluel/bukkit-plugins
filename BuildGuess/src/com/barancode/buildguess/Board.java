package com.barancode.buildguess;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Board {
	// Async is used for optimization
	
	// This scoreboard is used for when the match is in-game
	@SuppressWarnings("deprecation")
	public static void gameBoard(final Player p){
		Main.scheduler.scheduleAsyncDelayedTask(Main.plugin, new Runnable(){
			@Override
			public void run(){
				Scoreboard board;
				Objective objective;
				if (Main.vars.boards.containsKey(p.getName())){
					board = Main.vars.boards.get(p.getName()).board;
					objective = board.getObjective(DisplaySlot.SIDEBAR);
				} else {
					board = Main.boardman.getNewScoreboard();
					objective = board.registerNewObjective("scoreboard", "dummy");
					objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				}
			    String timeMessage = ConfigValues.getScoreboardTime().replaceAll("<min>", Main.vars.matchTime / 60 + "").replaceAll("<sec>", Main.vars.matchTime % 60 + "");
			    int spaces = (16 - timeMessage.length()) / 2;
			    for (int i = 0; i < spaces; i++){
			    	timeMessage = " " + timeMessage + " ";
			    }
			    objective.setDisplayName(timeMessage);
			    Score players = objective.getScore(Bukkit.getOfflinePlayer(ConfigValues.getScoreboardPlayers()));
			    players.setScore(Bukkit.getOnlinePlayers().length);
			    int coinAmount;
			    if (Main.vars.coins.containsKey(p.getName())) coinAmount = Main.vars.coins.get(p.getName());
			    else coinAmount = 0;
			    Score coins = objective.getScore(Bukkit.getOfflinePlayer(ConfigValues.getScoreboardCoins()));
			    coins.setScore(coinAmount);
			    int hintsLeft;
			    if (Main.vars.hintsLeft.containsKey(p.getName())) hintsLeft = Main.vars.hintsLeft.get(p.getName());
			    else hintsLeft = 0;
			    Score hints = objective.getScore(Bukkit.getOfflinePlayer(ConfigValues.getScoreboardHints()));
			    hints.setScore(hintsLeft);
			    p.setScoreboard(board);
			    Main.vars.boards.put(p.getName(), new ScoreboardHolder(board, false));
			}
		});
	}
	// This scoreboard is used when the match is waiting for more players,
	// and when the match is ending (time for players to say GG)
	@SuppressWarnings("deprecation")
	public static void coinlessBoard(final Player p){
		Main.scheduler.scheduleAsyncDelayedTask(Main.plugin, new Runnable(){
			@Override
			public void run(){
				Scoreboard board;
				Objective objective;
				if (Main.vars.boards.containsKey(p.getName()) && Main.vars.boards.get(p.getName()).coinless){
					board = Main.vars.boards.get(p.getName()).board;
					objective = board.getObjective(DisplaySlot.SIDEBAR);
				} else {
					board = Main.boardman.getNewScoreboard();
					objective = board.registerNewObjective("scoreboard", "dummy");
					objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				}
			    String timeMessage = ConfigValues.getScoreboardTime().replaceAll("<min>", Main.vars.countdownTime / 60 + "").replaceAll("<sec>", Main.vars.countdownTime % 60 + "");
			    int spaces = (16 - timeMessage.length()) / 2;
			    for (int i = 0; i < spaces; i++){
			    	timeMessage = " " + timeMessage + " ";
			    }
			    objective.setDisplayName(timeMessage);
			    Score players = objective.getScore(Bukkit.getOfflinePlayer(ConfigValues.getScoreboardPlayers()));
			    players.setScore(Bukkit.getOnlinePlayers().length);
			    p.setScoreboard(board);
			    Main.vars.boards.put(p.getName(), new ScoreboardHolder(board, true));
			}
		});
	}
}
