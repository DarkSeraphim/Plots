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
            if(!player.getWorld().getName().equals(p.getWorld())) return true;
            ItemStack i8 = player.getInventory().getItem(8);
            boolean hasTool = hasTool(player, p.getTool());
            if(args[1].equals("r"))
            {
                if(!hasTool && (i8 == null || i8.getType() == Material.AIR))
                {
                    player.getInventory().setItem(8, super.p.getTool());
                    player.updateInventory();
                    player.sendMessage(GREEN+"Tool given");
                }
                else if((i8 != null && i8.getType() != Material.AIR) && !hasTool)
                {
                    player.sendMessage(RED+"Please clear slot 9 first");
                }
                else
                {
                    player.sendMessage(RED+"You already have a selector tool");
                }
            }
            else if(args[1].equals("e"))
            {
                if(hasTool(player, p.getTool()))
                {
                    ItemStack[] inv =  player.getInventory().getContents();
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    ItemStack is;
                    for(int i = 0; i < inv.length; i++)
                    {
                        is = inv[i];
                        if(p.getTool().isSimilar(is))
                        {
                            inv[i] = null;
                        }
                    }
                    for(int i = 0; i < inv.length; i++)
                    {
                        is = inv[i];
                        if(p.getTool().isSimilar(is))
                        {
                            armor[i] = null;
                        }
                    }
                    player.getInventory().setContents(inv);
                    player.getInventory().setArmorContents(armor);
                    player.updateInventory();
                    player.sendMessage(GREEN+"Selection tool removed");
                }
                else
                {
                    player.sendMessage(RED+"You don't have the selector tool");
                }
            }
        }
        return true;
    }
    
    public static boolean hasTool(Player player, ItemStack tool)
    {
        if(tool == null) return true;
        for(ItemStack i : player.getInventory().getContents())
        {
            if(tool.isSimilar(i))
            {
                return true;
            }
        }
        for(ItemStack i : player.getInventory().getArmorContents())
        {
            if(tool.isSimilar(i))
            {
                return true;
            }
        }
        return false;
    }

}
