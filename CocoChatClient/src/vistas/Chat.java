package vistas;

import cocochatclient.CocoChatClient;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Chat extends JFrame {
    
    private CocoChatClient cliente;  // Referencia al cliente
    private Socket socket;
    private JTextArea messageArea;
    private BufferedReader reader;

    public Chat(CocoChatClient cliente, Socket socket) {
        super();
        this.cliente = cliente;
        this.socket = socket;
        initUI();
        startListening();
    }
    
    private void initUI() {
        setTitle("ChatGeneral");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        messageArea = new JTextArea(20, 50);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        JTextField inputField = new JTextField(50);
        JButton sendButton = new JButton("Send");
        
        JPanel panel = new JPanel();
        GroupLayout gl = new GroupLayout(panel);
        panel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addComponent(scrollPane)
            .addGroup(gl.createSequentialGroup()
                .addComponent(inputField)
                .addComponent(sendButton))
        );
        
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(scrollPane)
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(inputField)
                .addComponent(sendButton))
        );
        
        add(panel);
        
        sendButton.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    messageArea.append("You: " + message + "\n");
                    out.println("ChatGeneral");
                    out.println(message);
                    inputField.setText(""); // Clear the input field after sending
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });
        
        setVisible(true);
    }

    private void startListening() {
        new Thread(() -> {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String message = line; // Assuming the sender's name is sent first
                    String sender = reader.readLine(); // Then, the actual message
                    messageArea.append(sender + ": " + message + "\n");
                }
            } catch (IOException e) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, e);
            }
        }).start();
    }

    private void logout() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Logout");
            socket.close();
        } catch (IOException e) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            setVisible(false);
            dispose();
        }
    }
}

