package com.github.DarkSeraphim.Plots.commands;

import com.github.DarkSeraphim.Plots.Plots;
import org.bukkit.command.CommandSender;

/**
 *
 * @author DarkSeraphim
 */
public abstract class CommandBase 
{
    protected Plots p;
    
    public CommandBase(Plots p)
    {
        this.p = p;
    }
    
    /**
     * Self declared CommandExecutor for inner commands
     * 
     * @return if the command was successful
     */
    public abstract boolean execute(CommandSender sender, String[] args);

}
