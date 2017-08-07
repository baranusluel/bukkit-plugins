package com.barancode.blockprotection.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.barancode.blockprotection.Main;

public class BoardManager {
	Main plugin;
	
	public BoardManager(Main plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void updateScoreboard(Player p){
		if (plugin.scoreboardOff.contains(p.getName())){
			p.setScoreboard(plugin.manager.getMainScoreboard());
			plugin.scoreboards.remove(p.getName());
			return;
		}
		Scoreboard board = plugin.scoreboards.get(p.getName());
		if (board != null){
		    Objective objective = board.getObjective("scoreboard");
		    Score influence = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes('&', 
					plugin.boardConfig.getCustomConfig().getString("influence"))));
		    influence.setScore(plugin.dbman.getAmount(p.getName()));
		    p.setScoreboard(board);
			plugin.scoreboards.put(p.getName(), board);
		} else {
			board = plugin.manager.getNewScoreboard();
		    Objective objective = board.registerNewObjective("scoreboard", "dummy");
		    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		    objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.boardConfig.getCustomConfig().getString("title")));
		    Score influence = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes('&', 
					plugin.boardConfig.getCustomConfig().getString("influence"))));
		    influence.setScore(plugin.dbman.getAmount(p.getName()));
		    Score info = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes('&', 
					plugin.boardConfig.getCustomConfig().getString("info"))));
		    info.setScore(0);
		    p.setScoreboard(board);
			plugin.scoreboards.put(p.getName(), board);
		}
	}
}
