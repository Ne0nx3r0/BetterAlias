package com.gmail.Ne0nx3r0.AliasManager;

import java.util.HashMap;
import java.util.Map;

class Alias{
    //mapping number of parameters to string(s) to be executed
    private Map<Integer,String[]> params = new HashMap<Integer,String[]>();
    
    public Alias(Map<Integer,String[]> p){
        this.params = p;
    }
    
    public String[] getCommands(int p){
        return this.params.get(p);
    }
}