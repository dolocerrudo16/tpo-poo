package modelo.personasRoles;

import excepciones.ReglaNegocioException;
import modelo.Validaciones;

//Proveedor externo (hotel, restaurante, transportista). El CUIT lo identifica.
public class Proveedor {

    private String nombre;
    private String cuit;
    private String contacto;
    private String servicioPrestado;

    public Proveedor(String nombre, String cuit, String contacto, String servicioPrestado) throws ReglaNegocioException {
        this.nombre = Validaciones.texto(nombre, "nombre");

        // Validación directa del CUIT en el constructor
        String limpio = Validaciones.texto(cuit, "CUIT").replaceAll("[^0-9]", "");
        if (limpio.length() != 11) {
            throw new ReglaNegocioException("El CUIT debe tener 11 dígitos.");
        }
        this.cuit = limpio;

        this.contacto = Validaciones.texto(contacto, "contacto");
        this.servicioPrestado = Validaciones.texto(servicioPrestado, "servicio prestado");
    }

    @Override
    public String toString() {
        return nombre + " (CUIT " + cuit + ", " + servicioPrestado + ")";
    }
}