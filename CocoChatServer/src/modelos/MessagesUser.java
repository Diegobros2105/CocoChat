/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author 52331
 */
public class MessagesUser {
    
    private int Id;
    private String Message;
    private int UserEmisor;
    private int UserReceptor;

    public MessagesUser(String Message, int UserEmisor, int UserReceptor) {
        this.Message = Message;
        this.UserEmisor = UserEmisor;
        this.UserReceptor = UserReceptor;
    }

    public MessagesUser() {
    }

    public int getId() {
        return Id;
    }

    public String getMessage() {
        return Message;
    }

    public int getUserEmisor() {
        return UserEmisor;
    }

    public int getUserReceptor() {
        return UserReceptor;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public void setUserEmisor(int UserEmisor) {
        this.UserEmisor = UserEmisor;
    }

    public void setUserReceptor(int UserReceptor) {
        this.UserReceptor = UserReceptor;
    }
    
    
   
}
