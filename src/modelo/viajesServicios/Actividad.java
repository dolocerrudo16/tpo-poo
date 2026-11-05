package modelo.viajesServicios;

import excepciones.ReglaNegocioException;
import modelo.Validaciones;
import modelo.personasRoles.Proveedor;

public class Actividad {

    private int dia;
    private int orden;
    private String horario;
    private String descripcion;
    private String lugar;

    // Relación 0..1 con Proveedor (el diagrama marca que Actividad "contrata" Proveedor)
    private Proveedor proveedor;

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

    // Getters indispensables
    public int getDia() { return dia; }
    public int getOrden() { return orden; }
    public String getHorario() { return horario; }
    public String getDescripcion() { return descripcion; }
    public String getLugar() { return lugar; }
    public Proveedor getProveedor() { return proveedor; }
}