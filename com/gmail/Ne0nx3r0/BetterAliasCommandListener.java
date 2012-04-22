//TODO: remove if not implemented

package com.gmail.Ne0nx3r0;

import com.gmail.Ne0nx3r0.AliasManager.AliasManager;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class BetterAliasCommandListener implements Listener{
        private final BetterAlias plugin;
        private final AliasManager aliasManager;
    
	BetterAliasCommandListener(BetterAlias p, AliasManager am) {
            this.plugin = p;
            this.aliasManager = am;
	}

	@EventHandler
	public void onServerCommandEvent(CommandSender sender, String command){
            System.out.println(sender);
            System.out.println(command);
	}
}