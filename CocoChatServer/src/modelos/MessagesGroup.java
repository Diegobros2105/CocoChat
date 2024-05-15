        /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author 52331
 */
public class MessagesGroup {
    
    private int Id;
    private String Message;
    private int UserEmisor;
    private int Groups;

    public MessagesGroup(String Message, int UserEmisor, int Groups) {
        this.Message = Message;
        this.UserEmisor = UserEmisor;
        this.Groups = Groups;
    }

    

    public MessagesGroup() {
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

    public int getGroups() {
        return Groups;
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

    public void setGroups(int Groups) {
        this.Groups = Groups;
    }
    
}
