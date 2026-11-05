package presentacion.controladores;

import java.util.List;

import modelo.Agencia;
import modelo.Guia;
import modelo.LiquidacionGuia;
import modelo.Salida;
import modelo.clasesAbstractas.Tour;
import reportes.Reportes;

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
