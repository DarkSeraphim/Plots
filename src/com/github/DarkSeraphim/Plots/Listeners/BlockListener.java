package com.github.DarkSeraphim.Plots.Listeners;

import com.github.DarkSeraphim.Plots.Plots;
import java.util.Set;
import org.bukkit.ChatColor;
import static org.bukkit.ChatColor.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author DarkSeraphim
 */
public class BlockListener implements Listener
{
    
    private Plots p;
        
    public BlockListener(Plots p)
    {
        this.p = p;
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent event)
    {
        if(!event.getBlock().getWorld().getName().equals(p.getWorld())) return;
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Chunk c = loc.getChunk();
        String chunk = c.getX()+","+c.getZ();
        if(p.getChunkManager().canBuild(player, chunk))
        {
            int localX = loc.getBlockX() - (16*c.getX());
            int localZ = loc.getBlockZ() - (16*c.getZ());
            if(((localX > (p.getOffset()-1) && localX < 16-p.getOffset()) && (localZ > (p.getOffset()-1) && localZ < 16-p.getOffset())) || player.hasPermission("plots.override"))
            {
                // Only do not cancel in this state
                // Other cases are always cancelled
                return;
            }
        }
        player.sendMessage(RED+"You cannot build here");
        event.setBuild(false);
        event.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event)
    {
        if(!event.getBlock().getWorld().getName().equals(p.getWorld())) return;
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Chunk c = loc.getChunk();
        String chunk = c.getX()+","+c.getZ();
        if(p.getChunkManager().canBuild(player, chunk))
        {
            int localX = loc.getBlockX() - (16*c.getX());
            int localZ = loc.getBlockZ() - (16*c.getZ());
            if(((localX > (p.getOffset()-1) && localX < 16-p.getOffset()) && (localZ > (p.getOffset()-1) && localZ < 16-p.getOffset())) || player.hasPermission("plots.override"))
            {
                // Only do not cancel in this state
                // Other cases are always cancelled
                return;
            }
        }
        player.sendMessage(RED+"You cannot build here");
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        String what = event.getLine(0);
        String who = event.getLine(1);
        if(who.equalsIgnoreCase("plot tool"))
        {
            if(what.equalsIgnoreCase("[recieve]") || what.equalsIgnoreCase("[erase]"))
            {
                event.setLine(0, ChatColor.AQUA+(what.equalsIgnoreCase("[recieve]") ? "[Recieve]" : "[Erase]"));
                event.setLine(1, "Plot tool");
            }
        }
    }
    
}
