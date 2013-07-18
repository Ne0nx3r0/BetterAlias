package com.ne0nx3r0.betteralias.listener;

import com.ne0nx3r0.betteralias.BetterAlias;
import com.ne0nx3r0.betteralias.alias.Alias;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class BetterAliasCommandListener implements Listener
{
    private final BetterAlias plugin;

    public BetterAliasCommandListener(BetterAlias plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e)
    {
        String sCommand = null;
        String sArgs = "";
        
        if(e.getMessage().contains(" "))
        {
            sCommand = e.getMessage().substring(1,e.getMessage().indexOf(" "));
            sArgs = e.getMessage().substring(e.getMessage().indexOf(" ")+1);
        }
        else
        {
            sCommand = e.getMessage().substring(1);
        }
        
        Alias alias = plugin.aliasManager.getAlias(sCommand);
        
        if(alias != null)
        {        
            Player player = e.getPlayer();

            if(alias.hasPermission() 
            && !player.hasPermission("betteralias."+alias.getPermissionNode()))
            {
                player.sendMessage(ChatColor.RED+"You do not have permission to use this alias.");
                
                e.setCancelled(true);
            }
            else
            {         
                if(plugin.aliasManager.sendAliasCommands(
                        alias,
                        (CommandSender) e.getPlayer(),
                        sArgs));
                {            
                    e.setCancelled(true);
                }      
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onConsoleCommand(ServerCommandEvent e)
    {
        Alias alias;
        String sCommand = null;
        
        if(e.getCommand().contains(" "))
        {
            sCommand = e.getCommand().substring(e.getCommand().indexOf(" ")+1);
            
            alias = plugin.aliasManager.getAlias(e.getCommand().substring(0,e.getCommand().indexOf(" ")));
        }
        else
        {            
            alias = plugin.aliasManager.getAlias(e.getCommand());
            
            sCommand = "";
        }

        if(alias != null)
        {
            if(plugin.aliasManager.sendAliasCommands(alias,e.getSender(),sCommand))
            {
                e.setCommand("bareload donothing");
            }
        }
    }
}
