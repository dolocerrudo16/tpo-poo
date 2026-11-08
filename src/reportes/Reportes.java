package reportes;

import java.time.LocalDate;
import java.util.List;

import modelo.estructura.Agencia;
import modelo.personasRoles.Vendedor;
import modelo.clasesAbstractas.Tour;
import modelo.reservas.Reserva;
import modelo.reservas.NotaDeAjuste;
import modelo.reservas.Dinero;

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
            if (r.getEstado() == modelo.enums.EstadoReserva.CONFIRMADA || r.getEstado() == modelo.enums.EstadoReserva.FINALIZADA) {
                if (!r.getFechaCreacion().isBefore(desde) && !r.getFechaCreacion().isAfter(hasta)) {
                    if (tour == null || r.getSalida().getTour().equals(tour)) {
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

    public void ingresosPorTour(LocalDate desde, LocalDate hasta, Tour tour) {
        System.out.println("\nReporte de Ingresos por Tour");
        double totalIngresos = 0.0;

        for (Reserva r : agencia.getTodasLasReservas()) {
            if (r.getEstado() != modelo.enums.EstadoReserva.CANCELADA && r.getEstado() != modelo.enums.EstadoReserva.ANULADA) {
                if (tour == null || r.getSalida().getTour().equals(tour)) {
                    for (modelo.reservas.Pago p : r.getPagos()) {
                        if (!p.getFecha().isBefore(desde) && !p.getFecha().isAfter(hasta)) {
                            totalIngresos += (p.getMonto() + p.getRecargo());
                        }
                    }
                }
            }
        }
        System.out.println("Total de ingresos percibidos en el período: " + Dinero.formatear(totalIngresos));
    }

    // CU11: Corrige el filtro temporal evaluando la fecha de cancelación en las notas de ajuste
    public void cancelaciones(LocalDate desde, LocalDate hasta, Tour tour) {
        System.out.println("\nReporte de Cancelaciones");
        int cantCanceladas = 0;
        double totalDevuelto = 0.0;

        for (Reserva r : agencia.getTodasLasReservas()) {
            if (r.getEstado() == modelo.enums.EstadoReserva.CANCELADA || r.getEstado() == modelo.enums.EstadoReserva.ANULADA) {
                if (tour == null || r.getSalida().getTour().equals(tour)) {
                    for (NotaDeAjuste nota : r.getNotasDeAjuste()) {
                        if (!nota.getFecha().isBefore(desde) && !nota.getFecha().isAfter(hasta)) {
                            cantCanceladas++;
                            totalDevuelto += nota.getMontoDevuelto();
                        }
                    }
                }
            }
        }
        System.out.println("Total de reservas canceladas/anuladas: " + cantCanceladas);
        System.out.println("Total devuelto en reintegros: " + Dinero.formatear(totalDevuelto));
    }
}