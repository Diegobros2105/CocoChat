/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelos.MessagesUser;
import modelos.User;

/**
 *
 * @author 52331
 */
public class MessagesUserController extends ProyectoCoco{
    
    public ArrayList<MessagesUser> getAll(){
        ArrayList<MessagesUser> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM messagesuser");
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                MessagesUser message = new MessagesUser();
                
                message.setId(rs.getInt("Id"));
                message.setMessage(rs.getString("Message"));
                message.setUserEmisor(rs.getInt("UserEmisor"));
                message.setUserReceptor(rs.getInt("UserReceptor"));
                
                lista.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public void add(MessagesUser message){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "INSERT INTO messagesuser values (null, ?, ?, ?)"
            );
            ps.setString(1, message.getMessage());
            ps.setInt(2, message.getUserEmisor());
            ps.setInt(3, message.getUserReceptor());
            ps.executeUpdate();
            
            System.out.println("Usuario agregado");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void Login(User user){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "UPDATE users SET Estado = 'A' WHERE user = ?" 
            );
            ps.setString(1, user.getUser());
            ps.executeUpdate();
            
            System.out.println("Usuario Activado");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /*public void Logout(User user){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "UPDATE users SET Estado = 'A' WHERE user = ?" 
            );
            ps.setString(1, user.getUser());
            ps.executeUpdate();
            
            System.out.println("Usuario Activado");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }*/
    
    public boolean buscarUsuario(String username, String password) {
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            // Preparamos la consulta SQL
            ps = getConnection().prepareStatement("SELECT * FROM users WHERE User = ? AND Password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            // Si rs.next() es true, significa que encontró al menos un registro que coincide con los criterios.
            if (rs.next()) {
                return true;  // El usuario existe
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        
        return false;  // No se encontró el usuario
    }
    
    public void update(User user){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "UPDATE users SET Name=?,LastName=?,User=?,Password=? WHERE Id=?"
            );
            ps.setString(1, user.getName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUser());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getId());
            ps.executeUpdate();
            
            System.out.println("Usuario Modificado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(int Id){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "DELETE FROM users WHERE Id = ?"
            );
            ps.setInt(1, Id);
            ps.executeUpdate();
            
            System.out.println("Usuario Eliminado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
