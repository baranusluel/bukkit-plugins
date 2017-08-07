package com.barancode.buildguess;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ConfigValues {
	static Random random = new Random();
	
	// This class is used to make getting values from the config easier, as well
	// as automatically doing the formatting and in some cases, some of the logic
	public static int getMaxPlayers(){
		return Main.config.getInt("max-players");
	}
	public static int getMinPlayers(){
		return Main.config.getInt("min-players");
	}
	public static String getIngameMessage(){
		return Utils.color(Main.config.getString("in-game"));
	}
	public static String getEndingMessage(){
		return Utils.color(Main.config.getString("ending"));
	}
	public static Location getSpawn(){
		return new Location(Bukkit.getWorld("world"), Main.config.getDouble("spawn.x"), Main.config.getDouble("spawn.y"), Main.config.getDouble("spawn.z"), (float)Main.config.getDouble("spawn.yaw"), (float)Main.config.getDouble("spawn.pitch"));
	}
	public static Location getBuildSpawn(){
		return new Location(Bukkit.getWorld("world"), Main.config.getDouble("buildspawn.x"), Main.config.getDouble("buildspawn.y"), Main.config.getDouble("buildspawn.z"), (float)Main.config.getDouble("buildspawn.yaw"), (float)Main.config.getDouble("buildspawn.pitch"));
	}
	public static int getFrozenTime(){
		return Main.config.getInt("frozen-time");
	}
	public static int getMatchTime(){
		return Main.config.getInt("match-time");
	}
	public static int getFinishedTime(){
		return Main.config.getInt("finished-time");
	}
	public static String getScoreboardTime(){
		return Utils.color(Main.config.getString("scoreboard.time"));
	}
	public static String getScoreboardCoins(){
		return Utils.color(Main.config.getString("scoreboard.coins"));
	}
	public static String getScoreboardPlayers(){
		return Utils.color(Main.config.getString("scoreboard.players"));
	}
	public static String getScoreboardHints(){
		return Utils.color(Main.config.getString("scoreboard.hints"));
	}
	public static String getJoinMessage(String username){
		return Utils.color(Main.config.getString("join")).replaceAll("<player>", username).replaceAll("<count>", Bukkit.getOnlinePlayers().length + "").replaceAll("<max>", getMaxPlayers() + "");
	}
	public static String getLeaveMessage(String username){
		return Utils.color(Main.config.getString("leave")).replaceAll("<player>", username).replaceAll("<count>", Bukkit.getOnlinePlayers().length - 1 + "").replaceAll("<max>", getMaxPlayers() + "");
	}
	public static String getCountdownStart(){
		return Utils.color(Main.config.getString("countdown.start")).replaceAll("<time>", getFrozenTime() + "");
	}
	public static String getCountdownSoon(){
		return Utils.color(Main.config.getString("countdown.soon")).replaceAll("<time>", Main.vars.countdownTime + "");
	}
	public static String getCountdownNotEnough(){
		return Utils.color(Main.config.getString("countdown.not-enough"));
	}
	public static String getCountdownEnd(){
		return Utils.color(Main.config.getString("countdown.end"));
	}
	public static HashMap<String, Integer> getBuildRegion(){
		HashMap<String, Integer> keys = new HashMap<String, Integer>();
		for (String s : Main.config.getConfigurationSection("build-region").getKeys(false)){
			keys.put(s, Main.config.getInt("build-region." + s));
		}
		return keys;
	}
	public static HashMap<String, Integer> getRollbackRegion(){
		HashMap<String, Integer> keys = new HashMap<String, Integer>();
		for (String s : Main.config.getConfigurationSection("rollback-region").getKeys(false)){
			keys.put(s, Main.config.getInt("rollback-region." + s));
		}
		return keys;
	}
	public static String getRandomTopic(){
		Set<String> keys = Main.config.getConfigurationSection("topics").getKeys(false);
		return (String) keys.toArray()[random.nextInt(keys.size())];
	}
	public static String getRandomTopic(String old){
		String newtopic = "";
		Set<String> keys = Main.config.getConfigurationSection("topics").getKeys(false);
		do {
			newtopic = (String) keys.toArray()[random.nextInt(keys.size())];
		} while (newtopic.equals(old));
		return newtopic;
	}
	public static String getHint(String topic){
		return Utils.color(Main.config.getString("topics." + topic));
	}
	public static String getBuilderTalk(){
		return Utils.color(Main.config.getString("builder-talk"));
	}
	public static String getTopicMessage(){
		return Utils.color(Main.config.getString("topic"));
	}
	public static String getHintMessage(){
		return Utils.color(Main.config.getString("hint"));
	}
	public static String getItemName(String item){
		return Utils.color(Main.config.getString(item + ".name"));
	}
	public static List<String> getItemLore(String item){
		return Utils.color(Main.config.getStringList(item + ".lore"));
	}
	public static String getItemType(String item){
		return Main.config.getString(item + ".type");
	}
	public static String getMatchEnd(){
		return Utils.color(Main.config.getString("match-end"));
	}
	public static String getBuilderSelected(){
		return Utils.color(Main.config.getString("builder-selected"));
	}
	public static String getGuessed(){
		return Utils.color(Main.config.getString("guessed"));
	}
	public static String getCoinsMessage(){
		return Utils.color(Main.config.getString("coins"));
	}
	public static int getDefaultCoins(){
		return Main.config.getInt("default-coins");
	}
	public static String getBuilderOutside(){
		return Utils.color(Main.config.getString("builder-outside"));
	}
	public static String getCommandMessage(){
		return Utils.color(Main.config.getString("command-blacklist-message"));
	}
	public static List<String> getCommands(){
		return Utils.toLowercase(Main.config.getStringList("command-blacklist"));
	}
	public static int getGuesserReward(){
		return Main.config.getInt("guesser-reward");
	}
	public static int getBuilderReward(){
		return Main.config.getInt("builder-reward");
	}
	public static String getWaitingMotd(){
		return Utils.color(Main.config.getString("motd.waiting"));
	}
	public static String getLobbyingMotd(){
		return Utils.color(Main.config.getString("motd.lobbying"));
	}
	public static String getIngameMotd(){
		return Utils.color(Main.config.getString("motd.ingame"));
	}
	public static String getFinishingMotd(){
		return Utils.color(Main.config.getString("motd.finishing"));
	}
	public static String getAFKKick(){
		return Utils.color(Main.config.getString("afk-kick"));
	}
	public static String getAFKKickBroadcast(){
		return Utils.color(Main.config.getString("afk-kick-broadcast"));
	}
	public static String getJoinMotd(){
		return Utils.color(Main.config.getString("join-motd"));
	}
	public static int getHubSlot(){
		return Main.config.getInt("hub-item.slot");
	}
	public static int getHintSlot(){
		return Main.config.getInt("hint-item.slot");
	}
	public static int getBookSlot(){
		return Main.config.getInt("book.slot");
	}
	public static String getGGCountdown(){
		return Utils.color(Main.config.getString("gg-countdown"));
	}
	public static String getMatchCountdown(){
		return Utils.color(Main.config.getString("match-countdown"));
	}
	public static String getWinnerMessage(){
		return Utils.color(Main.config.getString("winner"));
	}
	public static int getTipAmount(){
		return Main.config.getInt("tips");
	}
	public static int getVIPTipAmount(){
		return Main.config.getInt("tips-vip");
	}
	public static int getChatCooldown(){
		return Main.config.getInt("chat-cooldown");
	}
	public static String getMaxTipsMessage(){
		return Utils.color(Main.config.getString("max-tips"));
	}
	public static String getTipsLeftMessage(){
		return Utils.color(Main.config.getString("tips-left"));
	}
	public static String getChatCooldownMessage(){
		return Utils.color(Main.config.getString("chat-cooldown-message"));
	}
	public static int getAFKTime(){
		return Main.config.getInt("afk-time");
	}
}
