package presentacion.controladores;

import modelo.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ControladorOperaciones {

    private Agencia agencia;

    public ControladorOperaciones(Agencia agencia) {
        this.agencia = agencia;
    }

    public Salida programarSalida(String codigoTour, LocalDate inicio, LocalDate fin, int cupoMaximo) {
        return null;
    }

    public void asignarGuia(Salida salida, Guia guia) {
        // Lógica para asignar
    }

    public void asignarVehiculo(Salida salida, Vehiculo vehiculo) {
        // Lógica para asignar
    }

    public List<Pasajero> emitirListaPasajeros(Salida salida) {
        return new ArrayList<>();
    }

    public void iniciarSalida(Salida salida) {
        // Cambia estado a EN_CURSO
    }

    public void cerrarSalida(Salida salida) {
        // Cambia estado a FINALIZADA
    }

    public List<String> procesarVencimientos() {
        // Utiliza el Reloj para obtener "hoy" y evalúa cancelaciones
        return new ArrayList<>();
    }
}