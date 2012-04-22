package com.gmail.Ne0nx3r0;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BetterAliasCommandExecutor implements CommandExecutor {
    private BetterAlias plugin;
    
    public BetterAliasCommandExecutor(BetterAlias plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        if (!(cs instanceof Player)){
            System.out.println("You can't run this from the console!");
            return false;
        }
        
        Player player = (Player) cs;
        
        if(cmd.getName().equalsIgnoreCase("to") && args.length == 1 && player.isOp()){
            String sPlayerName = args[0].toLowerCase();
            
            for(Player p : plugin.getServer().getOnlinePlayers()){
                if(p.getName().toLowerCase().contains(sPlayerName)){
                    player.chat("/tp "+player.getName()+" "+p.getName());
                    break;
                }
            }
        }
        else if(cmd.getName().equalsIgnoreCase("gm")){
            if(player.getGameMode() == GameMode.SURVIVAL){
                player.chat("/gamemode "+player.getName()+" 1");
            }else{
                player.chat("/gamemode "+player.getName()+" 0");
            }
        }
        else if(cmd.getName().equalsIgnoreCase("go") && args.length > 0){
            player.chat("/res tp "+args[0]);
        }
        return true;
    }
}