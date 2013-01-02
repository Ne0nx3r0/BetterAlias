package com.gmail.Ne0nx3r0;

import com.gmail.Ne0nx3r0.AliasManager.AliasManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

class BetterAliasCommandExecutor implements CommandExecutor {
    private final BetterAlias plugin;
    
    public BetterAliasCommandExecutor(BetterAlias plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(cs.isOp() || cs.hasPermission("BetterAlias.reload"))
        {
            BetterAlias.aliasManager = new AliasManager(BetterAlias.self);

            cs.sendMessage(ChatColor.GOLD+"BetterAlias reloaded!");
        }
        else
        {
            cs.sendMessage(ChatColor.RED+"You do not have permission to use /bareload (node: BetterAlias.reload)");
        }
        
        return true;
    }
    
}
