package cocochatserver;

import db.ConformsController;
import db.FriendsController;
import db.GroupsController;
import db.MessagesGroupController;
import db.MessagesUserController;
import db.UsersController;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelos.Conform;
import modelos.Friends;
import modelos.Groups;
import modelos.MessagesGroup;
import modelos.MessagesUser;
import modelos.Users;

public class CocoChatServer {
    
    public static ArrayList<Handler> lista = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        int port = 12345; // Puerto en el que el servidor escucha
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en el puerto " + port);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");
                new Thread(new Handler(clientSocket)).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class Handler implements Runnable {
        
        private Socket clientSocket;
        private String usuario;

        public Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String contexto;

                while ((contexto = reader.readLine()) != null) {
                    System.out.println(contexto);

                    switch (contexto) {
                        case "Login":
                            handleLogin(reader);
                            break;
                        case "Register":
                            handleRegister(reader);
                            break;
                        case "Logout":
                            handleLogout();
                            return; // Termina el hilo después de manejar el logout
                        case "OlvidarContrasena":
                            handleOlvidar(reader);
                            break;
                        case "MessageUser":
                            String message = reader.readLine();
                            System.out.println(message);
                            String usuarioReceptor = reader.readLine();
                            System.out.println(usuarioReceptor);
                            for(Handler h : lista)
                            {
                                if(h.usuario.equals(usuarioReceptor))
                                {
                                    PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);
                                    out.println("MessageUser");
                                    out.flush();
                                    out.println(message);
                                    out.flush();
                                    out.println(this.usuario);
                                    out.flush();
                                }
                            }
                            break;
                        case "MessageFriend":
                            String messageFriendConected = reader.readLine();
                            String userFriendConected = reader.readLine();
                            
                            System.out.println(messageFriendConected);
                            System.out.println(userFriendConected);
                            
                            UsersController conUserMessageFriend = new UsersController();
                            MessagesUserController conMessageUSerMessageFriend = new MessagesUserController();
                            
                            MessagesUser msg = new MessagesUser(messageFriendConected,conUserMessageFriend.buscarId(userFriendConected),conUserMessageFriend.buscarId(this.usuario));
                            conMessageUSerMessageFriend.add(msg);
                            
                            for(Handler h : lista)
                            {
                                if(h.usuario.equals(userFriendConected))
                                {
                                    PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);
                                    out.println("MessageFriend");
                                    out.flush();
                                    out.println(messageFriendConected);
                                    out.flush();
                                    out.println(this.usuario);
                                    out.flush();
                                }
                            }
                            
                            break;
                        case "MessageFriendBD":
                            String messageFriendNoConected = reader.readLine();
                            String userFriendNoConected = reader.readLine();
                            
                            System.out.println(messageFriendNoConected);
                            System.out.println(userFriendNoConected);
                            
                            UsersController conUserMessageFriendNo = new UsersController();
                            MessagesUserController conMessageUSerMessageFriendNo = new MessagesUserController();
                            
                            MessagesUser msgNo = new MessagesUser(messageFriendNoConected,conUserMessageFriendNo.buscarId(userFriendNoConected),conUserMessageFriendNo.buscarId(this.usuario));
                            conMessageUSerMessageFriendNo.add(msgNo);
                            
                            break;
                        case "InvitationsFriendsAccepted":
                            String userAcepted = reader.readLine();
                            System.out.println(userAcepted);
                            UsersController conUsersAcepted = new UsersController();
                            FriendsController conFriendsAcepted = new FriendsController();
                            Friends frd = new Friends();
                            frd.setUserEmisor(conUsersAcepted.buscarId(userAcepted));
                            frd.setUserReceptor(conUsersAcepted.buscarId(this.usuario));
                            conFriendsAcepted.update(frd);
                            
                            sendUsers();
                            sendInvitationsFriends();
                            
                            break;
                        case "InvitationsFriendsDenied":
                            String userDenied = reader.readLine();
                            System.out.println(userDenied);
                            UsersController conUsersDenied = new UsersController();
                            FriendsController conFriendsDenied = new FriendsController();
                            Friends fri = new Friends();
                            fri.setUserEmisor(conUsersDenied.buscarId(userDenied));
                            fri.setUserReceptor(conUsersDenied.buscarId(this.usuario));
                            conFriendsDenied.delete(fri);
                            
                            sendUsers();
                            sendInvitationsFriends();
                            
                            break;
                        case "InvitationsFriendsSend":
                            String userSend = reader.readLine();
                            System.out.println(userSend);
                            UsersController conUsersSned = new UsersController();
                            FriendsController conFriendsSend = new FriendsController();
                            Friends friSend = new Friends();
                            friSend.setUserEmisor(conUsersSned.buscarId(this.usuario));
                            friSend.setUserReceptor(conUsersSned.buscarId(userSend));
                            conFriendsSend.add(friSend);
                            
                            sendUsers();
                            sendInvitationsFriends();
                            
                            break;
                        case "MessageGroup":
                            String messageGroup = reader.readLine();
                            String groupMessage = reader.readLine();
                            
                            System.out.println(messageGroup);
                            System.out.println(groupMessage);
                            
                            UsersController conUserMessageGroup = new UsersController();
                            GroupsController conGroup = new GroupsController();
                            MessagesGroupController conMessageGroupMessage = new MessagesGroupController();

                            MessagesGroup mgrp = new MessagesGroup(messageGroup,conUserMessageGroup.buscarId(this.usuario),conGroup.getGroupId(groupMessage).getId());
                            conMessageGroupMessage.add(mgrp);
                            sendMessages();

                            break;
                        case "SearchUsersInGroup":
                            String group = reader.readLine();
                            System.out.println(group);
                            
                            StringBuilder usuariosGrupo = new StringBuilder();
                            GroupsController conGroups = new GroupsController();
                            Groups g = new Groups();
                            ConformsController conConforms = new ConformsController();
                            g = conGroups.getGroupId(group);
                            ArrayList<Conform> listConforms = new ArrayList<>();
                            UsersController conUsers = new UsersController();
                            
                            listConforms = conConforms.getByGroup(g.getId());
                            
                            System.out.println(group);
                            usuariosGrupo.append(conUsers.buscarUserName(conGroups.getUserCreatorByName(group)));
                            
                            System.out.println(usuariosGrupo);
                            
                            for(Conform c : listConforms)
                            {
                                if(usuariosGrupo.length() > 0) usuariosGrupo.append(",");
                                if(c.getEstado().equals("A"))
                                {
                                    usuariosGrupo.append(conUsers.buscarUserName(c.getUser()));
                                }
                            }
                            
                            StringBuilder usuariosConectadosGrupo = new StringBuilder();
                            StringBuilder usuariosDesconectadosGrupo = new StringBuilder();
                            StringBuilder usuariosConectados = new StringBuilder();
                            
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            
                            for(Handler h : lista)
                            {
                                if(usuariosConectados.length() > 0) usuariosConectados.append(",");
                                usuariosConectados.append(h.usuario);
                            }
                            for(String user : usuariosGrupo.toString().split(","))
                            {
                                if(usuariosConectados.toString().contains(user))
                                {
                                    if(usuariosConectadosGrupo.length() > 0) usuariosConectadosGrupo.append(",");
                                    usuariosConectadosGrupo.append(user);
                                }
                                else
                                {
                                    if(usuariosDesconectadosGrupo.length() > 0) usuariosDesconectadosGrupo.append(",");
                                    usuariosDesconectadosGrupo.append(user);
                                }
                            }
                            System.out.println(usuariosGrupo);
                            System.out.println("Grupo Cliente Conectado: " + usuariosConectadosGrupo);
                            System.out.println("Grupo Cliente Desconectado: " + usuariosDesconectadosGrupo);
                            
                            out.println("UsuariosGrupo");
                            out.flush();
                            out.println(usuariosConectadosGrupo.toString());
                            out.flush();
                            out.println(usuariosDesconectadosGrupo.toString());
                            out.flush();
                            
                            break;
                        case "SearchUsersNoInGroup":
                            String groupSearch = reader.readLine();
                            System.out.println(groupSearch);
                            
                            StringBuilder usuariosConf = new StringBuilder();
                            StringBuilder usuariosConformGroup = new StringBuilder();
                            GroupsController conGroupsSearch = new GroupsController();
                            ConformsController conConformsSearch = new ConformsController();
                            UsersController conUsersSearch = new UsersController();
                            
                            ArrayList<Users> usersSearch = conUsersSearch.getAll();
                            
                            ArrayList<Conform> conformSearch = conConformsSearch.getAllConformsGroup(conGroupsSearch.getGroupId(groupSearch).getId());
                            
                            usuariosConf.append(this.usuario);
                            for(Conform cf : conformSearch)
                            {
                                if(usuariosConf.length() > 0) usuariosConf.append(",");
                                usuariosConf.append(conUsersSearch.buscarUserName(cf.getUser()));
                            }
                            
                            for(Users us : usersSearch)
                            {
                                if(!usuariosConf.toString().contains(us.getUser()))
                                {
                                    if(usuariosConformGroup.length() > 0) usuariosConformGroup.append(",");
                                    usuariosConformGroup.append(us.getUser());
                                }
                            }
                            System.out.println(usuariosConformGroup);
                            
                            PrintWriter outData = new PrintWriter(clientSocket.getOutputStream(), true);
                            
                            outData.println(usuariosConformGroup.toString());
                            outData.flush();
                            
                            break;
                        case "CreatorGroup":
                            UsersController conUserGroup = new UsersController();
                            String nombre = reader.readLine();
                            String descripcion = reader.readLine();
                            String seleccion = reader.readLine();
                            
                            System.out.println(nombre);
                            System.out.println(descripcion);
                            System.out.println(seleccion);
                            
                            GroupsController conGroupsCreator = new GroupsController();
                            
                            Groups grupoCreator = new Groups();
                            grupoCreator.setName(nombre);
                            grupoCreator.setDescription(descripcion);
                            grupoCreator.setUserCreator(conUserGroup.buscarId(this.usuario));
                            
                            conGroupsCreator.add(grupoCreator);
                            
                            grupoCreator = conGroupsCreator.getGroupId(nombre);
                            
                            int idGroup = grupoCreator.getId();
                            
                            ConformsController conConformsCreatorGroup = new ConformsController();
                            Conform conf = new Conform();
                            
                            for(String persona : seleccion.split(","))
                            {
                                conf.setGroups(idGroup);
                                conf.setUser(conUserGroup.buscarId(persona));
                                conConformsCreatorGroup.add(conf);
                            }
                            
                            sendGroups();
                            sendInvitationsGroups();
                            
                            break;
                        case "InvitationGroup":
                            String usuariosInvitados = reader.readLine();
                            String grupoInvitado = reader.readLine();
                            
                            if(grupoInvitado != null && usuariosInvitados != null)
                            {
                                System.out.println(usuariosInvitados);
                                System.out.println(grupoInvitado);

                                ConformsController conConformInvitation = new ConformsController();
                                GroupsController conGroupInvitation = new GroupsController();
                                UsersController conUserInvitation = new UsersController();

                                for(String usuarioInv : usuariosInvitados.split(","))
                                {
                                    Conform confi = new Conform();
                                    Groups grpInvi = conGroupInvitation.getGroupId(grupoInvitado);
                                    confi.setGroups(grpInvi.getId());
                                    confi.setUser(conUserInvitation.buscarId(usuarioInv));
                                    conConformInvitation.add(confi);
                                }

                                sendGroups();
                                sendInvitationsGroups();
                            }
                            
                            break;
                        case "InvitationGroupAccept":
                            ConformsController conConform = new ConformsController();
                            GroupsController conGrp = new GroupsController();
                            UsersController conUs = new UsersController();
                            
                            String grupoInvitacion = reader.readLine();
                            
                            System.out.println(grupoInvitacion);
                            
                            Conform conforms;
                            Groups grps = conGrp.getGroupId(grupoInvitacion);
                            conforms = conConform.getByGroupAndUser(grps.getId(), conUs.buscarId(this.usuario));
                            
                            conConform.update(conforms);
                            
                            sendGroups();
                            sendInvitationsGroups();
                            
                            break;
                        case "InvitationGroupDenny":
                            ConformsController conConform2 = new ConformsController();
                            GroupsController conGrp2 = new GroupsController();
                            UsersController conUs2 = new UsersController();
                            
                            String grupoInvitacion2 = reader.readLine();
                            
                            System.out.println(grupoInvitacion2);
                            
                            Conform conforms2;
                            Groups grps2 = conGrp2.getGroupId(grupoInvitacion2);
                            conforms2 = conConform2.getByGroupAndUser(grps2.getId(), conUs2.buscarId(this.usuario));
                            
                            conConform2.delete(conforms2.getId());
                            
                            sendGroups();
                            sendInvitationsGroups();
                            
                            break;
                        case "DeleteGroup":
                            String grupoBorrado = reader.readLine();
                            
                            if(grupoBorrado != null)
                            {
                                System.out.println(grupoBorrado);

                                ConformsController conConformBorrar = new ConformsController();
                                GroupsController conGroupBorrar = new GroupsController();
                                UsersController conUserBorrar = new UsersController();

                                conGroupBorrar.delete(conGroupBorrar.getGroupId(grupoBorrado).getId());

                                sendGroups();
                                sendInvitationsGroups();
                            }
                            
                            break;
                        case "DropDownGroup":
                            String dropDownGroup = reader.readLine();
                            
                            if(dropDownGroup != null)
                            {
                                System.out.println(dropDownGroup);

                                ConformsController conConformBorrar = new ConformsController();
                                GroupsController conGroupBorrar = new GroupsController();
                                UsersController conUserBorrar = new UsersController();

                                conConformBorrar.deleteConection(conUserBorrar.buscarId(this.usuario), conGroupBorrar.getGroupId(dropDownGroup).getId());

                                sendGroups();
                                sendInvitationsGroups();
                            }
                            
                            break;
                    }
                    
                }

            } catch (IOException e) {
                System.out.println("Error al manejar el cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();  // Cierra el socket al terminar
                } catch (IOException e) {
                    System.out.println("Error cerrando el socket del cliente: " + e.getMessage());
                }
                lista.remove(this); // Asegura remover el handler de la lista
            }
        }

        private void handleLogin(BufferedReader reader) throws IOException {
            String usu = reader.readLine();
            String contrasena = reader.readLine();
            UsersController con = new UsersController();
            boolean bandera = con.buscarUsuario(usu, contrasena);

            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            if (bandera) {
                this.usuario = usu;
                lista.add(this);
                dataOut.writeInt(1);

            } else {
                dataOut.writeInt(0);
            } 
            sendUsers();
            sendGroups();
            sendInvitationsFriends();
            sendInvitationsGroups();
            sendMessages();
        }

        private void handleRegister(BufferedReader reader) throws IOException {
            String nombre = reader.readLine();
            String apellido = reader.readLine();
            String usuario = reader.readLine();
            String contrasena = reader.readLine();
            Users u = new Users(nombre, apellido, usuario, contrasena);
            UsersController con = new UsersController();
            con.add(u);
            sendUsers();
        }
        
        private void handleOlvidar(BufferedReader reader) throws IOException {
            String usu = reader.readLine();
            UsersController con = new UsersController();
            String password = new String();
            System.out.println(usu);
            ArrayList<Users> list = con.getAll();
            for(Users l : list)
            {
                if(l.getUser().equals(usu))
                {
                    password = l.getPassword();   
                } 
            }

            PrintWriter outData = new PrintWriter(clientSocket.getOutputStream());
            outData.println(password);
            outData.flush();
            System.out.println("Enviado");
        }

        private void handleLogout() throws IOException {
            System.out.println("Usuario " + usuario + " ha cerrado sesión.");
            lista.remove(this);
            sendUsers();
        }
        
        
        
        private void sendUsers() throws IOException {
            for (Handler h : lista) {
                String f = sendFriends(h.usuario);
                System.out.println(f);
                StringBuilder usuariosAmigosConectados = new StringBuilder();
                StringBuilder usuariosAmigosDesconectados = new StringBuilder();

                StringBuilder usuariosConectados = new StringBuilder();
                StringBuilder usuariosDesconectados = new StringBuilder();
                StringBuilder BD = new StringBuilder();

                UsersController con = new UsersController();
                ArrayList<Users> l = con.getAll();

                for(Users u : l)
                {
                    BD.append(u.getUser()).append(",");
                }
                System.out.println("Clientes BD: " + BD);

                for (Handler x : lista) {
                    if(f.contains(x.usuario))
                    {
                        if (usuariosAmigosConectados.length() > 0) usuariosAmigosConectados.append(",");
                        usuariosAmigosConectados.append(x.usuario);
                    }
                    else
                    {
                        if (usuariosConectados.length() > 0) usuariosConectados.append(",");
                        usuariosConectados.append(x.usuario);
                    }
                }
                System.out.println("Clientes Amigos Conectados: " + usuariosAmigosConectados);
                System.out.println("Clientes Conectados: " + usuariosConectados);

                String[] allUsers = BD.toString().split(",");
                String[] excludedUsers = usuariosConectados.toString().split(",");

                Set<String> excludedSet = new HashSet<>();
                for (String user : excludedUsers) {
                    excludedSet.add(user);
                }

                // Filtrar usuarios y añadir al nuevo StringBuilder
                for (String user : allUsers) {
                    if (!excludedSet.contains(user)) {
                        if(f.contains(user))
                        {
                            if(!usuariosAmigosConectados.toString().contains(user))
                            {
                                if (usuariosAmigosDesconectados.length() > 0) usuariosAmigosDesconectados.append(",");
                                usuariosAmigosDesconectados.append(user);
                            }
                        }
                        else
                        {
                            if (usuariosDesconectados.length() > 0) usuariosDesconectados.append(",");
                            usuariosDesconectados.append(user); 
                        }

                    }
                }
                System.out.println("Clientes Desconectados: " + usuariosDesconectados);
                System.out.println("Clientes Amigos Desconectados: " + usuariosAmigosDesconectados);

                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);
                out.println("Conexion");
                out.flush();
                out.println(usuariosConectados.toString());
                out.flush();
                out.println(usuariosDesconectados.toString());
                out.flush();
                out.println(usuariosAmigosConectados.toString());
                out.flush();
                out.println(usuariosAmigosDesconectados.toString());
                out.flush();
            }
        }
        
        private String sendFriends(String user) throws IOException {
            StringBuilder usuariosAmigos = new StringBuilder();
            FriendsController conFriend = new FriendsController();
            UsersController conUser = new UsersController();
            ArrayList<Friends> l = conFriend.getAll();
            
            for(Friends f : l)
            {
                if(f.getUserEmisor() == conUser.buscarId(user))
                {
                    if(f.getEstado().equals("A"))
                    {
                        usuariosAmigos.append(conUser.buscarUserName(f.getUserReceptor())).append(",");
                    }
                }
                if(f.getUserReceptor() == conUser.buscarId(user))
                {
                    if(f.getEstado().equals("A"))
                    {
                        usuariosAmigos.append(conUser.buscarUserName(f.getUserEmisor())).append(",");
                    }
                }
            }
            if (usuariosAmigos.length() > 0) {
                usuariosAmigos.deleteCharAt(usuariosAmigos.length() - 1); // Eliminar la última coma
            }
            System.out.println("Clientes Amigos de " + user + " : " + usuariosAmigos);
            
            return usuariosAmigos.toString();
            
        }
        
        private void sendInvitationsFriends() throws IOException {
            for(Handler h : lista)
            {
                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);

                FriendsController conFriends = new FriendsController();
                UsersController conUser = new UsersController();
                ArrayList<Friends> list = new ArrayList<>();
                list = conFriends.getAll();
                StringBuilder invitaciones = new StringBuilder();
                StringBuilder invitacionesPosibles = new StringBuilder();

                for(Friends f : list)
                {
                    if(f.getUserReceptor() == conUser.buscarId(h.usuario) && f.getEstado().equals("P"))
                    {
                        if(invitaciones.length() > 0) invitaciones.append(",");
                        invitaciones.append(conUser.buscarUserName(f.getUserEmisor()));
                    }
                }
                System.out.println("Invitaciones: " + invitaciones);

                StringBuilder invitacionesEnviadas = new StringBuilder();
                for(Friends f : list)
                {
                    if(f.getUserEmisor() == conUser.buscarId(h.usuario))
                    {
                        if(invitacionesEnviadas.length() > 0) invitacionesEnviadas.append(",");
                        invitacionesEnviadas.append(conUser.buscarUserName(f.getUserEmisor()));
                    }
                }
                System.out.println("Invitaciones enviadas: " + invitacionesEnviadas);
                
                ArrayList<Friends> listFriendsExisting = new ArrayList<>();
                listFriendsExisting = conFriends.getAll();
                StringBuilder listExisting = new StringBuilder();

                for(Friends f : listFriendsExisting)
                {
                    if(f.getEstado().equals("A"))
                    {
                        if(listExisting.length() > 0) listExisting.append(",");
                        if(f.getUserEmisor() == conUser.buscarId(h.usuario))
                        {
                            listExisting.append(conUser.buscarUserName(f.getUserReceptor()));
                        }
                        if(f.getUserReceptor() == conUser.buscarId(h.usuario))
                        {
                            listExisting.append(conUser.buscarUserName(f.getUserEmisor()));
                        }
                    }
                }
                System.out.println("Amigos: " + listExisting);

                ArrayList<Users> listUsers = new ArrayList<>();
                listUsers = conUser.getAll();
                StringBuilder listBD = new StringBuilder();

                for(Users u : listUsers)
                {
                    if(listBD.length() > 0) listBD.append(",");
                    listBD.append(u.getUser());
                }
                System.out.println("BD: " + listBD);

                for(Users u : listUsers)
                {
                    if(!invitaciones.toString().contains(u.getUser()))
                    {
                        if(!invitacionesEnviadas.toString().contains(u.getUser()))
                        {
                            if(!listExisting.toString().contains(u.getUser()))
                            {
                                if(invitacionesPosibles.length() > 0) invitacionesPosibles.append(",");
                                invitacionesPosibles.append(u.getUser());
                            }
                        }
                        
                    }
                }
                System.out.println("Posibles invitaciones: " + invitacionesPosibles);

                out.println("InvitationsFriends");
                out.flush();
                out.println(listBD.toString());
                out.flush();
                out.println(invitaciones.toString());
                out.flush();
                out.println(invitacionesPosibles.toString());
                out.flush();
            }
        }
        
        private void sendGroups() throws IOException {
            for(Handler h : lista)
            {
                StringBuilder listGroupConform = new StringBuilder();
                StringBuilder listGroupCreator = new StringBuilder();

                GroupsController conGroup = new GroupsController();
                UsersController conUser = new UsersController();
                ConformsController conConform = new ConformsController();
                ArrayList<Groups> listGroup = new ArrayList<Groups>();
                int idUsuario = conUser.buscarId(h.usuario);

                listGroup = conGroup.getAll();

                System.out.println("Id del usuario: " + idUsuario);


                for(Groups g : listGroup)
                {
                    if(g.getUserCreator() == idUsuario)
                    {
                        if(listGroupCreator.length() > 0) listGroupCreator.append(",");
                        listGroupCreator.append(g.getName()).append(":").append(g.getDescription());    
                    }
                }
                System.out.println(listGroupCreator);

                ArrayList<Conform> listConform = new ArrayList<>();
                listConform = conConform.getAll();

                for(Conform c : listConform)
                {
                    if(c.getUser() == idUsuario && c.getEstado().equals("A"))
                    {
                        Groups g = new Groups();
                        g = conGroup.getGroup(c.getGroups());
                        if(listGroupConform.length() > 0) listGroupConform.append(",");
                        listGroupConform.append(g.getName()); 
                    }
                }

                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);
                out.println("Groups");
                out.flush();
                out.println(listGroupCreator.toString());
                out.flush();
                out.println(listGroupConform.toString());
                out.flush();

                //sendInvitationsGroups(h);
            }
            
        }

        private void sendInvitationsGroups() throws IOException {
            for(Handler h : lista)
            {
                UsersController conUser = new UsersController();
                GroupsController conGroup = new GroupsController();
                ConformsController conConform = new ConformsController();

                StringBuilder listGroupsInvitation = new StringBuilder();
                ArrayList<Conform> listaConform = conConform.getAll();
                
                for(Conform c : listaConform)
                {
                    if(c.getUser() == conUser.buscarId(this.usuario) && c.getEstado().equals("D"))
                    {
                        Groups grp = conGroup.getGroup(c.getGroups());
                        if(listGroupsInvitation.length() > 0) listGroupsInvitation.append(",");
                        listGroupsInvitation.append(grp.getName());
                    }
                }
                System.out.println(listGroupsInvitation);
                        
                int idUser = conUser.buscarId(h.usuario);

                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);
                out.println("GroupsInvitations");
                out.flush();
                out.println(listGroupsInvitation.toString());
                out.flush();
            }
        }
        
        private void sendMessages() throws IOException {
            UsersController conUser = new UsersController();
            GroupsController conGroup = new GroupsController();
            ConformsController conConform = new ConformsController();
            MessagesUserController conMessageUser = new MessagesUserController();
            MessagesGroupController conMessageGroup = new MessagesGroupController();

            StringBuilder listaMensajesUsuarios = new StringBuilder();
            StringBuilder listaMensajesGrupos = new StringBuilder();
                
            int idUser = conUser.buscarId(this.usuario);
                
            ArrayList<MessagesUser> listaUsuarios = conMessageUser.getAll();
            ArrayList<MessagesGroup> listaGrupo = conMessageGroup.getAll();
                
            for(MessagesUser mu : listaUsuarios)
            {
                if(mu.getUserEmisor() == idUser || mu.getUserReceptor() == idUser)
                {
                    if(listaMensajesUsuarios.length() > 0) listaMensajesUsuarios.append(",");
                    listaMensajesUsuarios.append(conUser.buscarUserName(mu.getUserEmisor()));
                    listaMensajesUsuarios.append(":");
                    listaMensajesUsuarios.append(conUser.buscarUserName(mu.getUserReceptor()));
                    listaMensajesUsuarios.append(":");
                    listaMensajesUsuarios.append(mu.getMessage());
                }
            }
            System.out.println("Mensajes Usuarios: " + listaMensajesUsuarios);
                
            ArrayList<Conform> listaConform = conConform.getByUser(conUser.buscarId(this.usuario));                
            ArrayList<Groups> listaGrupos = conGroup.getAll();

            StringBuilder existentesGrupos = new StringBuilder();
                
            for(Groups grp : listaGrupos)
            {
                if(grp.getUserCreator() == conUser.buscarId(this.usuario))
                {
                    if(existentesGrupos.length() > 0) existentesGrupos.append(",");
                    existentesGrupos.append(grp.getName());
                }
            }
                
            for(Conform conf : listaConform)
            {
                if(conf.getUser() == conUser.buscarId(this.usuario))
                {
                    if(existentesGrupos.length() > 0) existentesGrupos.append(",");
                    existentesGrupos.append(conGroup.getGroup(conf.getGroups()).getName());
                }
            }
            System.out.println("Grupos existentes: " + existentesGrupos);
                
            for(MessagesGroup mg : listaGrupo)
            {
                if(existentesGrupos.toString().contains(conGroup.getGroup(mg.getGroups()).getName()))
                {
                    if(listaMensajesGrupos.length() > 0) listaMensajesGrupos.append(",");
                    listaMensajesGrupos.append(conGroup.getGroup(mg.getGroups()).getName());
                    listaMensajesGrupos.append(":");
                    listaMensajesGrupos.append(conUser.buscarUserName(mg.getUserEmisor()));
                    listaMensajesGrupos.append(":");
                    listaMensajesGrupos.append(mg.getMessage());
                }
            }
            System.out.println("Mensajes Grupos: " + listaMensajesGrupos);

            PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            out.println("MessagesGroup");
            out.flush();
            out.println(listaMensajesUsuarios.toString());
            out.flush();
            out.println(listaMensajesGrupos.toString());
            out.flush();
        }
    }
 }