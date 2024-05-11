package vistas;

import cocochatclient.CocoChatClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class NuevoChat extends JFrame {

    private final String usuario;
    private CocoChatClient cliente;
    private Socket socket;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JList<String> userDesconectedList;
    private DefaultListModel<String> userDesconectedModel;
    private JList<String> userFriendConectedList;
    private DefaultListModel<String> userFriendConectedModel;
    private JList<String> userFriendDesconectedList;
    private DefaultListModel<String> userFriendDesconectedModel;
    private JList<String> groupList;
    private DefaultListModel<String> groupListModel;
    private BufferedReader reader;
    private JTextArea messageArea;
    private JTextField messageInput;
    
    private String userReceptor;
    
    private ArrayList<Mensaje> mensajesNoAmigos = new ArrayList<>();
    private ArrayList<Mensaje> mensajesAmigos = new ArrayList<>();
    private ArrayList<Mensaje> mensajesGrupos = new ArrayList<>();

    public NuevoChat(CocoChatClient cliente, Socket socket, String usuario) {
        super();
        this.cliente = cliente;
        this.socket = socket;
        this.usuario = usuario;
        initUI();
        
        startListening();
    }

    private void initUI() {
        setTitle(usuario);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        
        userList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events
                    userReceptor = userList.getSelectedValue();
                    messageArea.setText("");
                    for(Mensaje m : mensajesNoAmigos)
                    {
                        if(m.userEmisor.equals(userReceptor) || m.userReceptor.equals(userReceptor))
                        {
                           messageArea.append(m.userEmisor + ": " + m.message + "\n"); 
                        }
                    }
                    userDesconectedList.clearSelection();
                }
            }
        });
        
        userDesconectedModel = new DefaultListModel<>();
        userDesconectedList = new JList<>(userDesconectedModel);
        JScrollPane userDesconectedScrollPane = new JScrollPane(userDesconectedList);
        
        userDesconectedList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events
                    userReceptor = userDesconectedList.getSelectedValue();
                    messageArea.setText("");
                    
                    userList.clearSelection();
                    
                }
            }
        });
        
        userFriendConectedModel = new DefaultListModel<>();
        userFriendConectedList = new JList<>(userFriendConectedModel);
        JScrollPane userFriendConectedScrollPane = new JScrollPane(userFriendConectedList);
        
        userFriendDesconectedModel = new DefaultListModel<>();
        userFriendDesconectedList = new JList<>(userFriendDesconectedModel);
        JScrollPane userFriendDesconectedScrollPane = new JScrollPane(userFriendDesconectedList);
        
        
        
        JLabel usuarioConectado = new JLabel("Usuario Conectado");
        JLabel usuarioDesconectado = new JLabel("Usuario Desconectado");
        JLabel usuarioAmigoConectado = new JLabel("Amigo Conectado");
        JLabel usuarioAmigoDesconectado = new JLabel("Amigo Desconectado");

        messageArea = new JTextArea(10, 30);
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        
        messageInput = new JTextField(30);
        JButton sendButton = new JButton("Enviar");
        
        sendButton.addActionListener(e -> {
            String message = messageInput.getText();
            if (!message.isEmpty() || !this.userReceptor.isEmpty()) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    Mensaje m = new Mensaje(message,this.usuario,userReceptor);
                    System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                    messageArea.append(m.userEmisor + ": " + m.message + "\n");
                    mensajesNoAmigos.add(m);
                    out.println("MessageUser");
                    out.println(message);
                    out.flush();
                    out.println(userReceptor);
                    out.flush();
                    messageInput.setText(""); // Clear the input field after sending
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JPanel panel = new JPanel();
        GroupLayout gl = new GroupLayout(panel);
        panel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(
            gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(usuarioConectado,150,150,150)
                    .addComponent(userScrollPane,150,150,150)
                    .addComponent(usuarioDesconectado,150,150,150)
                    .addComponent(userDesconectedScrollPane,150,150,150))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(usuarioAmigoConectado,150,150,150)
                    .addComponent(userFriendConectedScrollPane,150,150,150)
                    .addComponent(usuarioAmigoDesconectado,150,150,150)
                    .addComponent(userFriendDesconectedScrollPane,150,150,150))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(messageScrollPane)
                    .addComponent(messageInput)
                    .addComponent(sendButton))
        );

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(usuarioConectado,15,15,15)
                        .addComponent(userScrollPane)
                        .addComponent(usuarioDesconectado,15,15,15)
                        .addComponent(userDesconectedScrollPane)
                    )
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(usuarioAmigoConectado,15,15,15)
                        .addComponent(userFriendConectedScrollPane)
                        .addComponent(usuarioAmigoDesconectado,15,15,15)
                        .addComponent(userFriendDesconectedScrollPane)
                    )
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(messageScrollPane)
                        .addComponent(messageInput,50,50,50)
                        .addComponent(sendButton,50,50,50)))
        );

        add(panel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        setVisible(true);
    }

    private void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String contexto;
                while ((contexto = reader.readLine()) != null) {
                    System.out.println("Contexto: " + contexto);
                    if (contexto.equals("Conexion")) {
                        String usuariosConectados = reader.readLine();
                        String usuariosDesconectados = reader.readLine();
                        String usuariosAmigosConectados = reader.readLine();
                        String usuariosAmigosDesconectados = reader.readLine();
                        updateList(usuariosConectados,usuariosDesconectados,usuariosAmigosConectados,usuariosAmigosDesconectados);
                        System.out.println("Clientes Conectados: " + usuariosConectados);
                        System.out.println("Clientes Desconectados: " + usuariosDesconectados);
                        System.out.println("Clientes Amigos Conectados: " + usuariosAmigosConectados);
                        System.out.println("Clientes Amigos Desconectados: " + usuariosAmigosDesconectados);
                    }
                    else if(contexto.equals("MessageUser"))
                    {
                        String message = reader.readLine();
                        String usuarioEmisor = reader.readLine();
                        Mensaje m = new Mensaje(message,usuarioEmisor,this.usuario);
                        System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                        mensajesNoAmigos.add(m);
                        if(this.userReceptor != null)
                        {
                            if(this.userReceptor.equals(m.userEmisor))
                            {
                                messageArea.append(m.userEmisor + ": " + m.message + "\n");
                            }
                        }
                    }
                    else if(contexto.equals("Friends"))
                    {
                        String amigos = reader.readLine();
                        System.out.println(amigos);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al leer del socket: " + e.getMessage());
            }
        });
        listenerThread.start();
    }

    private void updateList(String userData, String userDataDesconected, String userDataFriendConected, String userDataFriendDesconected) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            userDesconectedModel.clear();
            userFriendConectedModel.clear();
            userFriendDesconectedModel.clear();
            for (String user : userData.split(",")) {
                if(!user.equals(this.usuario))
                {
                   userListModel.addElement(user); 
                }
            }
            for(String user : userDataDesconected.split(","))
            {
                userDesconectedModel.addElement(user);
            }
            for(String user : userDataFriendConected.split(","))
            {
                userFriendConectedModel.addElement(user);
            }
            for(String user : userDataFriendDesconected.split(","))
            {
                userFriendDesconectedModel.addElement(user);
            }
        });
    }

    private void logout() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Logout");
            socket.close();
        } catch (IOException e) {
            Logger.getLogger(NuevoChat.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            setVisible(false);
            dispose();
        }
    }
    
    class Mensaje {
        
        public String message;
        public String userEmisor;
        public String userReceptor;
        
        public Mensaje(String message, String userEmisor, String userReceptor)
        {
            
            this.message = message;
            this.userEmisor = userEmisor;
            this.userReceptor = userReceptor;
            
        }
        
    }
    
}
