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

    // establece el total de la reserva tras calcularlo
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

        // RN07: Si está PENDIENTE y el total pagado alcanza el 30% del total, pasa a CONFIRMADA
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
        // Lógica interna
        return false;
    }

    // Calcula la penalidad según los días de anticipación y crea la nota de ajuste
    public double cancelar(String motivo, LocalDate hoy) {
        if (this.estado == EstadoReserva.CANCELADA || this.estado == EstadoReserva.ANULADA) {
            return 0.0;
        }

        long diasAnticipacion = java.time.temporal.ChronoUnit.DAYS.between(hoy, salida.getFechaInicio());
        double totalDevolver = politicaCancelacion.calcularDevolucion(getTotalPagado(), diasAnticipacion);
        double retencion = getTotalPagado() - totalDevolver;

        this.estado = EstadoReserva.CANCELADA;

        NotaDeAjuste nota = new NotaDeAjuste(notasDeAjuste.size() + 1, totalDevolver, retencion, hoy, motivo, "Mismo medio de pago");
        notasDeAjuste.add(nota);

        return totalDevolver;
    }

    public double cancelarPorAgencia(String motivo, LocalDate hoy) {
        if (this.estado == EstadoReserva.CANCELADA || this.estado == EstadoReserva.ANULADA) {
            return 0.0;
        }
        this.estado = EstadoReserva.CANCELADA;

        // RN13: Si la cancelación la provoca la agencia, se devuelve siempre el 100% de lo pagado.
        double totalDevolver = getTotalPagado();

        NotaDeAjuste nota = new NotaDeAjuste(
                notasDeAjuste.size() + 1,
                totalDevolver,
                0.0, // retención 0
                hoy,
                motivo,
                "Mismo medio de pago"
        );
        notasDeAjuste.add(nota);

        return totalDevolver;
    }

    // Valida que falten más de 3 días y hace el cambiazo del objeto pasajero
    public double reemplazarPasajero(Pasajero viejo, Pasajero nuevo, LocalDate hoy) throws ReglaNegocioException {
        long diasFaltantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, salida.getFechaInicio());
        if (diasFaltantes < 3) {
            throw new ReglaNegocioException("Faltan menos de 3 días para la salida. No se permiten cambios de pasajeros.");
        }

        int idx = pasajeros.indexOf(viejo);
        if (idx >= 0) {
            pasajeros.set(idx, nuevo);
            return 0.0;
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

    // Getters
    public int getNumero() { return numero; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public EstadoReserva getEstado() { return estado; }
    public double getTotal() { return total; }
    public Temporada getTemporada() { return temporada; }
    public List<Pago> getPagos() { return new ArrayList<>(pagos); }
    public Salida getSalida() { return salida; }
}