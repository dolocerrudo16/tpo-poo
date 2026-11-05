package modelo;

import excepciones.ReglaNegocioException;
import modelo.clasesAbstractas.Empleado;
import modelo.clasesAbstractas.Tour;
import modelo.interfaces.PoliticaCancelacion;

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

    public Agencia() {}

    public Salida programarSalida(String codigoTour, LocalDate inicio, LocalDate fin, int cupoMaximo) throws ReglaNegocioException {
        // Lógica para instanciar y agregar Salida
        return null;
    }

    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String pais) throws ReglaNegocioException {
        // Lógica para instanciar y agregar Cliente
        return null;
    }

    public List<String> procesarVencimientos(LocalDate hoy) {
        return new ArrayList<>();
    }

    public LiquidacionGuia liquidarHonorarios(Guia guia, List<Salida> salidasLiquidar, LocalDate hoy) {
        // Lógica de liquidación
        return null;
    }

    public Reserva buscarReserva(int numero) {
        return null;
    }
}
