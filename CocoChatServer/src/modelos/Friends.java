/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author 52331
 */
public class Friends {
    
    private int Id;
    private int UserReceptor;
    private int UserEmisor;
    private String Estado;

    public Friends(int UserReceptor, int UserEmisor, String Estado) {
        this.UserReceptor = UserReceptor;
        this.UserEmisor = UserEmisor;
        this.Estado = Estado;
    }

    public Friends() {
    }

    public int getId() {
        return Id;
    }

    public int getUserReceptor() {
        return UserReceptor;
    }

    public int getUserEmisor() {
        return UserEmisor;
    }

    public String getEstado() {
        return Estado;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setUserReceptor(int UserReceptor) {
        this.UserReceptor = UserReceptor;
    }

    public void setUserEmisor(int UserEmisor) {
        this.UserEmisor = UserEmisor;
    }

    public void setEstado(String Estado) {
        this.Estado = Estado;
    }
    
}
