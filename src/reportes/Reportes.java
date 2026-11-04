package reportes;

import excepciones.ReglaNegocioException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.Agencia;
import modelo.EstadoReserva;
import modelo.EstadoSalida;
import modelo.Dinero;
import modelo.NotaDeAjuste;
import modelo.Pago;
import modelo.Reserva;
import modelo.Salida;
import modelo.Vendedor;

/**
 * CU11 - Reportes de gestión. Todos se calculan a partir de las reservas, pagos y notas de ajuste registrados
 * (nada se acumula en contadores), de modo que respetan los filtros de período, tour y vendedor.
 *
 * Supuestos: la ocupación cuenta los lugares de reservas que ocupan lugar sobre el cupo máximo; las ventas
 * consideran reservas CONFIRMADA/FINALIZADA creadas en el período; los ingresos suman lo cobrado en pagos del
 * período (incluye el recargo por cuotas); las cancelaciones incluyen reservas CANCELADA y ANULADA.
 */
public class Reportes {

    public record FilaOcupacion(int idSalida, String tour, LocalDate fechaInicio, EstadoSalida estado,
                                int lugaresReservados, int cupoMaximo, double porcentajeOcupacion) { }

    public record FilaVentas(String vendedor, int cantidadReservas, double montoVendido, double comisionEstimada) { }

    public record FilaIngresos(String tour, int cantidadPagos, double ingresos) { }

    public record FilaCancelacion(int numeroReserva, String tour, EstadoReserva estado, LocalDate fecha,
                                  double importeDevuelto, double importeRetenido, String motivo) { }

    private final Agencia agencia;

    public Reportes(Agencia agencia) { this.agencia = agencia; }

    private static void validarPeriodo(LocalDate desde, LocalDate hasta) throws ReglaNegocioException {
        if (desde == null || hasta == null) throw new ReglaNegocioException("Debe indicar el período (desde y hasta).");
        if (hasta.isBefore(desde)) throw new ReglaNegocioException("Rango de fechas inválido: la fecha de fin es anterior a la de inicio.");
    }

    private static boolean enPeriodo(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    private static boolean coincideTour(Salida s, String codigoTour) {
        return codigoTour == null || codigoTour.isBlank() || s.getTour().getCodigo().equalsIgnoreCase(codigoTour.trim());
    }

    /** Ocupación de las salidas que inician en el período. */
    public List<FilaOcupacion> ocupacionPorSalida(LocalDate desde, LocalDate hasta, String codigoTour)
            throws ReglaNegocioException {
        validarPeriodo(desde, hasta);
        List<FilaOcupacion> filas = new ArrayList<>();
        for (Salida s : agencia.getSalidas()) {
            if (!enPeriodo(s.getFechaInicio(), desde, hasta) || !coincideTour(s, codigoTour)) continue;
            int reservados = s.getCupoMaximo() - s.calcularCupoDisponible();
            if (s.getEstado() == EstadoSalida.CANCELADA) reservados = 0;
            double pct = Dinero.redondear(100.0 * reservados / s.getCupoMaximo());
            filas.add(new FilaOcupacion(s.getIdSalida(), s.getTour().getNombre(), s.getFechaInicio(), s.getEstado(),
                    reservados, s.getCupoMaximo(), pct));
        }
        filas.sort(Comparator.comparing(FilaOcupacion::fechaInicio));
        return filas;
    }

    /** Ventas confirmadas por vendedor, con su comisión estimada. */
    public List<FilaVentas> ventasPorVendedor(LocalDate desde, LocalDate hasta, String codigoTour, Vendedor soloVendedor)
            throws ReglaNegocioException {
        validarPeriodo(desde, hasta);
        Map<Integer, double[]> acumulado = new LinkedHashMap<>();   // legajo -> {cantidad, monto}
        Map<Integer, Vendedor> vendedores = new LinkedHashMap<>();
        for (Reserva r : agencia.getReservas()) {
            if (r.getEstado() != EstadoReserva.CONFIRMADA && r.getEstado() != EstadoReserva.FINALIZADA) continue;
            if (!enPeriodo(r.getFechaCreacion(), desde, hasta) || !coincideTour(r.getSalida(), codigoTour)) continue;
            if (soloVendedor != null && r.getVendedor().getLegajo() != soloVendedor.getLegajo()) continue;
            vendedores.putIfAbsent(r.getVendedor().getLegajo(), r.getVendedor());
            double[] acc = acumulado.computeIfAbsent(r.getVendedor().getLegajo(), k -> new double[2]);
            acc[0] += 1;
            acc[1] += r.getTotal();
        }
        List<FilaVentas> filas = new ArrayList<>();
        for (Map.Entry<Integer, double[]> e : acumulado.entrySet()) {
            Vendedor v = vendedores.get(e.getKey());
            double monto = Dinero.redondear(e.getValue()[1]);
            filas.add(new FilaVentas(v.getNombre(), (int) e.getValue()[0], monto, v.calcularRemuneracionVariable(monto)));
        }
        filas.sort(Comparator.comparingDouble(FilaVentas::montoVendido).reversed());
        return filas;
    }

    /** Ingresos por tour según los pagos recibidos en el período. */
    public List<FilaIngresos> ingresosPorTour(LocalDate desde, LocalDate hasta, String codigoTour)
            throws ReglaNegocioException {
        validarPeriodo(desde, hasta);
        Map<String, double[]> porTour = new LinkedHashMap<>();
        for (Reserva r : agencia.getReservas()) {
            if (!coincideTour(r.getSalida(), codigoTour)) continue;
            for (Pago p : r.getPagos()) {
                if (!enPeriodo(p.getFecha(), desde, hasta)) continue;
                double[] acc = porTour.computeIfAbsent(r.getSalida().getTour().getNombre(), k -> new double[2]);
                acc[0] += 1;
                acc[1] += p.getMontoCobrado();
            }
        }
        List<FilaIngresos> filas = new ArrayList<>();
        for (Map.Entry<String, double[]> e : porTour.entrySet()) {
            filas.add(new FilaIngresos(e.getKey(), (int) e.getValue()[0], Dinero.redondear(e.getValue()[1])));
        }
        filas.sort(Comparator.comparingDouble(FilaIngresos::ingresos).reversed());
        return filas;
    }

    /** Reservas canceladas o anuladas en el período, con los importes devueltos y retenidos. */
    public List<FilaCancelacion> cancelaciones(LocalDate desde, LocalDate hasta, String codigoTour)
            throws ReglaNegocioException {
        validarPeriodo(desde, hasta);
        List<FilaCancelacion> filas = new ArrayList<>();
        for (Reserva r : agencia.getReservas()) {
            if (r.getEstado() != EstadoReserva.CANCELADA && r.getEstado() != EstadoReserva.ANULADA) continue;
            if (!coincideTour(r.getSalida(), codigoTour)) continue;
            List<NotaDeAjuste> notas = r.getNotasDeAjuste();
            NotaDeAjuste cierre = notas.get(notas.size() - 1);   // la nota del cierre es siempre la última
            if (!enPeriodo(cierre.getFecha(), desde, hasta)) continue;
            filas.add(new FilaCancelacion(r.getNumero(), r.getSalida().getTour().getNombre(), r.getEstado(),
                    cierre.getFecha(), cierre.getMontoDevuelto(), cierre.getMontoRetenido(), cierre.getMotivo()));
        }
        filas.sort(Comparator.comparing(FilaCancelacion::fecha));
        return filas;
    }
}
