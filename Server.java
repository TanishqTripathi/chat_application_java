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

public class Server extends JFrame{
     
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel();
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public Server(){
        try{
            server = new ServerSocket(7777);
            System.out.println("Server is ready for connection");
            System.out.println("Waiting....");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startreading();
            // startwriting();
        }
        catch(Exception e){
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
        this.setTitle("Server msg: ");
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
            while(!socket.isClosed()){
                String msg = br.readLine();
                if(msg.equals("exit")){
                    System.out.println("Client terminated chat");
                    JOptionPane.showMessageDialog(this, "Client terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                }
                // System.out.println("Client: "+msg);
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
        System.out.println("this is server");
        new Server();
    }
}
