package com.mycompany.udpsenderclient;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase encargada de asignar un obgetivo de ataque.
 * @author ElMendaXD
 */
public class Target implements Serializable {
    public String IP;
    public String nombre;
    public ArrayList<Integer> puertosAtaque;
    
    /**
     * Constructor de la clase Target.
     */
    public Target (){
        IP = "NULL";
        nombre = "NULL";
        puertosAtaque = new ArrayList<>();
    }
}
