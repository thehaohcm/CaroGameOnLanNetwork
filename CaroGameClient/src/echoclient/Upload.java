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

/**
 *
 * @author thehaohcm
 */
public class Upload implements Runnable {
    DataOutputStream os;
    Socket socket;
    String filepath;
    int port;
    String host;
    FileInputStream file;
    ClientApplication chatGUI;
    
    public Upload(ClientApplication chatGUI,String host,int port,String filepath) throws IOException{
        this.chatGUI=chatGUI;
        this.host=host;
        this.port=port;
        this.filepath=filepath;
        System.out.println("filepath: "+filepath);
        socket=new Socket(InetAddress.getByName(host),port);
        os=new DataOutputStream(socket.getOutputStream());
        file=new FileInputStream(filepath);
    }
    
    public String getHost(){
        return host;
    }
    
    public int getPort(){
        return port;
    }
    
    @Override
    public void run() {
        try {
            byte[] bf = new byte[1024];
            int count;
            while ((count = file.read(bf)) >= 0) {
                os.write(bf, 0, count);
            }
            os.flush();
            if (file != null) {
                file.close();
            }
            if (os != null) {
                os.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println("Đã có lỗi xảy ra, không thể upload file");
        }
        
    }
    
}
