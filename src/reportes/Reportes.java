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
import modelo.*;
import java.time.LocalDate;

public class Reportes {

    private Agencia agencia;

    public Reportes(Agencia agencia) {
        this.agencia = agencia;
    }

    public void ocupacionPorSalida(LocalDate desde, LocalDate hasta, Tour tour) {
    }

    public void ventasPorVendedor(LocalDate desde, LocalDate hasta, Tour tour, Vendedor vendedor) {
    }

    public void ingresosPorTour(LocalDate desde, LocalDate hasta, Tour tour) {
    }

    public void cancelaciones(LocalDate desde, LocalDate hasta, Tour tour) {
    }
}
