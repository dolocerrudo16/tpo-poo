package presentacion.controladores;

import excepciones.ReglaNegocioException;
import java.util.List;
import modelo.Actividad;
import modelo.Agencia;
import modelo.Guia;
import modelo.LiquidacionGuia;
import modelo.PoliticaCancelacion;
import modelo.Proveedor;
import modelo.Salida;
import modelo.TarifaTemporada;
import modelo.Tour;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import modelo.Vendedor;
import reportes.Reportes;

/** Controlador GRASP del administrador: CU01, CU02, CU10 y CU11. */
public class ControladorAdministracion {
    private final Agencia agencia;
    private final Reportes reportes;

    public ControladorAdministracion(Agencia agencia) {
        this.agencia = agencia;
        this.reportes = new Reportes(agencia);
    }

    // CU01
    public void altaTour(Tour tour) throws ReglaNegocioException { agencia.agregarTour(tour); }

    public void agregarActividad(Tour tour, Actividad actividad) throws ReglaNegocioException {
        tour.agregarActividad(actividad);
    }

    public void agregarTarifa(Tour tour, TarifaTemporada tarifa) throws ReglaNegocioException {
        tour.agregarTarifa(tarifa);
    }

    // CU02
    public Proveedor altaProveedor(String nombre, String cuit, String contacto, String servicio) throws ReglaNegocioException {
        return agencia.registrarProveedor(nombre, cuit, contacto, servicio);
    }

    public Guia altaGuia(Guia guia) throws ReglaNegocioException { return agencia.registrarGuia(guia); }

    public Vendedor altaVendedor(Vendedor vendedor) throws ReglaNegocioException {
        agencia.registrarEmpleado(vendedor);
        return vendedor;
    }

    public Vehiculo altaVehiculo(String patente, int capacidad, TipoVehiculo tipo) throws ReglaNegocioException {
        return agencia.registrarVehiculo(patente, capacidad, tipo);
    }

    public void bajaGuia(int legajo) throws ReglaNegocioException { agencia.eliminarGuia(legajo); }
    public void bajaVehiculo(String patente) throws ReglaNegocioException { agencia.eliminarVehiculo(patente); }
    public void bajaProveedor(String cuit) throws ReglaNegocioException { agencia.eliminarProveedor(cuit); }

    public void cambiarPoliticaCancelacion(PoliticaCancelacion politica) throws ReglaNegocioException {
        agencia.setPoliticaCancelacion(politica);
    }

    // CU10
    public List<Salida> salidasPendientesDeLiquidar(Guia guia) { return agencia.salidasPendientesDeLiquidar(guia); }

    public double calcularHonorario(Salida salida) { return agencia.calcularHonorarioSalida(salida); }

    public LiquidacionGuia liquidar(Guia guia, List<Salida> salidas) throws ReglaNegocioException {
        return agencia.liquidarHonorarios(guia, salidas, agencia.hoy());
    }

    // CU11
    public Reportes getReportes() { return reportes; }
}
