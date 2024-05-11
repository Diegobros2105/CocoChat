package cocochatserver;

import db.FriendsController;
import db.UsersController;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import modelos.Friend;
import modelos.User;

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
                        case "ChatGeneral":
                            message = reader.readLine();
                            System.out.println(message);
                            for(Handler h : lista)
                            {
                                if(!h.usuario.equals(this.usuario))
                                {
                                    
                                    PrintWriter out = new PrintWriter(h.clientSocket.getOutputStream(), true);

                                    out.println(message);
                                    out.flush();
                                    out.println(usuario);
                                    out.flush();
                                
                                }


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
        }

        private void handleRegister(BufferedReader reader) throws IOException {
            String nombre = reader.readLine();
            String apellido = reader.readLine();
            String usuario = reader.readLine();
            String contrasena = reader.readLine();
            User u = new User(nombre, apellido, usuario, contrasena);
            UsersController con = new UsersController();
            con.add(u);
        }
        
        private void handleOlvidar(BufferedReader reader) throws IOException {
            String usu = reader.readLine();
            UsersController con = new UsersController();
            String password = new String();
            System.out.println(usu);
            ArrayList<User> list = con.getAll();
            for(User l : list)
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
                ArrayList<User> l = con.getAll();

                for(User u : l)
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
            ArrayList<Friend> l = conFriend.getAll();
            
            for(Friend f : l)
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
        
        private void sendGroups() throws IOException {
            
        }

        
    }
    
    /*public static void main(String[] args) throws IOException {
        UsersController con = new UsersController();
        int Id = con.buscarId("Diego");
        System.out.println(Id);
    }*/

}