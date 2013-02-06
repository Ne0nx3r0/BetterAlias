package com.ne0nx3r0.betteralias.alias;

import com.ne0nx3r0.betteralias.BetterAlias;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class AliasManager
{
    private final BetterAlias plugin;
    private HashMap<String, Alias> aliases;

    public AliasManager(BetterAlias plugin)
    {
        this.plugin = plugin;
        
        this.loadAliases();
    }

    public final boolean loadAliases()
    {
        this.aliases = new HashMap<String,Alias>();
        
        File configFile = new File(plugin.getDataFolder(), "aliases.yml");   
        
        if(!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            copy(plugin.getResource("aliases.yml"), configFile);
        }

        FileConfiguration yml = YamlConfiguration.loadConfiguration(configFile);

        Set<String> aliasList = yml.getKeys(false);
        
        if(aliasList.isEmpty())
        {
            plugin.getLogger().log(Level.WARNING, "No aliases found in aliases.yml");
            
            return false;
        }

        for(String sAlias : aliasList)
        {        
            Alias alias;

            if(yml.isString(sAlias+".permission"))
            {
                alias = new Alias(sAlias,yml.getString("permission"));
            }
            else
            {
                alias = new Alias(sAlias);
            }

            for(String sArg : yml.getConfigurationSection(sAlias).getKeys(false))
            {
                List<AliasCommand> commandsList = new ArrayList<AliasCommand>();
                
                if(!sArg.equalsIgnoreCase("permission"))
                {
                    int iArg = Integer.parseInt(sArg);
                    
                    List<String> sArgLines = new ArrayList<String>();
                    
                    if(yml.isList(sAlias+"."+sArg))
                    {
                        sArgLines = yml.getStringList(sAlias+"."+sArg);
                    }
                    else
                    {
                        sArgLines.add(yml.getString(sAlias+"."+sArg));
                    }
                    
                    for(String sArgLine : sArgLines)
                    {
                        String sType = sArgLine.substring(0,sArgLine.indexOf(" "));
                        AliasCommandTypes type = AliasCommandTypes.PLAYER;

                        if(sType.equalsIgnoreCase("console"))
                        {
                            type = AliasCommandTypes.CONSOLE;
                            
                            sArgLine = sArgLine.substring(sArgLine.indexOf(" ")+1);
                        }
                        else if(sType.equalsIgnoreCase("reply"))
                        { 
                            type = AliasCommandTypes.REPLY_MESSAGE;
                            
                            sArgLine = sArgLine.substring(sArgLine.indexOf(" ")+1);
                        }
                        
                        sArgLine = this.replaceColorCodes(sArgLine);
                        
                        commandsList.add(new AliasCommand(sArgLine,type));
                    }
                    
                    alias.setCommandsFor(iArg,commandsList);
                }
            }
            
            this.aliases.put(sAlias, alias);
        }
        
        return true;
    }

    public Alias getAlias(String sCommand)
    {
        return this.aliases.get(sCommand);
    }

    public boolean sendAliasCommands(Alias alias,CommandSender cs, String commandString)
    {
        Player player = null;
        
        if(cs instanceof Player)
        {
            player = (Player) cs;
        }
        
        List<String> args = new ArrayList<String>();
            
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

        Matcher regexMatcher = regex.matcher(commandString);

        while(regexMatcher.find())
        {
            if(regexMatcher.group(1) != null)
            {
                // Add double-quoted string without the quotes
                args.add(regexMatcher.group(1));
            }
            else if (regexMatcher.group(2) != null)
            {
                // Add single-quoted string without the quotes
                args.add(regexMatcher.group(2));
            }
            else
            {
                // Add unquoted word
                args.add(regexMatcher.group());
            }
        }

        if(alias.hasCommandFor(args.size()))
        {
            for(AliasCommand ac : alias.getCommands(args.size()))
            {
                String sAliasCommand = ac.command;

                Matcher m = Pattern.compile("!([0-9a-zA-Z~*]+)").matcher(sAliasCommand);

                StringBuffer sb = new StringBuffer(sAliasCommand.length());

                while(m.find())
                {
                    String text = m.group(0).substring(1);

                    if(text.equalsIgnoreCase("name"))
                    {
                        if(player != null)
                        {
                            text = player.getName();
                        }
                        else
                        {
                            cs.sendMessage(ChatColor.RED+"[BA] A parameter of this alias requires a player.");
                        }
                    }
                    else if(text.equalsIgnoreCase("handItemName"))
                    {
                        if(player != null)
                        {
                            text = player.getItemInHand().getType().name();
                        }
                        else
                        {
                            cs.sendMessage(ChatColor.RED+"[BA] A parameter of this alias requires a player.");
                        }
                    }
                    else if(text.equalsIgnoreCase("handItemID"))
                    {
                        if(player != null)
                        {
                            text = new Integer(player.getItemInHand().getTypeId()).toString();
                        }
                        else
                        {
                            cs.sendMessage(ChatColor.RED+"[BA] A parameter of this alias requires a player.");
                        }
                    }
                    else if(text.length() >= 2 && text.substring(1,2).equalsIgnoreCase("p"))
                    {                        
                        int iParam = -1;
                        
                        try
                        {  
                            iParam = Integer.parseInt(text.substring(0,1));  
                        }
                        catch(Exception ex){}

                        if(iParam > -1 && args.size() >= iParam)
                        {
                            String sPlayerName = args.get(iParam-1).toLowerCase();

                            text = "BetterAliasPlayerNotFound";

                            for(Player p : plugin.getServer().getOnlinePlayers())
                            {
                                if(p.getName().toLowerCase().contains(sPlayerName))
                                {
                                    text = p.getName();
                                    
                                    break;
                                }
                            }
                        }
                    }
                    else
                    {
                        int iParam = -1;

                        try
                        {
                            iParam = Integer.parseInt(text);
                        }
                        catch(Exception ex){}
                            
                        if(iParam > -1 && args.size() >= iParam)
                        {
                            text = args.get(iParam-1);
                        }
                        else
                        {
                            text = "";
                        }
                    }

                    m.appendReplacement(sb, Matcher.quoteReplacement(text));
                }

                m.appendTail(sb);
                        
                String sNewCommand = sb.toString();
                if(ac.type.equals(AliasCommandTypes.REPLY_MESSAGE))
                {
                    cs.sendMessage(sNewCommand);
                }
                else if(ac.type.equals(AliasCommandTypes.CONSOLE)
                || player == null)
                {
                    if(player != null)
                    {
                        plugin.getLogger().log(Level.INFO,"[BA] Running console command for "+player.getName()+": "+sNewCommand);
                    }
                    else
                    {
                        cs.sendMessage(ChatColor.AQUA+"[BA] Running: "+sNewCommand);
                    }

                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), sNewCommand);
                }
                else 
                {
                    player.chat(sNewCommand);
                }
            }
            
            return true;
        }
        
        return false;
    }
    
// Helper method
    public void copy(InputStream in, File file)
    {
        try
        {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0)
            {
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private String replaceColorCodes(String str)
    {
        for(ChatColor cc : ChatColor.values())
        {
            str = str.replace("&"+cc.name(), cc.toString());
        }
        
        return str;
    }
}
