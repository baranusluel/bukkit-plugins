package me.empirewand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Start extends JavaPlugin
  implements Listener
{
  public Start plugin;
  public int HIGHEST_SPELL_NUMBER = Spells.values().length;
  public int HIGHEST_SPELL_NUMBER_BLOODMAGIC = BloodmagicSpells.values().length;
  Map<String, Block> Blocks = new HashMap();
  public Dagger Dagger = new Dagger(this);
  public BloodBlock BloodBlock = new BloodBlock(this);
  public BloodWave BloodWave = new BloodWave(this);
  public Capture Capture = new Capture(this);
  public Comet Comet = new Comet(this);
  public EnderSource EnderSource = new EnderSource(this);
  public Escape Escape = new Escape(this);
  public Explode Explode = new Explode(this);
  public ExplosionWave ExplosionWave = new ExplosionWave(this);
  public Explosive Explosive = new Explosive(this);
  public Flamethrower Flamethrower = new Flamethrower(this);
  public FlameWave FlameWave = new FlameWave(this);
  public Launch Launch = new Launch(this);
  public Leap Leap = new Leap(this);
  public LightningArrow LightningArrow = new LightningArrow(this);
  public Lightningstorm Lightningstorm = new Lightningstorm(this);
  public PoisonWave PoisonWave = new PoisonWave(this);
  public Confuse Confuse = new Confuse(this);
  public Spark Spark = new Spark(this);
  public LittleSpark LittleSpark = new LittleSpark(this);
  public BloodSpark BloodSpark = new BloodSpark(this);
  public GetTargets GetTargets = new GetTargets(this);

  public void onEnable() {
    this.plugin = this;
    getServer().getPluginManager().registerEvents(this, this);
    getServer().getPluginManager().registerEvents(this.Dagger, this);
    getConfig().options().copyDefaults(true);
    saveConfig();
  }

  public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
  {
    if ((command.getName().equalsIgnoreCase("empirewand")) || (command.getName().equalsIgnoreCase("ew"))) {
      if ((sender instanceof Player)) {
        if (args.length == 0) {
          sender.sendMessage(ChatColor.RED + "Command List of the EmpireWand");
          sender.sendMessage(ChatColor.RED + "/ew wand");
          sender.sendMessage(ChatColor.RED + "/ew dagger");
          sender.sendMessage(ChatColor.RED + "/ew bloodmagic");
          sender.sendMessage(ChatColor.RED + "/ew bind <spellname>");
          sender.sendMessage(ChatColor.RED + "/ew bind all");
          sender.sendMessage(ChatColor.RED + "/ew unbind <spellname>");
          sender.sendMessage(ChatColor.RED + "/ew unbind all");
        }
        else
        {
          Spells localSpells1;
          Spells s;
          if (args.length == 1)
          {
            Spells[] arrayOfSpells;
            int i;
            if (args[0].equalsIgnoreCase("wand")) {
              if (sender.hasPermission("empirewand.get")) {
                ItemStack wand = new ItemStack(Material.BLAZE_ROD);
                ItemMeta im = wand.getItemMeta();
                im.setLore(Arrays.asList(new String[] { getConfig().getString("Wands.latest") }));
                getConfig().set("Wands.latest", Integer.valueOf(getConfig().getInt("Wands.latest") + 1));
                im.setDisplayName(ChatColor.RED + "Empire Wand");
                wand.setDurability((short)-1);
                wand.setItemMeta(im);
                ((Player)sender).getInventory().addItem(new ItemStack[] { wand });
                ItemMeta meta = wand.getItemMeta();
                List lore = meta.getLore();
                int wandNumber = Integer.parseInt((String)lore.get(0));
                i = (arrayOfSpells = Spells.values()).length; for (localSpells1 = 0; localSpells1 < i; localSpells1++) { Spells s = arrayOfSpells[localSpells1];
                  getConfig().set("Wand." + wandNumber + ".BoundSpells." + s.getId(), Boolean.valueOf(false)); }
                saveConfig();
                sender.sendMessage(ChatColor.YELLOW + "The Empire Wand has been added to your inventory.");
              } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that!");
              }
            } else if (args[0].equalsIgnoreCase("dagger")) {
              if (sender.hasPermission("therosdagger.get")) {
                ItemStack dagger = new ItemStack(Material.RECORD_6);
                ItemMeta im = dagger.getItemMeta();
                im.setDisplayName(ChatColor.GRAY + "Theros Dagger");
                dagger.setItemMeta(im);
                ((Player)sender).getInventory().addItem(new ItemStack[] { dagger });
                sender.sendMessage(ChatColor.YELLOW + "The Theros Dagger has been added to your inventory.");
              } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that!");
              }
            } else if (args[0].equalsIgnoreCase("bloodmagic")) {
              if (sender.hasPermission("bloodmagic.get")) {
                ItemStack wand = new ItemStack(Material.NETHER_STALK);
                ItemMeta im = wand.getItemMeta();
                im.setLore(Arrays.asList(new String[] { getConfig().getString("Bloodmagic.latest") }));
                getConfig().set("Bloodmagic.latest", Integer.valueOf(getConfig().getInt("Bloodmagic.latest") + 1));
                im.setDisplayName(ChatColor.RED + "Bloodmagic");
                wand.setDurability((short)-1);
                wand.setItemMeta(im);
                ((Player)sender).getInventory().addItem(new ItemStack[] { wand });
                ItemMeta meta = wand.getItemMeta();
                List lore = meta.getLore();
                int wandNumber = Integer.parseInt((String)lore.get(0));
                i = (arrayOfSpells = Spells.values()).length; for (localSpells1 = 0; localSpells1 < i; localSpells1++) { s = arrayOfSpells[localSpells1];
                  getConfig().set("Bloodmagic." + wandNumber + ".BoundSpells." + s.getId(), Boolean.valueOf(false)); }
                saveConfig();
                sender.sendMessage(ChatColor.YELLOW + "The Bloodmagic has been added to your inventory.");
              } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that!");
              }
            } else {
              sender.sendMessage(ChatColor.RED + "Command List of the EmpireWand");
              sender.sendMessage(ChatColor.RED + "/ew wand");
              sender.sendMessage(ChatColor.RED + "/ew dagger");
              sender.sendMessage(ChatColor.RED + "/ew bloodmagic");
              sender.sendMessage(ChatColor.RED + "/ew bind <spellname>");
              sender.sendMessage(ChatColor.RED + "/ew bind all");
              sender.sendMessage(ChatColor.RED + "/ew unbind <spellname>");
              sender.sendMessage(ChatColor.RED + "/ew unbind all");
            }
          } else if (args.length == 2) {
            Player player = (Player)sender;
            Object localObject;
            if (args[0].equalsIgnoreCase("bind")) {
              if (player.hasPermission("empirewand.bind")) {
                if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.BLAZE_ROD) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Empire Wand"))) {
                  if (!args[1].equalsIgnoreCase("all")) {
                    Spells spell = Spells.byName(args[1]);
                    if (spell == null) {
                      sender.sendMessage(ChatColor.RED + "Spell not found!");
                    } else {
                      ItemMeta meta = player.getItemInHand().getItemMeta();
                      List lore = meta.getLore();
                      int wandNumber = Integer.parseInt((String)lore.get(0));
                      getConfig().set("Wand." + wandNumber + ".BoundSpells." + spell.getId(), Boolean.valueOf(true));
                      sender.sendMessage(ChatColor.GOLD + "You've bound spell: " + spell.name());
                      saveConfig();
                    }
                  } else {
                    sender.sendMessage(ChatColor.GOLD + "All spells bound!");
                    ItemMeta meta = player.getItemInHand().getItemMeta();
                    List lore = meta.getLore();
                    int wandNumber = Integer.parseInt((String)lore.get(0));
                    localSpells1 = (localObject = Spells.values()).length; for (s = 0; s < localSpells1; s++) { Spells s = localObject[s];
                      getConfig().set("Wand." + wandNumber + ".BoundSpells." + s.getId(), Boolean.valueOf(true));
                    }
                    saveConfig();
                  }
                } else if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.NETHER_STALK) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Bloodmagic")))
                  if (!args[1].equalsIgnoreCase("all")) {
                    BloodmagicSpells spell = BloodmagicSpells.byName(args[1]);
                    if (spell == null) {
                      sender.sendMessage(ChatColor.RED + "Spell not found!");
                    } else {
                      ItemMeta meta = player.getItemInHand().getItemMeta();
                      List lore = meta.getLore();
                      int wandNumber = Integer.parseInt((String)lore.get(0));
                      getConfig().set("Bloodmagic." + wandNumber + ".BoundSpells." + spell.getId(), Boolean.valueOf(true));
                      sender.sendMessage(ChatColor.GOLD + "You've bound spell: " + spell.name());
                      saveConfig();
                    }
                  } else {
                    sender.sendMessage(ChatColor.GOLD + "All spells bound!");
                    ItemMeta meta = player.getItemInHand().getItemMeta();
                    List lore = meta.getLore();
                    int wandNumber = Integer.parseInt((String)lore.get(0));
                    Spells localSpells2 = (localObject = BloodmagicSpells.values()).length; for (s = 0; s < localSpells2; s++) { BloodmagicSpells s = localObject[s];
                      getConfig().set("Bloodmagic." + wandNumber + ".BoundSpells." + s.getId(), Boolean.valueOf(true));
                    }
                    saveConfig();
                  }
              }
              else
                sender.sendMessage(ChatColor.RED + "You dont have the permission to do that!");
            }
            else if (args[0].equalsIgnoreCase("unbind")) {
              if (player.hasPermission("empirewand.unbind")) {
                if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.BLAZE_ROD) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Empire Wand"))) {
                  if (!args[1].equalsIgnoreCase("all")) {
                    Spells spell = Spells.byName(args[1]);
                    if (spell == null) {
                      sender.sendMessage(ChatColor.RED + "Spell not found!");
                    } else {
                      ItemMeta meta = player.getItemInHand().getItemMeta();
                      List lore = meta.getLore();
                      int wandNumber = Integer.parseInt((String)lore.get(0));
                      getConfig().set("Wand." + wandNumber + ".BoundSpells." + spell.getId(), Boolean.valueOf(false));
                      sender.sendMessage(ChatColor.GOLD + "You've unbound spell: " + spell.name());
                      saveConfig();
                    }
                  } else {
                    sender.sendMessage(ChatColor.GOLD + "All spells unbound!");
                    ItemMeta meta = player.getItemInHand().getItemMeta();
                    List lore = meta.getLore();
                    int wandNumber = Integer.parseInt((String)lore.get(0));
                    Spells localSpells3 = (localObject = Spells.values()).length; for (s = 0; s < localSpells3; s++) { Spells s = localObject[s];
                      getConfig().set("Wand." + wandNumber + ".BoundSpells." + s.getId(), Boolean.valueOf(false));
                    }
                    saveConfig();
                  }
                } else if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.NETHER_STALK) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Bloodmagic")))
                  if (!args[1].equalsIgnoreCase("all")) {
                    BloodmagicSpells spell = BloodmagicSpells.byName(args[1]);
                    if (spell == null) {
                      sender.sendMessage(ChatColor.RED + "Spell not found!");
                    } else {
                      ItemMeta meta = player.getItemInHand().getItemMeta();
                      List lore = meta.getLore();
                      int wandNumber = Integer.parseInt((String)lore.get(0));
                      getConfig().set("Bloodmagic." + wandNumber + ".BoundSpells." + spell.getId(), Boolean.valueOf(false));
                      sender.sendMessage(ChatColor.GOLD + "You've unbound spell: " + spell.name());
                      saveConfig();
                    }
                  } else {
                    sender.sendMessage(ChatColor.GOLD + "All spells unbound!");
                    ItemMeta meta = player.getItemInHand().getItemMeta();
                    List lore = meta.getLore();
                    int wandNumber = Integer.parseInt((String)lore.get(0));
                    Spells localSpells4 = (localObject = BloodmagicSpells.values()).length; for (s = 0; s < localSpells4; s++) { BloodmagicSpells s = localObject[s];
                      getConfig().set("Bloodmagic." + wandNumber + ".BoundSpells." + s.getId(), Boolean.valueOf(false));
                    }
                    saveConfig();
                  }
              }
              else
                sender.sendMessage(ChatColor.RED + "You dont have the permission to do that!");
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  @EventHandler
  public void Click(PlayerInteractEvent event) {
    if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      if (this.Flamethrower.FlameOn.contains(event.getPlayer().getName())) {
        this.Flamethrower.FlameOn.remove(event.getPlayer().getName());
      }
      Player player = event.getPlayer();
      if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.BLAZE_ROD) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Empire Wand"))) {
        if (event.getPlayer().hasPermission("empirewand.use")) {
          player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 119, 30);
          try {
            ItemMeta meta = player.getItemInHand().getItemMeta();
            List lore = meta.getLore();
            int wandNumber = Integer.parseInt((String)lore.get(0));
            ConfigurationSection sec = getConfig().getConfigurationSection("Wand." + wandNumber + ".BoundSpells");
            if (sec == null) {
              sec = getConfig().createSection("Wand." + wandNumber + ".BoundSpells");
            }
            int spellNumber = 0;
            try {
              spellNumber = player.getItemInHand().getDurability();
            } catch (NumberFormatException localNumberFormatException) {
            }
            int selectedSpell = -1;
            int counter = spellNumber;
            if (player.isSneaking())
              for (int i = 0; i < this.HIGHEST_SPELL_NUMBER; i++) {
                if (counter <= 0)
                  counter = this.HIGHEST_SPELL_NUMBER - 1;
                else {
                  counter--;
                }
                if (isSpellBound(sec, counter)) {
                  selectedSpell = counter;
                  break;
                }
              }
            else {
              for (int i = 0; i < this.HIGHEST_SPELL_NUMBER; i++) {
                if (counter < this.HIGHEST_SPELL_NUMBER - 1) {
                  counter++;
                }
                else {
                  counter = 0;
                }

                if (isSpellBound(sec, counter)) {
                  selectedSpell = counter;
                  break;
                }
              }
            }
            if (selectedSpell != -1) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("SelectMessage").replace("%spellname%", Spells.byId(selectedSpell).name())));
              player.getItemInHand().setDurability((short)selectedSpell);
            } else {
              player.sendMessage(ChatColor.RED + "Please bind any spells before casting!");
            }
          } catch (NumberFormatException localNumberFormatException1) {
          }
        }
        else {
          event.getPlayer().sendMessage(ChatColor.RED + "You don't have the permission to do that!");
        }
      } else if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.NETHER_STALK) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Bloodmagic"))) {
        if (event.getPlayer().hasPermission("bloodmagic.use")) {
          player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 119, 30);
          try {
            ItemMeta meta = player.getItemInHand().getItemMeta();
            List lore = meta.getLore();
            int wandNumber = Integer.parseInt((String)lore.get(0));
            ConfigurationSection sec = getConfig().getConfigurationSection("Bloodmagic." + wandNumber + ".BoundSpells");
            if (sec == null) {
              sec = getConfig().createSection("Bloodmagic." + wandNumber + ".BoundSpells");
            }
            int spellNumber = 0;
            try {
              spellNumber = player.getItemInHand().getDurability();
            } catch (NumberFormatException localNumberFormatException2) {
            }
            int selectedSpell = -1;
            int counter = spellNumber;
            if (player.isSneaking())
              for (int i = 0; i < this.HIGHEST_SPELL_NUMBER_BLOODMAGIC; i++) {
                if (counter <= 0)
                  counter = this.HIGHEST_SPELL_NUMBER_BLOODMAGIC - 1;
                else {
                  counter--;
                }
                if (isSpellBound(sec, counter)) {
                  selectedSpell = counter;
                  break;
                }
              }
            else {
              for (int i = 0; i < this.HIGHEST_SPELL_NUMBER_BLOODMAGIC; i++) {
                if (counter < this.HIGHEST_SPELL_NUMBER_BLOODMAGIC - 1) {
                  counter++;
                }
                else {
                  counter = 0;
                }

                if (isSpellBound(sec, counter)) {
                  selectedSpell = counter;
                  break;
                }
              }
            }
            if (selectedSpell != -1) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("SelectMessage").replace("%spellname%", BloodmagicSpells.byId(selectedSpell).name())));
              player.getItemInHand().setDurability((short)selectedSpell);
            } else {
              player.sendMessage(ChatColor.RED + "Please bind any spells before casting!");
            }
          } catch (NumberFormatException localNumberFormatException3) {
          }
        }
        else {
          event.getPlayer().sendMessage(ChatColor.RED + "You don't have the permission to do that!");
        }
      }
    }
    else if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK)) {
      Player player = event.getPlayer();
      if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.BLAZE_ROD) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Empire Wand"))) {
        if (event.getPlayer().hasPermission("empirewand.use")) {
          int wandDura = event.getPlayer().getItemInHand().getDurability();
          if (wandDura == 0)
            this.LittleSpark.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 1)
            this.Spark.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 2)
            this.LightningArrow.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 3)
            this.PoisonWave.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 4)
            this.ExplosionWave.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 5)
            this.FlameWave.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 6)
            this.Comet.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 7)
            this.Explosive.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 8)
            this.Leap.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 9)
            this.Confuse.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 10)
            this.Flamethrower.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 11)
            this.Capture.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 12)
            this.Launch.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 13)
            this.EnderSource.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 14)
            this.Explode.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 15)
            this.Lightningstorm.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 16)
            this.Escape.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
        }
        else {
          event.getPlayer().sendMessage(ChatColor.RED + "You don't have the permission to do that!");
        }
      } else if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.NETHER_STALK) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Bloodmagic")))
        if (event.getPlayer().hasPermission("bloodmagic.use")) {
          int wandDura = event.getPlayer().getItemInHand().getDurability();
          if (wandDura == 0)
            this.BloodBlock.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 1)
            this.BloodWave.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
          else if (wandDura == 2)
            this.BloodSpark.castSpell(event.getPlayer().getItemInHand().getDurability(), event.getPlayer());
        }
        else {
          event.getPlayer().sendMessage(ChatColor.RED + "You don't have the permission to do that!");
        }
    }
  }

  public boolean isSpellBound(ConfigurationSection c, int spell)
  {
    if (c.contains(String.valueOf(spell))) {
      return c.getBoolean(String.valueOf(spell), true);
    }
    c.set(String.valueOf(spell), Boolean.valueOf(true));
    saveConfig();
    return true;
  }
}