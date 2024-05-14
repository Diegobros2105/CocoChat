/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelos.Groups;
import modelos.MessagesUser;

/**
 *
 * @author 52331
 */
public class GroupsController extends ProyectoCoco {
    
    public ArrayList<Groups> getAll(){
        ArrayList<Groups> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM groups");
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                Groups grupo = new Groups();
                
                grupo.setId(rs.getInt("Id"));
                grupo.setName(rs.getString("Name"));
                grupo.setDescription(rs.getString("Description"));
                grupo.setUserCreator(rs.getInt("UserCreator"));
                
                lista.add(grupo);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public Groups getGroup(int Id){
        ResultSet rs;
        PreparedStatement ps;
        
        Groups group = new Groups();
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM groups WHERE Id=?");
            ps.setInt(1,Id);
            rs = ps.executeQuery();
            
            
            while(rs.next()){
                group.setId(rs.getInt("Id"));
                group.setName(rs.getString("Name"));
                group.setDescription(rs.getString("Description"));
                group.setUserCreator(rs.getInt("UserCreator"));
                
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return group;
    }
    
    
    
    public Groups getGroupId(String name){
        ResultSet rs;
        PreparedStatement ps;
        
        Groups group = new Groups();
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM groups WHERE Name=?");
            ps.setString(1,name);
            rs = ps.executeQuery();
            
            
            while(rs.next()){
                group.setId(rs.getInt("Id"));
                group.setName(rs.getString("Name"));
                group.setDescription(rs.getString("Description"));
                group.setUserCreator(rs.getInt("UserCreator"));
                
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return group;
    }
    
    public int getUserCreatorByName(String name){
        ResultSet rs;
        PreparedStatement ps;
        
        int Id = 0;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM groups WHERE Name=?");
            ps.setString(1,name);
            rs = ps.executeQuery();
            
            
            while(rs.next()){
                Id = rs.getInt("UserCreator");
                
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return Id;
    }
    
    public void add(Groups grupo){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "INSERT INTO groups values (null, ?, ?, ?)"
            );
            ps.setString(1, grupo.getName());
            ps.setString(2, grupo.getDescription());
            ps.setInt(3, grupo.getUserCreator());
            ps.executeUpdate();
            
            System.out.println("Grupo agregado");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void update(Groups grupo){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "UPDATE groups SET Name=?,Description=?,UserCreator=? WHERE Id=?"
            );
            ps.setString(1, grupo.getName());
            ps.setString(2, grupo.getDescription());
            ps.setInt(3, grupo.getUserCreator());
            ps.setInt(4, grupo.getId());
            ps.executeUpdate();
            
            System.out.println("Grupo Modificado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(int Id){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "DELETE FROM groups WHERE Id = ?"
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
