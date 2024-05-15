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
import modelos.Users;

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
            System.out.println(message.getMessage());
            System.out.println(message.getUserEmisor());
            System.out.println(message.getUserReceptor());
            ps.executeUpdate();
            
            System.out.println("Mensaje Usuario agregado");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void update(MessagesUser message){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "UPDATE messagesUser SET Message=?,UserEmisor=?,UserReceptor=? WHERE Id=?"
            );
            ps.setString(1, message.getMessage());
            ps.setInt(2, message.getUserEmisor());
            ps.setInt(3, message.getUserReceptor());
            ps.setInt(4, message.getId());
            ps.executeUpdate();
            
            System.out.println("Mensaje Usuario Modificado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(int Id){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "DELETE FROM messagesUser WHERE Id = ?"
            );
            ps.setInt(1, Id);
            ps.executeUpdate();
            
            System.out.println("Mensaje Usuario Eliminado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
