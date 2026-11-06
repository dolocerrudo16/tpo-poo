package modelo.viajesServicios;

import excepciones.CupoExcedidoException;
import excepciones.ReglaNegocioException;
import modelo.reservas.Reserva;
import modelo.clasesAbstractas.Tour;
import modelo.enums.EstadoReserva;
import modelo.enums.EstadoSalida;
import modelo.personasRoles.Guia;
import modelo.personasRoles.Pasajero;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Salida implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idSalida;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cupoMaximo;
    private EstadoSalida estado = EstadoSalida.PROGRAMADA;
    private boolean listaPasajerosEmitida;
    private boolean liquidada;

    // Relaciones del diagrama
    private Tour tour;
    private Guia guiaAsignado;
    private Vehiculo vehiculo;
    private List<Reserva> reservas = new ArrayList<>();

    public Salida(int idSalida, LocalDate fechaInicio, LocalDate fechaFin, int cupoMaximo, Tour tour, LocalDate hoy)
            throws ReglaNegocioException {
        if (tour == null) throw new ReglaNegocioException("La salida necesita un tour.");
        if (fechaInicio == null || fechaFin == null || hoy == null) {
            throw new ReglaNegocioException("Las fechas de la salida son obligatorias.");
        }
        if (!fechaInicio.isAfter(hoy)) throw new ReglaNegocioException("La fecha de inicio debe ser futura.");
        LocalDate finEsperado = fechaInicio.plusDays(tour.getDuracionDias() - 1L);
        if (!fechaFin.equals(finEsperado)) {
            throw new ReglaNegocioException("Las fechas no coinciden con la duración del tour.");
        }
        if (cupoMaximo < tour.getCupoMinimo() || cupoMaximo > tour.getCupoMaximo()) {
            throw new ReglaNegocioException("El cupo máximo está fuera del rango permitido por el tour.");
        }
        this.idSalida = idSalida;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupoMaximo = cupoMaximo;
        this.tour = tour;
    }

    public int calcularCupoDisponible() {
        int ocupados = 0;
        for (Reserva r : reservas) {
            if (r.ocupaLugar()) ocupados += r.getCantidadLugaresOcupados();
        }
        return cupoMaximo - ocupados;
    }

    public void agregarReserva(Reserva r, LocalDate hoy) throws ReglaNegocioException {
        if (r == null) throw new ReglaNegocioException("La reserva es obligatoria.");
        if (r.getSalida() != this) throw new ReglaNegocioException("La reserva pertenece a otra salida.");
        if (this.estado == EstadoSalida.COMPLETA) throw new ReglaNegocioException("La salida ya está COMPLETA.");

        long dias = fechaInicio.toEpochDay() - hoy.toEpochDay();
        if (dias < 10) {
            throw new ReglaNegocioException("Ya no se aceptan reservas (fuera de término).");
        }

        int disponible = calcularCupoDisponible();
        if (r.getCantidadLugaresOcupados() > disponible) {
            throw new CupoExcedidoException("Cupo insuficiente. Quedan " + disponible + " lugares.");
        }

        reservas.add(r);
        this.listaPasajerosEmitida = false;

        // RN01-06: Si al agregar la reserva el cupo llega a 0, la salida pasa a COMPLETA
        if (calcularCupoDisponible() == 0) {
            this.estado = EstadoSalida.COMPLETA;
        }
    }

    public void asignarGuia(Guia guia, List<Salida> otras) throws ReglaNegocioException {
        if (guia == null) throw new ReglaNegocioException("El guía es obligatorio.");
        if (!guia.estaHabilitadoPara(tour.getCategoria())) {
            throw new ReglaNegocioException("El guía no está habilitado para esta categoría de tour.");
        }

        for (Salida otra : otras) {
            if (otra != this && otra.getGuiaAsignado() != null && otra.getGuiaAsignado().equals(guia)) {
                // Hay superposición si el inicio de una <= fin de la otra y viceversa
                if (!this.fechaInicio.isAfter(otra.fechaFin) && !this.fechaFin.isBefore(otra.fechaInicio)) {
                    throw new ReglaNegocioException("El guía ya tiene la salida " + otra.getIdSalida() + " superpuesta en esas fechas.");
                }
            }
        }
        this.guiaAsignado = guia;
    }

    public void asignarVehiculo(Vehiculo v, List<Salida> otras) throws ReglaNegocioException {
        if (v == null) throw new ReglaNegocioException("El vehículo es obligatorio.");
        if (v.getCapacidad() < cupoMaximo) {
            throw new ReglaNegocioException("La capacidad del vehículo es menor al cupo de la salida.");
        }
        this.vehiculo = v;
    }

    public List<Pasajero> emitirListaPasajeros() throws ReglaNegocioException {
        if (guiaAsignado == null) throw new ReglaNegocioException("La salida no tiene guía asignado.");
        List<Pasajero> lista = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.CONFIRMADA) lista.addAll(r.getPasajeros());
        }
        if (lista.isEmpty()) {
            throw new ReglaNegocioException("No hay pasajeros para listar.");
        }
        Collections.sort(lista);
        this.listaPasajerosEmitida = true;
        return lista;
    }

    public void iniciar(LocalDate hoy) throws ReglaNegocioException {
        if (guiaAsignado == null || !listaPasajerosEmitida) {
            throw new ReglaNegocioException("Faltan requisitos para iniciar la salida.");
        }
        this.estado = EstadoSalida.EN_CURSO;
    }

    public void finalizar(LocalDate hoy) throws ReglaNegocioException {
        this.estado = EstadoSalida.FINALIZADA;
    }

    public boolean cancelarSiNoAlcanzaCupoMinimo(LocalDate hoy) throws ReglaNegocioException {
        long dias = fechaInicio.toEpochDay() - hoy.toEpochDay();

        // La regla dice que se evalúa a los 7 días antes
        if (dias == 7) {
            int ocupadosConfirmados = 0;
            // Solo cuentan los pasajeros de reservas que estén pagadas/confirmadas
            for (Reserva r : reservas) {
                if (r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.FINALIZADA) {
                    ocupadosConfirmados += r.getCantidadLugaresOcupados();
                }
            }

            // RN01-06: Si no alcanza el cupo mínimo, se cancela la salida
            if (ocupadosConfirmados < tour.getCupoMinimo()) {
                this.estado = EstadoSalida.CANCELADA;

                // Si la salida se cancela, la regla dicta que todas sus reservas vigentes también se cancelan
                for (Reserva r : reservas) {
                    if (r.ocupaLugar()) {
                        r.cancelarPorAgencia("Salida cancelada por no alcanzar cupo mínimo", hoy);
                    }
                }
                return true;
            }
        }
        return false;
    }

    // Getters
    public int getIdSalida() { return idSalida; }
    public EstadoSalida getEstado() { return estado; }
    public Tour getTour() { return tour; }
    public Guia getGuiaAsignado() { return guiaAsignado; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public List<Reserva> getReservas() { return new ArrayList<>(reservas); }
}