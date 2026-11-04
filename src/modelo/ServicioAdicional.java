package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;

public class ServicioAdicional implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String descripcion;
    private final double precio;
    private final TipoServicio tipo;

    public ServicioAdicional(int id, String descripcion, double precio, TipoServicio tipo) throws ReglaNegocioException {
        this.id = id;
        this.descripcion = Validaciones.texto(descripcion, "descripción");
        Validaciones.noNegativo(precio, "precio");
        this.precio = precio;
        if (tipo == null) throw new ReglaNegocioException("El tipo de servicio es obligatorio.");
        this.tipo = tipo;
    }

    public boolean isSeguroViaje() { return tipo == TipoServicio.SEGURO_VIAJE; }

    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public TipoServicio getTipo() { return tipo; }

    @Override
    public String toString() { return descripcion + " (" + Dinero.formatear(precio) + ")"; }
}
