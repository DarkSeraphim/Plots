package com.github.DarkSeraphim.Plots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author DarkSeraphim
 */
public class ChunkManager 
{
    
    private Map<String, Set<String>> ownedChunks = new HashMap<String, Set<String>>();
    
    // Internal Set for easy checking if a chunk is owned
    private Set<String> chunkSet = new HashSet<String>();
    
    File chunkFile;
    
    YamlConfiguration chunks;
    
    Logger log;
    
    protected ChunkManager(Plots p)
    {
        chunkFile = new File(p.getDataFolder(), File.separator+"chunks.yml");
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
        log = p.getLogger();
    }
    
    public void addChunk(String player, String chunk)
    {
        if(!ownedChunks.containsKey(player))
        {
            ownedChunks.put(player, new HashSet<String>());
        }
        ownedChunks.get(player).add(chunk);
        chunkSet.add(chunk);
        this.chunks.set(player, new ArrayList<String>(ownedChunks.get(player)));
    }
    
    public Set<String> getOwnedChunks(String name)
    {
        return ownedChunks.containsKey(name) ? ownedChunks.get(name) : new HashSet<String>();
    }
    
    public boolean chunkIsOwned(String coords)
    {
        return chunkSet.contains(coords);
    }

    public void save()
    {
        if(this.chunkFile == null)
        {
            log.warning("File not found. You might want to check that if you want to save anything.");
            return;
        }
        try
        {
            this.chunks.save(chunkFile);
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
            log.warning("File not found. You might want to check that if you want to save anything.");
            return;
        }
        this.ownedChunks.clear();
        this.chunkSet.clear();
        this.chunks = YamlConfiguration.loadConfiguration(chunkFile);
        for(String player : chunks.getKeys(false))
        {
            this.ownedChunks.put(player, new HashSet(chunks.getStringList(player)));
            this.chunkSet.addAll(this.ownedChunks.get(player));
        }
    }
}
