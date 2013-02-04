package com.github.DarkSeraphim.Plots;

import com.github.DarkSeraphim.Plots.Listeners.PlayerListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author DarkSeraphim
 */
public class Plots extends JavaPlugin
{
    
    private Logger log;
    
    private int ccost;
    
    private String world;
    
    private Map<String, String> selectedChunks = new HashMap<String, String>();
    
    private ItemStack tool;
    
    private ChunkManager cmanager;
    
    @Override
    public void onEnable()
    {
        this.log = getLogger();
        
        saveConfig();
        this.world = getConfig().getString("world", "world");
        this.ccost = getConfig().getInt("chunk-cost", 0);
        
        int id = getConfig().getInt("tool.id", 280);
        String name = getConfig().getString("tool.name", "Chunk Selector");
        List<String> lore = getConfig().getStringList("tool.lore");
        this.tool = new ItemStack(id);
        {
            ItemMeta meta = this.tool.getItemMeta();
            meta.setDisplayName(name);
            meta.setLore(lore);
            this.tool.setItemMeta(meta);
        }
        
        this.cmanager = new ChunkManager(this);
        this.cmanager.load();
        
        PluginManager pm = Bukkit.getPluginManager();
        
        if(Bukkit.getWorld(this.world) == null)
        {
            log.log(Level.SEVERE, "World {0} not found. Disabling plugin", this.world);
            pm.disablePlugin(this);
            return;
        }
        
        Plugin p = pm.getPlugin("Essentials");
        if(p == null)
        {
            log.log(Level.SEVERE, "Failed to hook into Essentials Eco. Disabling plugin");
            pm.disablePlugin(this);
            return;
        }
        
        // Register
        pm.registerEvents(new PlayerListener(this), this);
        
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                cmanager.save();
            }
        }.runTaskTimer(this, 300L, getConfig().getLong("save-interval", 300L)*20L);
    }
    
    @Override
    public void onDisable()
    {
        this.cmanager.save();
    }
    
    public String getWorld()
    {
        return this.world;
    }
    
    public int getCost()
    {
        return this.ccost;
    }

    public Map<String, String> getPlayerSelections()
    {
        return this.selectedChunks;
    }
    
    public ItemStack getTool()
    {
        return this.tool;
    }
    
    public ChunkManager getChunkManager()
    {
        return this.cmanager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(label.equals("plots"))
        {
            if(args.length > 0)
            {
                if(args[0].equals("tool"))
                {
                    if(sender instanceof Player == false)
                    {
                        sender.sendMessage("Tools have no effect for consoles :P");
                    }
                    else
                    {
                        ((Player)sender).getInventory().addItem(this.tool);
                    }
                }
                else if(args[0].equals("list"))
                {
                    if(args.length > 1)
                    {
                        sender.sendMessage(GREEN+args[1]+" owns the following chunks:");
                        for(String chunk: this.getChunkManager().getOwnedChunks(args[1]))
                        {
                            sender.sendMessage(GREEN+" * "+chunk);
                        }
                        if(this.getChunkManager().getOwnedChunks(args[1]).size() < 1)
                        {
                            sender.sendMessage(GOLD+" - No chunks found - ");
                        }
                    }
                    else
                    {
                        if(sender instanceof Player == false)
                        {
                            sender.sendMessage("The console cannot own chunks ;D");
                            return true;
                        }
                        sender.sendMessage(GREEN+"You own the following chunks:");
                        for(String chunk: this.getChunkManager().getOwnedChunks(sender.getName()))
                        {
                            sender.sendMessage(GREEN+" * "+chunk);
                        }
                        if(this.getChunkManager().getOwnedChunks(sender.getName()).size() < 1)
                        {
                            sender.sendMessage(GOLD+" - No chunks found - ");
                        }
                    }
                }
            }
            else
            {
                sender.sendMessage("Plots v"+this.getDescription().getVersion()+" by Fireblast709");
            }
        }
        return true;
    }
}
