package com.gmail.Ne0nx3r0.AliasManager;

import com.gmail.Ne0nx3r0.BetterAlias;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AliasManager{
    private static Map<String,Alias> aliases;
    private static BetterAlias plugin;
    
    public AliasManager(BetterAlias ba){
        plugin = ba;
        
        loadAliases();
    }
    
    private void loadAliases(){
        aliases = new HashMap<String,Alias>();
        
        File configFile = new File(plugin.getDataFolder(), "aliases.yml");   
        
        if(!configFile.exists()){
            configFile.getParentFile().mkdirs();
            copy(plugin.getResource("aliases.yml"), configFile);
        }

        FileConfiguration yml = YamlConfiguration.loadConfiguration(configFile);

        Set<String> aliasList = yml.getKeys(false);
        
        if(aliasList.isEmpty()){
            plugin.log("No aliases found in aliases.yml");
            return;
        }
        
        for(String sAlias : aliasList){
            Map<String,String[]> shareMap = new HashMap<String,String[]>();
            
            for(int i=0;i<10;i++){
                
                if(yml.isList(sAlias+"."+i)){
                    
                    shareMap.put(Integer.toString(i),yml.getStringList(sAlias+"."+i).toArray(new String[]{}));
                    
                }else if(yml.isString(sAlias+"."+i)){
                    
                    shareMap.put(Integer.toString(i),new String[]{yml.getString(sAlias+"."+i)});
                    
                }
                
            }
            
            if(yml.isList(sAlias+".*")){
                shareMap.put("*",yml.getStringList(sAlias+".*").toArray(new String[]{}));

            }else if(yml.isString(sAlias+".*")){
                shareMap.put("*",new String[]{yml.getString(sAlias+".*")});
            }
            
            aliases.put(sAlias,new Alias(shareMap)); 
            
        }      
        
    }
    
    public boolean isAliased(String alias){
        return aliases.containsKey(alias);
    }
    
    public String[] getAliasCommands(String alias,int paramsCount){
        return aliases.get(alias).getCommands(paramsCount);
    }
    
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
