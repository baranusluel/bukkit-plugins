package com.barancode.blockprotection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor{
	
	Main plugin;
	
	public Commands(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("accept")){
			plugin.pendingRules.remove(sender.getName());
			plugin.players.write(((Player)sender).getUniqueId());
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.ruleConfig.getCustomConfig().getString("accepted")));
			((Player)sender).setWalkSpeed(0.2F);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("getinfluence")){
			if (args.length != 1) return false;
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("get-influence")
					.replaceAll("<amount>", plugin.dbman.getAmount(args[0]) + "").replaceAll("<player>", args[0])));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setinfluence")){
			if (args.length != 2) return false;
			int amount = 0;
			try {
				amount = Utils.getInt(args[1]);
			} catch (Exception e) {
				return false;
			}
			if (amount > 500) return false;
			plugin.dbman.setAmount(args[0], amount);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("get-influence")
					.replaceAll("<amount>", amount + "").replaceAll("<player>", args[0])));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("override")){
			if (plugin.override.contains(sender.getName())){
				plugin.override.remove(sender.getName());
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("override-off")));
				return true;
			} else {
				plugin.override.add(sender.getName());
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("override-on")));
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("score")){
			if (plugin.scoreboardOff.contains(sender.getName())){
				plugin.scoreboardOff.remove(sender.getName());
				plugin.boardman.updateScoreboard((Player)sender);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("scoreboard-on")));
			} else {
				plugin.scoreboardOff.add(sender.getName());
				plugin.boardman.updateScoreboard((Player)sender);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("scoreboard-off")));
			}
			return true;
		}
		return false;
	}

}
