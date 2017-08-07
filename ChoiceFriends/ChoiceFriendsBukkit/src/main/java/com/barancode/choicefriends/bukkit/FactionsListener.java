package com.barancode.choicefriends.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.massivecore.event.EventMassiveCorePlayerToRecipientChat;

public class FactionsListener implements Listener{
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onMCoreChat(EventMassiveCorePlayerToRecipientChat e){
		if (e.getFormat().startsWith("[CR] ")) e.setFormat(ChatColor.GREEN + "[CR] " + ChatColor.GRAY + ChatColor.stripColor(e.getFormat().substring(5)));
		e.setCancelled(true);
	}
}
