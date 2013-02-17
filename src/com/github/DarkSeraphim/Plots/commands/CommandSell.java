package com.github.DarkSeraphim.Plots.commands;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.github.DarkSeraphim.Plots.Plots;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import static org.bukkit.ChatColor.*;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class CommandRemove extends CommandBase
{
        
    private DeletePrompt deletePrompt;
    
    private ConversationFactory cf;
    
    public CommandRemove(Plots p)
    {
        super(p);
        this.cf = new ConversationFactory(this.p);
        this.deletePrompt = new DeletePrompt();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage("Only players can own chunks ;D");
            return true;
        }
        Chunk c = ((Player)sender).getLocation().getChunk();
        String chunk = c.getX()+","+c.getZ();
        if(!(this.p.getChunkManager().getOwnedChunks(sender.getName()).contains(chunk) || sender.hasPermission("plots.remove.other")))
        {
            sender.sendMessage(RED+"That is not your chunk!");
            return true;
        }
        if(this.p.getChunkManager().getChunkOwner(chunk) == null)
        {
            sender.sendMessage(RED+"That chunk is not owned by anyone");
            return true;
        }
        Player player = (Player)sender;
        cf.thatExcludesNonPlayersWithMessage("Sorry, this is a player-only conversation");
        cf.withPrefix(new org.bukkit.conversations.ConversationPrefix() 
            {

                @Override
                public String getPrefix(ConversationContext cc)
                {
                    return ChatColor.GREEN+"[Plots]: "+ChatColor.WHITE;
                }
            });
        cf.withFirstPrompt(this.deletePrompt);
        cf.withLocalEcho(false);
        Map<Object, Object> session = new HashMap<Object, Object>();
        session.put("chunk", chunk);
        cf.withInitialSessionData(session);
        
        player.beginConversation(cf.buildConversation(player));
        return true;
    }
    
    private class DeletePrompt extends BooleanPrompt
    {

        List<String> validInput;
        
        private DeletePrompt()
        {
            validInput = Arrays.asList("yes", "y", "no", "n", "1", "0", "true", "false");
        }
        
        @Override
        protected boolean isInputValid(ConversationContext context, String input)
        {
            return validInput.contains(input.toLowerCase());
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, boolean input)
        {
            if(input == false)
            {
                cc.getForWhom().sendRawMessage("You cancelled the selling of the chunk");
                return Prompt.END_OF_CONVERSATION;
            }
            
            Object val = cc.getSessionData("chunk");
            if(val == null) return Prompt.END_OF_CONVERSATION;
            String chunk = val.toString();
            Player player = (Player)cc.getForWhom();
            if(CommandRemove.this.p.getChunkManager().removeChunk(player.getName(), chunk, player.hasPermission("plots.remove.others")))
            {
                try
                {
                    Economy.add(player.getName(), CommandRemove.this.p.getCost());
                    player.sendRawMessage(ChatColor.GREEN+"Successfully sold chunk");
                }
                catch (UserDoesNotExistException ex)
                {
                    CommandRemove.this.p.getLogger().log(Level.WARNING, "Player {0} not found in Essentials. Is it running properly?", player.getName());
                }
                catch (NoLoanPermittedException ex)
                {
                    CommandRemove.this.p.getLogger().log(Level.WARNING, "Player {0} tried to loan, but that was not allowed", player.getName());
                }
            }
            else
            {
                player.sendRawMessage(ChatColor.RED+"Failed to remove the chunk");
            }
            
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext cc)
        {
            return "Are you sure you want to sell this chunk? (yes/no)";
        }
        
    }
    
}
