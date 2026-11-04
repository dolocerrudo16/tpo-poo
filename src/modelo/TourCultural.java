package modelo;

import excepciones.ReglaNegocioException;
import java.time.LocalDate;

/** Tour cultural (circuitos históricos, bodegas, museos): sin requisitos especiales de pasajero. */
public class TourCultural extends Tour {
    private static final long serialVersionUID = 1L;

    public TourCultural(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo,
                        int cupoMaximo) throws ReglaNegocioException {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public boolean validarPasajero(Pasajero p, LocalDate fechaSalida) { return p != null; }

    @Override
    public CategoriaTour getCategoria() { return CategoriaTour.CULTURAL; }
}
