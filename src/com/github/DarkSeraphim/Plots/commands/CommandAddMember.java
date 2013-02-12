package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
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
        if(sender instanceof Player == false)
        {
            sender.sendMessage("You must be able to own chunks, to add players");
            return true;
        }
        if(args.length != 2)
        {
            sender.sendMessage(ChatColor.RED+"Incorrect syntax: /plots addmember <player>");
            return true;
        }
        if(Bukkit.getPlayer(args[1]) == null)
        {
            sender.sendMessage(ChatColor.RED+"That player is not online");
            return true;
        }
        Player player = (Player) sender;
        Chunk c = player.getLocation().getChunk();
        String chunk = c.getX()+","+c.getZ();
        if(p.getChunkManager().getOwnedChunks(player.getName()).contains(chunk))
        {
            p.getChunkManager().addMember(chunk, args[1]);
            sender.sendMessage(ChatColor.GREEN+"Member "+args[1]+" added to chunk "+chunk);
        }
        else
        {
            sender.sendMessage(ChatColor.RED+"You do not own that chunk");
        }
        return true;
    }

}
