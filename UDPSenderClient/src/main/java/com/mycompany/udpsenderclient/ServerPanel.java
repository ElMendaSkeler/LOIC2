package com.mycompany.udpsenderclient;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase encargada del manejo de los servidores.
 * @author ElMendaXD
 */
public class ServerPanel implements Serializable {
    public String ip;
    public String nombre;
    public int msgPort;
    public int hbPort;
    public transient String state;
    private transient Socket msgSocket;
    private transient PrintWriter msgOut;
    private transient BufferedReader msgIn;
    private transient Socket hbSocket;
    private transient PrintWriter hbOut;
    private transient BufferedReader hbIn;
    public Target target;
    
    private transient JPanel panel;
    private transient JLabel label;
    private JButton startButton;;
    private JButton stopButton;
    private JButton editObjectiveButton;
    private JButton finalizeButton;
    private JButton editButton;
    private JButton connectButton;
    private JButton dcButton;
    private JButton deleteButton;

    /**
     * Metodo constructor de un servidor.
     * @param nombre Nombre del servidor.
     * @param ip Ip del servidor.
     * @param msgPort Puerto de mensages.
     * @param hbPort Puerto de heartbeat.
     * @throws IOException
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ServerPanel(String nombre, String ip, int msgPort, int hbPort) throws IOException {
        this.nombre = nombre;
        this.ip = ip;
        this.msgPort = msgPort;
        this.hbPort = hbPort;
        this.state = "Desconectado";
        this.target = new Target();
        
        createPanel();
        updateLabel();
    }
    
    /**
     * Metodo encargado de la conexión TCP con el servidor.
     * @throws IOException
     */
    public void conectar() throws IOException {
        hbSocket = new Socket(ip, hbPort);
        hbOut = new PrintWriter(hbSocket.getOutputStream(), true);
        hbIn = new BufferedReader(new InputStreamReader(hbSocket.getInputStream()));

        new Thread(() -> {
            try {
                String hbresponse;
                while ((hbresponse = hbIn.readLine()) != null) {
                    if ("PING".equals(hbresponse)) {
                        hbOut.println("PONG");
                    }
                }
            } catch (IOException e) {}
        }).start();

        msgSocket = new Socket(ip, msgPort);
        msgOut = new PrintWriter(msgSocket.getOutputStream(), true);
        msgIn = new BufferedReader(new InputStreamReader(msgSocket.getInputStream()));

        state = msgIn.readLine();
        updateLabel();
    }
    
