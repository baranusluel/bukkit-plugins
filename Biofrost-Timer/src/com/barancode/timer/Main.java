package com.barancode.timer;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Main extends JavaPlugin{
	ScoreboardManager boardman;
	Scoreboard board = null;
	BukkitScheduler scheduler;
	boolean exists = false;
	int time = 0;
	boolean active = false;
	String timeformat;
	String players;
	HashSet<UUID> disabled = new HashSet<UUID>();
	
    Runnable runnable = new Runnable(){
        public void run(){
			if (active){
				if (time > 0) time--;
				else {
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("end-broadcast")));
					scheduler.cancelAllTasks();
					return;
				}
			}
	  		updateBoard();
        }
    };
	
	public void onEnable(){
		saveDefaultConfig();
		time = getConfig().getInt("time");
		timeformat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("time-format"));
		players = ChatColor.translateAlternateColorCodes('&', getConfig().getString("players"));
		scheduler = Bukkit.getScheduler();
		scheduler.scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				boardman = Bukkit.getScoreboardManager();
			}
		});
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
			    new Thread(runnable).start();
			}
		}, 20, 20);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player p = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("timer")){
			if (disabled.contains(p.getUniqueId())){
				disabled.remove(p.getUniqueId());
				p.sendMessage(ChatColor.GOLD + "You have toggled the timer on");
			} else {
				disabled.add(p.getUniqueId());
				p.setScoreboard(boardman.getNewScoreboard());
				p.sendMessage(ChatColor.GOLD + "You have toggled the timer off");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("start")){
			if (!active){
				active = true;
				p.sendMessage(ChatColor.GOLD + "You have started the timer");
			} else {
				p.sendMessage(ChatColor.RED + "The timer is already running");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("pause")){
			if (active){
				active = false;
				p.sendMessage(ChatColor.GOLD + "You have paused the timer");
			} else {
				p.sendMessage(ChatColor.RED + "The timer was already paused");
			}
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void updateBoard(){
		Objective objective;
		if (exists){
			objective = board.getObjective(DisplaySlot.SIDEBAR);
		} else {
		    exists = true;
			board = boardman.getMainScoreboard();
			objective = board.registerNewObjective("scoreboard", "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
	    String timeMessage = timeformat.replaceAll("<min>", time / 60 + "").replaceAll("<sec>", time % 60 + "");
	    int spaces = (16 - timeMessage.length()) / 2;
	    for (int i = 0; i < spaces; i++){
	    	timeMessage = " " + timeMessage + " ";
	    }
	    objective.setDisplayName(timeMessage);
	    Score score = objective.getScore(Bukkit.getOfflinePlayer(players));
	    score.setScore(Bukkit.getOnlinePlayers().length);
	    for (Player p : Bukkit.getOnlinePlayers()){
	    	if (!disabled.contains(p.getUniqueId())) p.setScoreboard(board);
	    }
	}
}
