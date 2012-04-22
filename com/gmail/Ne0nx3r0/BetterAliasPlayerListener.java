package com.gmail.Ne0nx3r0;

import com.gmail.Ne0nx3r0.AliasManager.AliasManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

class BetterAliasPlayerListener implements Listener{
        private final BetterAlias plugin;
        private final AliasManager aliasManager;
    
	BetterAliasPlayerListener(BetterAlias p, AliasManager am) {
            this.plugin = p;
            this.aliasManager = am;
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
            
            String[] cmd = e.getMessage().substring(1).split(" ");            
            
            if(aliasManager.isAliased(cmd[0])){
                String[] commandsToExecute = aliasManager.getAliasCommands(cmd[0],cmd.length-1);
                
                //in case they specified an invalid number of parameters
                if(commandsToExecute != null){
                    Player player = e.getPlayer();
                    
                    for(String commandToExecute : commandsToExecute){                        
                        //regex time!
                        Pattern patt = Pattern.compile("!([0-9a-zA-Z~]+)");
                        Matcher m = patt.matcher(commandToExecute);
                        StringBuffer sb = new StringBuffer(commandToExecute.length());
                        
                        while(m.find()){
                            String text = m.group(0).substring(1);
                            
                            if(text.equalsIgnoreCase("name")){
                                text = player.getName();
                            }else if(text.length() >= 2 && text.substring(1,2).equalsIgnoreCase("p")){
                                int iParam = -1;
                                
                                try{  
                                    iParam = Integer.parseInt(text.substring(0,1));  
                                }catch(Exception ex){return; }
                                
                                if(iParam > -1 && cmd.length >= iParam){
                                    String sPlayerName = cmd[iParam];
                                    
                                    text = "notFound";
                                    
                                    for(Player p : plugin.getServer().getOnlinePlayers()){
                                        if(p.getName().toLowerCase().contains(sPlayerName)){
                                            text = p.getName();
                                            break;
                                        }
                                    }
                                }
                            }else{
                                int iParam = -1;
                                
                                try{  
                                    iParam = Integer.parseInt(text);  
                                } catch(Exception ex){}
                                
                                if(iParam > -1 && cmd.length >= iParam){
                                    text = cmd[iParam];
                                }
                            }
                            //else if is numeric and cmd[i] exists, replace with cmd[i]
                            
                            m.appendReplacement(sb, Matcher.quoteReplacement(text));
                        }
                        m.appendTail(sb);
                        
                    //tp !name !~1 
                    //tp !1 !2       
                    //tp !~1 !~2
                        //debug: player.sendMessage(sb.toString());
                        player.chat(sb.toString());
                    }
                    
                    e.setCancelled(true);
                }
            }
	}
}