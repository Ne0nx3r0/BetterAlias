package com.gmail.Ne0nx3r0.AliasManager;

import java.util.HashMap;
import java.util.Map;

class Alias{
    //mapping number of parameters to string(s) to be executed
    private Map<String,String[]> params = new HashMap<String,String[]>();
    
    public Alias(Map<String,String[]> p){
        this.params = p;
    }
    
    public String[] getCommands(int p){
        if(this.params.containsKey(Integer.toString(p))){
            return this.params.get(Integer.toString(p));
        }
        return this.params.get("*");//or null if it doesn't exist
    }
}