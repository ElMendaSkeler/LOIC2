package com.mycompany.udpsender;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase encargada de asignar un obgetivo de ataque.
 * @author ElMendaXD
 */
public class Target implements Serializable {
    public static String IP = "NULL";
    public static ArrayList<Integer> puertosAtaque = new ArrayList<>();
    
    /**
     * Constructor de la clase Target.
     */
    public Target (){
    }
}