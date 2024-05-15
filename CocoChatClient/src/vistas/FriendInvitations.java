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
import modelos.Groups;

/**
 *
 * @author 52331
 */
public class FriendInvitations extends JFrame {
    
    private final String usuario;
    private CocoChatClient cliente;
    private Socket socket;
    
    private String invitationsFriends = new String();
    private String invitationsPosibleFriends = new String();
    private String bd = new String();
    
    private JList<String> invitationsFriendsList;
    private DefaultListModel<String> invitationsFriendsListModel;
    private JList<String> invitationsPosibleFriendsList;
    private DefaultListModel<String> invitationsPosibleFriendsListModel;
    
    private JFrame principal;
    
    private String selected;
    private String selectedPosible;
    
    public FriendInvitations(JFrame principal, CocoChatClient cliente, Socket socket, String usuario, String bd, String invitationsFriends, String invitationsPosibleFriends) throws IOException {
        super();
        this.cliente = cliente;
        this.socket = socket;
        this.usuario = usuario;
        this.bd = bd;
        this.invitationsFriends = invitationsFriends;
        this.invitationsPosibleFriends = invitationsPosibleFriends;
        this.principal = principal;
        initUI();
        updateInvitations();
    }
    
    private void initUI() {
        setTitle("Amigo" + usuario);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        invitationsFriendsListModel = new DefaultListModel<>();
        invitationsFriendsList = new JList<>(invitationsFriendsListModel);
        JScrollPane invitationsFriendsScrollPane = new JScrollPane(invitationsFriendsList);
        
        invitationsFriendsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events;
                    invitationsPosibleFriendsList.clearSelection();
                    selected = invitationsFriendsList.getSelectedValue();
                    invitationsPosibleFriendsList.clearSelection();
                }
            }
        });
        
        invitationsPosibleFriendsListModel = new DefaultListModel<>();
        invitationsPosibleFriendsList = new JList<>(invitationsPosibleFriendsListModel);
        JScrollPane invitationsPosibleFriendsScrollPane = new JScrollPane(invitationsPosibleFriendsList);
        
        invitationsPosibleFriendsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // This line prevents double events;
                    invitationsFriendsList.clearSelection();
                    selectedPosible = invitationsPosibleFriendsList.getSelectedValue();
                    invitationsFriendsList.clearSelection();
                }
            }
        });
        
        JLabel invitations = new JLabel("Invitaciones");
        JLabel invitationsPosible = new JLabel("Mandar Invitaciones");
        
        JButton acceptInvitation = new JButton("Aceptar");
        acceptInvitation.addActionListener(e -> {
            if(selected != null)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("InvitationsFriendsAccepted");
                    out.flush();
                    out.println(selected);
                    out.flush();
                    this.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(FriendInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        
        JButton dennyInvitation = new JButton("Denegar");
        dennyInvitation.addActionListener(e -> {
            if(selected != null)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("InvitationsFriendsDenied");
                    out.flush();
                    out.println(selected);
                    out.flush();
                    this.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(FriendInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        
        JButton sendInvitation = new JButton("Enviar Invitacion");
        sendInvitation.addActionListener(e -> {
            if(selectedPosible != null)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("InvitationsFriendsSend");
                    out.flush();
                    out.println(selectedPosible);
                    out.flush();
                    this.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(FriendInvitations.class.getName()).log(Level.SEVERE, null, ex);
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
                .addGroup(gl.createParallelGroup()
                    .addComponent(invitations)
                    .addComponent(invitationsFriendsScrollPane)
                    .addComponent(acceptInvitation,200,200,200)
                    .addComponent(dennyInvitation,200,200,200)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(invitationsPosible)
                    .addComponent(invitationsPosibleFriendsScrollPane)
                    .addComponent(sendInvitation,200,200,200)
                )
                
        );

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(invitations)
                    .addComponent(invitationsPosible)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(invitationsFriendsScrollPane)
                    .addComponent(invitationsPosibleFriendsScrollPane)
                )
                .addGroup(gl.createParallelGroup()
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(acceptInvitation,50,50,50)
                        .addComponent(dennyInvitation,50,50,50)
                    )
                    .addComponent(sendInvitation,100,100,100)
                ) 
        );
        
        add(panel);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                
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
    
    private void updateInvitations()
    {
        SwingUtilities.invokeLater(() -> {
            invitationsFriendsListModel.clear();
            invitationsPosibleFriendsListModel.clear();
            for(String pieza : invitationsFriends.split(","))
            {
                invitationsFriendsListModel.addElement(pieza);
            }
            for(String pieza : invitationsPosibleFriends.split(","))
            {
                if(!pieza.equals(this.usuario))
                {
                    invitationsPosibleFriendsListModel.addElement(pieza);
                }
            }
        });
    }

}
