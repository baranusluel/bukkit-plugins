package com.barancode.buildguess;

import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardHolder {
	Scoreboard board = null;
	boolean coinless = false;
	public ScoreboardHolder(Scoreboard board, boolean coinless){
		this.board = board;
		this.coinless = coinless;
	}
}
