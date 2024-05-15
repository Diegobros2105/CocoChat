package cocochatclient;

import java.io.*;
import java.net.*;
import vistas.Login;

public class CocoChatClient {
    public static void main(String[] args) {
 
        CocoChatClient c = new CocoChatClient();
        Login l = new Login(c);
        l.setVisible(true);
               
    }
}

