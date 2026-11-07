package modelo.reservas;

import excepciones.ReglaNegocioException;
import modelo.enums.EstadoReserva;
import modelo.enums.MedioPago;
import modelo.enums.Temporada;
import modelo.interfaces.PoliticaCancelacion;
import modelo.personasRoles.Cliente;
import modelo.personasRoles.Pasajero;
import modelo.personasRoles.Vendedor;
import modelo.viajesServicios.Salida;
import modelo.viajesServicios.ServicioAdicional;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numero;
    private LocalDate fechaCreacion;
    private EstadoReserva estado = EstadoReserva.PENDIENTE;
    private double total;
    private Temporada temporada;

    private Cliente cliente;
    private Salida salida;
    private Vendedor vendedor;
    private PoliticaCancelacion politicaCancelacion;
    private CalculadoraTarifa calculadora;
    private List<Pasajero> pasajeros = new ArrayList<>();
    private List<Pago> pagos = new ArrayList<>();
    private List<NotaDeAjuste> notasDeAjuste = new ArrayList<>();

    public Reserva(int numero, Cliente cliente, Salida salida, Vendedor vendedor, PoliticaCancelacion politicaCancelacion, CalculadoraTarifa calculadora, LocalDate fechaCreacion) {
        this.numero = numero;
        this.cliente = cliente;
        this.salida = salida;
        this.vendedor = vendedor;
        this.politicaCancelacion = politicaCancelacion;
        this.calculadora = calculadora;
        this.fechaCreacion = fechaCreacion;
    }

    public void agregarPasajero(Pasajero p) {
        if (p != null) pasajeros.add(p);
    }

    public void contratarServicio(Pasajero p, ServicioAdicional s) {
        if (pasajeros.contains(p) && s != null) p.agregarServicio(s);
    }

    public void setTotal(double total) {
        this.total = Dinero.redondear(total);
    }

    public double getTotalPagado() {
        double sum = 0.0;
        for (Pago p : pagos) {
            sum += p.getMonto();
        }
        return sum;
    }

    public Pago registrarPago(double monto, MedioPago medio, int cuotas, LocalDate hoy) throws ReglaNegocioException {
        if (this.estado == EstadoReserva.CANCELADA || this.estado == EstadoReserva.ANULADA) {
            throw new ReglaNegocioException("No se pueden registrar pagos en una reserva cancelada o anulada.");
        }

        Pago nuevo = new Pago(pagos.size() + 1, monto, hoy, medio, cuotas);
        pagos.add(nuevo);

        if (this.estado == EstadoReserva.PENDIENTE) {
            double pagado = getTotalPagado();
            double minimoRequerido = Dinero.redondear(this.total * 0.30);

            if (pagado >= minimoRequerido) {
                this.estado = EstadoReserva.CONFIRMADA;
            }
        }
        return nuevo;
    }

    public boolean anularSiVencido(LocalDate hoy) {
        if (this.estado == EstadoReserva.CANCELADA || this.estado == EstadoReserva.ANULADA || this.estado == EstadoReserva.FINALIZADA) {
            return false;
        }

        long diasFaltantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, salida.getFechaInicio());

        if (diasFaltantes <= 10 && getTotalPagado() < total) {
            this.estado = EstadoReserva.ANULADA;
            salida.actualizarEstado();
            return true;
        }
        return false;
    }

    public double cancelar(String motivo, LocalDate hoy) {
        if (this.estado == EstadoReserva.CANCELADA || this.estado == EstadoReserva.ANULADA) {
            return 0.0;
        }

        long diasAnticipacion = java.time.temporal.ChronoUnit.DAYS.between(hoy, salida.getFechaInicio());
        double totalDevolver = politicaCancelacion.calcularDevolucion(getTotalPagado(), diasAnticipacion);
        double retencion = getTotalPagado() - totalDevolver;

        this.estado = EstadoReserva.CANCELADA;
        salida.actualizarEstado();

        NotaDeAjuste nota = new NotaDeAjuste(notasDeAjuste.size() + 1, totalDevolver, retencion, hoy, motivo, "Mismo medio de pago");
        notasDeAjuste.add(nota);

        return totalDevolver;
    }

    public double cancelarPorAgencia(String motivo, LocalDate hoy) {
        if (this.estado == EstadoReserva.CANCELADA || this.estado == EstadoReserva.ANULADA) {
            return 0.0;
        }
        this.estado = EstadoReserva.CANCELADA;
        salida.actualizarEstado();

        double totalDevolver = getTotalPagado();

        NotaDeAjuste nota = new NotaDeAjuste(
                notasDeAjuste.size() + 1,
                totalDevolver,
                0.0,
                hoy,
                motivo,
                "Mismo medio de pago"
        );
        notasDeAjuste.add(nota);

        return totalDevolver;
    }

    public double reemplazarPasajero(Pasajero viejo, Pasajero nuevo, LocalDate hoy) throws ReglaNegocioException {
        long diasFaltantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, salida.getFechaInicio());
        if (diasFaltantes < 3) {
            throw new ReglaNegocioException("Faltan menos de 3 días para la salida. No se permiten cambios de pasajeros.");
        }

        int idx = pasajeros.indexOf(viejo);
        if (idx >= 0) {
            pasajeros.set(idx, nuevo);
            double nuevoTotal = calculadora.calcularTotal(pasajeros, salida.getTour(), salida.getFechaInicio(), hoy);
            double diferencia = nuevoTotal - this.total;
            setTotal(nuevoTotal);
            return diferencia;
        } else {
            throw new ReglaNegocioException("El pasajero a reemplazar no existe en la reserva.");
        }
    }

    public boolean ocupaLugar() {
        return this.estado != EstadoReserva.CANCELADA && this.estado != EstadoReserva.ANULADA;
    }

    public int getCantidadLugaresOcupados() {
        return pasajeros != null ? pasajeros.size() : 0;
    }

    public List<Pasajero> getPasajeros() {
        return new ArrayList<>(pasajeros);
    }

    public int getNumero() { return numero; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public EstadoReserva getEstado() { return estado; }
    public double getTotal() { return total; }
    public Temporada getTemporada() { return temporada; }
    public List<Pago> getPagos() { return new ArrayList<>(pagos); }
    public Salida getSalida() { return salida; }
}