package com.github.DarkSeraphim.Plots.Listeners;

import com.github.DarkSeraphim.Plots.Plots;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

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
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event)
    {
        if(event.getWhoClicked() instanceof Player == false) return;
        int slot = event.getView().convertSlot(event.getRawSlot());
        if(slot == 8 && event.getInventory().getType() != InventoryType.PLAYER && p.getTool().isSimilar(event.getCurrentItem()))
        {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }
}
