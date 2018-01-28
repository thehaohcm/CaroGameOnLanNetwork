/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CaroGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author thehaohcm
 */
public class ServerGame implements Runnable {
    private ClientGame[] clients;
    private int port=1959;
    private int maxClient=5;
    private ObjectOutputStream os;
    private ServerSocket server;
    private ServerGameApp GUI;
    private Socket client;
    
    public ServerGame(ServerGameApp GUI) throws IOException{
        this.clients=new ClientGame[5];
        this.GUI=GUI;
        
        try{
            server=new ServerSocket(port);
            GUI.txtLog.append("Đã khởi tạo server ở cổng "+port);
        }catch(Exception ex){
            GUI.txtLog.append("Không thể tạo server ở cổng "+port);
            server.close();
            
        }
        
    }
    
    public void writeServer(String txt){
        GUI.txtLog.setText(txt+"\n");
    }
    
    @Override
    public void run(){
        while(true){
            try{
                client=new Socket();
                client=server.accept();
                if(client.isConnected()){
                    int count=0;
                    for(int i=0;i<maxClient;i++){
                        if(clients[i]==null){
                            clients[i]=new ClientGame(this,client);
                            clients[i].start();
                            break;
                        }
                        count++;
                    }
                    if(count==maxClient){
                        //os.writeUTF("Server đang bận, xin quay lại sau");
                        GUI.txtLog.append("Không thể kết nối với Client vì Server đã đầy");
                    }
                }else{
                    GUI.txtLog.append("Không thể kết nối với Client");
                }
            }
            catch(Exception ex){
                GUI.txtLog.append("Không thể kết nối với Client");
            }
        }
    }
    
    public boolean checkName(String name){
        for(ClientGame client:clients)
            if(client!=null && client.checkName(name))
                return true;
        return false;
    }
    
    public synchronized void sendString(String str){
        for(ClientGame client:clients){
            if(client!=null){
                try{
                    client.sendString(str);
                }catch(Exception ex){
                    System.out.println("Không thể gửi đến client "+client.getName());
                }
            }
        }
    }
    
    public synchronized void sendMessage(Message msg){
        for(ClientGame client:clients){
            if(client!=null&&(client.checkName(msg.To)==true)){
                try{
                    client.sendMessage(msg);
                }catch(Exception ex){
                    GUI.txtLog.append("Không thể gửi thông tin đến "+msg.To);
                }
            }
        }
    }
    
    public synchronized void sendLocation(Location local){
        //System.out.println("Server da nhan dinh vi: "+local.ToString());
        for(ClientGame client:clients){
//            String to=local.getTo();
//            
            if(client!=null && client.checkName(local.getTo())){
            //if(client!=null){
                try{
                    client.sendLocation(local);
                }catch(Exception ex){
                    GUI.txtLog.append("Không thể gửi nội dung đến "+client.getName());//+to);
                }
            }
//                break;
//            }
        }
    }
    
    public synchronized void closeThread(ClientGame cl) throws IOException{
        long id = cl.getId();
        for (int i = 0; i < maxClient; i++) {
            if (clients[i] != null && clients[i].getId() == id) {
                clients[i].close();
                clients[i].interrupt();
                clients[i] = null;
                break;
            }
        }
    }
}

class ClientGame extends Thread{
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private ServerGame server;
    private Socket socket;
    private String name;
    private boolean flag=true;
    
    public void setEnabledFlag(boolean flag){
        this.flag=flag;
    }
    
    public boolean checkName(String name){
        if(this.name.equals(name))
            return true;
        return false;
    }
    
    public ClientGame(ServerGame server,Socket socket){
        this.server=server;
        this.socket=socket;
    }
    
    @Override
    public void run(){
        try {
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            Message msg;
            Location local;
            os.flush();
            Object obj;
            while(flag){
                try{
                    if((obj=is.readObject())!=null){// && stream instanceof Message){
                        if(obj instanceof Message)
                        {
                            msg=(Message)obj;
                            if(msg.type==Type.welcome)
                            {
                                this.name=msg.content;
                                continue;
                            }
                            else if(msg.type==Type.check){
                                if(server.checkName(msg.content)){
                                    sendMessage(new Message("server,",name,Type.sub,"Luồng chơi đã được thiết lập",Icon.information));
                                    server.sendMessage(new Message("server",msg.content,Type.msg,"Luồng chơi đã được thiết lập",Icon.information));
                                }
                            }
                            else if(msg.type==Type.close){
                                server.closeThread(this);
                            }
                            else
                                server.sendMessage(msg);
                            //System.out.println("Đã nhận message");
                            continue;
                        }
                        else if(obj instanceof Location){
                            local=(Location)obj;
                            server.sendLocation(local);
                            //System.out.println("Đã nhận location");
                            continue;
                        }
                        else if(obj instanceof String){
                            String str=(String)obj;
                            server.sendString(str);
                            continue;
                        }
                    
                    }
                }catch(Exception ex)
                {
                    System.out.println("Không lấy được nội dung");
                    flag=false;
                }
            }
        } catch (Exception ex) {
            System.out.println("Đã có lỗi xảy ra");
        }
    }
    
    public void sendMessage(Message msg) throws IOException{
        
        os.flush();
        os.writeObject(msg);
    }
    
    public void close() throws IOException{
        
        
        if(is!=null){
            is.close();
            is=null;
        }
        if(os!=null){
            os.close();
            os=null;
        }
        if(socket!=null){
            socket.close();
            socket=null;
        }
        server=null;
        name=null;
    }

    public void sendLocation(Location local) throws IOException {
        os.flush();
        os.writeObject(local);
    }

    public void sendString(String str) throws IOException {
        os.flush();
        os.writeObject(str);
    }
}