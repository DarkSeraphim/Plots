package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import static org.bukkit.ChatColor.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author s129977
 */
public class CommandTool extends CommandBase
{
    
    public CommandTool(Plots p)
    {
        super(p);
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage("Tools have no effect for consoles :P");
        }
        else
        {
            Player player = (Player)sender;
            ItemStack i8 = player.getInventory().getItem(8);
            if(i8 == null || i8.getType() == Material.AIR)
            {
                player.getInventory().setItem(8, super.p.getTool());
                player.sendMessage(GREEN+"Tool given");
            }
            else
            {
                if(super.p.getTool().isSimilar(i8))
                {
                    player.getInventory().setItem(8, new ItemStack(Material.AIR, 1));
                    player.sendMessage(GREEN+"Tool removed");
                }
                else
                {
                    player.sendMessage(RED+"Please clear slot 9 first");
                }
            }
        }
        return true;
    }

}
