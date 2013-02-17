package com.github.DarkSeraphim.Plots;

import com.github.DarkSeraphim.Plots.Listeners.*;
import com.github.DarkSeraphim.Plots.commands.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    
    private final int offset = 3;
    
    /*********************************************\
     *                 Commands                  * 
    \*********************************************/
    private CommandAddMember cmdAddMember;
    private CommandTool cmdTool;
    private CommandList cmdList;
    private CommandSell cmdSell;
    private CommandRemoveMember cmdRemoveMember;
    private CommandNoClaim cmdNoClaim;
    
    private final List<String> ADDMEMBER_ALIAS = Arrays.asList("addmember", "addm", "am", "addmem");
    private final List<String> REMMEMBER_ALIAS = Arrays.asList("removemember", "remm", "rm", "remmem", "kick", "kickmember");
    
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
        
        // Initializing commands
        this.cmdTool = new CommandTool(this);
        this.cmdList = new CommandList(this);
        this.cmdSell = new CommandSell(this);
        this.cmdNoClaim = new CommandNoClaim(this);
        this.cmdAddMember = new CommandAddMember(this);
        this.cmdRemoveMember = new CommandRemoveMember(this);
        
        // Register
        pm.registerEvents(new PlayerListener(this, this.cmdTool), this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
        
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
    
    public int getOffset()
    {
        return this.offset;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(cmd.getName().equals("plot"))
        {
            if(args.length > 0)
            {
                if(ADDMEMBER_ALIAS.contains(args[0].toLowerCase()))
                {
                    this.cmdAddMember.execute(sender, args);
                }
                if(REMMEMBER_ALIAS.contains(args[0].toLowerCase()))
                {
                    this.cmdRemoveMember.execute(sender, args);
                }
                /*else if(args[0].equalsIgnoreCase("tool") || args[0].equalsIgnoreCase("t"))
                {
                    this.cmdTool.execute(sender, args);
                }*/
                /*else if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l"))
                {
                    this.cmdList.execute(sender, args);
                }*/
                else if(args[0].equalsIgnoreCase("sell"))
                {
                    this.cmdSell.execute(sender, args);
                }
                else if(args[0].equals("noclaim"))
                {
                    this.cmdNoClaim.execute(sender, args);
                }
                else if(args[0].equals("?") || args[0].equals("help") || args[0].equals("h"))
                {
                    sender.sendMessage(ChatColor.GREEN+"[Plots]"+ChatColor.RESET+": Plots command list");
                    //sender.sendMessage("/plot list "+(sender.hasPermission("plots.list.others") ? "[player] " : "")+"- show your "+(sender.hasPermission("plots.list.others") ? "or another player's " : "" )+"chunk list");
                    sender.sendMessage("/plot sell - Sell the chunk you are standing on (if you own it, of course)");
                    sender.sendMessage("/plot addmember <player> - Allow a player to build on your chunk");
                    sender.sendMessage("/plot removemember <player> - Remove the rights of a player to build on your chunk");
                    if(sender.hasPermission("plots.noclaim"))
                    {
                        sender.sendMessage("/plot noclaim add|remove - add or remove noclaim chunks");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED+"Incorrect syntax! For a complete list of commands, use: /plot");
                }
            }
            else
            {
                sender.sendMessage(ChatColor.GREEN+"[Plots]"+ChatColor.RESET+": Plots v"+this.getDescription().getVersion()+" by Fireblast709");
                sender.sendMessage(ChatColor.GREEN+"[Plots]"+ChatColor.RESET+": Plots command list");
                //sender.sendMessage("/plot list "+(sender.hasPermission("plots.list.others") ? "[player] " : "")+"- show your "+(sender.hasPermission("plots.list.others") ? "or another player's " : "" )+"chunk list");
                sender.sendMessage("/plot sell - Sell the chunk you are standing on (if you own it, of course)");
                sender.sendMessage("/plot addmember <player> - Allow a player to build on your chunk");
                sender.sendMessage("/plot removemember <player> - Remove the rights of a player to build on your chunk");
                if(sender.hasPermission("plots.noclaim"))
                {
                    sender.sendMessage("/plot noclaim add|remove - add or remove noclaim chunks");
                }
                
            }
        }
        return true;
    }
}
