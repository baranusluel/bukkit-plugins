package GobBob.games.src;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class Group
{
  String id;
  String displayName;
  String prefix;
  public static final Group empty = new Group("empty").setDisplayName("Empty").setPrefix("");

  public static final Group[] groups = { 
    new Group("admin").setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Admin").setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "<ADMIN>" + ChatColor.RESET).setMembers(
    new String[] { "Some1" })
    .setHat(new Hat(EntityType.FALLING_BLOCK).addData(new GameData("blockID", Integer.valueOf(22)))), 
    new Group("owner").setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Owner").setPrefix(ChatColor.GOLD + "" + ChatColor.BOLD + "<OWNER>" + ChatColor.RESET).setMembers(
    new String[] { "Harmiox" })
    .setHat(new Hat(EntityType.FALLING_BLOCK).addData(new GameData("blockID", Integer.valueOf(57)))), 
    new Group("developer").setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Developer").setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + "<DEVELOPER>" + ChatColor.RESET).setMembers(
    new String[] { "iwoplaza" })
    .setHat(new Hat(EntityType.ENDER_CRYSTAL)), 
    new Group("helper").setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Helper").setPrefix(ChatColor.YELLOW + "" + ChatColor.BOLD + "<HELPER>" + ChatColor.RESET).setMembers(
    new String[] { "jbena004" })
    .setHat(new Hat(EntityType.FALLING_BLOCK).addData(new GameData("blockID", Integer.valueOf(41)))) };
  Hat hat;
  String[] members;

  public Group(String par1)
  {
    this.id = par1;
  }

  public Group setMembers(String[] par1) {
    this.members = par1;
    return this;
  }

  public Group setDisplayName(String par1) {
    this.displayName = par1;
    return this;
  }

  public Group setPrefix(String par1) {
    this.prefix = par1;
    return this;
  }

  public Group setHat(Hat par1) {
    this.hat = par1;
    return this;
  }

  public String getID() {
    return this.id;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public String[] getMembers() {
    return this.members;
  }

  public boolean isPlayerMember(String par1) {
    for (int i = 0; i < this.members.length; i++) {
      if (this.members[i].equalsIgnoreCase(par1)) {
        return true;
      }
    }
    return false;
  }

  public static Group getPlayerGroup(String player) {
    for (int i = 0; i < groups.length; i++) {
      if (groups[i].isPlayerMember(player)) {
        return groups[i];
      }
    }

    return empty;
  }
}