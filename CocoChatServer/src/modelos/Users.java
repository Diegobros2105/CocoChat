package modelos;

import java.io.Serializable;

/**
 *
 * @author rocob
 */
public class Users implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    
    private int Id;
    private String Name;
    private String LastName;
    private String User;
    private String Password;
    private String Estado;
    
    public Users(){
        this.Id = 0;
        this.Name = null;
        this.LastName = null;
        this.User = null;
        this.Password = null;
        this.Estado = "D";
    }
    
    public Users(String User, String Password) {
        this.User = User;
        this.Password = Password;
        this.Estado = "D";
    }

    public Users(String Name, String LastName, String User, String Password) {
        this.Id = 0;
        this.Name = Name;
        this.LastName = LastName;
        this.User = User;
        this.Password = Password;
        this.Estado = "D";
    }
    
    public Users(int Id, String Name, String LastName, String User, String Password) {
        this.Id = Id;
        this.Name = Name;
        this.LastName = LastName;
        this.User = User;
        this.Password = Password;
        this.Estado = "D";
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String User) {
        this.User = User;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
    
    public String getEstado() {
        return Estado;
    }

    public void setEstado(String Estado) {
        this.Estado = Estado;
    }

}