package reportes;

import java.time.LocalDate;
import java.util.List;

import modelo.estructura.Agencia;
import modelo.personasRoles.Vendedor;

/**
 * CU11 - Reportes de gestión. Todos se calculan a partir de las reservas, pagos y notas de ajuste registrados
 * (nada se acumula en contadores), de modo que respetan los filtros de período, tour y vendedor.
 *
 * Supuestos: la ocupación cuenta los lugares de reservas que ocupan lugar sobre el cupo máximo; las ventas
 * consideran reservas CONFIRMADA/FINALIZADA creadas en el período; los ingresos suman lo cobrado en pagos del
 * período (incluye el recargo por cuotas); las cancelaciones incluyen reservas CANCELADA y ANULADA.
 */
import modelo.clasesAbstractas.Tour;
import modelo.reservas.Reserva;

public class Reportes {

    private Agencia agencia;

    public Reportes(Agencia agencia) {
        this.agencia = agencia;
    }

    public void ocupacionPorSalida(LocalDate desde, LocalDate hasta, Tour tour) {
        System.out.println("\nReporte de Ocupación por Salida");
        for (modelo.viajesServicios.Salida s : agencia.getSalidas()) {
            if (!s.getFechaInicio().isBefore(desde) && !s.getFechaInicio().isAfter(hasta)) {
                if (tour == null || s.getTour().equals(tour)) {
                    int lugaresOcupados = s.getCupoMaximo() - s.calcularCupoDisponible();
                    System.out.println("Salida #" + s.getIdSalida() + " | Tour: " + s.getTour().getNombre() +
                            " | Ocupación: " + lugaresOcupados + "/" + s.getCupoMaximo() + " lugares");
                }
            }
        }
    }

    public void ventasPorVendedor(LocalDate desde, LocalDate hasta, Tour tour, Vendedor vendedor) {
        System.out.println("--- Reporte de Ventas por Vendedor ---");
        System.out.println("Vendedor: " + vendedor.getNombre());

        int cantidadReservas = 0;
        double totalVendido = 0.0;

        List<Reserva> reservas = agencia.getTodasLasReservas();

        for (Reserva r : reservas) {
            // Filtrar por estado CONFIRMADA o FINALIZADA
            if (r.getEstado() == modelo.enums.EstadoReserva.CONFIRMADA || r.getEstado() == modelo.enums.EstadoReserva.FINALIZADA) {
                // Filtrar por fechas
                if (!r.getFechaCreacion().isBefore(desde) && !r.getFechaCreacion().isAfter(hasta)) {
                    // Filtrar por vendedor y tour
                    // (Si el tour pasado por parámetro es null, ignora el filtro de tour)
                    if (r.getSalida().getTour().equals(tour) || tour == null) {
                        cantidadReservas++;
                        totalVendido += r.getTotal();
                    }
                }
            }
        }

        double comisionEstimada = vendedor.calcularRemuneracionVariable(totalVendido);

        System.out.println("Reservas confirmadas: " + cantidadReservas);
        System.out.println("Total vendido: $" + totalVendido);
        System.out.println("Comisión estimada (3%): $" + comisionEstimada);
    }

    // Calcula sumando los pagos efectuados de reservas NO canceladas
    public void ingresosPorTour(LocalDate desde, LocalDate hasta, Tour tour) {
        System.out.println("\nReporte de Ingresos por Tour");
        double totalIngresos = 0.0;

        for (Reserva r : agencia.getTodasLasReservas()) {
            if (r.getEstado() != modelo.enums.EstadoReserva.CANCELADA && r.getEstado() != modelo.enums.EstadoReserva.ANULADA) {
                if (tour == null || r.getSalida().getTour().equals(tour)) {
                    for (modelo.reservas.Pago p : r.getPagos()) {
                        // Filtramos por la fecha en que ingresó el pago
                        if (!p.getFecha().isBefore(desde) && !p.getFecha().isAfter(hasta)) {
                            totalIngresos += (p.getMonto() + p.getRecargo());
                        }
                    }
                }
            }
        }
        System.out.println("Total de ingresos percibidos en el período: " + modelo.reservas.Dinero.formatear(totalIngresos));
    }

    public void cancelaciones(LocalDate desde, LocalDate hasta, Tour tour) {
        System.out.println("\nReporte de Cancelaciones");
        int cantCanceladas = 0;

        for (Reserva r : agencia.getTodasLasReservas()) {
            if (r.getEstado() == modelo.enums.EstadoReserva.CANCELADA || r.getEstado() == modelo.enums.EstadoReserva.ANULADA) {
                // Filtramos por la fecha en que se creó la reserva (para saber de ese período cuántas se cayeron)
                if (!r.getFechaCreacion().isBefore(desde) && !r.getFechaCreacion().isAfter(hasta)) {
                    if (tour == null || r.getSalida().getTour().equals(tour)) {
                        cantCanceladas++;
                    }
                }
            }
        }
        System.out.println("Total de reservas canceladas/anuladas: " + cantCanceladas);
    }
}