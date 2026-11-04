package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraTarifa implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Promocion> promociones = new ArrayList<>();

    public CalculadoraTarifa() {
        promociones.add(new PromocionAnticipada());
        promociones.add(new PromocionGrupo());
    }

    public void agregarPromocion(Promocion promocion) {
        if (promocion != null) promociones.add(promocion);
    }

    public double calcularMejorDescuento(int cantidadPasajeros, long diasAnticipacion) {
        double mejor = 0.0;
        for (Promocion p : promociones) {
            mejor = Math.max(mejor, p.calcularDescuento(cantidadPasajeros, diasAnticipacion));
        }
        return mejor;
    }

    public double calcularTotal(List<Pasajero> pasajeros, Tour tour, LocalDate fechaSalida, LocalDate fechaReserva)
            throws ReglaNegocioException {
        double tarifaBase = tour.obtenerTarifaVigente(fechaSalida).getValor();
        double totalBase = 0.0;
        double totalAdicionales = 0.0;
        for (Pasajero p : pasajeros) {
            totalBase += tarifaBase * p.getTipo().factorTarifa(p.calcularEdad(fechaSalida));
            totalAdicionales += p.getTotalServicios();
        }
        long diasAnticipacion = ChronoUnit.DAYS.between(fechaReserva, fechaSalida);
        double descuento = calcularMejorDescuento(pasajeros.size(), diasAnticipacion);
        return Dinero.redondear(totalBase * (1.0 - descuento) + totalAdicionales);
    }
}
