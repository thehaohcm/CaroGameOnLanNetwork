package CaroGame;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.LineBorder;
import CaroGame.*;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CaroGameApp extends JFrame {

    private String name = "";
    private String rival = "";
    public String host = "localhost";
    public int port = 1959;
    private char whoseTurn = 'X';
    private boolean pause=false;
    private boolean gameOver = false;
    private Cell[][] cells = new Cell[15][15];
    JLabel jlblStatus;
    private Thread thread;
    public JPanel panel;
    private JPanel contrlPnl;
    private CaroThread caroCommand;
    public JLabel lblKlockan;
    public JLabel lblStatus;
    public TimeCounter timer;
//    private JRadioButton jrdbtnName,jrdbtnRival;

    public CaroGameApp(String host,String name,String rival) {
        this.host=host;
        this.name = name;
        this.rival=rival;
        jlblStatus = new JLabel("Lượt đi của "+name+" (Bạn)");
        panel = new JPanel(new GridLayout(15, 15, 0, 0));
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                panel.add(cells[i][j] = new Cell(j,i));
            }
        }
        
        panel.setBorder(new LineBorder(Color.red, 1));
        jlblStatus.setBorder(new LineBorder(Color.yellow, 1));
        add(panel, BorderLayout.CENTER);
        add(jlblStatus, BorderLayout.SOUTH);
        
        contrlPnl=new JPanel(new GridLayout(1,0,0,0));
        contrlPnl.setBorder(new LineBorder(Color.black,1));
        
        add(contrlPnl,BorderLayout.EAST);
        
        JPanel ctrlPnl=new JPanel(new BorderLayout());
        contrlPnl.add(ctrlPnl,BorderLayout.NORTH);
        lblStatus=new JLabel("Game Caro");
        lblStatus.setFont(new Font("Courier",Font.BOLD,30));
        ctrlPnl.add(lblStatus,BorderLayout.PAGE_START);
        
//        JPanel ctrlPnl1=new JPanel();
//        contrlPnl.add(ctrlPnl1,BorderLayout.AFTER_LAST_LINE);
        lblKlockan=new JLabel();
        lblKlockan.setFont(new Font("Courier",Font.PLAIN,30));
        ctrlPnl.add(lblKlockan,BorderLayout.WEST);

        JButton jbtnClose=new JButton("Thoát chương trình");
        jbtnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                close();
                System.exit(0);
            }
        });
        ctrlPnl.add(jbtnClose,BorderLayout.AFTER_LAST_LINE);
        
