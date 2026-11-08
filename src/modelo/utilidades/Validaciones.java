//Revisa que los textos no vengan vacíos y que los números o tarifas no sean negativos.
// SOLID (SRP) centraliza validaciones de datos
package modelo.utilidades;

import excepciones.ReglaNegocioException;

public class Validaciones {

    public static String texto(String valor, String nombreCampo) throws ReglaNegocioException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo " + nombreCampo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    public static void positivo(double valor, String nombreCampo) throws ReglaNegocioException {
        if (valor <= 0) {
            throw new ReglaNegocioException("El campo " + nombreCampo + " debe ser positivo.");
        }
    }

    public static void noNegativo(double valor, String nombreCampo) throws ReglaNegocioException {
        if (valor < 0) {
            throw new ReglaNegocioException("El campo " + nombreCampo + " no puede ser negativo.");
        }
    }
}