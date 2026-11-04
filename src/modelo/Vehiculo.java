package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;

/** Vehículo de la flota. La patente (en mayúsculas) lo identifica. */
public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String patente;
    private final int capacidad;
    private final TipoVehiculo tipo;

    public Vehiculo(String patente, int capacidad, TipoVehiculo tipo) throws ReglaNegocioException {
        this.patente = Validaciones.texto(patente, "patente").toUpperCase();
        if (capacidad <= 0) throw new ReglaNegocioException("La capacidad del vehículo debe ser mayor a cero.");
        this.capacidad = capacidad;
        if (tipo == null) throw new ReglaNegocioException("El tipo de vehículo es obligatorio.");
        this.tipo = tipo;
    }

    public String getPatente() { return patente; }
    public int getCapacidad() { return capacidad; }
    public TipoVehiculo getTipo() { return tipo; }

    @Override
    public String toString() { return tipo + " " + patente + " (" + capacidad + " asientos)"; }
}
