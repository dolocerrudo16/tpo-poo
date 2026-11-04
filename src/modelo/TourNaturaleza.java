package modelo;

import excepciones.ReglaNegocioException;
import java.time.LocalDate;

/** Tour de naturaleza (parques nacionales, avistaje de fauna): sin requisitos especiales de pasajero. */
public class TourNaturaleza extends Tour {
    private static final long serialVersionUID = 1L;

    public TourNaturaleza(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo,
                          int cupoMaximo) throws ReglaNegocioException {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public boolean validarPasajero(Pasajero p, LocalDate fechaSalida) { return p != null; }

    @Override
    public CategoriaTour getCategoria() { return CategoriaTour.NATURALEZA; }
}
