package com.mycompany.udpsender;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Clase con utilidades para la entrada de datos desde teclado y fichero.
 * @author ElMendaXD
 */
public class MyInput {
    
    
    // Lee una cadena de caracteres desde el teclado

    /**
     * Método que permite leer una cadena de caracteres del teclado.
     * @return retorna una cadena de caracteres
     */
    public static String readString() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in),1);
        String  string="";
        try {
                string = br.readLine();
        } catch (IOException ex) {
                System.out.println(ex);
        }
        return string;
    }
    
    
    // Lee un dato tipo int desde el teclado

    /**
     * Método que permite leer un número entero de simple precisión por teclado.
     * @return Retorna un número entero de precisión simple.
     */
    public static int readInt(){
        try {
            return Integer.parseInt(readString());
        } catch (NumberFormatException ex) {
            System.out.print("Formato no valido, introduzca un valor numerico: ");
            return readInt();
        }
    }
    

    // Lee un dato tipo double desde el teclado

    /**
     * Método que permite leer número real por teclado.
     * @return retorna un número de doble precisión.
     */
    public static double readDouble() {
	return Double.parseDouble(readString());
    }
    
    
    // Lee un dato tipo byte desde el teclado

    /**
     * Método que permite leer un número entero por teclado.
     * @return Retorna un número entero comprendido entre -128 y 127.
     */
    public static byte readByte() {
	return Byte.parseByte(readString());
    }
    
    
    // Lee un dato tipo short desde el teclado

    /**
     * Método que permite leer un número entero por teclado.
     * @return Retorna un número entero comprendido entre -32768 y 32767.
     */
    public static short readShort() {
	return Short.parseShort(readString());
    }
    
    
    // Lee un dato tipo long desde el teclado

    /**
     * Método que permite leer un número entero de doble precisión por teclado.
     * @return Retorna un número entero de doble precisión.
     */
    public static long readLong() {
	return Long.parseLong(readString());
    }
    
    
    // Lee un dato tipo date desde el teclado

    /**
     * Método que permite leer una fecha por teclado.
     * @return Retorna una fecha.
     */
    public static Date readDate() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return formatoFecha.parse(readString());
        } catch (ParseException ex) {
            System.out.print("Formato no valido, introduzca una fecha en el formato 'dd/MM/yyyy': ");
            return readDate();
        }
    }
    
    
    //Lee un dato tipo float desde el teclado

    /**
     * Método que permite leer número real por teclado.
     * @return Retorna un número de precisión simple.
     */
    public static float readFloat() {
	return Float.parseFloat(readString());
    }
    
    /**
     * Añade el metodo getYear el cual no esta en el paquete date.
     * @param fecha Variable fecha.
     * @return Retorna el año concreto de una fecha.
     */
    public static int getYear(Date fecha) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fecha);
        return calendario.get(Calendar.YEAR);
    }

    
    //Almacena una clase en un fichero

    /**
     * Método que permite almacenar una clase en un fichero.
     * @param <A> Clase de la variable a serializar.
     * @param a Variable a serializar.
     * @param nombreFichero Nombre del fichero en el que se guardaran los datos.
     */
    public static <A> void serialize(A a, String nombreFichero) {
        try {
            FileOutputStream fos = new FileOutputStream(nombreFichero);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(a);
        } catch (IOException e) {
                System.err.println("Problem: "+e);
        }
    }
    
    
    //Carga una clase con los datos de un fichero

    /**
     * Método que permite cargar una clase con los datos de un fichero.
     * @param <A> Clase de la variable a deserializar.
     * @param nombreFichero Nombre del fichero del que se cargaran los datos.
     * @return retorna los datos de la clase.
     */
    public static <A> A deserialize(String nombreFichero) {
        try {
            FileInputStream fis = new FileInputStream(nombreFichero);
            ObjectInputStream iis = new ObjectInputStream(fis);
            return (A) iis.readObject();
        } catch (IOException | ClassNotFoundException e) {}
        return null ;
    }
}
