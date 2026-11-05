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

import modelo.*;
import reportes.Reportes;
import java.util.List;

public class ControladorAdministracion {

    private Agencia agencia;

    public ControladorAdministracion(Agencia agencia) {
        this.agencia = agencia;
    }

    public void altaTour(Tour tour) {
        // Lógica para registrar el tour
    }

    public void altaProveedor(String nombre, String cuit, String contacto, String servicioPrestado) {
        // Lógica para registrar proveedor
    }

    public LiquidacionGuia liquidar(Guia guia, List<Salida> salidas) {
        return null;
    }

    public Reportes getReportes() {
        return new Reportes(agencia);
    }
}
