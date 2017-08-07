package com.barancode.mc.votelistener;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.barancode.mc.db.TokenDatabase;
import com.barancode.mc.db.UUIDDatabase;
import com.barancode.mc.db.VoteDatabase;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class Main extends JavaPlugin implements Listener{
	
	TokenDatabase tokendb;
	UUIDDatabase uuiddb;
	VoteDatabase votedb;
	boolean dbenabled = false;
	
	public void onEnable(){
		uuiddb = new UUIDDatabase();
		tokendb = new TokenDatabase();
		votedb = new VoteDatabase();
		
		saveDefaultConfig();
		if (getConfig().getBoolean("is-hub")){
			dbenabled = true;
		}
		
		getServer().getPluginManager().registerEvents(this, this);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				Calendar cal = Calendar.getInstance();
				int month = cal.get(Calendar.MONTH) + 1;
				if (month != getConfig().getInt("month")){
					votedb.resetVotes();
					getConfig().set("month", month);
				}
			}
		}, 0L, 60 * 60 * 20L);
	}
	
    @SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
    	Vote vote = event.getVote();
    	
    	if (dbenabled){
    		UUID uuid = uuiddb.getUUID(vote.getUsername());
    		tokendb.addAmount(uuid, 20);
    		votedb.addVote(uuid);
    	}
    	
    	if (Bukkit.getOfflinePlayer(vote.getUsername()).isOnline()){
    		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("broadcast").replaceAll("<player>", vote.getUsername()).replaceAll("<source>", vote.getServiceName())));
    		Bukkit.getPlayer(vote.getUsername()).sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("thanks")));
    	}
    }
}
