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
        startListening(); //Se inicializa el Hilo que espera respuestas del servidor
    }

    private void initUI() {
        setTitle(usuario);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        
        userList.addListSelectionListener(new ListSelectionListener() { //Lista que muestra los usuarios conectados
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Esta linea previene eventos dobles
                    selected = userList.getSelectedValue(); //Se selecciona el usuario conectado
                    messageArea.setText("");
                    cont = 1;
                    for(Mensaje m : mensajesNoAmigos) //A partir de los mensajes de los no amigos se colocan los mensajes en el area del texto
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
        
        userDesconectedModel = new DefaultListModel<>(); //Lista que muestra los usuarios desconectados
        userDesconectedList = new JList<>(userDesconectedModel);
        JScrollPane userDesconectedScrollPane = new JScrollPane(userDesconectedList);
        
        userDesconectedList.setEnabled(false); //Hace que no se pueda dar clic
        
        userFriendConectedModel = new DefaultListModel<>();
        userFriendConectedList = new JList<>(userFriendConectedModel);
        JScrollPane userFriendConectedScrollPane = new JScrollPane(userFriendConectedList);
        
        userFriendConectedList.addListSelectionListener(new ListSelectionListener() { //Lista que muestra amigos conectados
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Esta linea previene eventos dobles
                    selectedFriendConected = userFriendConectedList.getSelectedValue();
                    messageArea.setText("");
                    cont = 2;
                    
                    for(Mensaje mensaje : mensajesAmigos) //A partir de los mensajes de los amigos se colocan los mensajes en el area del texto
                    {
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
        
        userFriendDesconectedList.addListSelectionListener(new ListSelectionListener() { //Lista que muestra amigos desconectados
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Esta linea previene eventos dobles
                    selectedFriendDesconected = userFriendDesconectedList.getSelectedValue();
                    messageArea.setText("");
                    cont = 3;
                    
                    for(Mensaje mensaje : mensajesAmigos) //A partir de los mensajes de los amigos desconectados se colocan los mensajes en el area del texto
                    {
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
        
        groupOwnerList.addListSelectionListener(new ListSelectionListener() { //Lista que muestra grupos creados por el usuario
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Esta linea previene eventos dobles
                    selectedGroup = groupOwnerList.getSelectedValue();
                    messageArea.setText("");
                    contador = 1;
                    conteo = 1;
                    cont = 4;
                    
                    if(groupOwnerList.getSelectedValue() != null)
                    {
                        messageArea.setText("");
                        for(Mensaje m: mensajesGrupos) //A partir de los mensajes de los grupos se colocan los mensajes en el area del texto
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
                        out.println(selectedGroup);
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
        
        groupConformerList.addListSelectionListener(new ListSelectionListener() { //Lista que muestra grupos unidos
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Esta linea previene eventos dobles
                    selectedGroup = groupConformerList.getSelectedValue();
                    messageArea.setText("");
                    contador = 2;
                    conteo = 2;
                    cont = 4;
                    
                    if(groupConformerList.getSelectedValue() != null)
                    {
                        messageArea.setText("");
                        for(Mensaje m: mensajesGrupos) //A partir de los mensajes de los grupos unidos se colocan los mensajes en el area del texto
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
                        out.println(selectedGroup);
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
        
        groupUsuerConectedListModel = new DefaultListModel<>(); //Lista que muestra los usuarios conectados del grupo seleccionado
        groupUsuerConectedList = new JList<>(groupUsuerConectedListModel);
        JScrollPane groupUsuerConectedScrollPane = new JScrollPane(groupUsuerConectedList);
        
        groupUsuerDesconectedListModel = new DefaultListModel<>(); //Lista que muestra los usuarios desconectados del grupo seleccionado
        groupUsuerDesconectedList = new JList<>(groupUsuerDesconectedListModel);
        JScrollPane groupUsuerDesconectedListScrollPane = new JScrollPane(groupUsuerDesconectedList);
        
        groupUsuerConectedList.setEnabled(false); //Hace que no se pueda dar clic
        groupUsuerDesconectedList.setEnabled(false); //Hace que no se pueda dar clic
        
        JLabel usuarioConectado = new JLabel("Usuario Conectado");
        JLabel usuarioDesconectado = new JLabel("Usuario Desconectado");
        JLabel usuarioAmigoConectado = new JLabel("Amigo Conectado");
        JLabel usuarioAmigoDesconectado = new JLabel("Amigo Desconectado");
        JLabel grupoCreador = new JLabel("Grupos Creados");
        JLabel grupoCompuesto = new JLabel("Grupos Unidos");
        JLabel grupoUsuarioConectado = new JLabel("Uusarios Conectados");
        JLabel grupoUsuarioDesconectado = new JLabel("Usuarios Desconectados");

        messageArea = new JTextArea(10, 30); //Aqui es donde se muestran todos los mensajes
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        
        messageInput = new JTextField(30); //Aqui es donde se escribe el mensaje
        JButton sendButton = new JButton("Enviar"); //Aqui es con lo que se envia
        
        sendButton.addActionListener(e -> {
            String message = messageInput.getText();
            if(cont == 1)
            {
                if (message != null || this.selected != null) {
                
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        Mensaje m = new Mensaje(message,this.usuario,selected);
                        //La clase abajo tiene el constructor. Es el mensaje, el usuario que lo envia y el usuario al que se envia
                        System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                        messageArea.append(m.userEmisor + ": " + m.message + "\n");
                        //Se agrega el mensaje al message area, el cual es Usuario:Mensaje
                        mensajesNoAmigos.add(m);
                        //Se agrega al ArrayList de los mensajesNoAmigos
                        out.println("MessageUser"); //Se envia el contexto
                        out.flush();
                        out.println(message); //Se envia el mensaje
                        out.flush();
                        out.println(selected); //Se envia el usuario destinatario
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
                    //La clase abajo tiene el constructor. Es el mensaje, el usuario que lo envia y el usuario al que se envia
                    System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                    messageArea.append(m.userEmisor + ": " + m.message + "\n");
                    //Se agrega el mensaje al message area, el cual es Usuario:Mensaje
                    mensajesAmigos.add(m);
                    //Se agrega al ArrayList de los mensajesAmigos
                    out.println("MessageFriend"); //Se envia el contexto
                    out.flush();
                    out.println(message); //Se envia el mensaje
                    out.flush();
                    out.println(selectedFriendConected); //Se envia el usuario destinatario
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
                    //La clase abajo tiene el constructor. Es el mensaje, el usuario que lo envia y el usuario al que se envia
                    System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                    messageArea.append(m.userEmisor + ": " + m.message + "\n");
                    //Se agrega el mensaje al message area, el cual es Usuario:Mensaje
                    mensajesAmigos.add(m);
                    //Se agrega al ArrayList de los mensajesAmigos
                    out.println("MessageFriendBD"); //Se envia el contexto
                    out.flush();
                    out.println(message); //Se envia el mensaje
                    out.flush();
                    out.println(selectedFriendDesconected); //Se envia el usuario destinatario
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
                    //La clase abajo tiene el constructor. Es el mensaje, el usuario que lo envia y el usuario al que se envia
                    System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                    messageArea.append(m.userEmisor + ": " + m.message + "\n");
                    //Se agrega el mensaje al message area, el cual es Usuario:Mensaje
                    mensajesGrupos.add(m);
                    //Se agrega al ArrayList de los mensajesGrupos
                    out.println("MessageGroup"); //Se envia el contexto
                    out.flush();
                    out.println(message); //Se envia el mensaje
                    out.flush();
                    out.println(selectedGroup); //Se envia el usuario destinatario
                    out.flush();
                    messageInput.setText(""); // Clear the input field after sending
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JButton friendInvitation = new JButton("Friends"); //Envia a la pesataña de amigos
        friendInvitation.addActionListener(e -> {
            FriendInvitations f;
            try {
                f = new FriendInvitations(this,cliente,socket,usuario,this.bd,this.invitationsFriends,this.invitationsPosibleFriends);
                f.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(NuevoChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        JButton groupInvitation = new JButton("Groups"); //Envia a la pestaña de grupos
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
        
        JButton enviarInvitacionGrupo = new JButton("Enviar Invitacion"); //Envia la invitacion de todos los usuarios seleccionados del grupo creado
        enviarInvitacionGrupo.addActionListener(e -> {
            List<String> selectedItems = invitationsToGroupsList.getSelectedValuesList(); //Todos los usuarios invitados
            String grupo = groupOwnerList.getSelectedValue(); //El grupo creado seleccionado
            
            StringBuilder usuariosSeleccionados = new StringBuilder();
            for(String s : selectedItems) //Agrega al StringBuilder cada uno de los usuarios al que se envia la invitacion
            {
                if(usuariosSeleccionados.length() > 0) usuariosSeleccionados.append(",");
                usuariosSeleccionados.append(s);
            }
            System.out.println(usuariosSeleccionados);
                
            if(selectedItems.size() >= 1) //Comprobacion para que sean al menos 3 usuarios en el grupo
            {
                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("InvitationGroup"); //String de contexto
                    out.flush();
                    out.println(usuariosSeleccionados.toString()); //Seleccion de usuarios al enviar la invitacion
                    out.flush();
                    out.println(grupo); //El grupo al que se hace la invitacion
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JButton eliminarGrupo = new JButton("Darse de baja de Grupo"); //Elimina o da debaja del grupo seleccionado
        eliminarGrupo.addActionListener(e -> {
            String grupo = groupOwnerList.getSelectedValue();
            String grupo2 = groupConformerList.getSelectedValue();
            if(conteo == 1) //El grupo tiene que ser creado por el usuario
            {
                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("DeleteGroup"); //String de contexto
                    out.flush();
                    out.println(grupo); //El grupo al que se elimina
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(conteo == 2) //El grupo tiene que ser unido por el usuario
            {
                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("DropDownGroup"); //String de contexto
                    out.flush();
                    out.println(grupo2); //El grupo al que se da de baja
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        invitationsToGroupsListModel = new DefaultListModel<>(); //Lista que muestra los usuarios para invitar
        invitationsToGroupsList = new JList<>(invitationsToGroupsListModel);
        JScrollPane invitationsToGroupsScrollPane = new JScrollPane(invitationsToGroupsList);
        
        invitationsToGroupsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); //Permite seleccionar multiples opciones

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
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Se crea un escritor para mandar informacion por el socket
                String contexto;
                while ((contexto = reader.readLine()) != null) { //Espera hasta recibir un contexto en forma de String
                    System.out.println(contexto);
                    if (contexto.equals("Conexion")) //Se mando un Conexion al cliente
                    { 
                        String usuariosConectados = reader.readLine(); //Recibe los usuarios conectados
                        String usuariosDesconectados = reader.readLine(); //Recibe los usuarios desconectados
                        String usuariosAmigosConectados = reader.readLine(); //Recibe los usuarios amigos conectados
                        String usuariosAmigosDesconectados = reader.readLine(); //Recibe los usuarios amigos desconectados
                        updateList(usuariosConectados,usuariosDesconectados,usuariosAmigosConectados,usuariosAmigosDesconectados);
                        //Hace un update en sus respectivos listobox
                        System.out.println("Clientes Conectados: " + usuariosConectados);
                        System.out.println("Clientes Desconectados: " + usuariosDesconectados);
                        System.out.println("Clientes Amigos Conectados: " + usuariosAmigosConectados);
                        System.out.println("Clientes Amigos Desconectados: " + usuariosAmigosDesconectados);
                    }
                    else if(contexto.equals("MessageUser")) //Se mando un MessageUser al cliente
                    {
                        String message = reader.readLine(); //Recibe el mensaje
                        String usuarioEmisor = reader.readLine(); //Recibe los usuarios emisor
                        Mensaje m = new Mensaje(message,usuarioEmisor,this.usuario); //Crea el mensaje
                        System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                        mensajesNoAmigos.add(m); //Añade el mensaje a la lista
                        if(this.selected != null)
                        {
                            if(this.selected.equals(m.userEmisor)) //Si el usuario seleccionado es igual al enviado por el emisor
                            {
                                messageArea.append(m.userEmisor + ": " + m.message + "\n"); //Se añade al message area
                            }
                        }
                    }
                    else if(contexto.equals("MessageFriend")) //Se mando un MessageFriend al cliente
                    {
                        String message = reader.readLine(); //Recibe el mensaje
                        String usuarioEmisor = reader.readLine(); //Recibe el amigo
                        Mensaje m = new Mensaje(message,usuarioEmisor,this.usuario); //Crea el mensaje
                        System.out.println(m.userEmisor + " envia un mensaje a " + m.userReceptor + " el cual es: " + m.message);
                        mensajesAmigos.add(m); //Añade el mensaje a la lista
                        if(this.selectedFriendConected != null)
                        {
                            if(this.selectedFriendConected.equals(m.userEmisor))  //Si el amigo seleccionado es igual al enviado por el emisor
                            {
                                messageArea.append(m.userEmisor + ": " + m.message + "\n");
                            }
                        }
                    }
                    else if(contexto.equals("Groups")) //Se mando un Groups al cliente
                    {
                        String listGroupCreator = reader.readLine(); //Recibe la lista de grupos creados por el usuario
                        String listGroupConform = reader.readLine(); //Recibe la lista de grupos unidos por el usuario
                        System.out.println("Grupos creados:" + listGroupCreator);
                        System.out.println("Grupos unidos:" + listGroupConform);
                        updateListGroup(listGroupCreator,listGroupConform); //Se hace un update de ambas list box
                        createdGroups = listGroupCreator;
                    }
                    else if(contexto.equals("UsuariosGrupo")) //Se mando un UsuariosGrupo al cliente
                    {
                        String usuariosConectadosGrupo = reader.readLine(); //Recibe la lista de los usuarios conectados a un grupo
                        String usuariosDesconectadosGrupo = reader.readLine(); //Recibe la lista de los usuarios desconectados a un grupo
                        
                        usuariosGrupo = usuariosConectadosGrupo + "," + usuariosDesconectadosGrupo; //Se agregan ambos para manejarlos despues
                        
                        System.out.println("Usuarios Grupo Conectados: " + usuariosConectadosGrupo);
                        System.out.println("Usuarios Grupo Desconectados: " + usuariosDesconectadosGrupo);
                        
                        updateListGroupUser(usuariosConectadosGrupo,usuariosDesconectadosGrupo); //Se actualizan los listbox de usuarios coenctados y desconectados en grupos
                        updateListGroupInvitation(); //Se actualizan las invitaciones de gruposñ
                    }
                    else if(contexto.equals("InvitationsFriends")) //Se mando un InvitationsFriends al cliente
                    {
                        String bd = reader.readLine(); //Recibe todos los usuarios existentes en la BD
                        String invitacionesAmigos = reader.readLine(); //Recibe todas las invitaciones para amigos
                        String posiblesInvitacionesAmigos = reader.readLine(); //Recibe los posibles invitaciones de amigos
                        System.out.println("BD: " + bd);
                        System.out.println("Invitaciones: " + invitacionesAmigos);
                        System.out.println("Posibles invitaciones: " + posiblesInvitacionesAmigos);
                        
                        this.invitationsFriends = invitacionesAmigos; // Externa las invitaciones y usuarios
                        this.invitationsPosibleFriends = posiblesInvitacionesAmigos;
                        this.bd = bd;
                    }
                    else if(contexto.equals("GroupsInvitations")) //Se mando un GroupsInvitations al cliente
                    {
                        String invitacionesGrupos = reader.readLine(); //Recibe las invitaciones de grupos
                        this.invitationsGroups = invitacionesGrupos;
                        System.out.println("Invitaciones Grupo: " + invitacionesGrupos);
                    }
                    else if(contexto.equals("MessagesGroup")) //Se mando un MessagesGroup al cliente
                    {
                        String mensajesUsuariosEnviados = reader.readLine(); //Recibe los mensajes de usuarios 
                        String mensajesGruposEnviados = reader.readLine(); //Recibe los mensajes de grupos
                        this.messagesUsers = mensajesUsuariosEnviados;
                        this.messagesGroup = mensajesGruposEnviados;
                        mensajesAmigos = new ArrayList<>();
                        for(String str : messagesUsers.split(",")) //Separa por las comas el String de usuarios
                        {
                            String[] s = str.split(":"); //Separa en tres por emisor, receptor y mensaje
                            if(s.length == 3)
                            {
                                Mensaje m = new Mensaje(s[2],s[0],s[1]); //Crea el mensaje
                                mensajesAmigos.add(m); //Agrega el mensaje
                            }
                        }
                        System.out.println("Mensajes Usuarios: " + messagesUsers);
                        System.out.println("Mensajes Grupos: " + messagesGroup);
                        
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
            userListModel.clear(); //Limpia los modelos de informacion para los usuarios conectado y desconectados a la vez que los amigos
            userDesconectedModel.clear();
            userFriendConectedModel.clear();
            userFriendDesconectedModel.clear();
            for (String user : userData.split(",")) //Se añaden al listbox separando por la ,
            {
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
            groupOwnerListModel.clear(); //Limpia los modelos de informacion para los usuarios conectado y desconectados a la vez que los amigos
            groupConformerListModel.clear();
            for(String pieza : groupOwnedData.split(",")) //Se añaden al listbox separando por la ,
            {
                String[] go = new String[2];
                go = pieza.split(":"); //Se divide en 2
                groupOwnerListModel.addElement(go[0]); //Se introduce el primero
            }
            for(String pieza : groupConfromedData.split(",")) //Se añaden al listbox separando por la ,
            {
                String[] gc = new String[2];
                gc = pieza.split(":"); //Se divide en 2
                groupConformerListModel.addElement(gc[0]); //Se introduce el primero
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

            for(String s : todos.split(",")) //Esto se hace para excluir los de quitar sobre los de todos
            {
                if(!quitar.contains(s))
                {
                    if(ultimo.length() > 0) ultimo.append(",");
                    ultimo.append(s); //Agrega al StringBuilder el usuario
                }
            }
            System.out.println("Usuarios no en el grupo: " + ultimo);

            SwingUtilities.invokeLater(() -> {
                invitationsToGroupsListModel.clear(); //Se ingresan las posibles invitaciones del grupo seleccionado
                for(String pieza : ultimo.toString().split(","))
                {
                    if(!pieza.equals(usuario)) //Esto excluye el usuario propio del login
                    {
                        invitationsToGroupsListModel.addElement(pieza); //Ingresa las invitaciones de grupos
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
            groupUsuerConectedListModel.clear();//Limpia los modelos de informacion para los usuarios conectado y desconectados a la vez que los amigos
            groupUsuerDesconectedListModel.clear();
            for(String pieza : usersConectedData.split(",")) //Se añaden al listbox separando por la ,
            {
                if(!pieza.equals(usuario))//Esto excluye el usuario propio del login
                {
                    groupUsuerConectedListModel.addElement(pieza); //Ingresa las los usuarios del grupo
                }
            }
            for(String pieza : usersDesconectedData.split(",")) //Se añaden al listbox separando por la ,
            {
                if(!pieza.equals(usuario)) //Esto excluye el usuario propio del login
                {
                    groupUsuerDesconectedListModel.addElement(pieza); //Ingresa las los usuarios del grupo
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
        for(String str : messagesGroup.split(",")) //Divide el String por la ,
        {
            String[] s = str.split(":"); //Divide la parte por :
            if(s.length  == 3)
            {
                Mensaje m = new Mensaje(s[2],s[0],s[1]); //Se agrega un nuevo mensaje con la informacion
                mensajesGrupos.add(m);
            }
        }
        if(groupOwnerList.getSelectedValue() != null)
        {
            messageArea.setText("");
            for(Mensaje m: mensajesGrupos) //Se navega a traves de los mensajes de grupo
            {
                if(m.userEmisor.equals(groupOwnerList.getSelectedValue())) //Si son iguales el emisor y la seleccion de grupo
                {
                    messageArea.append(m.userReceptor + ": " + m.message + "\n"); //Se añade a el area de mensaje
                }
            }
        }
        if(groupConformerList.getSelectedValue() != null)
        {
            messageArea.setText("");
            for(Mensaje m: mensajesGrupos) //Se navega a traves de los mensajes de grupo
            {
                if(m.userEmisor.equals(groupConformerList.getSelectedValue())) //Si son iguales el emisor y la seleccion de grupo
                {
                    messageArea.append(m.userReceptor + ": " + m.message + "\n"); //Se añade a el area de mensaje
                }
            }
        }
    }
    
    class Mensaje { //Clase creada para los mensajes de los usuarios
        
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
