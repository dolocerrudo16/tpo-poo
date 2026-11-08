//(SRP SOLID) Inicialmente reserva calculaba los precios, aplicaba promos y guardaba pagos, para evitar q tenga muchas razones para cambiar lo trajimos aca.
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
        int pasajerosPagos = 0; // Cuenta únicamente pasajeros que abonan tarifa para la promo de grupo

        for (Pasajero p : pasajeros) {
            int edad = p.calcularEdad(fechaSalida);
            double multiplicador = 1.0;

            // RN07: menores de 3 no pagan (no computan para el umbral de promo grupo)
            if (edad < 3) {
                multiplicador = 0.0;
            }
            // RN07: menores de 12 pagan el 60%
            else if (edad < 12 && p.getTipo() == modelo.enums.TipoPasajero.MENOR) {
                multiplicador = 0.60;
                pasajerosPagos++;
            }
            // RN07: jubilados tienen un 10% de descuento (pagan el 90%)
            else if (p.getTipo() == modelo.enums.TipoPasajero.JUBILADO) {
                multiplicador = 0.90;
                pasajerosPagos++;
            }
            // RN07: adultos pagan el 100%
            else {
                multiplicador = 1.0;
                pasajerosPagos++;
            }
            subtotalPasajeros += tarifaBase * multiplicador;

            for (ServicioAdicional s : p.getServicios()) {
                totalAdicionales += s.getPrecio();
            }
        }

        long diasAnticipacion = ChronoUnit.DAYS.between(fechaReserva, fechaSalida);
        double mejorDescuento = 0.0;

        for (Promocion promo : promociones) {
            double descActual = promo.calcularDescuento(pasajerosPagos, diasAnticipacion);
            if (descActual > mejorDescuento) {
                mejorDescuento = descActual;
            }
        }

        double subtotalConDescuento = subtotalPasajeros * (1.0 - mejorDescuento);
        return Dinero.redondear(subtotalConDescuento + totalAdicionales);
    }
}