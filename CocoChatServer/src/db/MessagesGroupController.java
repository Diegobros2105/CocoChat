/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelos.MessagesGroup;
import modelos.MessagesUser;

/**
 *
 * @author 52331
 */
public class MessagesGroupController extends ProyectoCoco {
    
    public ArrayList<MessagesGroup> getAll(){
        ArrayList<MessagesGroup> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM messagesgroup");
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                MessagesGroup message = new MessagesGroup();
                
                message.setId(rs.getInt("Id"));
                message.setMessage(rs.getString("Message"));
                message.setUserEmisor(rs.getInt("UserEmisor"));
                message.setGroups(rs.getInt("Groups"));
                
                lista.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public void add(MessagesGroup message){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "INSERT INTO messagesgroup values (null, ?, ?, ?)"
            );
            ps.setString(1, message.getMessage());
            ps.setInt(2, message.getUserEmisor());
            ps.setInt(3, message.getGroups());
            ps.executeUpdate();
            
            System.out.println("Mensaje Grupo agregado");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void update(MessagesGroup message){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "UPDATE messagesgroup SET Message=?,UserEmisor=?,Groups=? WHERE Id=?"
            );
            ps.setString(1, message.getMessage());
            ps.setInt(2, message.getUserEmisor());
            ps.setInt(3, message.getGroups());
            ps.setInt(4, message.getId());
            ps.executeUpdate();
            
            System.out.println("Mensaje Grupo Modificado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(int Id){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "DELETE FROM messagesgroup WHERE Id = ?"
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
