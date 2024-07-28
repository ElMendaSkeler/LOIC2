package com.mycompany.udpsenderclient;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Cursor;

/**
 * Clase principal.
 * @author ElMendaXD
 */
public class UDPSenderClient {
    public JFrame frame;
    public static JPanel panelContainer;
    public static ArrayList<ServerPanel> serverPanels = new ArrayList<>();
    public final String CONFIG_FILE = "configCL.bin";

    /**
     * Metodo encargado del manejo de la interfaz gráfica.
     * @throws IOException
     */
    public void createAndShowGUI() throws IOException {
        frame = new JFrame("UDP Sender Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        panelContainer = new JPanel();
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));
        
        ArrayList<ServerPanel> serverListTemp = MyInput.deserialize("configCL.bin");
        if (serverListTemp != null){
            for (ServerPanel server : serverListTemp){
                ServerPanel newServer = new ServerPanel(server.nombre, server.ip, server.msgPort, server.hbPort);
                newServer.target.nombre = server.target.nombre;
                newServer.target.IP = server.target.IP;
                newServer.target.puertosAtaque = server.target.puertosAtaque;
                serverPanels.add(newServer);
                panelContainer.add(newServer.getPanel());
                panelContainer.revalidate();
                panelContainer.repaint();
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(panelContainer);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Añadir Servidor");
        addButton.addActionListener((ActionEvent e) -> {
            try {
                addServer();
            } catch (IOException ex) {}
        });
        
        // Cambiar el cursor al pasar sobre el botón
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(addButton, BorderLayout.SOUTH);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setSize(1060, 600);
        frame.setVisible(true);
    }
    
    /**
     * Metodo encargado de actualizar la ventana.
     * @throws IOException
     */
    public static void actualizarVista() throws IOException {
        panelContainer.revalidate();
        panelContainer.repaint();
    }

    /**
      Metodo encargado de agregar un servidor, tanto en la base de datos como en la interfaz.
     * @throws IOException
     */
    public void addServer() throws IOException {
        JTextField nameField = new JTextField();
        JTextField ipField = new JTextField();
        JTextField messagePortField = new JTextField();
        JTextField heartbeatPortField = new JTextField();

        JPanel dialogPanel = new JPanel(new GridLayout(4, 2));
        dialogPanel.add(new JLabel("Nombre:"));
        dialogPanel.add(nameField);
        dialogPanel.add(new JLabel("IP:"));
        dialogPanel.add(ipField);
        dialogPanel.add(new JLabel("Puerto Mensajes:"));
        dialogPanel.add(messagePortField);
        dialogPanel.add(new JLabel("Puerto Heartbeat:"));
        dialogPanel.add(heartbeatPortField);

        int result = JOptionPane.showConfirmDialog(frame, dialogPanel, "Introducir IP y Puertos", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String nombre = nameField.getText();
            String ip = ipField.getText();
            int messagePort = Integer.parseInt(messagePortField.getText());
            int heartbeatPort = Integer.parseInt(heartbeatPortField.getText());
            
            ServerPanel serverPanel = new ServerPanel(nombre, ip, messagePort, heartbeatPort);
            serverPanels.add(serverPanel);
            panelContainer.add(serverPanel.getPanel());
            panelContainer.revalidate();
            panelContainer.repaint();
            save();
        }
    }

    /**
     * Metodo encargado de la eliminación de un servidor.
     * @param serverPanel Servidor a eliminar.
     */
    public static void removeServerPanel(ServerPanel serverPanel) {
        panelContainer.remove(serverPanel.getPanel());
        serverPanels.remove(serverPanel);
        panelContainer.revalidate();
        panelContainer.repaint();
        save();
    }
    
    /**
     * Metodo encargado de almacenar los servidores configurados.
     */
    public static void save() {
        MyInput.serialize(serverPanels, "configCL.bin");
    }

    /**
     * Metodo main.
     * @param args
     * @throws IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {}
        SwingUtilities.invokeLater(() -> {
            try {
                new UDPSenderClient().createAndShowGUI();
            } catch (IOException ex) {}
        });
    }
}