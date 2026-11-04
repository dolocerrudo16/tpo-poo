package presentacion.controladores;

import excepciones.ReglaNegocioException;
import java.time.LocalDate;
import java.util.List;
import modelo.Agencia;
import modelo.Guia;
import modelo.Pasajero;
import modelo.Salida;
import modelo.Vehiculo;

/** Controlador GRASP del coordinador de operaciones: CU03, CU04 y CU09. */
public class ControladorOperaciones {
    private final Agencia agencia;

    public ControladorOperaciones(Agencia agencia) { this.agencia = agencia; }

    public Salida programarSalida(String codigoTour, LocalDate inicio, LocalDate fin, int cupoMaximo)
            throws ReglaNegocioException {
        return agencia.programarSalida(codigoTour, inicio, fin, cupoMaximo);
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

    public void iniciarSalida(Salida salida) throws ReglaNegocioException { salida.iniciar(agencia.hoy()); }

    public void cerrarSalida(Salida salida) throws ReglaNegocioException { salida.finalizar(agencia.hoy()); }

    /** Evaluación diaria: anula reservas con saldo vencido y cancela salidas sin cupo mínimo a 7 días. */
    public List<String> procesarVencimientos() throws ReglaNegocioException {
        return agencia.procesarVencimientos(agencia.hoy());
    }
}
