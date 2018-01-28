/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import GUI.ClientApplication;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author thehaohcm
 */
public class Download implements Runnable {
    ServerSocket server;
    Socket socket;
    DataInputStream is;
    FileOutputStream file;
    int port;
    String host;
    String path;
    ClientApplication chatGUI;
    
    public Download(ClientApplication chatGUI,String path) throws IOException{ 
        try{
            this.chatGUI=chatGUI;
            server=new ServerSocket(0);
            port=server.getLocalPort();
            this.path=path;
        }catch(Exception ex){
            System.out.println("Đã có lỗi xảy ra");
        }
        
    }
    
    @Override
    public void run() {
        try {
            socket=server.accept();
            file=new FileOutputStream(path);
            is=new DataInputStream(socket.getInputStream());
            byte[] bf=new byte[1024];
            int count;
            while((count=is.read(bf))>=0){
                file.write(bf, 0, count);
            }
            file.close();
            is.close();
            server.close();
            JOptionPane.showMessageDialog(chatGUI,"Đã download file thành công","Thành công",JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(chatGUI,"Không thể download file thành công","Lỗi",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public String getHost(){
        return host;
    }
    public int getPort(){
        return port;
    }
}
