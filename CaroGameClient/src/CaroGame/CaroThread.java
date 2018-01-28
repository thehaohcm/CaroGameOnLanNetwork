/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CaroGame;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author thehaohcm
 */
public class CaroThread implements Runnable {
    int port = 1959;
    String host = "localhost";
    //BufferedReader bf;
    ObjectInputStream is;
    ObjectOutputStream os;
    Socket client;
    CaroGameApp caroGUI;
    String name;
    String rival;
    boolean flag=true;
    
    public CaroThread(CaroGameApp caroGUI){
        this.caroGUI=caroGUI;
        this.host=caroGUI.host;
        this.port=caroGUI.port;
        this.name=caroGUI.getName();
        this.rival=caroGUI.getRival();
    }

    @Override
    public void run() {
        try {
            //client = new Socket(InetAddress.getByName(host), port);
            client=new Socket(InetAddress.getByName(host),port);
            if (client.isConnected()) {
                
                os = new ObjectOutputStream(client.getOutputStream());
                is = new ObjectInputStream(client.getInputStream());
                os.flush();
                //os.writeUTF(caroGUI.getName()); //Gửi đến server tên người chơi
                os.writeObject(new Message(Type.welcome,name,Icon.information));
                os.writeObject(new Message(Type.check,rival,Icon.information));
                //os.writeObject(new Message(name,rival,Type.msg, "Anh Hào rất đẹp trai",Icon.information));
                os.flush();
                Object stream=null,oldstream=null;
                //caroGUI.showMsg(new Message(Type.msg,"Đã kết với với Server ở cổng 1959",Icon.information));
                while(flag){
                    if (((stream = is.readObject()) != null)&&(stream.equals(oldstream)==false)) {
                        try {
                            if(stream instanceof Message){
                                Message msg_t=(Message)stream;
                                if(msg_t.getType().equals(Type.msg)){
                                    caroGUI.showMsg((Message)stream);
                                }
                                else if(msg_t.getType().equals(Type.sub)){
                                    caroGUI.setSubClient();
                                    caroGUI.showMsg(msg_t);
                                }
                            }
                            else if(stream instanceof Location){
                                Location local=(Location)stream;
                                caroGUI.Move(local.getY(), local.getX());
                                //System.out.println("Đã nhận thông tin từ Client: "+local.ToString());
                            }
                            else if(stream instanceof String){
                                //System.out.println("String Test bla bla: "+(String)stream);
                            }
                            oldstream=stream;
                        }catch(Exception ex){
                            caroGUI.showMsg(new Message(Type.msg,"Không nhận được tín hiệu từ người chơi",Icon.error));
                        }
                    }
                }
            } else {
                client.close();
            }
        }catch(Exception ex){
            flag=false;
        }
    }
    
//    public synchronized void send(Object obj) throws IOException{
//        Location local=(Location)obj;
//        System.out.println("Da gui dinh vi: "+local.ToString());
//        os.flush();
//        os.writeObject(obj);
//    }
    
    public void sendLocation(Location local) throws IOException{
        os.flush();
        os.writeObject(local);
    }
    
    public void sendString(String str) throws IOException{
        os.flush();
        os.writeObject(str);
    }
    
    public void sendMessage(Message msg) throws IOException{
        os.flush();
        os.writeObject(msg);
    }
    
    
    public void stop() throws IOException{
        flag=false;
        if(client!=null) client.close();
        if(os!=null) os.close();
        if(is!=null) is.close();
    }
}
