package org.amg.Utils;

public class UtilsPrecios {

    public final static double PRECIO_ELIMINAR_ENCANTAMIENTO = 0.01;

    /*public static double calcularPrecioMejoraEncantamiento(int nivelDiferencia){
        return Math.pow(nivelDiferencia+1,3);
    }*/
    public static int calcularPrecioNivelesMejoraEncantamiento(int nivelDiferencia){
        return (30)+(5*nivelDiferencia);
    }
    public static double calcularPrecioNivelesMejoraEncantamientoNuevo(int nivelDiferencia){
        return Math.pow(2,nivelDiferencia);
    }
}
