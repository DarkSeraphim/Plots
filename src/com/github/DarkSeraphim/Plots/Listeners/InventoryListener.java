package com.github.DarkSeraphim.Plots.Listeners;

import com.github.DarkSeraphim.Plots.Plots;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
            //event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            final Player clicker = (Player) event.getWhoClicked();
            final ItemStack cursor = event.getCursor();
            ItemStack current = event.getCurrentItem();
            //clicker.getOpenInventory().setCursor(cursor);
            clicker.sendMessage("Cursor: "+(cursor != null ? cursor.getType().name().toLowerCase().replace('_', ' ') : "null"));
            clicker.sendMessage("Current: "+(current != null ? current.getType().name().toLowerCase().replace('_', ' ') : "null"));
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    clicker.getOpenInventory().setCursor(cursor);
                    clicker.updateInventory();
                }
            }.runTaskLater(p, 1L);
            clicker.sendMessage(ChatColor.RED+"You cannot remove your tool. Use /plots tool to remove");
        }
    }

}
