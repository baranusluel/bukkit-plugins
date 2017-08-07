package com.barancode.mc.survival;

import java.util.List;

import org.bukkit.Bukkit;

public class Announcements {
	Main plugin;
	List<String> list;
	int index = 0;
	public Announcements(Main plugin2){
		plugin = plugin2;
		list = plugin.getConfig().getStringList("announcements");
        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run(){
				Bukkit.broadcastMessage(Utils.replace(list.get(index)));
				index++;
				if (index == list.size()) index = 0;
            }
        }, 0L, 120 * 20L);
	}
}
