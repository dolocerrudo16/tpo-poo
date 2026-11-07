package modelo.clasesAbstractas;

import excepciones.ReglaNegocioException;
import modelo.Validaciones;
import java.io.Serializable;

public abstract class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int legajo;
    protected String nombre;
    protected String documento;
    protected String telefono;

    protected Empleado(int legajo, String nombre, String documento, String telefono) throws ReglaNegocioException {
        if (legajo <= 0) {
            throw new ReglaNegocioException("El legajo debe ser mayor a cero.");
        }
        this.legajo = legajo;
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.documento = Validaciones.texto(documento, "documento");
        this.telefono = telefono == null ? "" : telefono.trim();
    }


    public int getLegajo() { return legajo; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }

    @Override
    public String toString() {
        return "Legajo " + legajo + ": " + nombre + " (DNI " + documento + ")";
    }
}