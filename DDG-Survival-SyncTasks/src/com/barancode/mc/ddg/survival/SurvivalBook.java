package com.barancode.mc.ddg.survival;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class SurvivalBook {
	
	Main plugin;
	
	public SurvivalBook(Main plugin){
		this.plugin = plugin;
	}
	
	// These functions have been taken from the InfoBook plugin made by Gnacik
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean saveBook(Player player, String bookName)
	  {
	    ItemStack stack = player.getInventory().getItemInHand();

	    if ((stack != null) && (stack.getType() == Material.WRITTEN_BOOK))
	    {
	      BookMeta meta = (BookMeta)stack.getItemMeta();

	      if (meta.hasAuthor())
	        plugin.getConfig().set(bookName + ".author", meta.getAuthor());
	      else {
		    plugin.getConfig().set(bookName + ".author", "Author");
	      }
	      if (meta.hasTitle())
		    plugin.getConfig().set(bookName + ".title", meta.getTitle());
	      else {
		    plugin.getConfig().set(bookName + ".title", "Title");
	      }
	      if (meta.hasLore()) {
		    plugin.getConfig().set(bookName + ".description", meta.getLore());
	      }
	      if (meta.hasPages())
	      {
	        List pages = new ArrayList();
	        for (String page : meta.getPages())
	        {
	          page = page.replaceAll("\n", "&&");
	          page = page.replaceAll("§", "&");

	          pages.add(page);
	        }

	        plugin.getConfig().set(bookName + ".pages", pages);
	      }
	      else {
		    plugin.getConfig().set(bookName + ".pages", Arrays.asList(new String[] { "First", "Second" }));
	      }
	      plugin.saveConfig();
	      plugin.reloadConfig();
	    }
	    return false;
	  }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ItemStack createDefaultBook(String bookname)
	  {
	    ItemStack i = new ItemStack(Material.WRITTEN_BOOK, 1);

	    BookMeta bm = (BookMeta)i.getItemMeta();

	    String a = plugin.getConfig().getString(bookname + ".author", "");
	    String t = plugin.getConfig().getString(bookname + ".title", "");

	    List l = plugin.getConfig().getStringList(bookname + ".description");

	    bm.setAuthor(a);
	    bm.setTitle(t);
	    bm.setLore(l);

	    for (String page : plugin.getConfig().getStringList(bookname + ".pages"))
	    {
	      page = page.replaceAll("&&", "\n");
	      page = page.replaceAll("&", "§");
	      bm.addPage(new String[] { page });
	    }
	    i.setItemMeta(bm);

	    return i;
	  }
}
