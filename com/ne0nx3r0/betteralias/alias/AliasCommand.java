package com.ne0nx3r0.betteralias.alias;

public class AliasCommand
{
    final String command;
    final AliasCommandTypes type;
    
    public AliasCommand(String command,AliasCommandTypes type)
    {
        this.command = command;
        this.type = type;
    }
}
