package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;

/** Actividad del itinerario de un tour (visita, traslado, comida, alojamiento). El proveedor es opcional. */
public class Actividad implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int dia;
    private final int orden;
    private final String horario;
    private final String descripcion;
    private final String lugar;
    private final Proveedor proveedor;

    public Actividad(int dia, int orden, String horario, String descripcion, String lugar, Proveedor proveedor)
            throws ReglaNegocioException {
        if (dia < 1) throw new ReglaNegocioException("El día de la actividad debe ser 1 o mayor.");
        if (orden < 1) throw new ReglaNegocioException("El orden de la actividad debe ser 1 o mayor.");
        this.dia = dia;
        this.orden = orden;
        this.horario = Validaciones.texto(horario, "horario");
        this.descripcion = Validaciones.texto(descripcion, "descripción");
        this.lugar = Validaciones.texto(lugar, "lugar");
        this.proveedor = proveedor;
    }

    public int getDia() { return dia; }
    public int getOrden() { return orden; }
    public String getHorario() { return horario; }
    public String getDescripcion() { return descripcion; }
    public String getLugar() { return lugar; }
    public Proveedor getProveedor() { return proveedor; }

    @Override
    public String toString() {
        return "Día " + dia + " #" + orden + " " + horario + " - " + descripcion + " (" + lugar + ")"
                + (proveedor != null ? " [" + proveedor.getNombre() + "]" : "");
    }
}
