package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final double PORCENTAJE_SENA = 0.30;
    public static final int DIAS_LIMITE_SALDO = 10;
    public static final int DIAS_LIMITE_REEMPLAZO = 3;
    private static final double TOLERANCIA = 0.005;
    private static final PoliticaCancelacion POLITICA_AGENCIA = new CancelacionPorAgencia();

    private final int numero;
    private final LocalDate fechaCreacion;
    private EstadoReserva estado = EstadoReserva.PENDIENTE;
    private double total;
    private Temporada temporada;
    private final Cliente cliente;
    private final Salida salida;
    private final Vendedor vendedor;
    private final PoliticaCancelacion politicaCancelacion;
    private final CalculadoraTarifa calculadora;
    private final List<Pasajero> pasajeros = new ArrayList<>();
    private final List<Pasajero> pasajerosReemplazados = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();
    private final List<NotaDeAjuste> notasDeAjuste = new ArrayList<>();

    public Reserva(int numero, Cliente cliente, Salida salida, Vendedor vendedor,
                   PoliticaCancelacion politicaCancelacion, CalculadoraTarifa calculadora, LocalDate fechaCreacion)
            throws ReglaNegocioException {
        if (cliente == null) throw new ReglaNegocioException("La reserva necesita un cliente titular.");
        if (salida == null) throw new ReglaNegocioException("La reserva necesita una salida.");
        if (vendedor == null) throw new ReglaNegocioException("La reserva necesita un vendedor.");
        if (politicaCancelacion == null || calculadora == null || fechaCreacion == null) {
            throw new ReglaNegocioException("Faltan datos internos para crear la reserva.");
        }
        this.numero = numero;
        this.cliente = cliente;
        this.salida = salida;
        this.vendedor = vendedor;
        this.politicaCancelacion = politicaCancelacion;
        this.calculadora = calculadora;
        this.fechaCreacion = fechaCreacion;
    }


    public void agregarPasajero(Pasajero p) throws ReglaNegocioException {
        if (estado != EstadoReserva.PENDIENTE || !pagos.isEmpty()) {
            throw new ReglaNegocioException("Solo se pueden agregar pasajeros mientras la reserva se está armando.");
        }
        validarPasajeroParaTour(p);
        if (pasajeros.contains(p)) {
            throw new ReglaNegocioException("El pasajero con documento " + p.getDocumento() + " ya está en la reserva.");
        }
        pasajeros.add(p);
    }

    private void validarPasajeroParaTour(Pasajero p) throws ReglaNegocioException {
        if (p == null) throw new ReglaNegocioException("El pasajero es obligatorio.");
        LocalDate fechaViaje = salida.getFechaInicio();
        if (!p.esTipoCoherente(fechaViaje)) {
            throw new ReglaNegocioException("El tipo " + p.getTipo() + " no es coherente con la edad de "
                    + p.getNombre() + " (" + p.calcularEdad(fechaViaje) + " años).");
        }
        if (!salida.getTour().validarPasajero(p, fechaViaje)) {
            throw new ReglaNegocioException(p.getNombre() + " no cumple los requisitos del tour " + salida.getTour().getNombre()
                    + (salida.getTour() instanceof TourAventura
                    ? " (edad mínima " + ((TourAventura) salida.getTour()).getEdadMinima() + " años)." : "."));
        }
    }

    public void contratarServicio(Pasajero pasajero, ServicioAdicional servicio) throws ReglaNegocioException {
        verificarVigente();
        int idx = pasajeros.indexOf(pasajero);
        if (idx < 0) throw new ReglaNegocioException("El pasajero no pertenece a esta reserva.");
        if (servicio == null) throw new ReglaNegocioException("El servicio es obligatorio.");
        pasajeros.get(idx).agregarServicio(servicio);
        recalcularTotal();
    }

    public void quitarServicio(Pasajero pasajero, ServicioAdicional servicio) throws ReglaNegocioException {
        verificarVigente();
        if (servicio != null && servicio.isSeguroViaje() && salida.getTour().requiereSeguro()) {
            throw new ReglaNegocioException("El seguro de viaje es obligatorio en tours de aventura y no puede quitarse.");
        }
        if (!pagos.isEmpty()) {
            throw new ReglaNegocioException("No se pueden quitar servicios de una reserva con pagos registrados.");
        }
        int idx = pasajeros.indexOf(pasajero);
        if (idx < 0 || !pasajeros.get(idx).quitarServicio(servicio)) {
            throw new ReglaNegocioException("El pasajero no tiene ese servicio contratado.");
        }
        recalcularTotal();
    }

    public void validarReglasDeCreacion() throws ReglaNegocioException {
        if (pasajeros.isEmpty()) throw new ReglaNegocioException("La reserva debe tener al menos un pasajero.");
        if (salida.getTour().requiereSeguro()) {
            for (Pasajero p : pasajeros) {
                if (!p.tieneSeguro()) {
                    throw new ReglaNegocioException("El seguro de viaje es obligatorio en tours de aventura: falta para " + p.getNombre() + ".");
                }
            }
        }
        recalcularTotal();
        if (total <= 0) {
            throw new ReglaNegocioException("La reserva debe incluir al menos un pasajero que abone tarifa.");
        }
    }

    public void recalcularTotal() throws ReglaNegocioException {
        this.total = calculadora.calcularTotal(pasajeros, salida.getTour(), salida.getFechaInicio(), fechaCreacion);
        this.temporada = salida.getTour().obtenerTarifaVigente(salida.getFechaInicio()).getTemporada();
    }


    public Pago registrarPago(double monto, MedioPago medio, int cuotas, LocalDate hoy) throws ReglaNegocioException {
        if (estado != EstadoReserva.PENDIENTE && estado != EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("No se pueden registrar pagos en una reserva " + estado + ".");
        }
        if (anularSiVencido(hoy)) {
            throw new ReglaNegocioException("Faltan menos de " + DIAS_LIMITE_SALDO
                    + " días para la salida y había saldo pendiente: la reserva fue ANULADA y el pago no se registró.");
        }
        if (monto <= 0) throw new ReglaNegocioException("El importe del pago debe ser mayor a cero.");
        if (monto > getSaldoPendiente() + TOLERANCIA) {
            throw new ReglaNegocioException("El importe supera el saldo pendiente. Máximo permitido: " + Dinero.formatear(getSaldoPendiente()));
        }
        Pago nuevo = new Pago(pagos.size() + 1, monto, hoy, medio, cuotas);
        pagos.add(nuevo);
        if (estado == EstadoReserva.PENDIENTE && getTotalNetoPagado() + TOLERANCIA >= total * PORCENTAJE_SENA) {
            estado = EstadoReserva.CONFIRMADA;
            salida.actualizarEstadoSegunOcupacion();
            salida.invalidarLista();
        }
        return nuevo;
    }

    public boolean anularSiVencido(LocalDate hoy) {
        if (estado != EstadoReserva.PENDIENTE && estado != EstadoReserva.CONFIRMADA) return false;
        if (getSaldoPendiente() <= TOLERANCIA) return false;
        if (diasHastaLaSalida(hoy) >= DIAS_LIMITE_SALDO) return false;
        cerrar(politicaCancelacion, EstadoReserva.ANULADA,
                "Anulada por falta de pago del saldo hasta " + DIAS_LIMITE_SALDO + " días antes de la salida", hoy);
        return true;
    }


    public double cancelar(String motivo, LocalDate hoy) throws ReglaNegocioException {
        validarCancelable(hoy);
        String detalle = (motivo == null || motivo.isBlank()) ? "Cancelación solicitada por el cliente" : motivo.trim();
        return cerrar(politicaCancelacion, EstadoReserva.CANCELADA, detalle, hoy);
    }

    public double cancelarPorAgencia(String motivo, LocalDate hoy) throws ReglaNegocioException {
        validarCancelable(hoy);
        return cerrar(POLITICA_AGENCIA, EstadoReserva.CANCELADA, "Cancelación por la agencia: " + motivo, hoy);
    }

    private void validarCancelable(LocalDate hoy) throws ReglaNegocioException {
        if (estado != EstadoReserva.PENDIENTE && estado != EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("No se puede cancelar una reserva en estado " + estado + ".");
        }
        if (!hoy.isBefore(salida.getFechaInicio())) {
            throw new ReglaNegocioException("La salida ya comenzó: la reserva no puede cancelarse.");
        }
    }

    private double cerrar(PoliticaCancelacion politica, EstadoReserva nuevoEstado, String motivo, LocalDate hoy) {
        double pagado = getTotalNetoPagado();
        double devolucion = Dinero.redondear(politica.calcularDevolucion(pagado, diasHastaLaSalida(hoy)));
        double retenido = Dinero.redondear(pagado - devolucion);
        this.estado = nuevoEstado;
        notasDeAjuste.add(new NotaDeAjuste(notasDeAjuste.size() + 1, devolucion, retenido, hoy, motivo, mediosUsados()));
        salida.actualizarEstadoSegunOcupacion();
        salida.invalidarLista();
        return devolucion;
    }

    public double reemplazarPasajero(Pasajero viejo, Pasajero nuevo, LocalDate hoy) throws ReglaNegocioException {
        verificarVigente();
        if (!hoy.isBefore(salida.getFechaInicio())) {
            throw new ReglaNegocioException("La salida ya comenzó: no se puede modificar la reserva.");
        }
        int idx = pasajeros.indexOf(viejo);
        if (idx < 0) throw new ReglaNegocioException("El pasajero a reemplazar no pertenece a la reserva.");
        if (diasHastaLaSalida(hoy) < DIAS_LIMITE_REEMPLAZO) {
            throw new ReglaNegocioException("Faltan menos de " + DIAS_LIMITE_REEMPLAZO
                    + " días para la salida: ya no se puede reemplazar un pasajero sin cargo.");
        }
        validarPasajeroParaTour(nuevo);
        if (pasajeros.contains(nuevo)) {
            throw new ReglaNegocioException("El nuevo pasajero (documento " + nuevo.getDocumento() + ") ya está en la reserva.");
        }
        for (ServicioAdicional s : viejo.getServicios()) nuevo.agregarServicio(s);
        if (salida.getTour().requiereSeguro() && !nuevo.tieneSeguro()) {
            throw new ReglaNegocioException("El seguro de viaje es obligatorio en tours de aventura.");
        }
        List<Pasajero> tentativa = new ArrayList<>(pasajeros);
        tentativa.set(idx, nuevo);
        double nuevoTotal = calculadora.calcularTotal(tentativa, salida.getTour(), salida.getFechaInicio(), fechaCreacion);

        double totalAnterior = this.total;
        pasajeros.set(idx, nuevo);
        pasajerosReemplazados.add(viejo);
        this.total = nuevoTotal;
        double diferencia = Dinero.redondear(nuevoTotal - totalAnterior);
        double exceso = Dinero.redondear(getTotalNetoPagado() - this.total);
        if (diferencia < 0 && exceso > TOLERANCIA) {
            notasDeAjuste.add(new NotaDeAjuste(notasDeAjuste.size() + 1, exceso, 0.0, hoy,
                    "Diferencia tarifaria a favor del cliente por reemplazo de pasajero", mediosUsados()));
        }
        salida.invalidarLista();
        return diferencia;
    }

    void marcarFinalizada() {
        if (estado == EstadoReserva.CONFIRMADA) estado = EstadoReserva.FINALIZADA;
    }


    private void verificarVigente() throws ReglaNegocioException {
        if (estado != EstadoReserva.PENDIENTE && estado != EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("La reserva está " + estado + " y no admite modificaciones.");
        }
    }

    public long diasHastaLaSalida(LocalDate hoy) {
        return ChronoUnit.DAYS.between(hoy, salida.getFechaInicio());
    }

    private String mediosUsados() {
        if (pagos.isEmpty()) return "sin pagos registrados";
        Set<MedioPago> medios = new LinkedHashSet<>();
        for (Pago p : pagos) medios.add(p.getMedioPago());
        return medios.stream().map(Enum::name).collect(Collectors.joining(", "));
    }

    public double getTotalPagado() { return Dinero.redondear(pagos.stream().mapToDouble(Pago::getMonto).sum()); }

    public double getTotalDevuelto() {
        return Dinero.redondear(notasDeAjuste.stream().mapToDouble(NotaDeAjuste::getMontoDevuelto).sum());
    }

    public double getTotalNetoPagado() { return Dinero.redondear(getTotalPagado() - getTotalDevuelto()); }

    public double getSaldoPendiente() { return Math.max(0.0, Dinero.redondear(total - getTotalNetoPagado())); }

    public double getTotalAdicionales() { return pasajeros.stream().mapToDouble(Pasajero::getTotalServicios).sum(); }

    public int getCantidadLugaresOcupados() { return pasajeros.size(); }

    public boolean ocupaLugar() { return estado != EstadoReserva.CANCELADA && estado != EstadoReserva.ANULADA; }

    public int getNumero() { return numero; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public EstadoReserva getEstado() { return estado; }
    public double getTotal() { return total; }
    public Temporada getTemporada() { return temporada; }
    public Cliente getCliente() { return cliente; }
    public Salida getSalida() { return salida; }
    public Vendedor getVendedor() { return vendedor; }
    public PoliticaCancelacion getPoliticaCancelacion() { return politicaCancelacion; }
    public List<Pasajero> getPasajeros() { return new ArrayList<>(pasajeros); }
    public List<Pasajero> getPasajerosReemplazados() { return new ArrayList<>(pasajerosReemplazados); }
    public List<Pago> getPagos() { return new ArrayList<>(pagos); }
    public List<NotaDeAjuste> getNotasDeAjuste() { return new ArrayList<>(notasDeAjuste); }

    @Override
    public String toString() {
        return "Reserva #" + numero + " [" + estado + "] " + cliente.getNombre() + " - " + salida.getTour().getNombre()
                + " (" + salida.getFechaInicio() + ") - " + pasajeros.size() + " pax - total " + Dinero.formatear(total)
                + " - saldo " + Dinero.formatear(getSaldoPendiente());
    }
}
