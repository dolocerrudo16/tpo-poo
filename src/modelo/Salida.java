package modelo;

import excepciones.CupoExcedidoException;
import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Salida implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DIAS_EVALUACION_CUPO_MINIMO = 7;

    private final int idSalida;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int cupoMaximo;
    private EstadoSalida estado = EstadoSalida.PROGRAMADA;
    private final Tour tour;
    private Guia guiaAsignado;
    private Vehiculo vehiculo;
    private final List<Reserva> reservas = new ArrayList<>();
    private boolean listaPasajerosEmitida;
    private int versionLista;
    private List<Pasajero> ultimaLista = new ArrayList<>();
    private boolean liquidada;
    private LocalDate fechaLiquidacion;

    public Salida(int idSalida, LocalDate fechaInicio, LocalDate fechaFin, int cupoMaximo, Tour tour, LocalDate hoy)
            throws ReglaNegocioException {
        if (tour == null) throw new ReglaNegocioException("La salida necesita un tour.");
        if (fechaInicio == null || fechaFin == null || hoy == null) {
            throw new ReglaNegocioException("Las fechas de la salida son obligatorias.");
        }
        if (!fechaInicio.isAfter(hoy)) throw new ReglaNegocioException("La fecha de inicio debe ser futura.");
        LocalDate finEsperado = fechaInicio.plusDays(tour.getDuracionDias() - 1L);
        if (!fechaFin.equals(finEsperado)) {
            throw new ReglaNegocioException("Las fechas no coinciden con la duración del tour (" + tour.getDuracionDias()
                    + " día/s): si inicia el " + fechaInicio + " debe terminar el " + finEsperado + ".");
        }
        if (cupoMaximo < tour.getCupoMinimo() || cupoMaximo > tour.getCupoMaximo()) {
            throw new ReglaNegocioException("El cupo máximo debe estar entre " + tour.getCupoMinimo() + " y "
                    + tour.getCupoMaximo() + " (rango del tour).");
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

    public int calcularLugaresConfirmados() {
        int confirmados = 0;
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.FINALIZADA) {
                confirmados += r.getCantidadLugaresOcupados();
            }
        }
        return confirmados;
    }

    public void agregarReserva(Reserva r, LocalDate hoy) throws ReglaNegocioException {
        if (r == null) throw new ReglaNegocioException("La reserva es obligatoria.");
        if (r.getSalida() != this) throw new ReglaNegocioException("La reserva pertenece a otra salida.");
        if (estado != EstadoSalida.PROGRAMADA && estado != EstadoSalida.CONFIRMADA) {
            throw new ReglaNegocioException("La salida está " + estado + " y no admite nuevas reservas.");
        }
        long dias = ChronoUnit.DAYS.between(hoy, fechaInicio);
        if (dias < Reserva.DIAS_LIMITE_SALDO) {
            throw new ReglaNegocioException("Faltan menos de " + Reserva.DIAS_LIMITE_SALDO
                    + " días para la salida: ya no se aceptan reservas (el saldo debe estar pago 10 días antes).");
        }
        int disponible = calcularCupoDisponible();
        if (r.getCantidadLugaresOcupados() > disponible) {
            throw new CupoExcedidoException("Cupo insuficiente: la reserva pide " + r.getCantidadLugaresOcupados()
                    + " lugar/es y solo quedan " + disponible + ".");
        }
        reservas.add(r);
        actualizarEstadoSegunOcupacion();
        invalidarLista();
    }

    void actualizarEstadoSegunOcupacion() {
        if (estado == EstadoSalida.EN_CURSO || estado == EstadoSalida.FINALIZADA || estado == EstadoSalida.CANCELADA) return;
        if (calcularCupoDisponible() == 0) {
            estado = EstadoSalida.COMPLETA;
        } else if (calcularLugaresConfirmados() >= tour.getCupoMinimo()) {
            estado = EstadoSalida.CONFIRMADA;
        } else {
            estado = EstadoSalida.PROGRAMADA;
        }
    }

    public boolean cancelarSiNoAlcanzaCupoMinimo(LocalDate hoy) throws ReglaNegocioException {
        if (estado != EstadoSalida.PROGRAMADA && estado != EstadoSalida.CONFIRMADA && estado != EstadoSalida.COMPLETA) {
            return false;
        }
        long dias = ChronoUnit.DAYS.between(hoy, fechaInicio);
        if (dias > DIAS_EVALUACION_CUPO_MINIMO || dias <= 0) return false;
        if (calcularLugaresConfirmados() >= tour.getCupoMinimo()) return false;
        this.estado = EstadoSalida.CANCELADA;
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.PENDIENTE || r.getEstado() == EstadoReserva.CONFIRMADA) {
                r.cancelarPorAgencia("la salida no alcanzó el cupo mínimo", hoy);
            }
        }
        return true;
    }


    private void validarAsignable() throws ReglaNegocioException {
        if (estado == EstadoSalida.EN_CURSO || estado == EstadoSalida.FINALIZADA || estado == EstadoSalida.CANCELADA) {
            throw new ReglaNegocioException("La salida está " + estado + ": no se puede modificar la asignación.");
        }
    }

    public void asignarGuia(Guia guia, List<Salida> otrasSalidas) throws ReglaNegocioException {
        validarAsignable();
        if (guia == null) throw new ReglaNegocioException("El guía es obligatorio.");
        if (!guia.estaHabilitadoPara(tour.getCategoria())) {
            throw new ReglaNegocioException("El guía " + guia.getNombre() + " no está habilitado para tours de categoría "
                    + tour.getCategoria() + ".");
        }
        if (otrasSalidas != null) {
            for (Salida otra : otrasSalidas) {
                if (otra == this || otra.estado == EstadoSalida.CANCELADA || otra.guiaAsignado == null) continue;
                if (otra.guiaAsignado.getLegajo() == guia.getLegajo() && seSuperponeCon(otra)) {
                    throw new ReglaNegocioException("El guía ya tiene la salida #" + otra.idSalida + " (" + otra.fechaInicio
                            + " a " + otra.fechaFin + ") que se superpone con estas fechas.");
                }
            }
        }
        this.guiaAsignado = guia;
    }

    public void asignarVehiculo(Vehiculo v, List<Salida> otrasSalidas) throws ReglaNegocioException {
        validarAsignable();
        if (v == null) throw new ReglaNegocioException("El vehículo es obligatorio.");
        if (v.getCapacidad() < cupoMaximo) {
            throw new ReglaNegocioException("La capacidad del vehículo (" + v.getCapacidad()
                    + ") es menor al cupo de la salida (" + cupoMaximo + ").");
        }
        if (otrasSalidas != null) {
            for (Salida otra : otrasSalidas) {
                if (otra == this || otra.estado == EstadoSalida.CANCELADA || otra.vehiculo == null) continue;
                if (otra.vehiculo.getPatente().equals(v.getPatente()) && seSuperponeCon(otra)) {
                    throw new ReglaNegocioException("El vehículo " + v.getPatente() + " ya está asignado a la salida #"
                            + otra.idSalida + " en fechas superpuestas.");
                }
            }
        }
        this.vehiculo = v;
    }

    private boolean seSuperponeCon(Salida otra) {
        return !this.fechaInicio.isAfter(otra.fechaFin) && !this.fechaFin.isBefore(otra.fechaInicio);
    }


    public List<Pasajero> emitirListaPasajeros() throws ReglaNegocioException {
        if (guiaAsignado == null) throw new ReglaNegocioException("La salida no tiene guía asignado.");
        List<Pasajero> lista = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.CONFIRMADA) lista.addAll(r.getPasajeros());
        }
        if (lista.isEmpty()) {
            throw new ReglaNegocioException("La salida no tiene reservas confirmadas: no hay pasajeros para listar.");
        }
        Collections.sort(lista);
        this.ultimaLista = new ArrayList<>(lista);
        this.listaPasajerosEmitida = true;
        this.versionLista++;
        return new ArrayList<>(lista);
    }

    void invalidarLista() { this.listaPasajerosEmitida = false; }

    public void iniciar(LocalDate hoy) throws ReglaNegocioException {
        if (estado != EstadoSalida.CONFIRMADA && estado != EstadoSalida.COMPLETA) {
            throw new ReglaNegocioException("La salida está " + estado + ": solo puede iniciarse si está CONFIRMADA o COMPLETA.");
        }
        if (hoy.isBefore(fechaInicio)) {
            throw new ReglaNegocioException("La salida no puede iniciarse antes de su fecha de inicio (" + fechaInicio + ").");
        }
        List<String> faltantes = new ArrayList<>();
        if (guiaAsignado == null) faltantes.add("guía asignado");
        if (!listaPasajerosEmitida) faltantes.add("lista de pasajeros vigente (emitirla de nuevo si hubo cambios)");
        if (!faltantes.isEmpty()) {
            throw new ReglaNegocioException("No se puede iniciar la salida. Falta: " + String.join(", ", faltantes) + ".");
        }
        this.estado = EstadoSalida.EN_CURSO;
    }

    public void finalizar(LocalDate hoy) throws ReglaNegocioException {
        if (estado != EstadoSalida.EN_CURSO) {
            throw new ReglaNegocioException("Solo se puede cerrar una salida EN_CURSO (estado actual: " + estado + ").");
        }
        if (hoy.isBefore(fechaFin)) {
            throw new ReglaNegocioException("La salida no puede cerrarse antes de su fecha de fin (" + fechaFin + ").");
        }
        for (Reserva r : reservas) r.marcarFinalizada();
        this.estado = EstadoSalida.FINALIZADA;
    }

    public double calcularTotalAdicionales() {
        double total = 0.0;
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.FINALIZADA) {
                total += r.getTotalAdicionales();
            }
        }
        return Dinero.redondear(total);
    }

    void marcarLiquidada(LocalDate fecha) {
        this.liquidada = true;
        this.fechaLiquidacion = fecha;
    }

    public int getIdSalida() { return idSalida; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public int getCupoMaximo() { return cupoMaximo; }
    public EstadoSalida getEstado() { return estado; }
    public Tour getTour() { return tour; }
    public Guia getGuiaAsignado() { return guiaAsignado; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public List<Reserva> getReservas() { return new ArrayList<>(reservas); }
    public boolean isListaPasajerosEmitida() { return listaPasajerosEmitida; }
    public int getVersionLista() { return versionLista; }
    public List<Pasajero> getUltimaLista() { return new ArrayList<>(ultimaLista); }
    public boolean isLiquidada() { return liquidada; }
    public LocalDate getFechaLiquidacion() { return fechaLiquidacion; }

    @Override
    public String toString() {
        return "Salida #" + idSalida + " [" + estado + "] " + tour.getNombre() + " del " + fechaInicio + " al " + fechaFin
                + " - cupo disponible " + calcularCupoDisponible() + "/" + cupoMaximo
                + (guiaAsignado != null ? " - guía " + guiaAsignado.getNombre() : " - sin guía")
                + (vehiculo != null ? " - " + vehiculo.getPatente() : " - sin vehículo");
    }
}
