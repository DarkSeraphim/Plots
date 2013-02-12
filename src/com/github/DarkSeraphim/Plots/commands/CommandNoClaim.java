package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import static org.bukkit.ChatColor.*;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class CommandNoClaim extends CommandBase
{
    
    public CommandNoClaim(Plots p)
    {
        super(p);
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage("You would have to be an online player");
            return true;
        }
        if(args.length > 1)
        {
            if(!sender.hasPermission("plots.noclaim"))
            {
                sender.sendMessage(RED+"You cannot (un)claim noclaim chunks!");
                return true;
            }
            Chunk c = ((Player)sender).getLocation().getChunk();
            String chunk = c.getX()+","+c.getZ();
            if(args[1].equals("add"))
            {
                if(this.p.getChunkManager().addNoClaimChunk(chunk))
                {
                    sender.sendMessage("Noclaim chunk added");
                }
                else
                {
                    sender.sendMessage("Failed to add noclaim chunk (was it added already?)");
                }
            }
            else if(args[1].equals("remove"))
            {
                if(this.p.getChunkManager().removeNoClaimChunk(chunk))
                {
                    sender.sendMessage("Noclaim chunk removed");
                }
                else
                {
                    sender.sendMessage("Failed to remove noclaim chunk (was it even added?)");
                }
            }
            else
            {
                sender.sendMessage("Please specify if you want to add or remove noclaim chunks");
            }
        }
        else
        {
            sender.sendMessage("Please specify if you want to add or remove noclaim chunks");
        }
        return true;
    }

}
