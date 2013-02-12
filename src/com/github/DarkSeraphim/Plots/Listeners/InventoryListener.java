package com.github.DarkSeraphim.Plots.Listeners;

import com.github.DarkSeraphim.Plots.Plots;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DarkSeraphim
 */
public class InventoryListener implements Listener
{
    
    private Plots p;
    
    public InventoryListener(Plots p)
    {
        this.p = p;
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if(event.getWhoClicked() instanceof Player == false) return;
        if(p.getTool().isSimilar((event.getCurrentItem())))
        {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            Player clicker = (Player) event.getWhoClicked();
            ItemStack cursor = event.getCursor();
            //clicker.getOpenInventory().setCursor(cursor);
            //clicker.sendMessage("Cursor: "+(cursor != null ? cursor.getType().name().toLowerCase().replace('_', ' ') : "null"));
            clicker.updateInventory();
            clicker.sendMessage(ChatColor.RED+"You cannot remove your tool. Use /plots tool to remove");
        }
    }

}
