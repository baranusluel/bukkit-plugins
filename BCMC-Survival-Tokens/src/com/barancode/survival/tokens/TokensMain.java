package com.barancode.survival.tokens;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.barancode.mc.db.TokenDatabase;
import com.barancode.mc.db.UUIDDatabase;
import com.barancode.mc.survival.Main;

public class TokensMain extends JavaPlugin{
	TokenDatabase td = new TokenDatabase();
	UUIDDatabase ud = new UUIDDatabase();
	Main survival;
	
	public void onEnable(){
		survival = Main.getPlugin();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "Your tokens: " + ChatColor.AQUA + td.getAmount(((Player)sender).getUniqueId()));
			sender.sendMessage(ChatColor.GREEN + "/token get <amount> " + ChatColor.DARK_GREEN + "- Sell your Survival power to get tokens (1 power = 1 token)");
			sender.sendMessage(ChatColor.GREEN + "/token sell <amount> " + ChatColor.DARK_GREEN + "- Sell your tokens to get Survival power (1 token = 1 power)");
			sender.sendMessage(ChatColor.GREEN + "/token send <player> <amount> " + ChatColor.DARK_GREEN + "- Send someone an amount of your tokens");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("send")){
			UUID uuid = ud.getUUID(args[1]);
			if (uuid == null || uuid.toString().equalsIgnoreCase("")){
				sender.sendMessage(ChatColor.RED + "A player by that name hasn't played on BCMC");
				return true;
			}
			int amount;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e){
				sender.sendMessage(ChatColor.RED + "Please enter a valid amount");
				return true;
			}
			if (td.takeAmount(((Player)sender).getUniqueId(), amount)){
				td.addAmount(uuid, amount);
				sender.sendMessage(ChatColor.GOLD + "You have sent " + amount + " tokens to " + args[1]);
				if (Bukkit.getOfflinePlayer(args[1]).isOnline()) Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GOLD + sender.getName() + " has sent you " + amount + " tokens");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have that many tokens!");
				return true;
			}
		} else if (args.length == 2){
			Player p = (Player)sender;
			UUID id = p.getUniqueId();
			if (args[0].equalsIgnoreCase("get")){
				int power = survival.db.getPower(id);
				int amount;
				try {
					amount = Integer.parseInt(args[1]);
				} catch (Exception e){
					p.sendMessage(ChatColor.RED + "Please enter a valid amount!");
					return true;
				}
				if (amount > power){
					p.sendMessage(ChatColor.RED + "You don't have that much power!");
					return true;
				}
				survival.db.setPower(id, power - amount);
				td.addAmount(id, amount);
				p.sendMessage(ChatColor.GOLD + "You have successfully exchanged " + amount + " of your power for tokens");
				return true;
			} else if (args[0].equalsIgnoreCase("sell")){
				int tokens = td.getAmount(id);
				int power = survival.db.getPower(id);
				int amount;
				try {
					amount = Integer.parseInt(args[1]);
				} catch (Exception e){
					p.sendMessage(ChatColor.RED + "Please enter a valid amount!");
					return true;
				}
				if (amount > tokens){
					p.sendMessage(ChatColor.RED + "You don't have that many tokens!");
					return true;
				}
				if (power + amount > 200){
					p.sendMessage(ChatColor.RED + "You aren't allowed to have more than 200 power!");
					return true;
				}
				survival.db.setPower(id, power + amount);
				td.takeAmount(id, amount);
				p.sendMessage(ChatColor.GOLD + "You have successfully exchanged " + amount + " of your tokens for power");
				return true;
			}
		}
		return false;
	}
}
