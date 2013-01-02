package com.gmail.Ne0nx3r0;

import com.gmail.Ne0nx3r0.AliasManager.AliasManager;
import java.util.logging.Level;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterAlias extends JavaPlugin{    
    public static AliasManager aliasManager;
    public static BetterAlias self;
    
    @Override
    public void onEnable()
    {
        this.self = this;
    //Create an alias manager (also loads aliases)
        aliasManager = new AliasManager(this);
        
    //Register listeners
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(new BetterAliasPlayerListener(this,aliasManager), this);
        
    //Register commands
        getCommand("bareload").setExecutor(new BetterAliasCommandExecutor(this)); 
        
        log("[BetterAlias] Enabled");
    }	
    
    @Override
    public void onDisable() {
        log("[BetterAlias] Disabled");
    }
    
//Generic wrappers for console messages
    public void log(Level level,String sMessage){
        if(!sMessage.equals(""))
            getLogger().log(level,sMessage);
    }
    public void log(String sMessage){
        log(Level.INFO,sMessage);
    }
    public void error(String sMessage){
        log(Level.WARNING,sMessage);
    }
}