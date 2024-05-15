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
        ServerSocket serverSocket = new ServerSocket(port);//Se inicializa el servor con el puerto antes establecido
        System.out.println("Servidor iniciado en el puerto " + port);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept(); //Esperando que se conecte el cliente
                System.out.println("Nuevo cliente conectado");
                new Thread(new Handler(clientSocket)).start();//Se crea un objeto de la lase Handler y se actia como un nuevo thread
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class Handler implements Runnable {
        
        private final Socket clientSocket;
        private String usuario;

        public Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String contexto;

                while ((contexto = reader.readLine()) != null) { //Mientras que el string enviado como conntexto no sea null
                    System.out.println(contexto);

                    switch (contexto) {
                        case "Login": //Si llega el String para hacer Login
                            handleLogin(reader); //Se llama al metodo de login
                            
                            break;
                        case "Register": //Si llega el String para hacer Register
                            handleRegister(reader); //Se llama al metodo de Register
                            
                            break;
                        case "Logout": //Si llega el String Logout
                            handleLogout(); //Se llama al metodo de Logout
                            
                            return; // Termina el hilo después de manejar el logout
                        case "OlvidarContrasena": //Si llega el String 
                            handleOlvidar(reader); //Se llama al metodo olvidar
                            
                            break;
                        case "MessageUser": //Si llega el String Message User
                            String message = reader.readLine(); //Recibir el mensaje
                            System.out.println(message);
                            String usuarioReceptor = reader.readLine(); //Recibir el usuario que recibe el mensaje
                            System.out.println(usuarioReceptor);
                            if(message != null && usuarioReceptor != null)
                            {
                                for(Handler h : lista) //Navegar por la lista de clientes conectados
                                {
                                    if(h.usuario.equals(usuarioReceptor)) //Si algun nombre de cliente es igua al usuario enviado
                                    {
                                        PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true); 
                                        //Hacer un canal de envio de informacion para el socket del cliente al que se le envia el mensaje
                                        out.println("MessageUser"); //Enviar el String MessageUser para dar a conocer al clienteReceptor
                                        out.flush();
                                        out.println(message); //Enviar el mensaje al clienteReceptor
                                        out.flush();
                                        out.println(this.usuario); //Enviar que usuario envia el mensaje
                                        out.flush();
                                    }
                                }
                            }
                            
                            break;
                        case "MessageFriend": //Si llega el String MessageFriend
                            String messageFriendConected = reader.readLine(); //Recibe el mensaje
                            String userFriendConected = reader.readLine(); //Recibe el usuario al que le envia el mensaje
                            
                            System.out.println(messageFriendConected);
                            System.out.println(userFriendConected);
                            
                            if(messageFriendConected != null && userFriendConected != null)
                            {
                                UsersController conUserMessageFriend = new UsersController();
                                MessagesUserController conMessageUSerMessageFriend = new MessagesUserController();

                                MessagesUser msg = new MessagesUser(messageFriendConected,conUserMessageFriend.buscarId(userFriendConected),conUserMessageFriend.buscarId(this.usuario));
                                conMessageUSerMessageFriend.add(msg); //Se crea el Mensaje para guardarlo en la BD y se agrega a la BD
                            
                                for(Handler h : lista) //Se navega a traves de la lista de clientes
                                {
                                    if(h.usuario.equals(userFriendConected)) //Si el nombre del cliente es igual al nombre enviado
                                    {
                                        PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true); //Se crea la conexion al cliente que recibe
                                        out.println("MessageFriend"); //Se envia el String MessageFriend al cliente que se envia
                                        out.flush();
                                        out.println(messageFriendConected); //Se envia el mensaje
                                        out.flush();
                                        out.println(this.usuario);//Se envia el nombre del usuario que envia el mensaje
                                        out.flush();
                                    }
                                }
                            }
                            
                            break;
                        case "MessageFriendBD": //Si llega el String MessageFriendBF
                            String messageFriendNoConected = reader.readLine(); //Recibe el mensaje
                            String userFriendNoConected = reader.readLine(); //Recibe el usuario
                            
                            System.out.println(messageFriendNoConected);
                            System.out.println(userFriendNoConected);
                            
                            if(messageFriendNoConected != null && userFriendNoConected != null)
                            {
                                UsersController conUserMessageFriendNo = new UsersController();
                                MessagesUserController conMessageUSerMessageFriendNo = new MessagesUserController();

                                MessagesUser msgNo = new MessagesUser(messageFriendNoConected,conUserMessageFriendNo.buscarId(userFriendNoConected),conUserMessageFriendNo.buscarId(this.usuario));
                                conMessageUSerMessageFriendNo.add(msgNo); //Se crea el Mensaje y se añade a la BD
                            }
                            
                            break;
                        case "InvitationsFriendsAccepted": //Si llega el String InvitationsFriendsAccepted
                            String userAcepted = reader.readLine(); //Recibe el usuario del que se acepta la invitacion
                            System.out.println(userAcepted);
                            
                            if(userAcepted != null)
                            {
                                UsersController conUsersAcepted = new UsersController();
                                FriendsController conFriendsAcepted = new FriendsController();
                                Friends frd = new Friends(); //Se crea un objeto de Friends
                                frd.setUserEmisor(conUsersAcepted.buscarId(userAcepted)); 
                                //Se agregar el int de UserEmisor a partir del usuario al que va a acpetar la invitacion
                                frd.setUserReceptor(conUsersAcepted.buscarId(this.usuario));
                                //Se agrega el int de UserReceptor el cual es este usuario.
                                conFriendsAcepted.update(frd); //A partir de esta informacion se actualiza 

                                sendUsers(); //Se vuelven a enviar usuarios conectados y desconectados
                                sendInvitationsFriends(); //Se vuelven a enviar las invitaciones de amigos
                            }
                            
                            break;
                        case "InvitationsFriendsDenied": //Si llega el String InvitationsFriendsDenied
                            String userDenied = reader.readLine(); //Recibe el usuario del que se deniega la invitacion
                            System.out.println(userDenied);
                            
                            if(userDenied != null)
                            {
                                UsersController conUsersDenied = new UsersController();
                                FriendsController conFriendsDenied = new FriendsController();
                                Friends fri = new Friends(); //Se crea un objeto de Friends
                                fri.setUserEmisor(conUsersDenied.buscarId(userDenied));
                                //Se agregar el int de UserEmisor a partir del usuario al que va a denegar la invitacion
                                fri.setUserReceptor(conUsersDenied.buscarId(this.usuario));
                                //Se agrega el int de UserReceptor el cual es este usuario.
                                conFriendsDenied.delete(fri);//A partir de esta informacion se borra de la base de datos

                                sendUsers(); //Se vuelven a enviar usuarios conectados y desconectados
                                sendInvitationsFriends(); //Se vuelven a enviar las invitaciones de amigos
                            }
                            
                            break;
                        case "InvitationsFriendsSend": //Si llega el String InvitationsFriendsSend
                            String userSend = reader.readLine(); //Recibe el usuarios al que se manda la invitacion
                            System.out.println(userSend);
                            
                            if(userSend != null)
                            {
                                UsersController conUsersSned = new UsersController();
                                FriendsController conFriendsSend = new FriendsController();
                                Friends friSend = new Friends(); //Se crea un objeto de Friends
                                friSend.setUserEmisor(conUsersSned.buscarId(this.usuario));
                                //Se agregar el int de UserEmisor a partir del usuario que la envia
                                friSend.setUserReceptor(conUsersSned.buscarId(userSend));
                                //Se agrega el int de UserReceptor el cual es el usuario que se manda
                                conFriendsSend.add(friSend); //A partir de esta informacion se agrega la invitacion pendiente

                                sendUsers();
                                sendInvitationsFriends();
                            }
                            
                            break;
                        case "MessageGroup": //Si llega el String MessageGroup
                            String messageGroup = reader.readLine(); //Recibe el mensaje de grupo
                            String groupMessage = reader.readLine();  //Recibe el grupo al que se envia el mensaje
                            
                            System.out.println(messageGroup);
                            System.out.println(groupMessage);
                            
                            if(messageGroup != null && groupMessage != null)
                            {
                                UsersController conUserMessageGroup = new UsersController();
                                GroupsController conGroup = new GroupsController();
                                MessagesGroupController conMessageGroupMessage = new MessagesGroupController();

                                MessagesGroup mgrp = new MessagesGroup(messageGroup,conUserMessageGroup.buscarId(this.usuario),conGroup.getGroupId(groupMessage).getId());
                                //Se crea el objeto del mensaje grupo con el mensaje, el Id del usuario y el Id del grupo
                                conMessageGroupMessage.add(mgrp); //Se agrega elo objeto a la tabla de mensajes de grupo

                                sendMessages(); //Se vuelven a enviar los mensajes
                            }

                            break;
                        case "SearchUsersInGroup": //Si llega el String SearchUsersInGroup
                            String group = reader.readLine(); //Recibe el grupo
                            System.out.println("Grupo enviado: " + group);
                            
                            if(group != null)
                            {
                                StringBuilder usuariosGrupo = new StringBuilder();
                                GroupsController conGroups = new GroupsController();
                                Groups g = new Groups();
                                ConformsController conConforms = new ConformsController();
                                ArrayList<Conform> listConforms = new ArrayList<>();
                                UsersController conUsers = new UsersController();

                                g = conGroups.getGroupId(group);//Se obtiene el objeto Group a partir del nombre del grupo
                                listConforms = conConforms.getByGroup(g.getId()); 
                                //Se obtienen todas los vinculos/invitaciones entre el grupo y el resto de usuarios

                                System.out.println(group);
                                usuariosGrupo.append(conUsers.buscarUserName(conGroups.getUserCreatorByName(group)));
                                //Se agrega al String builder el usuario creador del grupo

                                System.out.println(usuariosGrupo);

                                for(Conform c : listConforms) //Se navega a traves de la lista de conexiones/invitaciones grupo usuario
                                {
                                    if(usuariosGrupo.length() > 0) usuariosGrupo.append(",");
                                    if(c.getEstado().equals("A")) //Si la invitacion a sido aceptada
                                    {
                                        usuariosGrupo.append(conUsers.buscarUserName(c.getUser())); 
                                        //Se agregan los usuarios del grupo a un StringBuilder
                                    }
                                }

                                StringBuilder usuariosConectadosGrupo = new StringBuilder();
                                StringBuilder usuariosDesconectadosGrupo = new StringBuilder();
                                StringBuilder usuariosConectados = new StringBuilder();

                                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); 
                                //Se envia la informacion al usuario que pidio los ususarios del grupo

                                for(Handler h : lista) //Se navega por la lista de todos los usuarios conectados
                                {
                                    if(usuariosConectados.length() > 0) usuariosConectados.append(",");
                                    usuariosConectados.append(h.usuario); //Se agregan todos los usuarios conectados al StringBuilder
                                }
                                for(String user : usuariosGrupo.toString().split(",")) //Se navega a traves del StringBuilder dividido por las comas
                                {
                                    if(usuariosConectados.toString().contains(user)) //Si el usuario de la lista grupo tambien esta en la lista conectada
                                    {
                                        if(usuariosConectadosGrupo.length() > 0) usuariosConectadosGrupo.append(",");
                                        usuariosConectadosGrupo.append(user);//Se agregan todos los usuarios conectados al StringBuilder
                                    }
                                    else
                                    {
                                        if(usuariosDesconectadosGrupo.length() > 0) usuariosDesconectadosGrupo.append(",");
                                        usuariosDesconectadosGrupo.append(user);//Se agregan todos los usuarios desconectados al StringBuilder
                                    }
                                }
                                System.out.println(usuariosGrupo);
                                System.out.println("Grupo Cliente Conectado: " + usuariosConectadosGrupo);
                                System.out.println("Grupo Cliente Desconectado: " + usuariosDesconectadosGrupo);

                                out.println("UsuariosGrupo"); //Se envia la etiqueta UsuariosGrupo al cliente 
                                out.flush();
                                out.println(usuariosConectadosGrupo.toString()); //Se envia los usuarios conectados del grupo 
                                out.flush();
                                out.println(usuariosDesconectadosGrupo.toString()); //Se envia los usuarios desconectados del grupo
                                out.flush();
                            }
                            
                            break;
                        case "SearchUsersNoInGroup": //Si llega el String SearchUsersNoInGroup
                            String groupSearch = reader.readLine(); //Recibe el grupo que se busca
                            System.out.println(groupSearch);
                            
                            if(groupSearch != null)
                            {
                                StringBuilder usuariosConf = new StringBuilder();
                                StringBuilder usuariosConformGroup = new StringBuilder();
                                GroupsController conGroupsSearch = new GroupsController();
                                ConformsController conConformsSearch = new ConformsController();
                                UsersController conUsersSearch = new UsersController();

                                ArrayList<Users> usersSearch = conUsersSearch.getAll();

                                ArrayList<Conform> conformSearch = conConformsSearch.getAllConformsGroup(conGroupsSearch.getGroupId(groupSearch).getId());
                                //Se obtienen todas las invitaciones a partir del id del gupo
                                
                                usuariosConf.append(this.usuario); 
                                for(Conform cf : conformSearch) //Se navega a traves de la lista de invitaciones
                                {
                                    if(usuariosConf.length() > 0) usuariosConf.append(",");
                                    usuariosConf.append(conUsersSearch.buscarUserName(cf.getUser())); //Se agrega al StringBUilder los usuarios
                                }

                                for(Users us : usersSearch) //Se navega a traves de la lista de invitaciones
                                {
                                    if(!usuariosConf.toString().contains(us.getUser())) //Si los usuarios en el grupo no se encuentran en el grupo
                                    {
                                        if(usuariosConformGroup.length() > 0) usuariosConformGroup.append(",");
                                        usuariosConformGroup.append(us.getUser()); //Se agregan al StrinBuilder los usuarios
                                    }
                                }
                                System.out.println(usuariosConformGroup);

                                PrintWriter outData = new PrintWriter(clientSocket.getOutputStream(), true);

                                outData.println(usuariosConformGroup.toString()); //Se envia el String de los usuarios que no estan en el grupo
                                outData.flush();
                            }
                            
                            break;
                        case "CreatorGroup": //Si llega el String CreatorGroup
                            String nombre = reader.readLine(); //Recibe el nombre del grupo a crear
                            String descripcion = reader.readLine(); //Recibe la descripcion del grupo a crear
                            String seleccion = reader.readLine(); //Recibe la seleccion de usuarios del grupo a crear
                            
                            System.out.println(nombre);
                            System.out.println(descripcion);
                            System.out.println(seleccion);
                            
                            if(nombre != null && descripcion != null && seleccion != null)
                            {
                                GroupsController conGroupsCreator = new GroupsController();
                                UsersController conUserGroup = new UsersController();
                                
                                Groups grupoCreator = new Groups(); //Se crea el objeto Grupo
                                grupoCreator.setName(nombre); //Se inserta el nombre en el objeto
                                grupoCreator.setDescription(descripcion); //Se inserta la descripcion en el objeto
                                grupoCreator.setUserCreator(conUserGroup.buscarId(this.usuario)); //Insertar el usuario creador a partir el usuario que envio en el objeto

                                conGroupsCreator.add(grupoCreator); //Se añade el grupo 

                                grupoCreator = conGroupsCreator.getGroupId(nombre); //Se obtiene el grupo recien creado

                                int idGroup = grupoCreator.getId(); //Se obtiene el Id del grupo recien creado

                                ConformsController conConformsCreatorGroup = new ConformsController();
                                Conform conf = new Conform();

                                for(String persona : seleccion.split(",")) //Se navega a traves de los usuarios en la seleccion de usuarios
                                {
                                    conf.setGroups(idGroup); //Se agrega el id del grupo como FK de grupo para el vinculo
                                    conf.setUser(conUserGroup.buscarId(persona)); //Se agrega el id del usuario seleccionado para el vinculo
                                    conConformsCreatorGroup.add(conf); //Se agrega la invitacion
                                }

                                sendGroups(); //Se vuelven a enviar los mensajes
                                sendInvitationsGroups(); //Se vuelven a enviar las invitaciones de amigos
                            }
                            
                            break;
                        case "InvitationGroup": //Si llega el String InvitationGroup
                            String usuariosInvitados = reader.readLine(); //Recibe los usuarios invitados
                            String grupoInvitado = reader.readLine(); //Recibe el grupo al que se invita
                            
                            if(grupoInvitado != null && usuariosInvitados != null)
                            {
                                System.out.println(usuariosInvitados);
                                System.out.println(grupoInvitado);

                                ConformsController conConformInvitation = new ConformsController();
                                GroupsController conGroupInvitation = new GroupsController();
                                UsersController conUserInvitation = new UsersController();

                                for(String usuarioInv : usuariosInvitados.split(",")) //Se navega a traves de los usuarios
                                {
                                    Conform confi = new Conform();
                                    Groups grpInvi = conGroupInvitation.getGroupId(grupoInvitado); //Se busca el id del grupo al que se invita
                                    confi.setGroups(grpInvi.getId()); //Se inserta el id del grupo
                                    confi.setUser(conUserInvitation.buscarId(usuarioInv)); //Se inserta el id del usuario
                                    conConformInvitation.add(confi); //Se añade la invitacion
                                }

                                sendGroups(); //Se vuelven a enviar los mensajes
                                sendInvitationsGroups(); //Se vuelven a enviar las invitaciones de amigos
                            }
                            
                            break;
                        case "InvitationGroupAccept": //Si llega el String InvitationGroupAccept
                            String grupoInvitacion = reader.readLine(); //Recibe el nombre del grupo
                            
                            System.out.println(grupoInvitacion);
                            
                            if(grupoInvitacion != null)
                            {
                                ConformsController conConform = new ConformsController();
                                GroupsController conGrp = new GroupsController();
                                UsersController conUs = new UsersController();
                                Conform conforms;
                                Groups grps = conGrp.getGroupId(grupoInvitacion); //Se busca el id del grupo
                                conforms = conConform.getByGroupAndUser(grps.getId(), conUs.buscarId(this.usuario));
                                //Se busca la invitacion a partir del id del grupo y el id del usuario

                                conConform.update(conforms); //Se actualiza a partir del objeto, se pone en A

                                sendGroups(); //Se vuelven a enviar las invitaciones de grupos
                                sendInvitationsGroups();  //Se vuelven a enviar las invitaciones de grupos
                            }
                            
                            break;
                        case "InvitationGroupDenny": //Si llega el String InvitationGroupDenny
                            String grupoInvitacion2 = reader.readLine(); //Recibe el nombre del grupo
                            
                            System.out.println(grupoInvitacion2);
                            
                            if(grupoInvitacion2 != null)
                            {
                                ConformsController conConform2 = new ConformsController();
                                GroupsController conGrp2 = new GroupsController();
                                UsersController conUs2 = new UsersController();
                                Conform conforms2;
                                Groups grps2 = conGrp2.getGroupId(grupoInvitacion2); //Se busca el id del grupo
                                conforms2 = conConform2.getByGroupAndUser(grps2.getId(), conUs2.buscarId(this.usuario));
                                //Se busca la invitacion a partir del id del grupo y el id del usuario
                                
                                conConform2.delete(conforms2.getId()); //Se borra la invitacion a partir de la id anteriormente conseguida

                                sendGroups(); //Se vuelven a enviar las invitaciones de grupos
                                sendInvitationsGroups(); //Se vuelven a enviar las invitaciones de grupos
                            }
                            
                            break;
                        case "DeleteGroup": //Si llega el String DeleteGroup
                            String grupoBorrado = reader.readLine(); //Recibe el grupo a borrar
                            
                            if(grupoBorrado != null)
                            {
                                System.out.println(grupoBorrado);

                                ConformsController conConformBorrar = new ConformsController();
                                GroupsController conGroupBorrar = new GroupsController();
                                UsersController conUserBorrar = new UsersController();

                                conGroupBorrar.delete(conGroupBorrar.getGroupId(grupoBorrado).getId());
                                //Se elimina el grupo con el id del grupo
                                
                                sendGroups(); //Se vuelven a enviar las invitaciones de grupos
                                sendInvitationsGroups(); //Se vuelven a enviar las invitaciones de grupos
                            }
                            
                            break;
                        case "DropDownGroup": //Si llega el String DropDownGroup
                            String dropDownGroup = reader.readLine(); //Recibe el usuarios que se quiere dejar
                            
                            if(dropDownGroup != null)
                            {
                                System.out.println(dropDownGroup);

                                ConformsController conConformBorrar = new ConformsController();
                                GroupsController conGroupBorrar = new GroupsController();
                                UsersController conUserBorrar = new UsersController();

                                conConformBorrar.deleteConection(conUserBorrar.buscarId(this.usuario), conGroupBorrar.getGroupId(dropDownGroup).getId());
                                //Se elimina la invitacion aceptada con el id del usuario y el id del grupo
                                
                                sendGroups(); //Se vuelven a enviar las invitaciones de grupos
                                sendInvitationsGroups(); //Se vuelven a enviar las invitaciones de grupos
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
            String usu = reader.readLine(); //Recibe el usuario
            String contrasena = reader.readLine(); //Recibe la contraseña
            UsersController con = new UsersController();
            boolean bandera = con.buscarUsuario(usu, contrasena); //Se busca el usuario que concuerde con ambos parametro y devuelve un boleano

            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            if (bandera) { //Si el boleano es verdadero
                this.usuario = usu; //La conexion actual se le introduce el usuario
                lista.add(this);
                dataOut.writeInt(1); //Se envia un 1

            } else { //Si no
                dataOut.writeInt(0); //Se envia un 0
            } 
            sendUsers(); //Se envian los usuarios conectados y desconectados, al igual que los amigos conectados y desconectados
            sendGroups(); //Se envian los grupos creados y conformados
            sendInvitationsFriends(); //Se envian Las invitaciones de amigos
            sendInvitationsGroups(); //Se envian las invitaciones de grupos
            sendMessages(); //Se envian los mensajes de amigos y grupos
        }

        private void handleRegister(BufferedReader reader) throws IOException {
            String nombre = reader.readLine(); //Recibe el nombre
            String apellido = reader.readLine(); //Recibe el apellido
            String usuario = reader.readLine(); //Recibe el usuario
            String contrasena = reader.readLine(); //Recibe el contraseña
            Users u = new Users(nombre, apellido, usuario, contrasena); //Se crea el objeto a partir de los valores anteriores
            UsersController con = new UsersController();
            con.add(u); //Se agrega el usuario
            sendUsers(); //Se envian los usuarios
        }
        
        private void handleOlvidar(BufferedReader reader) throws IOException {
            String usu = reader.readLine(); //Recibe el usuario
            UsersController con = new UsersController();
            String password = new String();
            System.out.println(usu);
            ArrayList<Users> list = con.getAll(); //Se obtienen todos los usuarios de la BD
            for(Users l : list) //Se navega a traves de la lista de usuarios
            {
                if(l.getUser().equals(usu)) //Si el usuario recibido y el usuarios en el mmomento coinciden
                {
                    password = l.getPassword(); //Se guarda localmente la varible contraseña
                } 
            }

            PrintWriter outData = new PrintWriter(clientSocket.getOutputStream());
            outData.println(password); //Se envia la contraseña
            outData.flush();
            System.out.println("Enviado");
        }

        private void handleLogout() throws IOException {
            System.out.println("Usuario " + usuario + " ha cerrado sesión.");
            lista.remove(this); //Se elimina este usuario de la lista
            sendUsers(); //Se vuelven a enviar todos los usuarios
        }
        
        
        
        private void sendUsers() throws IOException {
            for (Handler h : lista) { //Se navega a traves de la lista de clientes
                String f = sendFriends(h.usuario); //Se envian los amigos y se obtiene el String de estos amigos del usuario
                System.out.println(f);
                StringBuilder usuariosAmigosConectados = new StringBuilder();
                StringBuilder usuariosAmigosDesconectados = new StringBuilder();

                StringBuilder usuariosConectados = new StringBuilder();
                StringBuilder usuariosDesconectados = new StringBuilder();
                StringBuilder BD = new StringBuilder();

                UsersController con = new UsersController();
                ArrayList<Users> l = con.getAll(); //Se obtienen todos los usuarios en la BD

                for(Users u : l) //Se navega a traves del ArrayList de usuarios en la BD
                {
                    BD.append(u.getUser()).append(","); //Se agrega el usuario a el StringBuilder
                }
                System.out.println("Clientes BD: " + BD);

                for (Handler x : lista) { //Se navega de nuevo en toda la lista de clientes conectados
                    if(f.contains(x.usuario)) //Si el usuario se encuentra en el String de amigos
                    {
                        if (usuariosAmigosConectados.length() > 0) usuariosAmigosConectados.append(",");
                        usuariosAmigosConectados.append(x.usuario); //Se agrega el usuario al usuario amigo conectado
                    }
                    else
                    {
                        if (usuariosConectados.length() > 0) usuariosConectados.append(",");
                        usuariosConectados.append(x.usuario); //Se agrega el usuario al usuario conectado
                    }
                }
                System.out.println("Clientes Amigos Conectados: " + usuariosAmigosConectados);
                System.out.println("Clientes Conectados: " + usuariosConectados);

                String[] allUsers = BD.toString().split(","); //Se agrega a un arreglo todos los usuarios de una BD
                String[] excludedUsers = usuariosConectados.toString().split(","); //Se agrega a un arreglo todos los usuarios ya conectados

                Set<String> excludedSet = new HashSet<>();
                for (String user : excludedUsers) {
                    excludedSet.add(user);
                }

                // Filtrar usuarios y añadir al nuevo StringBuilder
                for (String user : allUsers) { //Se navega a traves de la BD
                    if (!excludedSet.contains(user)) { //Si se encuentra fuera de la lista de excluidos
                        if(f.contains(user)) //Si se encuentra en usuarios amigos
                        {
                            if(!usuariosAmigosConectados.toString().contains(user)) //Si no esta contenido en usuarios conectados
                            {
                                if (usuariosAmigosDesconectados.length() > 0) usuariosAmigosDesconectados.append(",");
                                usuariosAmigosDesconectados.append(user); //Se agrega al StringBuilder de amigos desconectados
                            }
                        }
                        else
                        {
                            if (usuariosDesconectados.length() > 0) usuariosDesconectados.append(",");
                            usuariosDesconectados.append(user); //Se agrega al StringBuilder de desconectados
                        }

                    }
                }
                System.out.println("Clientes Desconectados: " + usuariosDesconectados);
                System.out.println("Clientes Amigos Desconectados: " + usuariosAmigosDesconectados);

                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true); //Se envia al cliente especifico
                out.println("Conexion"); //Se envia todo
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
            ArrayList<Friends> l = conFriend.getAll(); //Se obtienen todas las invitaciones de amigos de la BD
            
            for(Friends f : l) //Se navega  a travez de la Lista
            {
                if(f.getUserEmisor() == conUser.buscarId(user)) //Si el id del usuario que envia es igual al de usuario recibido
                {
                    if(f.getEstado().equals("A")) // Si el estado es Activo
                    {
                        usuariosAmigos.append(conUser.buscarUserName(f.getUserReceptor())).append(","); //Se junta el usuario emisor, contrario al usuario cliente
                    }
                }
                if(f.getUserReceptor() == conUser.buscarId(user)) //Si el id del usuario receptor es igual al de usuario recibido
                {
                    if(f.getEstado().equals("A"))
                    {
                        usuariosAmigos.append(conUser.buscarUserName(f.getUserEmisor())).append(","); //Se junta el usuario receptor, contrario al usuario cliente
                    }
                }
            }
            if (usuariosAmigos.length() > 0) {
                usuariosAmigos.deleteCharAt(usuariosAmigos.length() - 1); // Eliminar la última coma
            }
            System.out.println("Clientes Amigos de " + user + " : " + usuariosAmigos);
            
            return usuariosAmigos.toString(); //Regresa el string de usuarios amigos
            
        }
        
        private void sendInvitationsFriends() throws IOException {
            for(Handler h : lista) //Se navega a traves de la lista de clientes
            {
                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);

                FriendsController conFriends = new FriendsController();
                UsersController conUser = new UsersController();
                ArrayList<Friends> list = new ArrayList<>();
                list = conFriends.getAll(); //Se obtienen todos las invitaciones de amigos de la BD
                StringBuilder invitaciones = new StringBuilder();
                StringBuilder invitacionesPosibles = new StringBuilder();

                for(Friends f : list) //Se navega a traves d ela lista de amigos
                {
                    if(f.getUserReceptor() == conUser.buscarId(h.usuario) && f.getEstado().equals("P")) //Si el usuario que recibe y el usuario enviado y que el estado sea Pendiente
                    {
                        if(invitaciones.length() > 0) invitaciones.append(",");
                        invitaciones.append(conUser.buscarUserName(f.getUserEmisor())); //Se agrega el usuario de la invitacion
                    }
                }
                System.out.println("Invitaciones: " + invitaciones);

                StringBuilder invitacionesEnviadas = new StringBuilder();
                for(Friends f : list) //Se navega a traves d ela lista de amigos
                {
                    if(f.getUserEmisor() == conUser.buscarId(h.usuario)) //Si el usuario que envia y el usuario enviado y que el estado sea Pendiente
                    {
                        if(invitacionesEnviadas.length() > 0) invitacionesEnviadas.append(",");
                        invitacionesEnviadas.append(conUser.buscarUserName(f.getUserEmisor()));
                    }
                }
                System.out.println("Invitaciones enviadas: " + invitacionesEnviadas);
                
                ArrayList<Friends> listFriendsExisting = new ArrayList<>();
                listFriendsExisting = conFriends.getAll(); //Se obtiene toda la lista de amigos
                StringBuilder listExisting = new StringBuilder();

                for(Friends f : listFriendsExisting) //Se navega a traves de la lista de amigos
                {
                    if(f.getEstado().equals("A")) //Si el estado es Activo
                    {
                        if(listExisting.length() > 0) listExisting.append(",");
                        if(f.getUserEmisor() == conUser.buscarId(h.usuario)) //Si el usuario que envia es el usuario enviado
                        {
                            listExisting.append(conUser.buscarUserName(f.getUserReceptor())); //Agregar al StringBuilder el usuario 
                        }
                        if(f.getUserReceptor() == conUser.buscarId(h.usuario)) //Si el usuario que recibe es el usuario enviado
                        {
                            listExisting.append(conUser.buscarUserName(f.getUserEmisor())); //Agregar al StringBuilder el usuario 
                        }
                    }
                }
                System.out.println("Amigos: " + listExisting);

                ArrayList<Users> listUsers = new ArrayList<>();
                listUsers = conUser.getAll();//Se obtiene toda la lista de usuarios
                StringBuilder listBD = new StringBuilder();

                for(Users u : listUsers) //Se navega a traves de la lista de usuarios
                {
                    if(listBD.length() > 0) listBD.append(",");
                    listBD.append(u.getUser()); //Se agrega el usuario al StringBuilder
                }
                System.out.println("BD: " + listBD);

                for(Users u : listUsers) //Se navega a traves de la lista de usuarios
                {
                    if(!invitaciones.toString().contains(u.getUser())) //Si el usuario no se contiene en el string invitaciones
                    {
                        if(!invitacionesEnviadas.toString().contains(u.getUser())) //Si el usuario no se contiene en el string invitaciones enviadas
                        {
                            if(!listExisting.toString().contains(u.getUser())) //Si el usuario no se contiene en el string de existentes
                            {
                                if(invitacionesPosibles.length() > 0) invitacionesPosibles.append(",");
                                invitacionesPosibles.append(u.getUser()); //Se agrega el usuario al StringBuilder
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
            for(Handler h : lista) //Se navega a traves de la lista de clientes
            {
                StringBuilder listGroupConform = new StringBuilder();
                StringBuilder listGroupCreator = new StringBuilder();

                GroupsController conGroup = new GroupsController();
                UsersController conUser = new UsersController();
                ConformsController conConform = new ConformsController();
                ArrayList<Groups> listGroup = new ArrayList<Groups>();
                int idUsuario = conUser.buscarId(h.usuario); //Se obtiene el id del usuario cliente

                listGroup = conGroup.getAll(); //Se obtienen todos los grupos de la BD

                System.out.println("Id del usuario: " + idUsuario);


                for(Groups g : listGroup) //Se navega a traves de la lista de grupos
                {
                    if(g.getUserCreator() == idUsuario) //Si el usuario creador es igual al id del usuario que hace la peticion
                    {
                        if(listGroupCreator.length() > 0) listGroupCreator.append(",");
                        listGroupCreator.append(g.getName()).append(":").append(g.getDescription()); //Se agrega el usuario al StringBuilder
                    }
                }
                System.out.println(listGroupCreator);

                ArrayList<Conform> listConform = new ArrayList<>();
                listConform = conConform.getAll(); //Se obtienen todas las invitaciones de los grupos

                for(Conform c : listConform) //Se navega a tarves de la lista de invitaciones
                {
                    if(c.getUser() == idUsuario && c.getEstado().equals("A")) //Si el usuario en la invitacion de grupo es igual a este usuario y su estado es Activado
                    {
                        Groups g = new Groups();
                        g = conGroup.getGroup(c.getGroups()); //Se obtiene el grupo a partir de su id
                        if(listGroupConform.length() > 0) listGroupConform.append(",");
                        listGroupConform.append(g.getName()); //Se agrega el usuario al StringBuilder
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
            for(Handler h : lista) //Se navega a traves de la lista de clientes
            {
                UsersController conUser = new UsersController();
                GroupsController conGroup = new GroupsController();
                ConformsController conConform = new ConformsController();

                StringBuilder listGroupsInvitation = new StringBuilder();
                ArrayList<Conform> listaConform = conConform.getAll(); //Se obtiene todas las invtiacions de grupo de la BD
                
                for(Conform c : listaConform) //Se navega a traves de la lista de invitaciones
                {
                    if(c.getUser() == conUser.buscarId(this.usuario) && c.getEstado().equals("D")) // Si el usuario en la invitacion y el usuario cliente y que su estado se Detenido
                    {
                        Groups grp = conGroup.getGroup(c.getGroups()); //Se busca el grupo a partir de su id
                        if(listGroupsInvitation.length() > 0) listGroupsInvitation.append(",");
                        listGroupsInvitation.append(grp.getName()); //Se agrega el usuario al StringBuilder
                    }
                }
                System.out.println(listGroupsInvitation);
                        
                int idUser = conUser.buscarId(h.usuario);

                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true); //Se envia a traves del cliente especifico de la lista
                out.println("GroupsInvitations"); //Se envia el string de contexto
                out.flush();
                out.println(listGroupsInvitation.toString()); //Se envia la lista de invitaciones de grupos
                out.flush();
            }
        }
        
        private void sendMessages() throws IOException {
            for(Handler h : lista) //Se navega a traves de la lista de clientes
            {
                UsersController conUser = new UsersController();
                GroupsController conGroup = new GroupsController();
                ConformsController conConform = new ConformsController();
                MessagesUserController conMessageUser = new MessagesUserController();
                MessagesGroupController conMessageGroup = new MessagesGroupController();

                StringBuilder listaMensajesUsuarios = new StringBuilder();
                StringBuilder listaMensajesGrupos = new StringBuilder();

                int idUser = conUser.buscarId(h.usuario); //Se obtiene el id del usuario

                ArrayList<MessagesUser> listaUsuarios = conMessageUser.getAll(); //Se obtienen todos los mensajes de usuarios de la BD
                ArrayList<MessagesGroup> listaGrupo = conMessageGroup.getAll(); //Se obtienen todos los mensajes de grupos de la BD

                for(MessagesUser mu : listaUsuarios) //Se navega a traves de la lista de mensajes de usuarios
                {
                    if(mu.getUserEmisor() == idUser || mu.getUserReceptor() == idUser) //Si el usuario enviado o el usuario receptor son iguales al id del usuario
                    {
                        if(listaMensajesUsuarios.length() > 0) listaMensajesUsuarios.append(",");
                        listaMensajesUsuarios.append(conUser.buscarUserName(mu.getUserEmisor())); //Se agrega el usuario al StringBuilder
                        listaMensajesUsuarios.append(":");
                        listaMensajesUsuarios.append(conUser.buscarUserName(mu.getUserReceptor())); //Se agrega el usuario al StringBuilder
                        listaMensajesUsuarios.append(":");
                        listaMensajesUsuarios.append(mu.getMessage()); //Se agrega el usuario al StringBuilder
                    }
                }
                System.out.println("Mensajes Usuarios: " + listaMensajesUsuarios);

                ArrayList<Conform> listaConform = conConform.getAll(); //Se obtienen todas las invitaciones de grupos de la BD        
                ArrayList<Groups> listaGrupos = conGroup.getAll(); //Se obtienen todos los grupos de la BD

                StringBuilder existentesGrupos = new StringBuilder();

                for(Groups grp : listaGrupos) //Se navega a traves de la lista de  los grupos
                {
                    if(grp.getUserCreator() == conUser.buscarId(h.usuario)) //Si el usuario creador del grupo es igual al id del usuario actual
                    {
                        if(existentesGrupos.length() > 0) existentesGrupos.append(",");
                        existentesGrupos.append(grp.getName()); //Se agrega el usuario al StringBuilder
                    }
                }
                
                System.out.println("Grupos de " + h.usuario + ": " + existentesGrupos);

                for(Conform conf : listaConform) //Se navega a traves de la lista de invitaciones
                {
                    if(conf.getUser() == conUser.buscarId(h.usuario)) //Si el usuario de la invitacion y el actual coinciden
                    {
                        System.out.println(conf.getGroups() + " " + conf.getUser());
                        if(existentesGrupos.length() > 0) existentesGrupos.append(",");
                        existentesGrupos.append(conGroup.getGroup(conf.getGroups()).getName());//Se agrega el usuario al StringBuilder
                    }
                }
                System.out.println("Grupos de " + h.usuario + ": " + existentesGrupos);

                for(MessagesGroup mg : listaGrupo) //Se navega a traves de la lista de grupos
                {
                    if(existentesGrupos.toString().contains(conGroup.getGroup(mg.getGroups()).getName())) //Si los grupos existentes contienen el grupo
                    {
                        if(listaMensajesGrupos.length() > 0) listaMensajesGrupos.append(",");
                        listaMensajesGrupos.append(conGroup.getGroup(mg.getGroups()).getName()); //Se agrega el usuario al StringBuilder
                        listaMensajesGrupos.append(":");
                        listaMensajesGrupos.append(conUser.buscarUserName(mg.getUserEmisor())); //Se agrega el usuario al StringBuilder
                        listaMensajesGrupos.append(":");
                        listaMensajesGrupos.append(mg.getMessage()); //Se agrega el usuario al StringBuilder
                    }
                }
                System.out.println("Mensajes Grupos: " + listaMensajesGrupos);

                PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);
                out.println("MessagesGroup");
                out.flush();
                out.println(listaMensajesUsuarios.toString()); //Se envia la lista de mensajes de usuarios
                out.flush();
                out.println(listaMensajesGrupos.toString()); //Se envia la lista de mensajes de grupos
                out.flush();
            }
        }
    }
 }