/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelos.Conform;
import modelos.MessagesUser;

/**
 *
 * @author 52331
 */
public class ConformsController extends ProyectoCoco{
    
    public ArrayList<Conform> getAll(){
        ArrayList<Conform> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM conform");
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                Conform conf = new Conform();
                
                conf.setId(rs.getInt("Id"));
                conf.setUser(rs.getInt("User"));
                conf.setGroups(rs.getInt("Groups"));
                conf.setEstado(rs.getString("Estado"));
                
                lista.add(conf);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public ArrayList<Conform> getAllConformsGroup(int Groups){
        ArrayList<Conform> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM conform WHERE Groups=?");
            ps.setInt(1, Groups);
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                Conform conf = new Conform();
                
                conf.setId(rs.getInt("Id"));
                conf.setUser(rs.getInt("User"));
                conf.setGroups(rs.getInt("Groups"));
                conf.setEstado(rs.getString("Estado"));
                
                lista.add(conf);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public ArrayList<Conform> getByGroup(int Id){
        ArrayList<Conform> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM conform WHERE Groups=?");
            ps.setInt(1, Id);
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                Conform conf = new Conform();
                
                conf.setId(rs.getInt("Id"));
                conf.setUser(rs.getInt("User"));
                conf.setGroups(rs.getInt("Groups"));
                conf.setEstado(rs.getString("Estado"));
                
                lista.add(conf);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public Conform getByGroupAndUser(int IdGroup, int IdUser){
        ResultSet rs;
        PreparedStatement ps;
        Conform conf = new Conform();
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM conform WHERE Groups=? AND User=?");
            ps.setInt(1, IdGroup);
            ps.setInt(2, IdUser);
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                conf.setId(rs.getInt("Id"));
                conf.setUser(rs.getInt("User"));
                conf.setGroups(rs.getInt("Groups"));
                conf.setEstado(rs.getString("Estado"));
                
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return conf;
    }
    
    public ArrayList<Conform> getByUser(int IdUser){
        ResultSet rs;
        PreparedStatement ps;
        Conform conf = new Conform();
        ArrayList<Conform> lista = new ArrayList<>();
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM conform WHERE User=?");
            ps.setInt(1, IdUser);
            rs = ps.executeQuery();
            
            while(rs.next()){
                
                conf.setId(rs.getInt("Id"));
                conf.setUser(rs.getInt("User"));
                conf.setGroups(rs.getInt("Groups"));
                conf.setEstado(rs.getString("Estado"));
                
                lista.add(conf);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public void add(Conform conf){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "INSERT INTO conform values (null, ?, ?, 'D')"
            );
            ps.setInt(1, conf.getUser());
            ps.setInt(2, conf.getGroups());
            ps.executeUpdate();
            
            System.out.println("Invitacion agregada");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void update(Conform conf){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "UPDATE conform SET Estado='A' WHERE User=? AND Groups=?"
            );
            ps.setInt(1, conf.getUser());
            ps.setInt(2, conf.getGroups());
            ps.executeUpdate();
            
            System.out.println("Invitacion Modificada");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(int Id){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "DELETE FROM conform WHERE Id = ?"
            );
            ps.setInt(1, Id);
            ps.executeUpdate();
            
            System.out.println("Invitacion eliminada");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void deleteConection(int IdUser, int IdGroups){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "DELETE FROM conform WHERE User = ? AND Groups=?"
            );
            ps.setInt(1, IdUser);
            ps.setInt(2, IdGroups);
            ps.executeUpdate();
            
            System.out.println("Invitacion eliminada");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
