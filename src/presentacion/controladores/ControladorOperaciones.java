//Atiende los pedidos operativos
//GRASP (Indireccion)
package presentacion.controladores;

import modelo.estructura.Agencia;
import modelo.personasRoles.Guia;
import modelo.personasRoles.Pasajero;
import modelo.reservas.Reserva;
import modelo.viajesServicios.Salida;
import modelo.viajesServicios.Vehiculo;
import modelo.clasesAbstractas.Tour;
import excepciones.ReglaNegocioException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ControladorOperaciones {

    private Agencia agencia;

    public ControladorOperaciones(Agencia agencia) {
        this.agencia = agencia;
    }

    public Salida programarSalida(String codigoTour, LocalDate inicio, LocalDate fin, int cupoMaximo, Tour tour, LocalDate hoy) throws ReglaNegocioException {
        return agencia.programarSalida(codigoTour, inicio, fin, cupoMaximo, tour, hoy);
    }

    public void asignarGuia(Salida salida, Guia guia) throws ReglaNegocioException {
        salida.asignarGuia(guia, agencia.getSalidas());
    }

    public void asignarVehiculo(Salida salida, Vehiculo vehiculo) throws ReglaNegocioException {
        salida.asignarVehiculo(vehiculo, agencia.getSalidas());
    }

    public List<Pasajero> emitirListaPasajeros(Salida salida) throws ReglaNegocioException {
        return salida.emitirListaPasajeros();
    }

    public void iniciarSalida(Salida salida, LocalDate hoy) throws ReglaNegocioException {
        salida.iniciar(hoy);
    }

    public void cerrarSalida(Salida salida, LocalDate hoy) throws ReglaNegocioException {
        salida.finalizar(hoy);
    }

    public List<String> procesarVencimientos(LocalDate hoy) throws ReglaNegocioException {
        List<String> canceladas = new ArrayList<>();

        // Anulación de reservas a 10 días de la salida
        for (Reserva r : agencia.getTodasLasReservas()) {
            if (r.anularSiVencido(hoy)) {
                canceladas.add("Reserva #" + r.getNumero() + " anulada por falta de pago del saldo a 10 días del viaje.");
            }
        }

        // Cancelación de salidas que a 7 días no alcanzaron cupo mínimo
        for (Salida s : agencia.getSalidas()) {
            if (s.cancelarSiNoAlcanzaCupoMinimo(hoy)) {
                canceladas.add("Salida " + s.getIdSalida() + " cancelada por falta de cupo mínimo (a 7 días del inicio).");
            }
        }
        return canceladas;
    }
}