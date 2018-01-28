/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CaroGame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author thehaohcm
 */
public class Caro {
    public static void main(String[] args) {
        // TODO code application logic here
        //CaroGameApp caro =new CaroGameApp("The","Hao");
        CaroGameApp caro;
        if(args.length!=0){
            caro=new CaroGameApp(args[0],args[1],args[2]);
        }
        else{
            String host="",name="",rival="";
            while((host!=null&&host.trim().equals(""))){
                host=JOptionPane.showInputDialog("Bạn vui lòng nhập vào IP Server");
                if(host==null)
                    System.exit(0);
                if(host.length()>15)
                    host="";
            }
            while(name!=null&&name.trim().equals("")){
                name=JOptionPane.showInputDialog("Bạn vui lòng nhập vào Tên người chơi");
                if(name==null)
                    System.exit(0);
                if(name.length()>10)
                    name="";
            }
            while(rival!=null&&rival.trim().equals("")){
                rival=JOptionPane.showInputDialog("Bạn vui lòng nhập vào Tên đối thủ");
                if(rival==null)
                    System.exit(0);
                if(rival.length()>10)
                    rival="";
            }
            
            caro=new CaroGameApp(host,name,rival);
            //caro=new CaroGameApp("Hào","Thế");
        }
        caro.setSize(800, 700);
        caro.setResizable(false);
        caro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        caro.setLocationRelativeTo(null);
        caro.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        caro.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                //frame.dispose();
                caro.close();
                caro.dispose();
            }
        });
        caro.setVisible(true);
        caro.setTitle("Game Caro qua mạng Lan - "+caro.getName()+" vs. "+caro.getRival());
    }
    
}
