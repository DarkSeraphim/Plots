package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import static org.bukkit.ChatColor.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class CommandList extends CommandBase
{

    public CommandList(Plots p)
    {
        super(p);
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(args.length > 1 && sender.hasPermission("plots.list.other"))
        {
            sender.sendMessage(GREEN+""+args[1]+" owns the following chunks:");
            for(String chunk: this.p.getChunkManager().getOwnedChunks(args[1]))
            {
                sender.sendMessage(" * "+chunk);
            }
            if(this.p.getChunkManager().getOwnedChunks(args[1]).size() < 1)
            {
                sender.sendMessage(" - No chunks found - ");
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
            for(String chunk: this.p.getChunkManager().getOwnedChunks(sender.getName()))
            {
                sender.sendMessage(" * "+chunk);
            }
            if(this.p.getChunkManager().getOwnedChunks(sender.getName()).size() < 1)
            {
                sender.sendMessage(" - No chunks found - ");
            }
        }
        return true;
    }

}
