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
public class Location implements Serializable{
    private String From,To;
    private int x;
    private int y;
    
    public Location(){
        this.From=null;
        this.To=null;
        this.y=0;
        this.x=0;
    }
    public Location(String From,String To,int y,int x){
        this.From=From;
        this.To=To;
        this.x=x;
        this.y=y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setLocation(int y,int x){
        this.x=x;
        this.y=y;
    }
    
    public String ToString(){
        return "x:"+x+" | y:"+y;
    }
    
    public String getFrom(){
        return From;
    }
    public String getTo(){
        return To;
    }
}
