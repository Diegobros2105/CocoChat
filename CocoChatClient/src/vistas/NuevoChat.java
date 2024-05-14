package vistas;

import cocochatclient.CocoChatClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import modelos.Groups;

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
    private JList<String> groupOwnerList;
    private DefaultListModel<String> groupOwnerListModel;
    private JList<String> groupConformerList;
    private DefaultListModel<String> groupConformerListModel;
    private JList<String> groupUsuerConectedList;
    private DefaultListModel<String> groupUsuerConectedListModel;
    private JList<String> groupUsuerDesconectedList;
    private DefaultListModel<String> groupUsuerDesconectedListModel;
    
    private BufferedReader reader;
    private JTextArea messageArea;
    private JTextField messageInput;
    
    private String selected;
    private String selectedFriendConected;
    private String selectedFriendDesconected;
    private String selectedGroup;
    
    private ArrayList<Mensaje> mensajesNoAmigos = new ArrayList<>();
    private ArrayList<Mensaje> mensajesAmigos = new ArrayList<>();
    private ArrayList<Mensaje> mensajesGrupos = new ArrayList<>();
    
    private String invitationsFriends = new String();
    private String invitationsPosibleFriends = new String();
    private String bd = new String();
    private String invitationsGroups = new String();
    private String invitationsPosibleGroups = new String();
    private String createdGroups = new String();
    
    private String usuariosGrupo = new String();
    
    private JList<String> invitationsToGroupsList;
    private DefaultListModel<String> invitationsToGroupsListModel;
    
    private int contador = 0;
    private int conteo = 0;
    private int cont = 0;
    
    private String messagesUsers;
    private String messagesGroup;

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
                    selected = userList.getSelectedValue();
                    messageArea.setText("");
                    cont = 1;
                    for(Mensaje m : mensajesNoAmigos)
                    {
                        if(m.userEmisor.equals(selected) || m.userReceptor.equals(selected))
                        {
                           messageArea.append(m.userEmisor + ": " + m.message + "\n"); 
                        }
                    }
                    userDesconectedList.clearSelection();
                    userFriendConectedList.clearSelection();
                    userDesconectedList.clearSelection();
                    userFriendDesconectedList.clearSelection();
                    groupOwnerList.clearSelection();
                    groupConformerList.clearSelection();
                    groupUsuerConectedList.clearSelection();
                    groupUsuerDesconectedList.clearSelection();
                    invitationsToGroupsList.clearSelection();
                }
            }
        });
        
        userDesconectedModel = new DefaultListModel<>();
        userDesconectedList = new JList<>(userDesconectedModel);
        JScrollPane userDesconectedScrollPane = new JScrollPane(userDesconectedList);
        
        userDesconectedList.setEnabled(false);
        
        userFriendConectedModel = new DefaultListModel<>();
        userFriendConectedList = new JList<>(userFriendConectedModel);
        JScrollPane userFriendConectedScrollPane = new JScrollPane(userFriendConectedList);
        
        userFriendConectedList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events
                    selectedFriendConected = userFriendConectedList.getSelectedValue();
                    messageArea.setText("");
                    cont = 2;
                    
                    for(Mensaje mensaje : mensajesAmigos)
                    {
                        System.out.println(mensaje.message + " " + mensaje.userEmisor + " " + mensaje.userReceptor);
                        if(mensaje.userReceptor.equals(selectedFriendConected) || mensaje.userEmisor.equals(selectedFriendConected))
                        {
                            messageArea.append(mensaje.userEmisor + ": " + mensaje.message + "\n");
                        }
                    }
                    
                    userList.clearSelection();
                    userDesconectedList.clearSelection();
                    userFriendDesconectedList.clearSelection();
                    groupOwnerList.clearSelection();
                    groupConformerList.clearSelection();
                    groupUsuerConectedList.clearSelection();
                    groupUsuerDesconectedList.clearSelection();
                    invitationsToGroupsList.clearSelection();
                }
            }
        });
        
        userFriendDesconectedModel = new DefaultListModel<>();
        userFriendDesconectedList = new JList<>(userFriendDesconectedModel);
        JScrollPane userFriendDesconectedScrollPane = new JScrollPane(userFriendDesconectedList);
        
        userFriendDesconectedList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events
                    selectedFriendDesconected = userFriendDesconectedList.getSelectedValue();
                    messageArea.setText("");
                    cont = 3;
                    
                    for(Mensaje mensaje : mensajesAmigos)
                    {
                        System.out.println(mensaje.message + " " + mensaje.userEmisor + " " + mensaje.userReceptor);
                        if(mensaje.userReceptor.equals(selectedFriendDesconected) || mensaje.userEmisor.equals(selectedFriendDesconected))
                        {
                            messageArea.append(mensaje.userEmisor + ": " + mensaje.message + "\n");
                        }
                    }
                    
                    userList.clearSelection();
                    userFriendConectedList.clearSelection();
                    userDesconectedList.clearSelection();
                    groupOwnerList.clearSelection();
                    groupConformerList.clearSelection();
                    groupUsuerConectedList.clearSelection();
                    groupUsuerDesconectedList.clearSelection();
                    invitationsToGroupsList.clearSelection();
                }
            }
        });
        
        groupOwnerListModel = new DefaultListModel<>();
        groupOwnerList = new JList<>(groupOwnerListModel);
        JScrollPane groupOwnerScrollPane = new JScrollPane(groupOwnerList);
        
        groupOwnerList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events
                    selectedGroup = groupOwnerList.getSelectedValue();
                    messageArea.setText("");
                    contador = 1;
                    conteo = 1;
                    cont = 4;
                    
                    if(groupOwnerList.getSelectedValue() != null)
                    {
                        messageArea.setText("");
                        for(Mensaje m: mensajesGrupos)
                        {
                            if(m.userEmisor.equals(groupOwnerList.getSelectedValue()))
                            {
                                messageArea.append(m.userReceptor + ": " + m.message + "\n");
                            }
                        }
                    }
                    
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("SearchUsersInGroup");
                        out.flush();
                        out.println(selected);
                        out.flush();
                        
                    } catch (IOException ex) {
                        Logger.getLogger(NuevoChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    userList.clearSelection();
                    userFriendConectedList.clearSelection();
                    userDesconectedList.clearSelection();
                    userFriendDesconectedList.clearSelection();
                    groupConformerList.clearSelection();
                    groupUsuerConectedList.clearSelection();
                    groupUsuerDesconectedList.clearSelection();
                    
                }
            }
        });
        
        groupConformerListModel = new DefaultListModel<>();
        groupConformerList = new JList<>(groupConformerListModel);
        JScrollPane groupConformerScrollPane = new JScrollPane(groupConformerList);
        
        groupConformerList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events
                    selectedGroup = groupConformerList.getSelectedValue();
                    messageArea.setText("");
                    contador = 2;
                    conteo = 2;
                    cont = 4;
                    
                    if(groupConformerList.getSelectedValue() != null)
                    {
                        messageArea.setText("");
                        for(Mensaje m: mensajesGrupos)
                        {
                            if(m.userEmisor.equals(groupConformerList.getSelectedValue()))
                            {
                                messageArea.append(m.userReceptor + ": " + m.message + "\n");
                            }
                        }
                    }
                    
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("SearchUsersInGroup");
                        out.flush();
                        out.println(selected);
                        out.flush();
                        
                    } catch (IOException ex) {
                        Logger.getLogger(NuevoChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    userList.clearSelection();
                    userDesconectedList.clearSelection();
                    userFriendConectedList.clearSelection();
                    userDesconectedList.clearSelection();
                    userFriendDesconectedList.clearSelection();
                    groupOwnerList.clearSelection();
                    groupUsuerConectedList.clearSelection();
                    groupUsuerDesconectedList.clearSelection();
                    invitationsToGroupsList.clearSelection();
                }
            }
        });
        
        groupUsuerConectedListModel = new DefaultListModel<>();
        groupUsuerConectedList = new JList<>(groupUsuerConectedListModel);
        JScrollPane groupUsuerConectedScrollPane = new JScrollPane(groupUsuerConectedList);
        
        groupUsuerDesconectedListModel = new DefaultListModel<>();
        groupUsuerDesconectedList = new JList<>(groupUsuerDesconectedListModel);
        JScrollPane groupUsuerDesconectedListScrollPane = new JScrollPane(groupUsuerDesconectedList);
        
        groupUsuerConectedList.setEnabled(false);
        groupUsuerDesconectedList.setEnabled(false);
        
        JLabel usuarioConectado = new JLabel("Usuario Conectado");
        JLabel usuarioDesconectado = new JLabel("Usuario Desconectado");
        JLabel usuarioAmigoConectado = new JLabel("Amigo Conectado");
        JLabel usuarioAmigoDesconectado = new JLabel("Amigo Desconectado");
        JLabel grupoCreador = new JLabel("Grupos Creados");
        JLabel grupoCompuesto = new JLabel("Grupos Unidos");
        JLabel grupoUsuarioConectado = new JLabel("Uusarios Conectados");
        JLabel grupoUsuarioDesconectado = new JLabel("Usuarios Desconectados");

        messageArea = new JTextArea(10, 30);
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        
        messageInput = new JTextField(30);
        JButton sendButton = new JButton("Enviar");
        
        sendButton.addActionListener(e -> {
            String message = messageInput.getText();
            if(cont == 1)
            {
                if (message != null || this.selected != null) {
                
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        Mensaje m = new Mensaje(message,this.usuario,selected);
                        System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                        messageArea.append(m.userEmisor + ": " + m.message + "\n");
                        mensajesNoAmigos.add(m);
                        out.println("MessageUser");
                        out.flush();
                        out.println(message);
                        out.flush();
                        out.println(selected);
                        out.flush();
                        messageInput.setText(""); // Clear the input field after sending
                    } catch (IOException ex) {
                        Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else if(cont == 2)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    Mensaje m = new Mensaje(message,this.usuario,selectedFriendConected);
                    System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                    messageArea.append(m.userEmisor + ": " + m.message + "\n");
                    mensajesAmigos.add(m);
                    out.println("MessageFriend");
                    out.flush();
                    out.println(message);
                    out.flush();
                    out.println(selectedFriendConected);
                    out.flush();
                    messageInput.setText(""); // Clear the input field after sending
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(cont == 3)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    Mensaje m = new Mensaje(message,this.usuario,selectedFriendDesconected);
                    System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                    messageArea.append(m.userEmisor + ": " + m.message + "\n");
                    mensajesAmigos.add(m);
                    out.println("MessageFriendBD");
                    out.flush();
                    out.println(message);
                    out.flush();
                    out.println(selectedFriendDesconected);
                    out.flush();
                    messageInput.setText(""); // Clear the input field after sending
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(cont == 4)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    Mensaje m = new Mensaje(message,this.usuario,selectedGroup);
                    System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                    messageArea.append(m.userEmisor + ": " + m.message + "\n");
                    mensajesAmigos.add(m);
                    out.println("MessageGroup");
                    out.flush();
                    out.println(message);
                    out.flush();
                    out.println(selectedGroup);
                    out.flush();
                    messageInput.setText(""); // Clear the input field after sending
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JButton friendInvitation = new JButton("Friends");
        friendInvitation.addActionListener(e -> {
            FriendInvitations f;
            try {
                f = new FriendInvitations(this,cliente,socket,usuario,this.bd,this.invitationsFriends,this.invitationsPosibleFriends);
                f.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(NuevoChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        JButton groupInvitation = new JButton("Groups");
        groupInvitation.addActionListener(e -> {
            GroupInvitations g;
            try {
                g = new GroupInvitations(this,cliente,socket,usuario,bd,invitationsGroups,createdGroups);
                g.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(NuevoChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        JLabel invitacionGrupo = new JLabel("Invitaciones a Grupos");
        
        JButton enviarInvitacionGrupo = new JButton("Enviar Invitacion");
        enviarInvitacionGrupo.addActionListener(e -> {
            List<String> selectedItems = invitationsToGroupsList.getSelectedValuesList();
            String grupo = groupOwnerList.getSelectedValue();
            
            StringBuilder usuariosSeleccionados = new StringBuilder();
            for(String s : selectedItems)
            {
                if(usuariosSeleccionados.length() > 0) usuariosSeleccionados.append(",");
                usuariosSeleccionados.append(s);
            }
            System.out.println(usuariosSeleccionados);
                
            if(selectedItems.size() >= 1)
            {
                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("InvitationGroup");
                    out.flush();
                    out.println(usuariosSeleccionados.toString());
                    out.flush();
                    out.println(grupo);
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JButton eliminarGrupo = new JButton("Darse de baja de Grupo");
        eliminarGrupo.addActionListener(e -> {
            String grupo = groupOwnerList.getSelectedValue();
            String grupo2 = groupConformerList.getSelectedValue();
            if(conteo == 1)
            {
                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("DeleteGroup");
                    out.flush();
                    out.println(grupo);
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(conteo == 2)
            {
                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("DropDownGroup");
                    out.flush();
                    out.println(grupo2);
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        invitationsToGroupsListModel = new DefaultListModel<>();
        invitationsToGroupsList = new JList<>(invitationsToGroupsListModel);
        JScrollPane invitationsToGroupsScrollPane = new JScrollPane(invitationsToGroupsList);
        
        invitationsToGroupsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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
                    .addComponent(userDesconectedScrollPane,150,150,150)
                )
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(usuarioAmigoConectado,150,150,150)
                    .addComponent(userFriendConectedScrollPane,150,150,150)
                    .addComponent(usuarioAmigoDesconectado,150,150,150)
                    .addComponent(userFriendDesconectedScrollPane,150,150,150)
                )
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(grupoCreador,150,150,150)
                    .addComponent(groupOwnerScrollPane,150,150,150)
                    .addComponent(grupoCompuesto,150,150,150)
                    .addComponent(groupConformerScrollPane,150,150,150)
                    .addComponent(eliminarGrupo,150,150,150)
                )
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(invitacionGrupo,150,150,150)
                    .addComponent(invitationsToGroupsScrollPane,150,150,150)
                    .addComponent(enviarInvitacionGrupo,150,150,150)                     
                )
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(grupoUsuarioConectado,150,150,150)
                    .addComponent(groupUsuerConectedScrollPane,150,150,150)
                    .addComponent(grupoUsuarioDesconectado,150,150,150)
                    .addComponent(groupUsuerDesconectedListScrollPane,150,150,150)
                )
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(messageScrollPane)
                    .addComponent(messageInput)
                    .addComponent(sendButton)
                    .addComponent(friendInvitation,150,150,150)
                    .addComponent(groupInvitation,150,150,150)
                )   
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
                        .addComponent(grupoCreador,15,15,15)
                        .addComponent(groupOwnerScrollPane)
                        .addComponent(grupoCompuesto,15,15,15)
                        .addComponent(groupConformerScrollPane)
                        .addComponent(eliminarGrupo,25,25,25)
                    )
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(invitacionGrupo,15,15,15)
                        .addComponent(invitationsToGroupsScrollPane,150,150,150)
                        .addComponent(enviarInvitacionGrupo,25,25,25)                     
                    )
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(grupoUsuarioConectado,15,15,15)
                        .addComponent(groupUsuerConectedScrollPane)
                        .addComponent(grupoUsuarioDesconectado,15,15,15)
                        .addComponent(groupUsuerDesconectedListScrollPane)
                    )    
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(messageScrollPane)
                        .addComponent(messageInput,50,50,50)
                        .addComponent(sendButton,50,50,50)
                        .addComponent(friendInvitation,30,30,30)
                        .addComponent(groupInvitation,30,30,30)
                            
                    )
                    )
                    
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
                    System.out.println(contexto);
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
                        if(this.selected != null)
                        {
                            if(this.selected.equals(m.userEmisor))
                            {
                                messageArea.append(m.userEmisor + ": " + m.message + "\n");
                            }
                        }
                    }
                    else if(contexto.equals("MessageFriend"))
                    {
                        String message = reader.readLine();
                        String usuarioEmisor = reader.readLine();
                        Mensaje m = new Mensaje(message,usuarioEmisor,this.usuario);
                        System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                        mensajesAmigos.add(m);
                        if(this.selectedFriendConected != null)
                        {
                            if(this.selectedFriendConected.equals(m.userEmisor))
                            {
                                messageArea.append(m.userEmisor + ": " + m.message + "\n");
                            }
                        }
                    }
                    else if(contexto.equals("Groups"))
                    {
                        String listGroupCreator = reader.readLine();
                        String listGroupConform = reader.readLine();
                        System.out.println("Grupos creados:" + listGroupCreator);
                        System.out.println("Grupos unidos:" + listGroupConform);
                        updateListGroup(listGroupCreator,listGroupConform);
                        createdGroups = listGroupCreator;
                    }
                    else if(contexto.equals("UsuariosGrupo"))
                    {
                        String usuariosConectadosGrupo = reader.readLine();
                        String usuariosDesconectadosGrupo = reader.readLine();
                        
                        usuariosGrupo = usuariosConectadosGrupo + "," + usuariosDesconectadosGrupo;
                        
                        System.out.println("Usuarios Grupo Conectados: " + usuariosConectadosGrupo);
                        System.out.println("Usuarios Grupo Desconectados: " + usuariosDesconectadosGrupo);
                        
                        updateListGroupUser(usuariosConectadosGrupo,usuariosDesconectadosGrupo);
                        updateListGroupInvitation();
                    }
                    else if(contexto.equals("InvitationsFriends"))
                    {
                        String bd = reader.readLine();
                        String invitacionesAmigos = reader.readLine();
                        String posiblesInvitacionesAmigos = reader.readLine();
                        System.out.println("BD: " + bd);
                        System.out.println("Invitaciones: " + invitacionesAmigos);
                        System.out.println("Posibles invitaciones: " + posiblesInvitacionesAmigos);
                        
                        this.invitationsFriends = invitacionesAmigos;
                        this.invitationsPosibleFriends = posiblesInvitacionesAmigos;
                        this.bd = bd;
                    }
                    else if(contexto.equals("GroupsInvitations"))
                    {
                        String invitacionesGrupos = reader.readLine();
                        this.invitationsGroups = invitacionesGrupos;
                        System.out.println("Invitaciones Grupo: " + invitacionesGrupos);
                    }
                    else if(contexto.equals("MessagesGroup"))
                    {
                        String mensajesUsuariosEnviados = reader.readLine();
                        String mensajesGruposEnviados = reader.readLine();
                        this.messagesUsers = mensajesUsuariosEnviados;
                        this.messagesGroup = mensajesGruposEnviados;
                        for(String str : messagesUsers.split(","))
                        {
                            String[] s = str.split(":");
                            Mensaje m = new Mensaje(s[2],s[0],s[1]);
                            mensajesAmigos.add(m);
                            System.out.println(m.userEmisor + " " + m.userReceptor + " " + m.message);
                        }
                        System.out.println("Mensajes Usuarios: " + mensajesUsuariosEnviados);
                        System.out.println("Mensajes Grupos: " + mensajesGruposEnviados);
                        
                        updateGroups();
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
    
    private void updateListGroup(String groupOwnedData, String groupConfromedData) {
        SwingUtilities.invokeLater(() -> {
            groupOwnerListModel.clear();
            groupConformerListModel.clear();
            for(String pieza : groupOwnedData.split(","))
            {
                String[] go = new String[2];
                go = pieza.split(":");
                groupOwnerListModel.addElement(go[0]);
            }
            for(String pieza : groupConfromedData.split(","))
            {
                String[] gc = new String[2];
                gc = pieza.split(":");
                groupConformerListModel.addElement(gc[0]);
            }
        });
    }
    
    private void updateListGroupInvitation()
    {
        if(contador == 1)
        {
            String todos = this.bd;
            String quitar = this.usuariosGrupo;
            StringBuilder ultimo = new StringBuilder();

            for(String s : todos.split(","))
            {
                if(!quitar.contains(s))
                {
                    if(ultimo.length() > 0) ultimo.append(",");
                    ultimo.append(s);
                }
            }
            System.out.println("Usuarios no en el grupo: " + ultimo);

            SwingUtilities.invokeLater(() -> {
                invitationsToGroupsListModel.clear();
                for(String pieza : ultimo.toString().split(","))
                {
                    if(!pieza.equals(usuario))
                    {
                        invitationsToGroupsListModel.addElement(pieza);
                    }
                }
            });
        }
        else
        {
            SwingUtilities.invokeLater(() -> {
                invitationsToGroupsListModel.clear();
            });
        }
    }
    
    private void updateListGroupUser(String usersConectedData, String usersDesconectedData) {
        SwingUtilities.invokeLater(() -> {
            groupUsuerConectedListModel.clear();
            groupUsuerDesconectedListModel.clear();
            for(String pieza : usersConectedData.split(","))
            {
                if(!pieza.equals(usuario))
                {
                    groupUsuerConectedListModel.addElement(pieza);
                }
            }
            for(String pieza : usersDesconectedData.split(","))
            {
                if(!pieza.equals(usuario))
                {
                    groupUsuerDesconectedListModel.addElement(pieza);
                }
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

    private void updateGroups() {
        mensajesGrupos = new ArrayList<>();
        for(String str : messagesGroup.split(","))
        {
            String[] s = str.split(":");
            Mensaje m = new Mensaje(s[2],s[0],s[1]);
            mensajesGrupos.add(m);
        }
        if(groupOwnerList.getSelectedValue() != null)
        {
            messageArea.setText("");
            for(Mensaje m: mensajesGrupos)
            {
                if(m.userEmisor.equals(groupOwnerList.getSelectedValue()))
                {
                    messageArea.append(m.userReceptor + ": " + m.message + "\n");
                }
            }
        }
        if(groupConformerList.getSelectedValue() != null)
        {
            messageArea.setText("");
            for(Mensaje m: mensajesGrupos)
            {
                if(m.userEmisor.equals(groupConformerList.getSelectedValue()))
                {
                    messageArea.append(m.userReceptor + ": " + m.message + "\n");
                }
            }
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
