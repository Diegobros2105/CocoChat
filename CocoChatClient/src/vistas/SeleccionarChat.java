/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class SeleccionarChat extends JFrame {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private JComboBox<String> comboBoxUsuarios;
    private JTextArea areaChat;
    private JTextField campoTexto;

    public SeleccionarChat() {
        super("Chat Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());
        campoTexto = new JTextField();
        panelInferior.add(campoTexto, BorderLayout.CENTER);
        JButton botonEnviar = new JButton("Enviar");

        panelInferior.add(botonEnviar, BorderLayout.EAST);

        comboBoxUsuarios = new JComboBox<>();
        panelInferior.add(comboBoxUsuarios, BorderLayout.NORTH);

        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
        conectarAlServidor();
    }

    private void conectarAlServidor() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            String usuario = JOptionPane.showInputDialog(this, "Ingrese su usuario:");
            output.writeObject(usuario);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void actualizarUsuarios(List<String> usuarios) {
        comboBoxUsuarios.removeAllItems();
        for (String usuario : usuarios) {
            comboBoxUsuarios.addItem(usuario);
        }
    }
    
}
