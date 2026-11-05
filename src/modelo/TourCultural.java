package modelo;

import excepciones.ReglaNegocioException;
import modelo.enums.CategoriaTour;

import java.time.LocalDate;

public class TourCultural extends Tour {

    public TourCultural(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo,
                        int cupoMaximo) throws ReglaNegocioException {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public boolean validarPasajero(Pasajero p, LocalDate fechaSalida) {
        return p != null;
    }

    @Override
    public CategoriaTour getCategoria() {
        return CategoriaTour.CULTURAL;
    }

    @Override
    public boolean requiereSeguro() {
        return false;
    }
}
