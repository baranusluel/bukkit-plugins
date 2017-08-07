package com.barancode.contest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.Metrics;

public class Main extends JavaPlugin{
	
	public static Logger log;
	public static Main instance;
	{
		instance = this;
	}
	private BCDatabase db;
	
	public void onEnable(){
		log.info("Enabling BuildingContest..");
		
		saveDefaultConfig();
		log = getLogger();
		if (getConfig().getBoolean("mysql.enabled")){
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (Exception e) {
				getLogger().warning("MySQL JDBC driver missing!");
				Bukkit.getPluginManager().disablePlugin(this);
			}
			BCDatabase.setDatabase(getConfig().getString("mysql.database"));
			BCDatabase.setHost(getConfig().getString("mysql.host"));
			BCDatabase.setUser(getConfig().getString("mysql.username"));
			BCDatabase.setPassword(getConfig().getString("mysql.password"));
			BCDatabase.mysql = true;
			db = new BCDatabase();
		} else {
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (Exception e) {
				getLogger().warning("MySQL JDBC driver missing!");
				Bukkit.getPluginManager().disablePlugin(this);
			}
			BCDatabase.mysql = false;
			db = new BCDatabase();
		}
		
		db.query("CREATE TABLE `contest`(`key` VARCHAR(16), `value` TEXT);");
		db.query("CREATE TABLE `contest_users`(`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(32), `username` VARCHAR(16), PRIMARY KEY (`id`));");
		
	    try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	    	getLogger().severe("Metrics for CreativeContest are not working!");
	    }
	}
	
	public void onDisable(){
		
	}
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	Player player = (Player) sender;
    	if(cmd.getName().equalsIgnoreCase("contest")){
    		if (args.length == 0){
    			sender.sendMessage(ChatColor.AQUA + "----Building Contest by BaranCODE----");
    			sender.sendMessage(ChatColor.AQUA + "|| " + ChatColor.GOLD + "/contest submit  " + ChatColor.YELLOW + "- Submit your build");
    			sender.sendMessage(ChatColor.RED + "IMPORTANT: Use the submit command while looking at your submission");
    			sender.sendMessage(ChatColor.BLUE + "Topic of the contest: " + ChatColor.AQUA + getTopic());
    			sender.sendMessage(ChatColor.BLUE + "Status of the contest: " + ChatColor.AQUA + getStatus());
    			sender.sendMessage(ChatColor.ITALIC + "Note: When the points are being calculated, if there is a tie, the person that submitted earlier will win");
    			sender.sendMessage(ChatColor.AQUA + "---------------------------------");
    			return true;
    		}
    		if (args.length == 1){
				if (args[0].equalsIgnoreCase("submit")){
					for (int count = 1; count <= customconfig.getCustomConfig().getInt("submissioncount"); count++) {
						if (customconfig.getCustomConfig().getBoolean("submitters." + player.getName())) {
							resubmit(player, count);
							player.sendMessage(ChatColor.AQUA + "You have successfully resubmitted");
							return true;
						}
					}
					if (this.getConfig().getInt("status") == 1){
						submit(player);
						player.sendMessage(ChatColor.AQUA + "You have successfully submitted your project");
						return true;
					}
					player.sendMessage(ChatColor.RED + "You can not submit right now");
					return true;
	    		}
    		}
    	}
    	if(cmd.getName().equalsIgnoreCase("review")){
    		if (args.length == 0){
    			sender.sendMessage(ChatColor.AQUA + "----Building Contest by BaranCODE----");
    			sender.sendMessage(ChatColor.AQUA + "| " + ChatColor.GOLD + "/review random  " + ChatColor.YELLOW + "- Teleport to a submission");
    			sender.sendMessage(ChatColor.AQUA + "| " + ChatColor.GOLD + "/review vote <submission ID> <1 to 10>  " + ChatColor.YELLOW + "- Vote on how good that submission is, over 10");
    			sender.sendMessage(ChatColor.AQUA + "---------------------------------");
    			return true;
    		}
    		if (args.length == 1){
				if (args[0].equalsIgnoreCase("random")){
					Location loc = getRandomSubmission(player);
					if (loc != null) player.teleport(loc);
	    			return true;
	    		}
    		}
    		if (args.length == 3){
				if (args[0].equalsIgnoreCase("vote")){
					if (this.getConfig().getInt("status") == 2) {
						if (!customconfig.getCustomConfig().getBoolean("submissions." + Integer.parseInt(args[1]) + ".DoNotTouchThis")) {
							player.sendMessage(ChatColor.RED + "That ID isn't valid");
							return true;
						}
						if (customconfig.getCustomConfig().getBoolean("submissions." + Integer.parseInt(args[1]) + ".voters." + player.getName())) {
							player.sendMessage(ChatColor.RED + "You already voted for that person");
							return true;
						}
						if (customconfig.getCustomConfig().getString("submissions." + Integer.parseInt(args[1]) + ".name") == player.getName()) {
							player.sendMessage(ChatColor.RED + "You can't vote for yourself, you CHEATER!");
							return true;
						}
						
						int newpoints = customconfig.getCustomConfig().getInt("submissions." + Integer.parseInt(args[1]) + ".points") + Integer.parseInt(args[2]);
						customconfig.getCustomConfig().set("submissions." + Integer.parseInt(args[1]) + ".points", newpoints);
						
						int newvotecount = customconfig.getCustomConfig().getInt("submissions." + Integer.parseInt(args[1]) + ".votecount") + 1;
						customconfig.getCustomConfig().set("submissions." + Integer.parseInt(args[1]) + ".votecount", newvotecount);
						
						customconfig.getCustomConfig().set("submissions." + Integer.parseInt(args[1]) + ".voters." + player.getName(), true);
		    			customconfig.saveCustomConfig();
		    			player.sendMessage(ChatColor.AQUA + "You gave " + customconfig.getCustomConfig().getString("submissions." + Integer.parseInt(args[1]) + ".name") + " " + Integer.parseInt(args[2]) + " out of 10");
		    			
		    			return true;
					}
					player.sendMessage(ChatColor.RED + "You can not vote for the submissions yet");
					return true;
	    		}
    		}
    	}
    	if(cmd.getName().equalsIgnoreCase("contestreload")){
    		reloadConfig();
    		sender.sendMessage(ChatColor.AQUA + "Plugin reloaded");
    		return true;
    	}
    	if(cmd.getName().equalsIgnoreCase("contestadmin")){
    		if (args.length == 0){
    			sender.sendMessage(ChatColor.AQUA + "----Building Contest by BaranCODE----");
    			sender.sendMessage(ChatColor.AQUA + "| " + ChatColor.GOLD + "/contestadmin delete <submission ID>  " + ChatColor.YELLOW + "- Delete someone's submission");
    			sender.sendMessage(ChatColor.AQUA + "| " + ChatColor.GOLD + "/contestadmin topic <Topic of contest>  " + ChatColor.YELLOW + "- Set the topic");
    			sender.sendMessage(ChatColor.AQUA + "| " + ChatColor.GOLD + "/contestadmin status <Status of contest>  " + ChatColor.YELLOW + "- Set the status");
    			sender.sendMessage(ChatColor.YELLOW + "Status will be 0 for 'no contest', 1 for 'contest running', 2 for 'submissions being reviewed'");
    			sender.sendMessage(ChatColor.RED + "IMPORTANT: Setting this to 0 will reset the contest and delete all data from the last contest");
    			sender.sendMessage(ChatColor.AQUA + "| " + ChatColor.GOLD + "/contestadmin settime <Time until contest ends, in minutes>  " + ChatColor.YELLOW + "- Set when the contest will change from status 1 to 2");
    			sender.sendMessage(ChatColor.AQUA + "| " + ChatColor.GOLD + "/contestadmin calculatefirst  " + ChatColor.YELLOW + "- Calculates who is first");
    			sender.sendMessage(ChatColor.RED + "IMPORTANT: Run this after the voting proccess is done");
    			sender.sendMessage(ChatColor.AQUA + "---------------------------------");
    			return true;
    		} else if (args.length == 1) {
        		if (args[0].equalsIgnoreCase("calculatefirst")){
        			int topint = 0;
        			String topstring = "";
        			for (int i = 1; i <= customconfig.getCustomConfig().getInt("submissioncount"); i++){
        				int points = customconfig.getCustomConfig().getInt("submissions." + i + ".points") / customconfig.getCustomConfig().getInt("submissions." + i + ".votecount");
        				if (points > topint) {
        					topint = points;
        					topstring = customconfig.getCustomConfig().getString("submissions." + i + ".name");
        				}
        			}
        			player.sendMessage(ChatColor.AQUA + topstring + " is in first place!");
        			return true;
        		}
    		}
    		else if (args.length == 2){
    			if (args[0].equalsIgnoreCase("settime")){
        			customconfig.getCustomConfig().set("remainingminutes", Integer.parseInt(args[1]));
        			customconfig.saveCustomConfig();
        			final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        	        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
        	            @Override
        	            public void run(){
        	            	int i = customconfig.getCustomConfig().getInt("remainingminutes");
        	            	i--;
        	            	if (i > 0){
            	            	customconfig.getCustomConfig().set("remainingminutes", i);
            	            	customconfig.saveCustomConfig();
        	            	} else {
        	            		customconfig.getCustomConfig().set("remainingminutes", "");

                				getConfig().set("status", 2);
                				saveConfig();
                				scheduler.cancelAllTasks();
        	            	}
        	            }
        	        }, 60* 20L, 60 * 20L);
        	    	return true;
        		}
        		if (args[0].equalsIgnoreCase("status")){
        			String statussentence = "";
        			if (args[1].equalsIgnoreCase("0")){
        				this.getConfig().set("topic", "None");
        				this.getConfig().set("status", 0);
        				customconfig.getCustomConfig().set("submissioncount", 0);
        				customconfig.getCustomConfig().set("submissions", "");
        				customconfig.getCustomConfig().set("submitters", "");
        				statussentence = "There is no available contest";
        			}
        			if (args[1].equalsIgnoreCase("1")) statussentence = "There is a contest, open for submissions";
        			if (args[1].equalsIgnoreCase("2")) statussentence = "The submissions from the last contest are being reviewed";
        			player.sendMessage(ChatColor.AQUA + "Set status to: '" + statussentence + "'");
        			this.getConfig().set("status", Integer.parseInt(args[1]));
        		    this.saveConfig();
        		    customconfig.saveCustomConfig();
        			return true;
        		}
    			if (args[0].equalsIgnoreCase("delete")){
    				if(!player.hasPermission("contest.delete")) {
    					player.sendMessage(ChatColor.RED + "You don't have permission!");
    					return true;
    				}
    				if (customconfig.getCustomConfig().getBoolean("submissions." + Integer.parseInt(args[1]) + ".DoNotTouchThis")){
	    				int i = customconfig.getCustomConfig().getInt("submissioncount");
	    				i--;
	    				customconfig.getCustomConfig().set("submissioncount", i);
	    				String name = customconfig.getCustomConfig().getString("submissions." + Integer.parseInt(args[1]) + ".name");
	    				customconfig.getCustomConfig().set("submissions." + Integer.parseInt(args[1]), "");
	    				customconfig.getCustomConfig().set("submitters." + name, "");
	    				customconfig.saveCustomConfig();
	    				player.sendMessage(ChatColor.AQUA + "You have deleted that submission");
	    				Bukkit.getPlayer(name).sendMessage(ChatColor.AQUA + player.getName() + " has deleted your submission");
	    				if (this.getConfig().getBoolean("deletionlog")){
	    					logToFile(player.getName() + " has deleted a submission by " + name);
	    				}
	    				return true;
    				} else {
    					player.sendMessage(ChatColor.RED + "There is no submission with that ID");
    					return true;
    				}
    			}
    		}
    		
    		if (args[0].equalsIgnoreCase("topic")){
    			String finalString = "";
    			for (int i = 1; i < args.length; i++) {
    			    finalString += args[i] + ' ';
    			}
    			finalString = finalString.trim();
    			player.sendMessage(ChatColor.AQUA + "Set topic to: '" + finalString + "'");
    			this.getConfig().set("topic", finalString);
    		    this.saveConfig();
    			return true;
    		}
    	}
    	return false;
    }
    
    public void submit(Player p) {
		int i = customconfig.getCustomConfig().getInt("submissioncount");
		i++;
		customconfig.getCustomConfig().set("submissioncount", i);
		Location loc = p.getLocation();
		customconfig.getCustomConfig().set("submissions." + i + ".world", loc.getWorld().getName().toString());
		customconfig.getCustomConfig().set("submissions." + i + ".x", loc.getX());
		customconfig.getCustomConfig().set("submissions." + i + ".y", loc.getY());
		customconfig.getCustomConfig().set("submissions." + i + ".z", loc.getZ());
		customconfig.getCustomConfig().set("submissions." + i + ".yaw", loc.getYaw());
		customconfig.getCustomConfig().set("submissions." + i + ".pitch", loc.getPitch());
		customconfig.getCustomConfig().set("submissions." + i + ".name", p.getName().toString());
		customconfig.getCustomConfig().set("submissions." + i + ".points", 0);
		customconfig.getCustomConfig().set("submissions." + i + ".votecount", 0);
		customconfig.getCustomConfig().set("submissions." + i + ".voters", "");
		customconfig.getCustomConfig().set("submissions." + i + ".DoNotTouchThis", true);
		customconfig.getCustomConfig().set("submitters." + p.getName(), true);
		customconfig.saveCustomConfig();
    }
    
    public void resubmit(Player p, int i) {
		Location loc = p.getLocation();
		customconfig.getCustomConfig().set("submissions." + i + ".world", loc.getWorld().getName().toString());
		customconfig.getCustomConfig().set("submissions." + i + ".x", loc.getX());
		customconfig.getCustomConfig().set("submissions." + i + ".y", loc.getY());
		customconfig.getCustomConfig().set("submissions." + i + ".z", loc.getZ());
		customconfig.getCustomConfig().set("submissions." + i + ".yaw", loc.getYaw());
		customconfig.getCustomConfig().set("submissions." + i + ".pitch", loc.getPitch());
		customconfig.getCustomConfig().set("submissions." + i + ".name", p.getName().toString());
		customconfig.getCustomConfig().set("submissions." + i + ".points", 0);
		customconfig.getCustomConfig().set("submissions." + i + ".votecount", 0);
		customconfig.getCustomConfig().set("submissions." + i + ".voters", "");
		customconfig.getCustomConfig().set("submissions." + i + ".DoNotTouchThis", true);
		customconfig.getCustomConfig().set("submitters." + p.getName(), true);
		customconfig.saveCustomConfig();
    }
    
    public String getStatus() {
    	int i = this.getConfig().getInt("status");
    	if (i == 0) return "There is no available contest";
    	if (i == 1) return "There is a contest, open for submissions";
    	if (i == 2) return "The submissions from the last contest are being reviewed";
    	return "";
    }
    
    public String getTopic() {
    	String s = this.getConfig().getString("topic");
    	return s;
    }
    
    public Location getRandomSubmission(Player player) {
    	Random random = new Random();
    	if (customconfig.getCustomConfig().getInt("submissioncount") == 0) {
    		player.sendMessage(ChatColor.RED + "There aren't any submissions");
    		return null;
    	}
    	int i;
    	i = random.nextInt(customconfig.getCustomConfig().getInt("submissioncount"));
    	i++;
        World w = Bukkit.getWorld(customconfig.getCustomConfig().getString("submissions." + i + ".world"));
        double x = customconfig.getCustomConfig().getDouble("submissions." + i + ".x");
        double y = customconfig.getCustomConfig().getDouble("submissions." + i + ".y");
        double z = customconfig.getCustomConfig().getDouble("submissions." + i + ".z");
        float yaw = (float)customconfig.getCustomConfig().getDouble("submissions." + i + ".yaw");
        float pitch = (float)customconfig.getCustomConfig().getDouble("submissions." + i + ".pitch");
        Location loc = new Location(w, x, y, z, yaw, pitch);
        player.sendMessage(ChatColor.GREEN + "Owner: " + customconfig.getCustomConfig().getString("submissions." + i + ".name") + ", ID: " + i);
        return loc;
    }
    
    public void logToFile(String message){
    	try {
    		File saveTo = new File(getDataFolder(), "DeletionLog.log");
    		if (!saveTo.exists()){
    			saveTo.createNewFile();
    		}
    		FileWriter fw = new FileWriter(saveTo, true);
    		PrintWriter pw = new PrintWriter(fw);
    		pw.println(message);
    		pw.close();
    		fw.close();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
}
