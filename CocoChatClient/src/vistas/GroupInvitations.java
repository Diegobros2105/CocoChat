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
import static java.nio.file.Files.list;
import java.util.ArrayList;
import static java.util.Collections.list;
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
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import modelos.Groups;

/**
 *
 * @author 52331
 */
public class GroupInvitations extends JFrame {
    
    private final String usuario;
    private CocoChatClient cliente;
    private Socket socket;
    
    private String invitationsGroups = new String();
    private String createdGroups = new String();
    private String bd = new String();
    
    private JList<String> creationGroupsList;
    private DefaultListModel<String> creationGroupsListModel;
    private JList<String> invitationsGroupsList;
    private DefaultListModel<String> invitationsGroupsListModel;
    
    private JFrame principal;
    
    private String selected;
    private String selectedPosible;
    
    public GroupInvitations(JFrame principal, CocoChatClient cliente, Socket socket, String usuario, String bd, String invitationsGroups, String createdGroups) throws IOException {
        super();
        this.cliente = cliente;
        this.socket = socket;
        this.usuario = usuario;
        this.bd = bd;
        this.invitationsGroups = invitationsGroups;
        this.createdGroups = createdGroups;
        this.principal = principal;
        System.out.println(invitationsGroups);
        updateInvitations();
        initUI();
    }
    
    private void initUI() {
        setTitle("Amigo" + usuario);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel crearGrupoLabel = new JLabel("Crear Grupo");
        JLabel nombreGrupoLabel = new JLabel("Nombre: ");
        JTextField nombreGrupo = new JTextField();
        JLabel descripcionGrupoLabel = new JLabel("Descripcion: ");
        JTextField descripcionGrupo = new JTextField();
        
        creationGroupsListModel = new DefaultListModel<>();
        creationGroupsList = new JList<>(creationGroupsListModel);
        JScrollPane creationGroupsScrollPane = new JScrollPane(creationGroupsList);
        
        creationGroupsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JButton crearGrupo = new JButton("Crear Grupo");
        crearGrupo.addActionListener(e -> {
            String nom = nombreGrupo.getText();
            String des = descripcionGrupo.getText();
            List<String> selectedItems = creationGroupsList.getSelectedValuesList();
            
            if(nom != null && des != null && selectedItems.size() >= 2)
            {
                StringBuilder usuariosSeleccionados = new StringBuilder();
                for(String s : selectedItems)
                {
                    if(usuariosSeleccionados.length() > 0) usuariosSeleccionados.append(",");
                    usuariosSeleccionados.append(s);
                }

                System.out.println(nom);
                System.out.println(des);
                System.out.println(usuariosSeleccionados);

                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("CreatorGroup");
                    out.flush();
                    out.println(nom);
                    out.flush();
                    out.println(des);
                    out.flush();
                    out.println(usuariosSeleccionados.toString());
                    out.flush();
                    this.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JLabel invitacionesLabel = new JLabel("Invitaciones a grupos");
        
        invitationsGroupsListModel = new DefaultListModel<>();
        invitationsGroupsList = new JList<>(invitationsGroupsListModel);
        JScrollPane invitationGroupsScrollPane = new JScrollPane(invitationsGroupsList);
        
        JButton aceptarInvitacion = new JButton("Aceptar Invitacion");
        aceptarInvitacion.addActionListener(e -> {
            String inv = invitationsGroupsList.getSelectedValue();
            if(inv != null)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("InvitationGroupAccept");
                    out.flush();
                    out.println(inv);
                    out.flush();
                    dispose();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JButton denegarInvitacion = new JButton("Denegar Invitacion");
        denegarInvitacion.addActionListener(e -> {
            String inv = invitationsGroupsList.getSelectedValue();
            if(inv != null)
            {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("InvitationGroupDenny");
                    out.flush();
                    out.println(inv);
                    out.flush();
                    dispose();
                } catch (IOException ex) {
                    Logger.getLogger(GroupInvitations.class.getName()).log(Level.SEVERE, null, ex);
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
                    .addComponent(crearGrupoLabel)
                    .addComponent(creationGroupsScrollPane,200,200,200)
                    .addComponent(nombreGrupoLabel)
                    .addComponent(nombreGrupo,200,200,200)
                    .addComponent(descripcionGrupoLabel)
                    .addComponent(descripcionGrupo,200,200,200)
                    .addComponent(crearGrupo)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(invitacionesLabel)
                    .addComponent(invitationGroupsScrollPane,200,200,200)
                    .addComponent(aceptarInvitacion)
                    .addComponent(denegarInvitacion)
                )
        );

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(crearGrupoLabel)
                    .addComponent(invitacionesLabel)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(creationGroupsScrollPane,200,200,200)
                    .addComponent(invitationGroupsScrollPane,200,200,200)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(nombreGrupoLabel)
                    .addComponent(aceptarInvitacion)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(nombreGrupo,25,25,25)
                    .addComponent(denegarInvitacion)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(descripcionGrupoLabel)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(descripcionGrupo,25,25,25)
                )
                .addGroup(gl.createParallelGroup()
                    .addComponent(crearGrupo)
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
            creationGroupsListModel.clear();
            for(String pieza : this.bd.split(","))
            {
                if(!pieza.equals(this.usuario))
                {
                    creationGroupsListModel.addElement(pieza);
                }
            }
            invitationsGroupsListModel.clear();
            for(String pieza : this.invitationsGroups.split(","))
            {
                if(!pieza.equals(this.usuario))
                {
                    invitationsGroupsListModel.addElement(pieza);
                }
            }
        });
    }

}