    private void createPanel() {
        panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 4), 
            nombre + "==>" + ip + ":" + msgPort + "/" + hbPort + "======" + target.nombre + "==>"  + target.IP));
        panel.setLayout(new GridLayout(1, 9));
        
        panel.setPreferredSize(new Dimension(1000, 50)); // Establecer el tamaño preferido
        panel.setMaximumSize(new Dimension(1000, 50)); // Establecer el tamaño máximo
        panel.setMinimumSize(new Dimension(1000, 50)); // Establecer el tamaño mínimo
        
        label = new JLabel(state);
        label.setPreferredSize(new Dimension(200, 50)); // Establecer el tamaño preferido
        label.setMaximumSize(new Dimension(200, 50)); // Establecer el tamaño máximo
        label.setMinimumSize(new Dimension(200, 50)); // Establecer el tamaño mínimo
        panel.add(label);

        startButton = new JButton("Iniciar");
        startButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        startButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        startButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        startButton.addActionListener((ActionEvent e) -> {
            try {
                targetToString();
                state = "Iniciando";
                label.setText(state);
                updateButtonState();
                UDPSenderClient.actualizarVista();
                UDPSenderClient.save();
                Thread.sleep(3000);
                updateButtonState();
                UDPSenderClient.actualizarVista();

                updateLabel();
            } catch (IOException ex) {} catch (InterruptedException ex) {
                Logger.getLogger(ServerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        // Cambiar el cursor al pasar sobre el botón
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(startButton);

        stopButton = new JButton("Parar");
        stopButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        stopButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        stopButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        stopButton.addActionListener((ActionEvent e) -> {
            try {
                sendMessage("Pausar");
                updateLabel();
            } catch (IOException ex) {}
        });
        // Cambiar el cursor al pasar sobre el botón
        stopButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                stopButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                stopButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(stopButton);

        editObjectiveButton = new JButton("Objetivo");
        editObjectiveButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        editObjectiveButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        editObjectiveButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        editObjectiveButton.addActionListener((ActionEvent e) -> {
            try {
                editObjective();
                updateLabel();
            } catch (IOException ex) {}
        });
        // Cambiar el cursor al pasar sobre el botón
        editObjectiveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editObjectiveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editObjectiveButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(editObjectiveButton);

        finalizeButton = new JButton("Apagar");
        finalizeButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        finalizeButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        finalizeButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        finalizeButton.addActionListener((ActionEvent e) -> {
            try {
                sendMessage("Finalizar");
                if (msgSocket != null) msgSocket.close();
                if (hbSocket != null) hbSocket.close();
                updateLabel();
                UDPSenderClient.save();
            } catch (IOException ex) {
                Logger.getLogger(ServerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        // Cambiar el cursor al pasar sobre el botón
        finalizeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                finalizeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                finalizeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(finalizeButton);

        editButton = new JButton("Editar");
        editButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        editButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        editButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        editButton.addActionListener((ActionEvent e) -> { 
            try {
                editIpAndPorts();
            } catch (IOException | InterruptedException ex) {}  
        });
        // Cambiar el cursor al pasar sobre el botón
        editButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(editButton);
        
        connectButton = new JButton("Conectar");
        connectButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        connectButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        connectButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        connectButton.addActionListener((ActionEvent e) -> { 
            try {
                conectar();
            } catch (IOException ex) {}
        });
        // Cambiar el cursor al pasar sobre el botón
        connectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                connectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                connectButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(connectButton);
        
        dcButton = new JButton("Desconectar");
        dcButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        dcButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        dcButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        dcButton.addActionListener((ActionEvent e) -> {
            try {
                if (msgSocket != null) msgSocket.close();
                if (hbSocket != null) hbSocket.close();
                updateLabel();
            } catch (IOException ex) {}
        });
        // Cambiar el cursor al pasar sobre el botón
        dcButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dcButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dcButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(dcButton);

        deleteButton = new JButton("Borrar");
        deleteButton.setPreferredSize(new Dimension(100, 50)); // Establecer el tamaño preferido
        deleteButton.setMaximumSize(new Dimension(100, 50)); // Establecer el tamaño máximo
        deleteButton.setMinimumSize(new Dimension(100, 50)); // Establecer el tamaño mínimo
        deleteButton.addActionListener((ActionEvent e) -> {
            try {
                if (msgSocket != null) msgSocket.close();
                if (hbSocket != null) hbSocket.close();
                // Llamar al método removeServerPanel de UDPSenderClient
                UDPSenderClient.removeServerPanel(ServerPanel.this);
            } catch (IOException ex) {}
        });
        // Cambiar el cursor al pasar sobre el botón
        deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.add(deleteButton);
    }
    
    /**
     * Metodo encargado de proporcionar el panel del servidor generado.
     * @return El objeto JPanel.
     */
    public JPanel getPanel() {
        if (panel == null) {
            createPanel();
        }
        return panel;
    }
    
    
    private void editIpAndPorts() throws IOException, InterruptedException {
        JTextField nameField = new JTextField(nombre);
        JTextField ipField = new JTextField(ip);
        JTextField messagePortField = new JTextField(String.valueOf(msgPort));
        JTextField heartbeatPortField = new JTextField(String.valueOf(hbPort));

        JPanel dialogPanel = new JPanel(new GridLayout(4, 2));
        dialogPanel.add(new JLabel("Nombre:"));
        dialogPanel.add(nameField);
        dialogPanel.add(new JLabel("IP:"));
        dialogPanel.add(ipField);
        dialogPanel.add(new JLabel("Puerto Mensajes:"));
        dialogPanel.add(messagePortField);
        dialogPanel.add(new JLabel("Puerto Heartbeat:"));
        dialogPanel.add(heartbeatPortField);

        int result = JOptionPane.showConfirmDialog(panel, dialogPanel, "Editar servidor", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            nombre = nameField.getText();
            ip = ipField.getText();
            msgPort = Integer.parseInt(messagePortField.getText());
            hbPort = Integer.parseInt(heartbeatPortField.getText());
            panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 4), 
            nombre + "==>" + ip + ":" + msgPort + "/" + hbPort + "======" + target.nombre + "==>"  + target.IP));
            UDPSenderClient.actualizarVista();
            UDPSenderClient.save();
        }
    }

    private void editObjective() throws IOException {
        JTextField objectivenameField = new JTextField();
        if (!target.nombre.equals("NULL")) {
            objectivenameField.setText(target.nombre);
        }
        
        JTextField objectiveIpField = new JTextField();
        if (!target.IP.equals("NULL")) {
            objectiveIpField.setText(target.IP);
        }
        
        JTextField objectivePortsField = new JTextField();
        if (!target.puertosAtaque.isEmpty()) {
            for (Integer port : target.puertosAtaque) {
                objectivePortsField.setText(objectivePortsField.getText() + port + ",");
            }
        }

        JPanel dialogPanel = new JPanel(new GridLayout(3, 2));
        dialogPanel.add(new JLabel("Nombre Objetivo:"));
        dialogPanel.add(objectivenameField);
        dialogPanel.add(new JLabel("IP Objetivo:"));
        dialogPanel.add(objectiveIpField);
        dialogPanel.add(new JLabel("Puertos Objetivo (separados por comas):"));
        dialogPanel.add(objectivePortsField);

        int result = JOptionPane.showConfirmDialog(panel, dialogPanel, "Editar Objetivo", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            target.puertosAtaque.clear();
            target.nombre = objectivenameField.getText();
            target.IP = objectiveIpField.getText();
            String[] portsArray = objectivePortsField.getText().split(",");
            for (String ports : portsArray) {
                target.puertosAtaque.add(Integer.valueOf(ports));
            }
            panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 4), 
            nombre + "==>" + ip + ":" + msgPort + "/" + hbPort + "======" + target.nombre + "==>"  + target.IP));
            UDPSenderClient.actualizarVista();
            UDPSenderClient.save();
        }
    }

    /**
     * Metodo para enviar un mensage al servidor.
     * @param message Mensage a enviar.
     */
    public void sendMessage(String message) {
        if (msgOut != null) {
            msgOut.println(message);
        }
    }

    /**
     * Metodo encargado de actualizar el estado del servidor.
     * @throws IOException
     */
    public void updateLabel() throws IOException {
        if (hbSocket != null){
            if (hbSocket.isClosed()) {
                state = "Desconectado";
            } else if (!hbSocket.isConnected()) {
                state = "Desconectado";
            } else if (hbSocket.isConnected()) {
                sendMessage("Update");
                state = msgIn.readLine();
            }
        } else if (hbSocket == null){
            state = "Desconectado";
        }
        
        label.setText(state);
        UDPSenderClient.save();
        updateButtonState();
    }
    
    private void updateButtonState() throws IOException {
        startButton.setEnabled(!target.IP.equals("NULL") && "Listo".equals(state) && !"Iniciando".equals(state));
        stopButton.setEnabled("En ejecución".equals(state) && !"Iniciando".equals(state));
        editObjectiveButton.setEnabled("Listo".equals(state) && !"Iniciando".equals(state));
        finalizeButton.setEnabled("Listo".equals(state) && !"Iniciando".equals(state));
        editButton.setEnabled("Desconectado".equals(state) && !"Iniciando".equals(state));
        connectButton.setEnabled("Desconectado".equals(state) && !"Iniciando".equals(state));
        dcButton.setEnabled(!"Desconectado".equals(state) && !"Iniciando".equals(state));
        deleteButton.setEnabled("Desconectado".equals(state) && !"Iniciando".equals(state));
        UDPSenderClient.actualizarVista();
    }
    
    /**
     * Metodo encargado de actualizar la disponibilidad de los botones
     */
    public void targetToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(target.IP).append("|");

        for (int i = 0; i < target.puertosAtaque.size(); i++) {
            sb.append(target.puertosAtaque.get(i));
            if (i < target.puertosAtaque.size() - 1) {
                sb.append(",");
            }
        }
        sendMessage(sb.toString());
    }
}