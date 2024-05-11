package vistas;

import cocochatclient.CocoChatClient;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelos.User;

public class OlvidarContrasena extends JFrame{
    
    private CocoChatClient cliente;  // Referencia al cliente

    public OlvidarContrasena(CocoChatClient cliente) {
        super();
        this.cliente = cliente;
        start();
    }
    
    private void start(){
        setTitle("OlvidarContrasena");
        setSize(300, 190);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        GroupLayout gl = new GroupLayout(panel);
        panel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        
        JLabel userLabel = new JLabel("Usuario: ");
        JTextField userText = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Contraseña: ");
        JPasswordField passwordText = new JPasswordField(20);
        JLabel passwordLabelNew = new JLabel(" ");
        
        JButton buscarButton = new JButton("Buscar");
        
        
        JLabel loginLabel = new JLabel("Regresa al Login ");
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            
        });
        
        JLabel errorLabel = new JLabel();
        
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGroup(
                gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(userLabel)
                    .addComponent(passwordLabel)
                    .addComponent(loginLabel)
            )
            .addGroup(
                gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(userText)
                    .addComponent(passwordLabelNew)
                    .addComponent(buscarButton)
                    .addComponent(loginButton)
            )
        );
        
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(userLabel)
                    .addComponent(userText)
            )
            .addGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordLabelNew)
            )
            .addComponent(buscarButton)
            .addGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(loginLabel)
                    .addComponent(loginButton)
            )
        );
        
        add(panel);
        
        
        buscarButton.addActionListener(e -> {
            String usuario = userText.getText();
            User user = new User();
            
            PrintWriter out;
            try {
                Socket socket = new Socket("localhost", 12345);
                
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("OlvidarContrasena");
                out.flush();
                out.println(usuario);
                out.flush();

                String respuesta = in.readLine();
                System.out.println("Contraseña recibida: " + respuesta);
                
                passwordLabelNew.setText(respuesta);
                
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }

            
            
            
        });
        
        loginButton.addActionListener(e -> {
            
            this.dispose();
            
            Login l;
            l = new Login(cliente);
            l.setVisible(true);
            
            
        });
        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                setVisible(false);
                dispose();
            }
        });
    }
}