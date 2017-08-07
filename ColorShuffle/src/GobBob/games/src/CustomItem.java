package GobBob.games.src;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItem
{
  ItemStack item;
  String book_author;
  String book_title;
  String[] book_pages = null;

  public CustomItem(int par1) {
    this.item = new ItemStack(par1);
  }

  public CustomItem(ItemStack par1) {
    this.item = par1;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public CustomItem setBookAuthor(String par1) {
    this.book_author = par1;
    return this;
  }

  public CustomItem setBookTitle(String par1) {
    this.book_title = par1;
    return this;
  }

  public ItemStack getBook() {
    BookMeta meta = (BookMeta)this.item.getItemMeta();
    meta.setTitle(this.book_title);
    meta.setAuthor(this.book_author);
    if (this.book_pages != null) {
      for (int i = 0; i < this.book_pages.length; i++) {
        meta.addPage(new String[] { this.book_pages[i] });
      }
    }
    this.item.setItemMeta(meta);
    return this.item;
  }

  public CustomItem setBookPages(String[] par1) {
    this.book_pages = par1;
    return this;
  }

  public CustomItem addEnchantment(Enchantment ench, int level) {
    this.item.addEnchantment(ench, level);
    return this;
  }

  public CustomItem addEnchantments(Enchantment[] ench, int[] levels) {
    for (int i = 0; i < ench.length; i++) {
      this.item.addEnchantment(ench[i], levels[i]);
    }
    return this;
  }

  public CustomItem setDurability(int dur) {
    this.item.setDurability((short)dur);
    return this;
  }

  public CustomItem setName(String par1) {
    ItemMeta data = this.item.getItemMeta();
    data.setDisplayName(par1);
    this.item.setItemMeta(data);
    return this;
  }

  public CustomItem setAmount(int par1) {
    this.item.setAmount(par1);
    return this;
  }

  public String getName() {
    return this.item.getItemMeta().getDisplayName();
  }
}