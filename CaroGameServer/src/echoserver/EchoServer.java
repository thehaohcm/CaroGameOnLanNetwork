/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

/**
 *
 * @author thehaohcm
 */
import GUI.ServerApplication;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
 

public class EchoServer implements Runnable{
    ClientThread[] clients;
    int port=1995;
    ServerSocket server=null;
    int maxClient=10;
    DataOutputStream os;
    String availabelUser="@ListUser:All";
    ServerApplication serverUI;
    
    public EchoServer(ServerApplication serverUI){
        this.clients=new ClientThread[maxClient];
        this.port=1995;
        this.serverUI=serverUI;
        availabelUser="@ListUser:All";
        try{
            this.server=new ServerSocket(port);
            serverUI.logTxt.append("Đã khởi tạo Server ở cổng "+port+"\n");
        }catch(Exception ex){
            serverUI.logTxt.append("Không thể khởi tạo Server ở cổng "+port+"\n");
        }
        
    }
    
    public void writeServer(String str){
        serverUI.logTxt.append(str+"\n");
    }

    @Override
    public void run() {
        while(true){
            try{
                Socket client=server.accept();
                if(client.isConnected()){
                    int count=0;
                    for(int i=0;i<maxClient;i++){
                        if(clients[i]==null){
                            clients[i]=new ClientThread(this,client);
                            clients[i].start();
                            break;
                        }
                        count++;
                    }
                    if(count==maxClient){
                        os.writeUTF("Server đang bận, xin quay lại sau");
                        serverUI.logTxt.append("Không thể kết nối với Client vì Server đã đầy\n");
                    }
                }
                else
                    serverUI.logTxt.append("Không thể kết nối với Client");
            }catch(Exception ex){
                serverUI.logTxt.append("Không thể kết nối với Client");
            }
        }
    }
    
    public synchronized void sendUser() throws IOException{
        for(ClientThread cl:clients){
            if(cl!=null)
                availabelUser+=":"+cl.name;
        }
        //System.out.println(availabelUser);
        for(ClientThread cl:clients){
            if(cl!=null)
                cl.os.writeUTF(availabelUser);
        }
        availabelUser="@ListUser:All";
    }
    
    public boolean checkUsername(String user){
        int count=0;
        for(ClientThread cl:clients){
            if(cl!=null && cl.name.equals(user)){
                count++;
            }
        }
        if(count>1)
            return false;
        return true;
    }
    
    public synchronized void sendMessage(String from,String to,String msg) throws IOException{
        for (ClientThread cl : clients) {
            if (cl != null && (cl.name.equals(to)||cl.name.equals(from))) {
                serverUI.logTxt.append(msg+"\n");
                cl.os.writeUTF(msg);
            }
        }
    }
    
    public synchronized void sendMessage(String to, String msg) throws IOException{
        for (ClientThread cl : clients) {
            if (cl != null && cl.name.equals(to)) {
                if(msg.contains("@sendRequest"))
                    cl.os.writeUTF(msg+"|"+cl.client.getInetAddress().getHostAddress());
                    //cl.os.writeUTF(msg+"|"+cl.client.getRemoteSocketAddress().toString());
                else
                    cl.os.writeUTF(msg);
                break;
            }
        }
    }
    
    public synchronized void sendMessage(String msg) throws IOException{
        boolean flag = true;
        for (ClientThread cl : clients) {
            if (cl != null) {
                if (flag) {
                    serverUI.logTxt.append(msg + "\n");
                    flag = false;
                }
                cl.os.writeUTF(msg);
            }
        }
    }
    
    public synchronized void removeUser(ClientThread cl) throws IOException{
        long id=cl.getId();
        for(int i=0;i<maxClient;i++){
            if(clients[i]!=null&&clients[i].getId()==id){
                clients[i].close();
                clients[i].interrupt();
                clients[i]=null;
                break;
            }
        }
            
        sendUser();
    }
}

class ClientThread extends Thread{
    DataOutputStream os;
    DataInputStream is;
    EchoServer server;
    public Socket client;
    public String name;
    
    public ClientThread(EchoServer server,Socket client){
        super();
        this.server=server;
        this.client=client;
        
    }
    
     @Override
    public synchronized void run(){
        try{
            os=new DataOutputStream(client.getOutputStream());
            is=new DataInputStream(client.getInputStream());
            String str="";
            os.flush();
//            while((str=is.readUTF())!=null){
                name=is.readUTF();
//                str="";
//                break;
//            }
            if(server.checkUsername(name)==false){
                os.writeUTF("@CheckUser:false");
            }
            else{
                server.sendMessage("Tài khoản "+name+" vừa truy cập vào chat room");
                server.sendUser();
                while(true){
                    if((str=is.readUTF())!=null){
                        if(str.equals("/quit"))
                            break;
                        if(str.contains("@To")){
                            str=str.substring(str.indexOf(":")+1);
                            String user=str.substring(0, str.indexOf(":"));
                            str=str.substring(str.indexOf(":")+1);
                            server.sendMessage(name,user, "["+name+" > "+user+"]: "+str);
                        }
                        else if(str.contains("@sendUsers"))
                        {
                            server.sendUser();
                        }
                        else if(str.contains("@sendFile")){
                            String user=str.substring(str.indexOf(":")+1,str.indexOf("|"));
                            String path=str.substring(str.indexOf("|")+1);
                            //System.out.println("@sendFile: "+user+" "+path);
                            server.sendMessage(user,"@sendFile:"+name+"|"+path);
                        }
                        else if(str.contains("@sendRequest")){
                            String user=str.substring(str.indexOf(":")+1,str.indexOf("|"));
                            int port=Integer.parseInt(str.substring(str.indexOf("|")+1));
                            server.sendMessage(user, "@sendRequest:"+port);
                        }
                        else
                            server.sendMessage("["+name+" > "+"All]: "+str);
                    }
                }
            }
            server.sendMessage("Tài khoản "+name+" vừa rời khỏi chat room");
            server.removeUser(this);
        }catch(Exception ex){
            server.writeServer("Không thể tạo Thread #"+name);
            try {
                close();
            } catch (IOException ex1) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
    public void close() throws IOException{
        server=null;
        name=null;
        if(client!=null)
            client.close();
        if(is!=null)
            is.close();
        if(os!=null)
            os.close();
    }
    
}