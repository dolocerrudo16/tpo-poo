package presentacion.controladores;

import modelo.*;
import modelo.enums.MedioPago;

import java.util.List;
import java.util.ArrayList;

public class ControladorReserva {

    private Agencia agencia;

    public ControladorReserva(Agencia agencia) {
        this.agencia = agencia;
    }

    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String pais) {
        return null;
    }

    public Reserva crearReserva(Cliente cliente, Salida salida, Vendedor vendedor, List<Pasajero> pasajeros) {
        return null;
    }

    public Pago registrarPago(Reserva reserva, double monto, MedioPago medio, int cuotas) {
        return null;
    }

    public double reemplazarPasajero(Reserva reserva, Pasajero viejo, Pasajero nuevo) {
        return 0.0;
    }

    public double cancelarReserva(Reserva reserva, String motivo) {
        return 0.0;
    }

    public List<Reserva> getReservasOrdenadasPorTotal() {
        return new ArrayList<>();
    }
}