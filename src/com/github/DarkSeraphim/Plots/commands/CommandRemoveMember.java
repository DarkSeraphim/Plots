package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import static org.bukkit.ChatColor.*;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class CommandRemoveMember extends CommandBase
{
    
    public CommandRemoveMember(Plots p)
    {
        super(p);
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(sender instanceof ConsoleCommandSender == false)
        {
            return true;
        }
        if(args.length != 3)
        {
            // Ignore, its the console anyway
            //sender.sendMessage(RED+"Incorrect syntax: /plot addmember <owner> <member>");
            return true;
        }
        
        for(String chunk : p.getChunkManager().getOwnedChunks(args[1]))
        {
            if(p.getChunkManager().removeMember(chunk, args[2]))
            {
                //sender.sendMessage(GREEN+"Member "+args[2]+" removed from "+chunk);
            }
            else
            {
                //sender.sendMessage(RED+"Failed to remove member "+args[2]+" from "+chunk+". Was he a member?");
            }
        }
        return true;
    }
    

}
