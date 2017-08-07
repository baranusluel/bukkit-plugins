package main.java.com.barancode.buildguess;

import java.util.HashMap;

import org.bukkit.Location;

public class Variables {
	// This class with variables is used so that all the
	// variables can be reset and re-initalized when the
	// plugin is "restarting" itself after a match
	boolean ingame = false;
	boolean countdown = false;
	int countdownTime = ConfigValues.getFrozenTime();
	boolean kicking = false;
	String builder = "";
	HashMap<String, Integer> buildRegion = ConfigValues.getBuildRegion();
	HashMap<String, Integer> rollbackRegion = ConfigValues.getRollbackRegion();
	boolean gg = false;
	String word = "";
	int matchTime = ConfigValues.getMatchTime();
	Location builderLocation;
	int builderAFK;
	HashMap<String, Integer> coins = new HashMap<String, Integer>();
	HashMap<String, Integer> hintsLeft = new HashMap<String, Integer>();
	HashMap<String, Long> lastChat = new HashMap<String, Long>();
	HashMap<String, ScoreboardHolder> boards = new HashMap<String, ScoreboardHolder>();
}
