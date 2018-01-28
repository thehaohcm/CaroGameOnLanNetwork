/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CaroGame;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author thehaohcm
 */
public class TimeCounter {
    private long secondPassed=0;
    private CaroGameApp GUI;
    private Timer myTimer=new Timer();
    private TimerTask task=new TimerTask(){
        @Override
        public void run() {
            secondPassed++;
            int hours=(int)secondPassed/3600;
            int remainder=(int)secondPassed - hours * 3600;
            int minute=remainder/60;
            remainder=remainder - minute * 60;
            int second=remainder;
            String hours_str=Integer.toString(hours);
            String minute_str=Integer.toString(minute);
            String second_str=Integer.toString(second);
            if(hours<10)
                hours_str="0"+hours_str;
            if(minute<10)
                minute_str="0"+minute_str;
            if(second<10)
                second_str="0"+second_str;
                
            GUI.lblKlockan.setText(hours_str+" : "+minute_str+" : "+second_str);
        }
        
    };
    
    public TimeCounter(CaroGameApp GUI){
        this.GUI=GUI;
    }
    
    public void Start(){
        myTimer.scheduleAtFixedRate(task, 1000, 1000);
    }
    
    public void Stop(){
        task.cancel();
        myTimer.cancel();
        myTimer.purge();
    }
}
