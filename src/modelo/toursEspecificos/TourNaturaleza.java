package modelo.toursEspecificos;

import excepciones.ReglaNegocioException;
import modelo.clasesAbstractas.Tour;
import modelo.enums.CategoriaTour;
import modelo.personasRoles.Pasajero;

import java.time.LocalDate;

public class TourNaturaleza extends Tour {

    public TourNaturaleza(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo,
                          int cupoMaximo) throws ReglaNegocioException {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public boolean validarPasajero(Pasajero p, LocalDate fechaSalida) {
        return p != null;
    }

    @Override
    public CategoriaTour getCategoria() {
        return CategoriaTour.NATURALEZA;
    }

    @Override
    public boolean requiereSeguro() {
        return false;
    }
}