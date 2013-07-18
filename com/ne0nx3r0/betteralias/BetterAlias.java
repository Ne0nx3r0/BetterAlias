package com.ne0nx3r0.betteralias;

import com.ne0nx3r0.betteralias.alias.AliasManager;
import com.ne0nx3r0.betteralias.command.BetterAliasCommandExecutor;
import com.ne0nx3r0.betteralias.listener.BetterAliasCommandListener;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterAlias extends JavaPlugin
{
    public AliasManager aliasManager;
    
    @Override
    public void onEnable()
    {
        this.aliasManager = new AliasManager(this);
        
        this.getServer().getPluginManager().registerEvents(new BetterAliasCommandListener(this), this);
        
        BetterAliasCommandExecutor betterAliasCommandExecutor = new BetterAliasCommandExecutor(this);
        
        this.getCommand("bareload").setExecutor(betterAliasCommandExecutor);
    }
}
