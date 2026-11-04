package modelo;

import excepciones.ReglaNegocioException;

/** Validaciones comunes de datos obligatorios, usadas por los constructores del dominio. */
final class Validaciones {
    private Validaciones() { }

    /** Exige un texto no nulo ni vacío y lo devuelve sin espacios sobrantes. */
    static String texto(String valor, String campo) throws ReglaNegocioException {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo '" + campo + "' es obligatorio.");
        }
        return valor.trim();
    }

    static void positivo(double valor, String campo) throws ReglaNegocioException {
        if (valor <= 0) {
            throw new ReglaNegocioException("El campo '" + campo + "' debe ser mayor a cero.");
        }
    }

    static void noNegativo(double valor, String campo) throws ReglaNegocioException {
        if (valor < 0) {
            throw new ReglaNegocioException("El campo '" + campo + "' no puede ser negativo.");
        }
    }
}
