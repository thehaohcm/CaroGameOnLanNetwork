/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import GUI.ClientApplication;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Client implements Runnable {

    int port = 1995;
    String host = "localhost";
    BufferedReader bf;
    DataInputStream is;
    DataOutputStream os;
    Socket client;
    ClientApplication chatGUI;
    String username;
    boolean flag=true;
    String filepath;
    
    
    public Client(ClientApplication chatGUI) throws IOException{
        this.chatGUI = chatGUI;
        this.port = chatGUI.port;
        this.host = chatGUI.host;
        
    }
    
    @Override
    public void run() {
        try {
            client = new Socket(InetAddress.getByName(host), port);
            if (client.isConnected()) {
                os = new DataOutputStream(client.getOutputStream());
                os.flush();
                os.writeUTF(chatGUI.username);
                os.flush();
                is = new DataInputStream(client.getInputStream());
                bf = new BufferedReader(new InputStreamReader(System.in));
            } else {
                chatGUI.ChatAreatxt.append("Không thể kết nối với Server\n");
                client.close();
            }
            String str = "";
            while (flag) {
                if ((str = is.readUTF()) != null) {
                    try {
                        if (str.contains("@ListUser")) {
                            String usr[];
                            usr = str.split(":");
                            for (String u : usr) {
                                if (u.equals("@ListUser")) {
                                    continue;
                                }
                                //System.out.println("user: "+u);
                            }
                            chatGUI.getUser(usr);
                        } else if (str.equals("@CheckUser:false")) {
                            chatGUI.doDiscExistUser();
                        } //                    else if(str.equals("/quit")){
                        //                        stop();
                        //                    }
                        else if (str.contains("@sendFile")) {
                            String user = str.substring(str.indexOf(":") + 1, str.indexOf("|"));
                            //String filepath=str.substring(str.indexOf("|")+1);
                            String file = str.substring(str.lastIndexOf("\\") + 1);

                            if (JOptionPane.showConfirmDialog(chatGUI, "Bạn có muốn nhận file " + file + " từ " + user, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                JFileChooser jfile = new JFileChooser();
                                jfile.setSelectedFile(new File(file));
                                String filepath;
                                jfile.showSaveDialog(chatGUI);
                                if (jfile.getSelectedFile() != null) {
                                    filepath = jfile.getSelectedFile().getPath();
                                    Download dl = new Download(chatGUI, filepath);
                                    System.out.println("filepath destination: " + filepath);
                                    Thread dlThread = new Thread(dl);
                                    dlThread.start();
                                    os.writeUTF("@sendRequest:" + user + "|" + dl.getPort());

                                }
                            }
                        } else if (str.contains("@sendRequest")) {
                            int port = Integer.parseInt(str.substring(str.indexOf(":") + 1, str.indexOf("|")));
                            String host = str.substring(str.indexOf("|") + 1);
                            Upload upl = new Upload(chatGUI, host, port, filepath);
                            System.out.println("filepath source: " + filepath);
                            Thread uplThread = new Thread(upl);
                            uplThread.start();
                        } else {
                            chatGUI.ChatAreatxt.append(str + "\n");
                            //System.out.println(str);
                        }
                    } catch (Exception ex) {
                        chatGUI.ChatAreatxt.append("Không thể nhận được tin nhắn, vui lòng kiểm tra lại kết nối với Server\n");
                    }
                }
            }
        } catch (IOException ex) {
            chatGUI.ChatAreatxt.append("Không thể nhận được tin nhắn, vui lòng kiểm tra lại kết nối với Server\n");
            //System.out.println("Không nhận được tin nhắn, vui lòng kiểm tra lại kết nối với Server\n");   
        }

    }
    
    public synchronized void send(String msg){
        try{
            if(msg.equals("/quit")){
                chatGUI.ChatAreatxt.append("Xin tạm biệt "+chatGUI.username+"\n");
                os.writeUTF(msg);
            }
            else{
                os.writeUTF(msg);
                os.flush();
            }
            
        }
        catch(Exception ex){
            chatGUI.ChatAreatxt.append("Không thể gửi tin nhắn, vui lòng kiểm tra lại kết nối với Server\n");
        }
    }
    
    public synchronized void stop() throws IOException{
        flag=false;
        if(client!=null) client.close();
        if(os!=null) os.close();
        if(is!=null) is.close();
    }
    
    public void setFilePath(String filepath){
        this.filepath=filepath;
    }
}
