package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
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
        String chunk = "";
        String member = "";
        if(sender instanceof Player)
        {
            if(args.length != 2)
            {
                sender.sendMessage("Invalid arguments: /plots removemember <player>");
                return true;
            }
            Player player = (Player)sender;
            Chunk c = player.getLocation().getChunk();
            chunk = c.getX()+","+c.getZ();
        }
        else
        {
            if(args.length != 4)
            {
                sender.sendMessage("Invalid arguments: /plots removemember <cx> <cz> <player>");
                return true;
            }
            chunk = args[1]+","+args[2];
            member = args[3];
        }
        
        if(p.getChunkManager().removeMember(chunk, member))
        {
            sender.sendMessage(ChatColor.GREEN+"Member "+member+" removed from "+chunk);
        }
        else
        {
            sender.sendMessage(ChatColor.RED+"Failed to remove member "+member+" from "+chunk+". Was he a member?");
        }
        return true;
    }
    

}
