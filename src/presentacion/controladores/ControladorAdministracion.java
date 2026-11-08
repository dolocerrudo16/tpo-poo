package presentacion.controladores;

import java.util.List;
import java.time.LocalDate;
import modelo.estructura.Agencia;
import modelo.personasRoles.Guia;
import modelo.personasRoles.Proveedor;
import modelo.reservas.LiquidacionGuia;
import modelo.viajesServicios.Salida;
import modelo.reservas.Reserva;
import modelo.personasRoles.Pasajero;
import modelo.viajesServicios.ServicioAdicional;
import modelo.clasesAbstractas.Tour;
import reportes.Reportes;
import excepciones.ReglaNegocioException;

public class ControladorAdministracion {

    private Agencia agencia;

    public ControladorAdministracion(Agencia agencia) {
        this.agencia = agencia;
    }

    public LiquidacionGuia liquidar(Guia guia, List<Salida> salidas) {
        double totalAdicionales = 0.0;
        int diasTotales = 0;

        for (Salida s : salidas) {
            // Se liquida solo si la salida está finalizada y este es su guía
            if (s.getEstado() == modelo.enums.EstadoSalida.FINALIZADA &&
                    s.getGuiaAsignado() != null && s.getGuiaAsignado().equals(guia)) {

                diasTotales += s.getTour().getDuracionDias();

                // Sumamos lo recaudado en opcionales para calcular su 5%
                for (Reserva r : s.getReservas()) {
                    if (r.getEstado() == modelo.enums.EstadoReserva.CONFIRMADA || r.getEstado() == modelo.enums.EstadoReserva.FINALIZADA) {
                        for (Pasajero p : r.getPasajeros()) {
                            for (ServicioAdicional serv : p.getServicios()) {
                                totalAdicionales += serv.getPrecio();
                            }
                        }
                    }
                }
            }
        }

        double aPagar = guia.calcularHonorario(diasTotales, totalAdicionales);
        LiquidacionGuia liq = new LiquidacionGuia(agencia.getLiquidaciones().size() + 1, LocalDate.now(), aPagar);
        agencia.agregarLiquidacion(liq);
        return liq;
    }

    public Reportes getReportes() {
        return new Reportes(agencia);
    }
}