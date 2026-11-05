package modelo;

import excepciones.ReglaNegocioException;
import modelo.interfaces.Promocion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraTarifa implements Serializable {
    private static final long serialVersionUID = 1L;

    // Relación 1..* según UML
    private List<Promocion> promociones = new ArrayList<>();

    public CalculadoraTarifa() {
        promociones.add(new PromocionAnticipada());
        promociones.add(new PromocionGrupo());
    }

    public double calcularTotal(List<Pasajero> pasajeros, Tour tour, LocalDate fechaSalida, LocalDate fechaReserva) throws ReglaNegocioException {
        return 0.0;
    }
}