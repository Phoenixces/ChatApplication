import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Server extends JFrame{
    
    ServerSocket server;
    Socket socket;

    BufferedReader br;//for extracing stream from client socket
    PrintWriter out;//For Outputting stream 
    private JLabel heading  = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    //private JTextArea messageInput = new JTextArea(3, 10);
    private JTextArea messageInput = new JTextArea();
    private Font font = new Font("Roboto", Font.PLAIN, 20);



    //Constructor
    public Server(){

        //7777 tells which socket to use for server n at which port client would find Server
        try{
            server = new ServerSocket(7777);
            System.out.println("server is ready to accept connection");
            System.out.println("waiting....");

            socket = server.accept();//sever is accepting object of Socker from Client 

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
            //startWriting();
            // Above func implements terminal based writing mode which is now replaced by GUI and handleEvent func
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Handling Events
    private void handleEvents(){

        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
               
            }

            @Override
            public void keyPressed(KeyEvent e) {
            
            }

            @Override
            public void keyReleased(KeyEvent e) {

                ///"Enter" keyCode is 10
                if(e.getKeyCode() == 10){
                    String contentToSend = messageInput.getText();
                    
                    if(contentToSend.equals("exit")){
                        messageInput.setEnabled(false);
                        messageArea.setText("\nConnection Closed..");
                    }
                    messageArea.append("Me : " + contentToSend );
                    out.print(contentToSend);
                    out.flush();
                    
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
            
        });
    }

    //GUI Function
    private void createGUI(){

        //this tells about the window
        this.setTitle("Server[END]");
        this.getContentPane().setBackground(Color.CYAN);
        
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for components
        heading.setFont(font);
        messageArea.setFont(font);

        //Beloe func will help in autoscrolling scrollbar
        messageArea.setCaretPosition(messageArea.getDocument().getLength());

        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);
        messageInput.setFont(font);
        messageInput.setBorder(BorderFactory.createMatteBorder(10, 5, 10 , 5, Color.cyan));
        
        heading.setIcon(new ImageIcon("chat.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        //below function set uneditable messageArea
        messageArea.setEditable(false);
        //messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        //frame layout
        this.setLayout(new BorderLayout());

        //Adding the components to frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);


        this.setVisible(true);
    }

    //Below two func needs to be performed simulataneously
    public void startReading(){

        //thread - reads stream 
        Runnable r1 = ()->{

            System.out.println("Reader started"); 
            try{
            while(!socket.isClosed()){
                
                
                String msg = br.readLine();
                if(msg.equals("exit")){
                    //System.out.println("Client terminated the chat");
                    JOptionPane.showMessageDialog(this, "Client Terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                }

                
                //System.out.println("client: " + msg);
                messageArea.append("Client : " + msg + "\n");
                
                
            }
        }
        catch(Exception e){
            //e.printStackTrace();
            System.out.println("Closing connection..");
            
        }
        };

        new Thread(r1).start();
    }

    public void startWriting(){

        //thread - server start writing data for cient
        Runnable r2 = ()->{

            System.out.println("Writer started");

            try{
            while(!socket.isClosed()){
                
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));//take from console
                    String content  = br1.readLine();
                    out.println(content);
                    out.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                    


            }
        }
        catch(Exception e){
            //e.printStackTrace();
            System.out.println("closing connection....");
        }
        };

        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Server...\nServer Starting");
        new Server();
    }
}