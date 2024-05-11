package vistas;

import cocochatclient.CocoChatClient;
import modelos.User;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
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

/**
 *
 * @author rocob
 */
public class Login extends JFrame{
    
    private int contador = 0;
    private CocoChatClient cliente;  // Referencia al cliente

    public Login(CocoChatClient cliente) {
        super();
        this.cliente = cliente;
        start();
    }
    
    private void start(){
        setTitle("Login");
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
        
        JButton loginButton = new JButton("Ingresar");
        
        
        JLabel registerLabel = new JLabel("No tienes cuenta? ");
        JButton registerButton = new JButton("Registrate");
        registerButton.addActionListener(e -> {
            
        });
        
        JLabel errorLabel = new JLabel();
        
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(userLabel)
                .addComponent(passwordLabel)
                .addComponent(registerLabel)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(userText)
                .addComponent(passwordText)
                .addComponent(errorLabel)
                .addComponent(loginButton)
                .addComponent(registerButton)
            )
        );
        
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(userLabel)
                .addComponent(userText)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(passwordLabel)
                .addComponent(passwordText)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(errorLabel)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(loginButton)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(registerLabel)
                .addComponent(registerButton)
            )
        );
        
        add(panel);
        
        
        loginButton.addActionListener(e -> {
            String usuario = userText.getText();
            String contrasena = new String(passwordText.getPassword());
            User user = new User();
            user.setUser(usuario);
            user.setPassword(contrasena);
            
            PrintWriter out;
            try {
                Socket socket = new Socket("localhost", 12345);
                
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Login");
                out.println(usuario);
                out.println(contrasena);
                
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                int boleano = dataIn.readInt();
                
                if(boleano == 1)
                {
                    
                    System.out.println("Conectado con exito");
                    
                    NuevoChat chat = new NuevoChat(cliente,socket,usuario);
                    chat.setVisible(true);
                    this.setVisible(false);  
                    
                }
                else
                {
                    
                    contador++;
                    
                }
                if(contador == 3)
                {
                    
                    this.dispose();
            
                    Register r = new Register(cliente);
                    r.setVisible(true);
                    
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }

            
            
            
        });
        
        registerButton.addActionListener(e -> {
            
            this.dispose();
            
            Register r;
            try {
                r = new Register(cliente);
                r.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
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
