/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vistas;


import cocochatclient.CocoChatClient;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JTextField;
import modelos.User;

/**
 *
 * @author rocob
 */
public class Register extends JFrame{
    
    private Socket socket;
    private CocoChatClient cliente;
    
    public Register(CocoChatClient cliente) throws IOException {
        super();
        this.cliente = cliente;
        socket = new Socket("localhost",12345);
        start();
    }
    
    private void start() {
        setTitle("Registro");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        GroupLayout gl = new GroupLayout(panel);
        panel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        
        JLabel nameLabel = new JLabel("Nombre: ");
        JTextField nameTextField = new JTextField(20);
        
        JLabel lastNameLabel = new JLabel("Apellido: ");
        JTextField lastNameTextField = new JTextField(20);
        
        JLabel userLabel = new JLabel("Usuario: ");
        JTextField userTextField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Contraseña: ");
        JTextField passwordTextField = new JTextField(20);
        
        JButton registerButton = new JButton("Registrarse");
        
        JLabel forgotLabel = new JLabel("Olvidaste tu contraseña? ");
        JButton forgotButton = new JButton("Recuperar");
        
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.red);
        
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(nameLabel)
                .addComponent(lastNameLabel)
                .addComponent(userLabel)
                .addComponent(passwordLabel)
                .addComponent(forgotLabel)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(nameTextField)
                .addComponent(lastNameTextField)
                .addComponent(userTextField)
                .addComponent(passwordTextField)
                .addComponent(errorLabel)
                .addComponent(registerButton)
                .addComponent(forgotButton)
            )
        );
        
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(nameLabel)
                .addComponent(nameTextField)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lastNameLabel)
                .addComponent(lastNameTextField)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(userLabel)
                .addComponent(userTextField)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(passwordLabel)
                .addComponent(passwordTextField)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(errorLabel)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(registerButton)
            )
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(forgotLabel)
                .addComponent(forgotButton)
            )
        );
        
        registerButton.addActionListener(e -> {
            String name = nameTextField.getText();
            String lastName = lastNameTextField.getText();
            String user = userTextField.getText();
            String password = passwordTextField.getText();
            
            if (name.isBlank() || lastName.isBlank() || user.isBlank() || password.isBlank()) {
                errorLabel.setText("Campo vacio");
                return;
            }
            
            if (name.length() > 50) {
                errorLabel.setText("Nombre muy largo");
                return;
            }
            if (lastName.length() > 50) {
                errorLabel.setText("Apellido muy largo");
                return;
            }
            if (user.length() > 50) {
                errorLabel.setText("Usuario muy largo");
                return;
            }
            if (password.length() > 50) {
                errorLabel.setText("Contraseña muy larga");
                return;
            }
            
            User u = new User(name,lastName,user,password);
            
            PrintWriter out;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Register");
                out.println(name);
                out.println(lastName);
                out.println(user);
                out.println(password);
            } catch (IOException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            this.dispose();
            
            Login l = new Login(cliente);
            l.setVisible(true);  
            
        });
        
        add(panel);
        
        forgotButton.addActionListener(e -> {
            OlvidarContrasena o = new OlvidarContrasena(cliente);
            o.setVisible(true);
            this.setVisible(false);
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                //Login loginView = new Login();
                //loginView.setVisible(true);
                dispose();
            }
        });
    }
}
