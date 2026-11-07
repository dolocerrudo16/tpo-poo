package modelo.viajesServicios;

import excepciones.ReglaNegocioException;
import modelo.Validaciones;
import modelo.enums.TipoServicio;
import java.io.Serializable;

public class ServicioAdicional implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String descripcion;
    private double precio;
    private TipoServicio tipo;

    public ServicioAdicional(int id, String descripcion, double precio, TipoServicio tipo) throws ReglaNegocioException {
        this.id = id;
        this.descripcion = Validaciones.texto(descripcion, "descripción");

        Validaciones.noNegativo(precio, "precio");
        this.precio = precio;

        if (tipo == null) throw new ReglaNegocioException("El tipo de servicio es obligatorio.");
        this.tipo = tipo;
    }

    // Getters indispensables
    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public TipoServicio getTipo() {
        return tipo;
    }
}
