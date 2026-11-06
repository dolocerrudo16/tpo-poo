package modelo.estructura;

import excepciones.ReglaNegocioException;
import modelo.clasesAbstractas.Empleado;
import modelo.clasesAbstractas.Tour;
import modelo.interfaces.PoliticaCancelacion;
import modelo.personasRoles.Cliente;
import modelo.personasRoles.Guia;
import modelo.personasRoles.Proveedor;
import modelo.politicasPromos.CancelacionFlexible;
import modelo.reservas.CalculadoraTarifa;
import modelo.reservas.LiquidacionGuia;
import modelo.reservas.Reserva;
import modelo.viajesServicios.Salida;
import modelo.viajesServicios.ServicioAdicional;
import modelo.viajesServicios.Vehiculo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Agencia implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Tour> tours = new ArrayList<>();
    private List<Salida> salidas = new ArrayList<>();
    private List<Cliente> clientes = new ArrayList<>();
    private List<Empleado> personal = new ArrayList<>();
    private List<Vehiculo> vehiculos = new ArrayList<>();
    private List<Proveedor> proveedores = new ArrayList<>();
    private List<ServicioAdicional> servicios = new ArrayList<>();

    private List<LiquidacionGuia> liquidaciones = new ArrayList<>();

    private CalculadoraTarifa calculadora = new CalculadoraTarifa();
    private PoliticaCancelacion politicaCancelacion = new CancelacionFlexible();

    public Agencia() {
    }

    public Salida programarSalida(String codigoTour, LocalDate inicio, LocalDate fin, int cupoMaximo, Tour tour, LocalDate hoy) throws ReglaNegocioException {
        // El ID puede ser el tamaño de la lista + 1
        int idSalida = salidas.size() + 1;
        Salida nuevaSalida = new Salida(idSalida, inicio, fin, cupoMaximo, tour, hoy);
        salidas.add(nuevaSalida);
        return nuevaSalida;
    }

    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String pais) throws ReglaNegocioException {
        int idCliente = clientes.size() + 1;
        Cliente nuevoCliente = new Cliente(idCliente, nombre, documento, telefono, email, pais);
        clientes.add(nuevoCliente);
        return nuevoCliente;
    }

    // Agrega estos Getters fundamentales para que los controladores puedan consultar los datos
    public List<Salida> getSalidas() {
        return salidas;
    }

    public List<Reserva> getTodasLasReservas() {
        List<Reserva> todas = new ArrayList<>();
        for (Salida s : salidas) {
            todas.addAll(s.getReservas());
        }
        return todas;
    }
}