/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author 52331
 */
public class Conform {
    
    private int Id;
    private String Estado;
    private int User;
    private int Groups;

    public Conform(int User, int Groups) {
        this.User = User;
        this.Groups = Groups;
    }

    public Conform() {
    }

    public int getId() {
        return Id;
    }

    public String getEstado() {
        return Estado;
    }

    public int getUser() {
        return User;
    }

    public int getGroups() {
        return Groups;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setEstado(String Estado) {
        this.Estado = Estado;
    }

    public void setUser(int User) {
        this.User = User;
    }

    public void setGroups(int Groups) {
        this.Groups = Groups;
    }
    
}
