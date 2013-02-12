package com.github.DarkSeraphim.Plots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class ChunkManager 
{
    private Plots p;
    
    private Map<String, Set<String>> ownedChunks = new HashMap<String, Set<String>>();
    
    // Backwards for chunk->owner search
    private Map<String, String> owners = new HashMap<String, String>();
    
    // Building rights
    private Map<String, Set<String>> chunkMembers = new HashMap<String, Set<String>>();
    
    // Internal Set for easy checking if a chunk is owned
    private Set<String> chunkSet = new HashSet<String>();
    
    private Set<String> noclaim;
    
    File chunkFile;
    
    File memberFile;
    
    YamlConfiguration chunks;
    
    YamlConfiguration members;
    
    Logger log;
    
    protected ChunkManager(Plots p)
    {
        this.p = p;
        chunkFile = new File(p.getDataFolder(), File.separator+"chunks.yml");
        memberFile = new File(p.getDataFolder(), File.separator+"member.yml");
        if(!chunkFile.exists())
        {
            try
            {
                if(!chunkFile.createNewFile())
                {
                    p.getLogger().warning("Failed to create the save file for chunks");
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        
        if(!memberFile.exists())
        {
            try
            {
                if(!memberFile.createNewFile())
                {
                    p.getLogger().warning("Failed to create the save file for member rights");
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        
        log = p.getLogger();
        noclaim = new HashSet<String>(p.getConfig().getStringList("noclaim-chunks"));
    }
    
    public void addChunk(String player, String chunk)
    {
        if(!this.ownedChunks.containsKey(player))
        {
            this.ownedChunks.put(player, new HashSet<String>());
        }
        this.ownedChunks.get(player).add(chunk);
        this.owners.put(chunk, player);
        this.chunkSet.add(chunk);
        
        this.chunks.set(player, new ArrayList<String>(ownedChunks.get(player)));
    }
    
    public void addMember(String chunk, String member)
    {
        if(!this.chunkMembers.containsKey(member))
        {
            this.chunkMembers.put(member, new HashSet<String>());
        }
        this.chunkMembers.get(member).add(chunk);
    }
    
    public boolean removeMember(String chunk, String member)
    {
        return this.getMemberChunks(member).remove(chunk);
    }
    
    public boolean removeChunk(String player, String chunk, boolean force)
    {
        if(force)
        {
            player = this.owners.get(chunk);
        }
        Set<String> chunks = getOwnedChunks(player);
        if(chunks.contains(chunk))
        {
            chunks.remove(chunk);
            this.chunkSet.remove(chunk);
            return true;
        }
        return false;
    }
    
    public boolean addNoClaimChunk(String chunk)
    {
        if(this.noclaim.contains(chunk)) return false;
        if(this.chunkIsOwned(chunk))
        {
            if(this.owners.containsKey(chunk))
            {
                String owner = this.owners.remove(chunk);
                Player p = Bukkit.getPlayer(owner);
                if(p != null)
                {
                    p.sendMessage(GOLD+"Your chunk was claimed as a noclaim chunk");
                }
            }
            this.owners.remove(chunk);
            this.chunkSet.remove(chunk);
        }
        
        List<String> noclaims = p.getConfig().getStringList("noclaim-chunks");
        noclaims.add(chunk);
        p.getConfig().set("noclaim-chunks", noclaims);
        p.saveConfig();
        
        return this.noclaim.add(chunk);
    }
    
    public boolean removeNoClaimChunk(String chunk)
    {
        List<String> noclaims = p.getConfig().getStringList("noclaim-chunks");
        noclaims.remove(chunk);
        p.getConfig().set("noclaim-chunks", noclaims);
        p.saveConfig();
        return this.noclaim.remove(chunk);
    }
    
    public Set<String> getOwnedChunks(String name)
    {
        return this.ownedChunks.containsKey(name) ? this.ownedChunks.get(name) : new HashSet<String>();
    }
    
    public Set<String> getMemberChunks(String name)
    {
        return this.chunkMembers.containsKey(name) ? this.chunkMembers.get(name) : new HashSet<String>();
    }
    
    public String getChunkOwner(String chunk)
    {
        return this.owners.get(chunk);
    }
    
    public boolean chunkIsOwned(String coords)
    {
        return chunkSet.contains(coords);
    }
    
    public boolean canClaim(String chunk)
    {
        return !this.noclaim.contains(chunk);
    }
    
    public boolean canBuild(String player, String chunk)
    {
        return getOwnedChunks(player).contains(chunk) || getMemberChunks(player).contains(chunk);
    }

    public void save()
    {
        if(this.chunkFile == null)
        {
            log.warning("Chunk file not found. You might want to check that if you want to save anything.");
            return;
        }
        try
        {
            this.chunks.save(this.chunkFile);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        
        if(this.memberFile == null)
        {
            log.warning("Member file not found. You might want to check that if you want to save anything.");
            return;
        }
        try
        {
            this.members.save(this.memberFile);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void load()
    {
        if(this.chunkFile == null)
        {
            log.warning("Chunk file not found. You might want to check that if you want to save anything.");
            return;
        }
        this.ownedChunks.clear();
        this.chunkSet.clear();
        this.owners.clear();
        this.chunks = YamlConfiguration.loadConfiguration(this.chunkFile);
        for(String player : this.chunks.getKeys(false))
        {
            this.ownedChunks.put(player, new HashSet<String>(this.chunks.getStringList(player)));
            for(String chunk : this.ownedChunks.get(player))
            {
                this.owners.put(chunk, player);
            }
            this.chunkSet.addAll(this.ownedChunks.get(player));
        }
        
        if(this.memberFile == null)
        {
            log.warning("Member file not found. You might want to check that if you want to save anything.");
            return;
        }
        this.chunkMembers.clear();
        this.members = YamlConfiguration.loadConfiguration(this.memberFile);
        for(String player : this.members.getKeys(false))
        {
            this.chunkMembers.put(player, new HashSet<String>(this.members.getStringList(player)));
        }
    }
}
