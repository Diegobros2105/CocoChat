/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author 52331
 */
public class Groups {
    
    private int Id;
    private String Name;
    private String Description;
    private int UserCreator;

    public Groups(String Name, String Description, int UserCreator) {
        this.Name = Name;
        this.Description = Description;
        this.UserCreator = UserCreator;
    }

    public Groups() {
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public int getUserCreator() {
        return UserCreator;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void setUserCreator(int UserCreator) {
        this.UserCreator = UserCreator;
    }
    
}
