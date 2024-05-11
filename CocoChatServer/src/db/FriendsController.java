/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelos.Friend;
import modelos.User;

/**
 *
 * @author 52331
 */
public class FriendsController extends ProyectoCoco{
    
    public ArrayList<Friend> getAll(){
        ArrayList<Friend> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM friends");
            rs = ps.executeQuery();
            
            while(rs.next()){
                Friend friend = new Friend();
                friend.setId(rs.getInt("Id"));
                friend.setUserReceptor(rs.getInt("UserReceptor"));
                friend.setUserEmisor(rs.getInt("UserEmisor"));
                friend.setEstado(rs.getString("Estado"));
                lista.add(friend);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public void add(Friend friend){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "INSERT INTO friends values (null, ?, ?, 'P')"
            );
            ps.setInt(1, friend.getUserReceptor());
            ps.setInt(2, friend.getUserEmisor());
            ps.executeUpdate();
            
            System.out.println("Invitacion Amigo agregado");
            System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void update(Friend friend){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "UPDATE friends SET Estado='A' WHERE UserEmisor=? AND UserReceptor=?"
            );
            ps.setInt(1, friend.getUserEmisor());
            ps.setInt(2, friend.getUserReceptor());
            ps.executeUpdate();
            
            System.out.println("Invitacion Amigo Modificado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(Friend friend){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                    "DELETE FROM friends WHERE UserEmisor=? AND UserReceptor=?"
            );
            ps.setInt(1, friend.getUserEmisor());
            ps.setInt(2, friend.getUserReceptor());
            ps.executeUpdate();
            
            System.out.println("Invitacion Amigo Eliminado");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
