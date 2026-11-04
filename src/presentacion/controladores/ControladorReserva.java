package presentacion.controladores;

import excepciones.CupoExcedidoException;
import excepciones.ReglaNegocioException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import modelo.Agencia;
import modelo.Cliente;
import modelo.MedioPago;
import modelo.Pago;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;
import modelo.ServicioAdicional;
import modelo.Vendedor;

public class ControladorReserva {
    private final Agencia agencia;

    public ControladorReserva(Agencia agencia) { this.agencia = agencia; }

    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String pais)
            throws ReglaNegocioException {
        return agencia.registrarCliente(nombre, documento, telefono, email, pais);
    }

    public void actualizarCliente(Cliente cliente, String nombre, String telefono, String email, String pais)
            throws ReglaNegocioException {
        cliente.actualizarDatos(nombre, telefono, email, pais);
    }

    public Reserva crearReserva(Cliente cliente, Salida salida, Vendedor vendedor, List<Pasajero> pasajeros)
            throws ReglaNegocioException {
        if (cliente == null || salida == null || vendedor == null) {
            throw new ReglaNegocioException("Debe indicar cliente, salida y vendedor.");
        }
        if (pasajeros == null || pasajeros.isEmpty()) {
            throw new ReglaNegocioException("La reserva debe tener al menos un pasajero.");
        }
        int disponible = salida.calcularCupoDisponible();
        if (pasajeros.size() > disponible) {
            throw new CupoExcedidoException("Cupo insuficiente: se piden " + pasajeros.size()
                    + " lugares y solo quedan " + disponible + ".");
        }
        procesarExtras(salida, pasajeros);
        Reserva reserva = new Reserva(agencia.getProximoNumeroReserva(), cliente, salida, vendedor,
                agencia.getPoliticaCancelacion(), agencia.getCalculadora(), agencia.hoy());
        for (Pasajero p : pasajeros) reserva.agregarPasajero(p);
        reserva.validarReglasDeCreacion();
        salida.agregarReserva(reserva, agencia.hoy());
        agencia.avanzarNumeroReserva();
        return reserva;
    }

    void procesarExtras(Salida salida, List<Pasajero> pasajeros) throws ReglaNegocioException {
        if (!salida.getTour().requiereSeguro()) return;
        ServicioAdicional seguro = agencia.getSeguroViaje();
        for (Pasajero p : pasajeros) {
            if (!p.tieneSeguro()) p.agregarServicio(seguro);
        }
    }

    public Pago registrarPago(Reserva reserva, double monto, MedioPago medio, int cuotas) throws ReglaNegocioException {
        return reserva.registrarPago(monto, medio, cuotas, agencia.hoy());
    }

    public double reemplazarPasajero(Reserva reserva, Pasajero viejo, Pasajero nuevo) throws ReglaNegocioException {
        return reserva.reemplazarPasajero(viejo, nuevo, agencia.hoy());
    }

    public void contratarServicio(Reserva reserva, Pasajero pasajero, ServicioAdicional servicio)
            throws ReglaNegocioException {
        reserva.contratarServicio(pasajero, servicio);
    }

    public double cancelarReserva(Reserva reserva, String motivo) throws ReglaNegocioException {
        return reserva.cancelar(motivo, agencia.hoy());
    }

    public Reserva buscarReserva(int numero) { return agencia.buscarReserva(numero); }

    public List<Reserva> getReservas() { return agencia.getReservas(); }

    public List<Reserva> getReservasOrdenadasPorTotal() {
        List<Reserva> lista = new ArrayList<>(agencia.getReservas());
        lista.sort(Comparator.comparingDouble(Reserva::getTotal).reversed());
        return lista;
    }
}
