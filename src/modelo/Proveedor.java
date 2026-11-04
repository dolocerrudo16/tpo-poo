package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;

/** Proveedor externo (hotel, restaurante, transportista). El CUIT (11 dígitos) lo identifica. */
public class Proveedor implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String cuit;
    private final String contacto;
    private final String servicioPrestado;

    public Proveedor(String nombre, String cuit, String contacto, String servicioPrestado) throws ReglaNegocioException {
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.cuit = normalizarCuit(cuit);
        this.contacto = Validaciones.texto(contacto, "contacto");
        this.servicioPrestado = Validaciones.texto(servicioPrestado, "servicio prestado");
    }

    /** Deja solo los dígitos del CUIT y verifica que sean 11. */
    public static String normalizarCuit(String cuit) throws ReglaNegocioException {
        String limpio = Validaciones.texto(cuit, "CUIT").replaceAll("[^0-9]", "");
        if (limpio.length() != 11) {
            throw new ReglaNegocioException("El CUIT debe tener 11 dígitos.");
        }
        return limpio;
    }

    public String getNombre() { return nombre; }
    public String getCuit() { return cuit; }
    public String getContacto() { return contacto; }
    public String getServicioPrestado() { return servicioPrestado; }

    @Override
    public String toString() { return nombre + " (CUIT " + cuit + ", " + servicioPrestado + ")"; }
}
