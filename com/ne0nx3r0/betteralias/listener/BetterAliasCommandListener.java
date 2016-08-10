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

import java.util.Collection;
import java.util.List;

public class BetterAliasCommandListener implements Listener {
    private final BetterAlias plugin;

    public BetterAliasCommandListener(BetterAlias plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        String sCommand = e.getMessage().substring(1);

        for(Alias alias : plugin.aliasManager.getAliasMatches(sCommand)){
            String sArgs = sCommand.substring(alias.command.length());

            Player player = e.getPlayer();
            String sNode = "betteralias." + alias.getPermissionNode();

            if (alias.hasPermission()
                    && !player.hasPermission(sNode)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this alias.");
                player.sendMessage(ChatColor.GRAY + "Node: " + sNode);

                e.setCancelled(true);
            } else {
                if (plugin.aliasManager.sendAliasCommands(alias, (CommandSender) e.getPlayer(), sArgs)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onConsoleCommand(ServerCommandEvent e) {
        String sCommand = e.getCommand();

        for(Alias alias : plugin.aliasManager.getAliasMatches(sCommand)){
            String sArgs = sCommand.substring(alias.command.length());

            if (plugin.aliasManager.sendAliasCommands(alias, e.getSender(), sArgs)) {
                e.setCommand("bareload donothing");
            }
        }
    }
}
