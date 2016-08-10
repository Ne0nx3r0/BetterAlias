package com.ne0nx3r0.betteralias.command;

import com.ne0nx3r0.betteralias.BetterAlias;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BetterAliasCommandExecutor implements CommandExecutor {
    private final BetterAlias plugin;

    public BetterAliasCommandExecutor(BetterAlias plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        // To allow nulling server commands out
        if (args.length == 1 && args[0].equalsIgnoreCase("donothing")) {
            return true;
        }

        if (cs.hasPermission("BetterAlias.reload") || cs.isOp()) {

            cs.sendMessage(ChatColor.GOLD + "Reloading aliases...");

            if (plugin.aliasManager.loadAliases()) {
                cs.sendMessage(ChatColor.GOLD + "Aliases reloaded!");
            } else {
                cs.sendMessage(ChatColor.RED + "An error occurred while reloading aliases!");
            }
        } else {
            cs.sendMessage(ChatColor.RED + "You do not have permission to use /bareload (node: BetterAlias.reload)");
        }

        return true;
    }

}