//        jrdbtnName=new JRadioButton("Lượt đi của X");
//        jrdbtnName.setFont(new Font("Courier",Font.PLAIN,20));
//        jrdbtnName.setEnabled(false);
//        jrdbtnRival=new JRadioButton("Lượt đi của O");
//        jrdbtnRival.setFont(new Font("Courier",Font.PLAIN,20));
//        jrdbtnRival.setEnabled(false);
//        
//        jrdbtnName.setSelected(true);
//        jrdbtnRival.setSelected(false);
//        ctrlPnl1.add(jrdbtnName);
//        ctrlPnl1.add(jrdbtnRival);

        //Time Counter
        timer=new TimeCounter(this);
        
        
        caroCommand=new CaroThread(this);
        thread = new Thread(caroCommand);
        thread.start();
    }

    public String getName() {
        return name;
    }
    
    public String getRival(){
        return rival;
    }

    public boolean isFull() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (cells[i][j].getToken() == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void setEnableCells(boolean flag) { //tắt, mở Cell khi đến lượt người chơi
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                cells[i][j].setEnabled(flag);
            }
        }
    }

    public void Move(int y,int x) { //cột, hàng
        //System.out.println("Client da nhan dinh vi: x:"+x+" | y:"+y);
        if (pause==true && cells[y][x].getToken() == ' ') {
            whoseTurn='O';
            cells[y][x].setToken(whoseTurn);
            pause=false;
            
            //cells[y][x].repaint();
            
            //whoseTurn = (whoseTurn == 'X') ? 'O' : 'X';
            //jlblStatus.setText("Lượt đi của " + whoseTurn);
            if (isWon(whoseTurn)) {
//                if (whoseTurn == 'X') {
//                    jlblStatus.setText("Bạn đã chiến thắng, xin chúc mừng bạn");
//                    showMsg(new Message(CaroGame.Type.msg, "Bạn đã chiến thắng, xin chúc mừng", Icon.information));
//                } else {
                
                if(whoseTurn=='O'){
                    jlblStatus.setText("Tiếc quá, bạn đã thua");
                    timer.Stop();
                    showMsg(new Message(CaroGame.Type.msg, "Tiếc quá, bạn đã thua", Icon.information));
                    
                }
                whoseTurn = ' ';
                gameOver = true;
            } else if (isFull()) {
                jlblStatus.setText("Tie game! Game over!");
                whoseTurn = ' ';
                gameOver = true;
            }
            
            whoseTurn='X';
            jlblStatus.setText("Lượt đi của " + ((whoseTurn=='X')?(name+" (Bạn)"):(rival+" (Đối thủ)")));
        }
        setEnableCells(true);
    }

    public void showMsg(Message msg) {
        if(msg.content.contains("thiết lập"))
            timer.Start();
        if (msg.getIcon().equals(Icon.error)) {
            JOptionPane.showMessageDialog(null, msg.getContent(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } else if (msg.getIcon().equals(Icon.information)) {
            JOptionPane.showMessageDialog(null, msg.getContent(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else if (msg.getIcon().equals(Icon.warning)) {
            JOptionPane.showMessageDialog(null, msg.getContent(), "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, msg.getContent(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean isWon(char token) {
        //kiểm trang hàng ngang
        int count=0;
        for (int i = 0; i < 15; i++) {
            count=0;
            for(int j=0;j<15;j++){
                if(cells[i][j].getToken()==token)
                    count++;
                else
                    count=0;
                if(count==5)
                    return true;
            }
        }
        
        //kiểm trang cột dọc
        for (int i = 0; i < 15; i++) {
            count=0;
            for (int j = 0; j < 15; j++) {
                if(cells[j][i].getToken()==token)
                    count++;
                else
                    count=0;
                if(count==5)
                    return true;
            }
        }
        
        
        //kiểm tra đường chéo chính 2
        for(int j=0;j<=10;j++){
            count=0;
            for(int i=0;i<=14-j;i++){
                if(cells[j+i][i].getToken()==token)
                    count++;
                else
                    count=0;
                if(count==5)
                    return true;
            }
        }
        
        //kiểm tra đường chéo chính 1
        for(int i=0;i<=10;i++){
            count=0;
            for(int j=0;j<=14-i;j++){
                if(cells[j][i+j].getToken()==token)
                    count++;
                else
                    count=0;
                if(count==5)
                    return true;
            }
        }
        
        //kiểm tra đường chéo phụ 2
        for(int i=0;i<=10;i++){
            count=0;
            for(int j=0;j<=14-i;j++){
                if(cells[14-j][j+i].getToken()==token)
                    count++;
                else
                    count=0;
                if(count==5)
                    return true;
            }
        }
        
        //kiểm tra đường chéo phụ 1
        for(int j=14;j>=4;j--){
            count=0;
            for(int i=0;i<=j;i++){
                if(cells[j-i][i].getToken()==token)
                    count++;
                else
                    count=0;
                if(count==5)
                    return true;
            }
        }
        
        return false;
    }

    public void setSubClient() {
        whoseTurn='O';
        jlblStatus.setText("Lượt đi của "+((whoseTurn=='X')?(name+" (Bạn)"):(rival+" (Đối thủ)")));
        this.pause=true;
    }
    
    public void close(){
        try{
            timer.Stop();
            caroCommand.sendMessage(new Message(CaroGame.Type.close,name,Icon.information));
            caroCommand.stop();
            caroCommand=null;
            
        }
        catch(Exception ex){
            showMsg(new Message(CaroGame.Type.msg,"Không thể tắt luồng từ server về client",Icon.error));
        }
    }

//    public class MoveListener implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent ae) {
//
//        }
//
//    }

    public class Cell extends JPanel {
        // token of this cell

        private char token = ' ';
        private int x;
        private int y;

        /**
         * Constructor
         */
        public Cell(int x, int y) {
            setBorder(new LineBorder(Color.DARK_GRAY, 1));
            addMouseListener(new MyMouseListener());
            this.x = x;
            this.y = y;
        }

        /**
         * Gets the token of the cell.
         *
         * @return The token value of the cell.
         */
        public char getToken() {
            return token;
        }

        /**
         * Sets the token of the cell.
         *
         * @param c Character to use as token value.
         */
        public void setToken(char c) {
            token = c;
            repaint();
            //whoseTurn=(whoseTurn == 'X')?'O':'X';
            //jlblStatus.setText("Lượt đi của " + whoseTurn);
            if(whoseTurn=='X')
                setEnableCells(true);
            else
                setEnableCells(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try{
                
                if (token == 'X') {
                        caroCommand.sendLocation(new Location(name,rival,y,x));
                        //Location local=new Location(name,rival,y,x);
                        //Message msglocal=new Message(name, rival, CaroGame.Type.msg, "test", Icon.error);
                        //caroCommand.send(local);
                        //caroCommand.send(new Message(CaroGame.Type.welcome,getName(),Icon.information));

                        //caroCommand.send(new String("Anh Hào rất đẹp trai"));
                        g.setFont(new Font("Courier", Font.BOLD, 30));
                        g.setColor(Color.red);
                        g.drawLine(10, 10, getWidth() - 10, getHeight() - 10);
                        g.drawLine(getWidth() - 10, 10, 10, getHeight() - 10);
                        setEnableCells(false);

                } else if (token == 'O') {
                    g.setFont(new Font("Courier", Font.BOLD, 30));
                    g.setColor(Color.blue);
                    g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
    //                jrdbtnName.setSelected(true);
    //                jrdbtnRival.setSelected(false);
                    setEnableCells(false);
                }
                
            }catch(Exception ex){
                    showMsg(new Message(CaroGame.Type.msg,"Không thể gửi thông tin đến máy chủ, bạn vui lòng kiểm tra lại",Icon.error));
                    
//                jrdbtnName.setSelected(false);
//                jrdbtnRival.setSelected(true);
            }
            
        }

        private class MyMouseListener extends MouseAdapter {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver) {
                    return;
                }

                // if the cell is empty and the game is not over
                if (pause==false && token == ' ' && whoseTurn != ' ') {
                    setToken(whoseTurn);
                    pause=true;
                    
                    if(isWon(whoseTurn)){
                        if(whoseTurn=='X'){
                            jlblStatus.setText("Bạn đã chiến thắng, xin chúc mừng");
                            timer.Stop();
                            showMsg(new Message(CaroGame.Type.msg,"Bạn đã chiến thắng, xin chúc mừng",Icon.information));
                        }
                        whoseTurn=' ';
                        gameOver=true;
                    }
                    
                    whoseTurn = 'O';
                    jlblStatus.setText("Lượt đi của " + ((whoseTurn=='X')?(name+" (Bạn)"):(rival+" (Đối thủ)")));
                }

                // Check game status
//                if (isWon(whoseTurn)) {
//                    if (whoseTurn == 'X') {
//                        jlblStatus.setText("Bạn đã chiến thắng, xin chúc mừng bạn");
//                        showMsg(new Message(CaroGame.Type.msg,"Bạn đã chiến thắng, xin chúc mừng",Icon.information));
//                    }
//                    else{
//                        jlblStatus.setText("Thật tiếc, bạn đã thua");
//                        showMsg(new Message(CaroGame.Type.msg,"Thật tiếc, bạn đã thua",Icon.information));
//                    }
//                    whoseTurn = ' ';
//                    gameOver = true;
//                } else if (isFull()) {
//                    jlblStatus.setText("Tie game! Game over!");
//                    whoseTurn = ' ';
//                    gameOver = true;
//                } 
//                else {
//                        whoseTurn = (whoseTurn == 'X') ? 'O' : 'X';
//                        jlblStatus.setText("Lượt đi của " + whoseTurn);
//                }
            }
            
        } // end class MyMouseListener
    } // end class Cell
}
