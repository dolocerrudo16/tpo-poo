package modelo.reservas;

import excepciones.ReglaNegocioException;
import modelo.politicasPromos.PromocionAnticipada;
import modelo.politicasPromos.PromocionGrupo;
import modelo.clasesAbstractas.Tour;
import modelo.interfaces.Promocion;
import modelo.personasRoles.Pasajero;
import modelo.viajesServicios.ServicioAdicional;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraTarifa implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Promocion> promociones = new ArrayList<>();

    public CalculadoraTarifa() {
        promociones.add(new PromocionAnticipada());
        promociones.add(new PromocionGrupo());
    }

    public double calcularTotal(List<Pasajero> pasajeros, Tour tour, LocalDate fechaSalida, LocalDate fechaReserva) throws ReglaNegocioException {
        double tarifaBase = tour.obtenerTarifaVigente(fechaSalida).getValor();
        double subtotalPasajeros = 0.0;
        double totalAdicionales = 0.0;

        for (Pasajero p : pasajeros) {
            int edad = p.calcularEdad(fechaSalida);
            double multiplicador = 1.0;

            // RN07: menores de 3 no pagan (hasta los 2 años, 11 meses y 29 días)
            if (edad < 3) {
                multiplicador = 0.0;
            }
            // RN07: menores de 12 pagan el 60% (desde los 3 hasta los 11 años, 11 meses y 29 días)
            else if (edad < 12 && p.getTipo() == modelo.enums.TipoPasajero.MENOR) {
                multiplicador = 0.60;
            }
            // RN07: jubilados tienen un 10% de descuento (pagan el 90%)
            else if (p.getTipo() == modelo.enums.TipoPasajero.JUBILADO) {
                multiplicador = 0.90;
            }
            // RN07: adultos pagan el 100%
            else {
                multiplicador = 1.0;
            }
            subtotalPasajeros += tarifaBase * multiplicador;

            for (ServicioAdicional s : p.getServicios()) {
                totalAdicionales += s.getPrecio();
            }
        }

        long diasAnticipacion = ChronoUnit.DAYS.between(fechaReserva, fechaSalida);
        double mejorDescuento = 0.0;

        for (Promocion promo : promociones) {
            double descActual = promo.calcularDescuento(pasajeros.size(), diasAnticipacion);
            if (descActual > mejorDescuento) {
                mejorDescuento = descActual;
            }
        }

        double subtotalConDescuento = subtotalPasajeros * (1.0 - mejorDescuento);
        return Dinero.redondear(subtotalConDescuento + totalAdicionales);
    }
}