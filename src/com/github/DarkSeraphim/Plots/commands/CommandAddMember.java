package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import java.util.Set;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class CommandAddMember extends CommandBase
{

    public CommandAddMember(Plots p)
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
        
        Player player = Bukkit.getPlayer(args[1]);
        /*if(player == null)
        {
            sender.sendMessage(RED+"That player is not online");
            return true;
        }*/
        Player target = Bukkit.getPlayer(args[2]);
        /*if(target == null)
        {
            player.sendMessage(RED+"That player is not online");
            return true;
        }*/
        for(String chunk : p.getChunkManager().getOwnedChunks(args[1]))
        {
            if(p.getChunkManager().getOwnedChunks(player.getName()).contains(chunk))
            {
                p.getChunkManager().addMember(chunk, args[1]);
                //player.sendMessage(GREEN+"Member "+args[1]+" added to chunk "+chunk);
            }
            else
            {
                //sender.sendMessage(GREEN+"You do not own that chunk");
            }
        }
        return true;
    }

}
