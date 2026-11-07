package presentacion.controladores;

import modelo.enums.MedioPago;
import modelo.estructura.Agencia;
import modelo.personasRoles.Cliente;
import modelo.personasRoles.Pasajero;
import modelo.personasRoles.Vendedor;
import modelo.reservas.Pago;
import modelo.reservas.Reserva;
import modelo.viajesServicios.Salida;
import modelo.viajesServicios.ServicioAdicional;
import excepciones.ReglaNegocioException;

import java.time.LocalDate;
import java.util.List;

public class ControladorReserva {

    private Agencia agencia;

    public ControladorReserva(Agencia agencia) {
        this.agencia = agencia;
    }

    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String pais) throws ReglaNegocioException {
        return agencia.registrarCliente(nombre, documento, telefono, email, pais);
    }

    public Reserva crearReserva(Cliente cliente, Salida salida, Vendedor vendedor, List<Pasajero> pasajeros, LocalDate hoy) throws Exception {
        int nroReserva = agencia.getTodasLasReservas().size() + 1;

        // 1. Instanciamos la calculadora ANTES de crear la reserva
        modelo.reservas.CalculadoraTarifa calc = new modelo.reservas.CalculadoraTarifa();

        Reserva nuevaReserva = new Reserva(nroReserva, cliente, salida, vendedor,
                new modelo.politicasPromos.CancelacionFlexible(), calc, hoy);

        boolean requiereSeguro = salida.getTour().requiereSeguro();
        ServicioAdicional seguroObligatorio = null;

        if (requiereSeguro) {
            seguroObligatorio = new ServicioAdicional(99, "Seguro de Viaje Obligatorio", 5000.0, modelo.enums.TipoServicio.SEGURO_VIAJE);
        }

        for (Pasajero p : pasajeros) {
            if (requiereSeguro) {
                p.agregarServicio(seguroObligatorio);
            }
            nuevaReserva.agregarPasajero(p);
        }

        // Vincular la reserva a la salida (esto descuenta el cupo automáticamente en Salida)
        salida.agregarReserva(nuevaReserva, hoy);

        // 2. SOLUCIÓN: Calculamos el total real con la calculadora y se lo seteamos a la reserva
        double total = calc.calcularTotal(nuevaReserva.getPasajeros(), salida.getTour(), salida.getFechaInicio(), hoy);
        nuevaReserva.setTotal(total);

        return nuevaReserva;
    }

    public Pago registrarPago(Reserva reserva, double monto, MedioPago medio, int cuotas, LocalDate hoy) throws ReglaNegocioException {
        return reserva.registrarPago(monto, medio, cuotas, hoy);
    }

    public double reemplazarPasajero(Reserva reserva, Pasajero viejo, Pasajero nuevo, LocalDate hoy) throws ReglaNegocioException {
        return reserva.reemplazarPasajero(viejo, nuevo, hoy);
    }

    public double cancelarReserva(Reserva reserva, String motivo, LocalDate hoy) {
        return reserva.cancelar(motivo, hoy);
    }
}