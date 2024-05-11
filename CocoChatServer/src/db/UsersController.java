package db;

import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import modelos.User;
/**
 *
 * @author rocob
 */
public class UsersController extends ProyectoCoco{
    
    public ArrayList<User> getAll(){
        ArrayList<User> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM users");
            rs = ps.executeQuery();
            
            while(rs.next()){
                User user = new User();
                user.setId(rs.getInt("Id"));
                user.setName(rs.getString("Name"));
                user.setLastName(rs.getString("LastName"));
                user.setUser(rs.getString("User"));
                user.setPassword(rs.getString("Password"));
                lista.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public ArrayList<String> getAllUsuariosConectados(){
        ArrayList<String> lista = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        
        try {
            ps = getConnection().prepareStatement("SELECT * FROM users WHERE Estado = 'A'");
            rs = ps.executeQuery();
            
            while(rs.next()){
                String usuario = rs.getString("User");
                lista.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return lista;
    }
    
    public void add(User user){
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(
                "INSERT INTO users values (null, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, user.getName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUser());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getEstado());
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
    
    public int buscarId(String username) {
        ResultSet rs;
        PreparedStatement ps;
        int Id = 0;
        
        try {
            // Preparamos la consulta SQL
            ps = getConnection().prepareStatement("SELECT * FROM users WHERE User = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while(rs.next()){
                Id = rs.getInt("Id");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        
        return Id;
    }
    
    public String buscarUserName(int Id) {
        ResultSet rs;
        PreparedStatement ps;
        String user = "";
        
        try {
            // Preparamos la consulta SQL
            ps = getConnection().prepareStatement("SELECT * FROM users WHERE Id=?");
            ps.setInt(1, Id);
            rs = ps.executeQuery();
            while(rs.next()){
               user  = rs.getString("User");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        
        return user;
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
