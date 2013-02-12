package com.github.DarkSeraphim.Plots.Listeners;

import com.earth2me.essentials.api.Economy;
import com.github.DarkSeraphim.Plots.Plots;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author DarkSeraphim
 */
public class PlayerListener implements Listener
{

    private Plots p;
    
    private Map<String, Vector[]> locCache = new WeakHashMap<String, Vector[]>();
    
    private ItemStack zeroTool;
    
    public PlayerListener(Plots p)
    {
        this.p = p;
        this.zeroTool = p.getTool().clone();
        this.zeroTool.setAmount(0);
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!this.p.getTool().isSimilar(event.getItem())) return;
        final Player player = event.getPlayer();
        if(!player.getWorld().getName().equals(p.getWorld())) return;
        Chunk c = player.getLocation().getChunk();
        String chunk = c.getX()+","+c.getZ();
        if(p.getChunkManager().chunkIsOwned(chunk))
        {
            player.sendMessage((player.getName().equals(p.getChunkManager().getChunkOwner(chunk)) ? GREEN+"You have" : GOLD+"Someone else has")+" bought that chunk");
            return;
        }
        if(!p.getChunkManager().canClaim(chunk))
        {
            player.sendMessage(RED+"You cannot claim this chunk");
        }
        String oldchunk = p.getPlayerSelections().get(player.getName());
        if(chunk.equals(oldchunk))
        {
            try
            {
                if(Economy.hasEnough(player.getName(), p.getCost()))
                {
                    p.getChunkManager().addChunk(player.getName(), chunk);
                    Economy.subtract(player.getName(), p.getCost());
                    player.sendMessage(GREEN+"You bought chunk "+LIGHT_PURPLE+chunk);
                    p.getPlayerSelections().put(player.getName(), null);
                    resendChunk(player);
                }
                else
                {
                    player.sendMessage(RED+"Sorry, but you have insufficient funds to buy this chunk");
                }
            }
            catch(com.earth2me.essentials.api.UserDoesNotExistException ex)
            {
                p.getLogger().log(Level.WARNING, "Player {0} not found in Essentials. Is it running properly?", player.getName());
            }
            catch(com.earth2me.essentials.api.NoLoanPermittedException ex)
            {
                p.getLogger().log(Level.WARNING, "Player {0} tried to loan, but that was not allowed", player.getName());
            }
        }
        else if(oldchunk == null)
        {
            p.getPlayerSelections().put(player.getName(), chunk);
            selChunk(player, c);
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if(p.getPlayerSelections().get(player.getName()) != null)
                    {
                        p.getPlayerSelections().put(player.getName(), null);
                        resendChunk(player);
                    }
                }
            }.runTaskLater(p, p.getConfig().getLong("chunk-unselect-time", 15)*20L);
        }
        else
        {
            player.sendMessage(RED+"Please wait 15 seconds before selecting a new chunk");
        }
    }
    
    private void selChunk(Player player, Chunk chunk)
    {
        Vector[] store = new Vector[60];
        int y = 0;
        World w = chunk.getWorld();
        Location l = null;
        int cx = 16*chunk.getX(), cz = 16*chunk.getZ();
        int i = 0;
        for(int x = 0; x < 16; x++)
        {
            for(int z = 0; z < 16; z++)
            {
                if((x == 0 || x == 15) || (z == 0 || z == 15))
                {
                    y = w.getHighestBlockYAt(cx+x, cz+z) + 1;
                    l = new Location(w, cx + x, y, cz+z);
                    store[i++] = l.toVector();
                    player.sendBlockChange(l, Material.WOOL, DyeColor.RED.getWoolData());
                }
            }
        }
        this.locCache.put(player.getName(), store);
    }
    
    public void resendChunk(Player player)
    {
        Vector[] cache = this.locCache.get(player.getName());
        World w = Bukkit.getWorld(p.getWorld());
        if(cache != null)
        {
            for(Vector v : cache)
            {
                Location l = v.toLocation(w);
                player.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData());
            }
        }
    }
    
    @EventHandler
    public void onDrop(final PlayerDropItemEvent event)
    {
        if(p.getTool().isSimilar(event.getItemDrop().getItemStack()))
        {
            event.setCancelled(true);
            event.getItemDrop().setItemStack(this.zeroTool);
            event.getPlayer().getInventory().setItem(8, p.getTool());
        }
    }
    
}
