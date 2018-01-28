/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CaroGame;

import java.io.Serializable;

/**
 *
 * @author thehaohcm
 */
public class Message implements Serializable{
    String From,To;
    Type type;
    String content;
    Icon icon;
    
    public Message(){
        this.From=null;
        this.To=null;
        this.type=null;
        this.content=null;
        this.icon=null;
    }
    
    public Message(String From,String To,Type type,String content,Icon icon){
        this.From=From;
        this.To=To;
        this.type=type;
        this.content=content;
        this.icon=icon;
    }
    
    public Message(Type type,String content,Icon icon){
        this.type=type;
        this.content=content;
        this.icon=icon;
    }
    
    public Type getType(){
        return type;
    }
    
    public Icon getIcon(){
        return icon;
    }
    
    public String getContent(){
        return content;
    }
}

