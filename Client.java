import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
// import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Client extends JFrame{

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel();
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public Client() {
        
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1",7777);
            System.out.println("Connection done ");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            
            createGUI();
            handleEvents();

            startreading();
            // startwriting(); used when writes in cmd
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        
        messageInput.addKeyListener(new KeyListener() {
            @Override

            public void keyTyped(KeyEvent e){

            }

            @Override
            public void keyPressed(KeyEvent e){
                
            }

            @Override
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode()==10){
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me: "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                }
            }
        });
    }

    private void createGUI(){
        this.setTitle("Client msg: ");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        // heading.setIcon(new ImageIcon("")); use to create icon 
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);

        this.setLayout(new BorderLayout());

        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollpane = new JScrollPane(messageArea);
        this.add(jScrollpane,BorderLayout.CENTER);
        jScrollpane.setAutoscrolls(true);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void startreading(){
        Runnable r1=()->{
            System.out.println("Reader started");

            try{
            while(true){ 
                String msg = br.readLine();
                if(msg.equals("exit")){
                    System.out.println("Server terminated chat");
                    JOptionPane.showMessageDialog(this, "Server terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                }
                // System.out.println("Server: "+msg);
                messageArea.append("Server: "+msg+"\n");
            }
        }
        catch(Exception e){
            // e.printStackTrace();
            System.out.println("Connection closed");
        }
    };
    new Thread(r1).start();
}

    public void startwriting(){
        Runnable r2=()->{
            System.out.println("Writer started");
            try {
            while(!socket.isClosed()){
                
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args){
        System.out.println("this is client");
        new Client();
    }
}
