package com.github.DarkSeraphim.Plots.Listeners;

import com.github.DarkSeraphim.Plots.Plots;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author DarkSeraphim
 */
public class BlockListener implements Listener
{
    
    private Plots p;
    
    private final int offset = 3;
    
    public BlockListener(Plots p)
    {
        this.p = p;
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Chunk c = loc.getChunk();
        String chunk = c.getX()+","+c.getZ();
        if(p.getChunkManager().canBuild(player.getName(), chunk))
        {
            int localX = loc.getBlockX() - (16*c.getX());
            int localZ = loc.getBlockZ() - (16*c.getZ());
            if((localX > (offset-1) && localX < 16-offset) && (localZ > (offset-1) && localZ < 16-offset))
            {
                // Only do not cancel in this state
                // Other cases are always cancelled
                return;
            }
        }
        event.setBuild(false);
        event.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Chunk c = loc.getChunk();
        String chunk = c.getX()+","+c.getZ();
        if(p.getChunkManager().canBuild(player.getName(), chunk))
        {
            int localX = loc.getBlockX() - (16*c.getX());
            int localZ = loc.getBlockZ() - (16*c.getZ());
            if((localX > (offset-1) && localX < 16-offset) && (localZ > (offset-1) && localZ < 16-offset))
            {
                // Only do not cancel in this state
                // Other cases are always cancelled
                return;
            }
        }
        event.setCancelled(true);
    }
    
}
