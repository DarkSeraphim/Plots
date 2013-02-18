package com.github.DarkSeraphim.Plots.Listeners;

import com.earth2me.essentials.api.Economy;
import com.github.DarkSeraphim.Plots.Plots;
import com.github.DarkSeraphim.Plots.commands.CommandTool;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static org.bukkit.ChatColor.*;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    
    private CommandTool toolCmd;
    
    private Map<String, Vector[]> locCache = new WeakHashMap<String, Vector[]>();
    
    private ItemStack zeroTool;
    
    private Set<String> confirmed = new HashSet<String>();
    
    public PlayerListener(Plots p, CommandTool toolCmd)
    {
        this.p = p;
        this.toolCmd = toolCmd;
        this.zeroTool = p.getTool().clone();
        this.zeroTool.setAmount(0);
    }
    
    @EventHandler
    public void onItemUse(PlayerInteractEvent event)
    {
        if(!event.getPlayer().getWorld().getName().equals(p.getWorld())) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Chunk c = event.getClickedBlock().getChunk();
        String chunk = c.getX()+","+c.getZ();
        Location loc = event.getClickedBlock().getLocation();
        switch(event.getMaterial())
        {
            case INK_SACK:
            case WATER_BUCKET:
            case LAVA_BUCKET:
            case BUCKET:
            case FLINT_AND_STEEL:
            case GLASS_BOTTLE:
                if(p.getChunkManager().canBuild(event.getPlayer(), chunk))
                {
                    int localX = loc.getBlockX() - (16*c.getX());
                    int localZ = loc.getBlockZ() - (16*c.getZ());
                    if(((localX > (p.getOffset()-1) && localX < 16-p.getOffset()) && (localZ > (p.getOffset()-1) && localZ < 16-p.getOffset())) || event.getPlayer().hasPermission("plots.override"))
                    {
                        // Only do not cancel in this state
                        // Other cases are always cancelled
                        break;
                    }
                }
                event.setCancelled(true);
                event.setUseItemInHand(Event.Result.DENY);
                break;
        }
    }
    
    @EventHandler
    public void onBlockUse(PlayerInteractEvent event)
    {
        if(!event.getPlayer().getWorld().getName().equals(p.getWorld())) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Chunk c = event.getClickedBlock().getChunk();
        String chunk = c.getX()+","+c.getZ();
        Location loc = event.getClickedBlock().getLocation();
        switch(event.getClickedBlock().getType())
        {
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case WORKBENCH:
            case TRAP_DOOR:
            case CHEST:
            case DISPENSER:
            case LEVER:
            case WOOD_BUTTON:
            case STONE_BUTTON:
            case FENCE_GATE:
            case BEACON:
            case BED_BLOCK:
            case DIODE_BLOCK_ON:
            case DIODE_BLOCK_OFF:
            case BREWING_STAND:
            case CAULDRON:
                if(p.getChunkManager().canBuild(event.getPlayer(), chunk))
                {
                    int localX = loc.getBlockX() - (16*c.getX());
                    int localZ = loc.getBlockZ() - (16*c.getZ());
                    if(((localX > (p.getOffset()-1) && localX < 16-p.getOffset()) && (localZ > (p.getOffset()-1) && localZ < 16-p.getOffset())) || event.getPlayer().hasPermission("plots.override"))
                    {
                        // Only do not cancel in this state
                        // Other cases are always cancelled
                        break;
                    }
                }
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                break;
        }
    }
    
    @EventHandler
    public void onSign(PlayerInteractEvent event)
    {
        if(!event.getPlayer().getWorld().getName().equals(p.getWorld())) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock().getType() != SIGN_POST && event.getClickedBlock().getType() != WALL_SIGN)
        {
            return;
        }
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getClickedBlock().getState();
        String what = sign.getLine(0);
        String who = sign.getLine(1);
        if(who.equalsIgnoreCase("plot tool"))
        {
            if(what.equalsIgnoreCase(ChatColor.AQUA+"[recieve]"))
            {
                this.toolCmd.execute(player, new String[]{"t", "r"});
                event.setCancelled(true);
            }
            else if(what.equalsIgnoreCase(ChatColor.AQUA+"[erase]"))
            {
                this.toolCmd.execute(player, new String[]{"t", "e"});
                event.setCancelled(true);
            }
        }
        
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event)
    {
        if(!event.getPlayer().getWorld().getName().equals(p.getWorld())) return;
        Player player = event.getPlayer();
        Location loc = event.getRightClicked().getLocation();
        Chunk c = loc.getChunk();
        String chunk = c.getX()+","+c.getZ();
        
        if(event.getRightClicked().getType() == EntityType.PAINTING)
        {
            if(this.p.getChunkManager().canBuild(player, chunk))
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
        }
        else if(event.getRightClicked().getType() ==  EntityType.ITEM_FRAME)
        {
            if(this.p.getChunkManager().canBuild(player, chunk))
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
        }
        event.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent event)
    {
        if(!event.getPlayer().getWorld().getName().equals(p.getWorld())) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!this.p.getTool().isSimilar(event.getItem())) return;
        final Player player = event.getPlayer();
        if(!player.getWorld().getName().equals(p.getWorld())) return;
        Chunk c = player.getLocation().getChunk();
        String chunk = c.getX()+","+c.getZ();
        final String name = player.getName();
        if(p.getChunkManager().chunkIsOwned(chunk))
        {
            player.sendMessage((name.equals(p.getChunkManager().getChunkOwner(chunk)) ? GREEN+"You have" : GOLD+"Someone else has")+" bought that chunk");
            return;
        }
        if(!p.getChunkManager().canClaim(chunk))
        {
            player.sendMessage(RED+"You cannot claim this chunk");
        }
        String oldchunk = p.getPlayerSelections().get(name);
        if(chunk.equals(oldchunk))
        {
            if(p.getChunkManager().getOwnedChunks(name).size() > 0)
            {
                player.sendMessage(RED+"You can only buy one chunk");
                return;
            }
            try
            {
                if(Economy.hasEnough(name, p.getCost()))
                {
                    p.getChunkManager().addChunk(name, chunk);
                    Economy.subtract(name, p.getCost());
                    player.sendMessage(GREEN+"You bought chunk "+LIGHT_PURPLE+chunk);
                    
                    double x = c.getX()*16+8;
                    double z = c.getZ()*16+8;
                    
                    int y = c.getWorld().getHighestBlockYAt((int)x,(int)z)+1;
                    
                    c.getWorld().playEffect(new Location(c.getWorld(), x, y, z), Effect.MOBSPAWNER_FLAMES, 0, 50);
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1f, 1f);
                    
                    selChunk(player, c, DyeColor.LIME.getWoolData());
                    this.confirmed.add(name);
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            confirmed.remove(name);
                            if(player != null)
                                resendChunk(player);
                            p.getPlayerSelections().put(name, null);
                        }
                    }.runTaskLater(p, p.getConfig().getLong("chunk-unselect-time", 15)*5L);
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
            p.getPlayerSelections().put(name, chunk);
            selChunk(player, c, DyeColor.RED.getWoolData());
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if(p.getPlayerSelections().get(name) != null)
                    {
                        if(player != null && !confirmed.contains(name))
                        {
                            resendChunk(player);
                            p.getPlayerSelections().put(name, null);
                        }
                    }
                }
            }.runTaskLater(p, p.getConfig().getLong("chunk-unselect-time", 15)*20L);
        }
        else
        {
            player.sendMessage(RED+"Please wait 15 seconds before selecting a new chunk");
        }
    }
    
    private void selChunk(Player player, Chunk chunk, byte data)
    {
        Vector[] store = new Vector[60];
        int y = 0;
        World w = chunk.getWorld();
        Location l = null;
        int cx = 16*chunk.getX(), cz = 16*chunk.getZ();
        int i = 0;
        int minBorder = p.getOffset();
        int maxBorder = 15-p.getOffset();
        for(int x = 0; x < 16; x++)
        {
            for(int z = 0; z < 16; z++)
            {
                if(((x == minBorder || x == maxBorder) && (z >= minBorder && z <= maxBorder))
                || ((z == minBorder || z == maxBorder) && (x >= minBorder && x <= maxBorder)))
                {
                    y = w.getHighestBlockYAt(cx+x, cz+z) + 1;
                    l = new Location(w, cx + x, y, cz+z);
                    store[i++] = l.toVector();
                    player.sendBlockChange(l, WOOL, data);
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
                if(v == null) continue;
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
    
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        if(!player.getWorld().getName().equals(p.getWorld()))
        {
            ItemStack[] inv =  player.getInventory().getContents();
            ItemStack[] armor = player.getInventory().getArmorContents();
            ItemStack is;
            for(int i = 0; i < inv.length; i++)
            {
                is = inv[i];
                if(p.getTool().isSimilar(is))
                {
                    inv[i] = null;
                }
            }
            for(int i = 0; i < inv.length; i++)
            {
                is = inv[i];
                if(p.getTool().isSimilar(is))
                {
                    armor[i] = null;
                }
            }
            player.getInventory().setContents(inv);
            player.getInventory().setArmorContents(armor);
            player.saveData();
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        ItemStack[] inv =  player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack is;
        for(int i = 0; i < inv.length; i++)
        {
            is = inv[i];
            if(p.getTool().isSimilar(is))
            {
                inv[i] = null;
            }
        }
        for(int i = 0; i < inv.length; i++)
        {
            is = inv[i];
            if(p.getTool().isSimilar(is))
            {
                armor[i] = null;
            }
        }
        player.getInventory().setContents(inv);
        player.getInventory().setArmorContents(armor);
        player.saveData();
    }
    
}
