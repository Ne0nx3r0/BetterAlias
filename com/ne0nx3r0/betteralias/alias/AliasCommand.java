package com.ne0nx3r0.betteralias.alias;

public class AliasCommand
{
    final String command;
    final AliasCommandTypes type;
    int waitTime;
    
    public AliasCommand(String command,AliasCommandTypes type,int waitTime)
    {
        this.command = command;
        this.type = type;
        
        if(waitTime > 0)
        {
            this.waitTime = waitTime;
        }
    }
}
