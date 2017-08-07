package main.java.com.barancode.buildguess;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class InfoBook {
	
	Main plugin;
	
	public InfoBook(Main plugin){
		this.plugin = plugin;
	}
	
	public void save(ItemStack item){
	    if (item != null && item.getType() == Material.WRITTEN_BOOK){
		    BookMeta meta = (BookMeta)item.getItemMeta();
		    plugin.getConfig().set("book.author", meta.getAuthor());
		    plugin.getConfig().set("book.title", meta.getTitle());
		    plugin.getConfig().set("book.description", meta.getLore());
	        List<String> pages = new ArrayList<String>();
	        for (String page : meta.getPages()){
	            page = page.replaceAll("§", "&").replaceAll("\n", "&&");
	            pages.add(page);
	        }
	
	        plugin.getConfig().set("book.pages", pages);
	        plugin.saveConfig();
	        plugin.reloadConfig();
	    }
	}
	
	public ItemStack getBook(){
	    ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
	    BookMeta bm = (BookMeta)item.getItemMeta();
	    String author = Utils.color(plugin.getConfig().getString("book.author"));
	    String title = Utils.color(plugin.getConfig().getString("book.title"));
	    List<String> lore = Utils.color(plugin.getConfig().getStringList("book.description"));
	    bm.setAuthor(author);
	    bm.setTitle(title);
	    bm.setLore(lore);

	    for (String s : plugin.getConfig().getStringList("book.pages")){
	        s = Utils.color(s).replaceAll("&&", "\n");
	        bm.addPage(s);
	    }
	    
	    item.setItemMeta(bm);

	    return item;
	}
}
