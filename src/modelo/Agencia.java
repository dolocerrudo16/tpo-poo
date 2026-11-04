package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Agencia implements Serializable {
    private static final long serialVersionUID = 2L;

    private final Map<String, Tour> tours = new LinkedHashMap<>();
    private final List<Salida> salidas = new ArrayList<>();
    private final Map<String, Cliente> clientes = new LinkedHashMap<>();
    private final Map<Integer, Empleado> personal = new TreeMap<>();
    private final Map<String, Vehiculo> vehiculos = new LinkedHashMap<>();
    private final Map<String, Proveedor> proveedores = new LinkedHashMap<>();
    private final List<ServicioAdicional> servicios = new ArrayList<>();
    private final List<LiquidacionGuia> liquidaciones = new ArrayList<>();
    private final CalculadoraTarifa calculadora = new CalculadoraTarifa();
    private final Reloj reloj = new Reloj();
    private PoliticaCancelacion politicaCancelacion = new CancelacionFlexible();
    private int proximaSalida = 1;
    private int proximaReserva = 1;
    private int proximoCliente = 1;
    private int proximoServicio = 1;
    private int proximaLiquidacion = 1;

    public Agencia() {
        try {
            registrarServicio("Seguro de viaje", 5000, TipoServicio.SEGURO_VIAJE);
            registrarServicio("Traslado al aeropuerto", 8000, TipoServicio.TRASLADO);
            registrarServicio("Excursión opcional", 12000, TipoServicio.EXCURSION);
        } catch (ReglaNegocioException e) {
            throw new IllegalStateException("Catálogo de servicios inicial inválido", e);
        }
    }


    public LocalDate hoy() { return reloj.hoy(); }
    public Reloj getReloj() { return reloj; }
    public CalculadoraTarifa getCalculadora() { return calculadora; }
    public PoliticaCancelacion getPoliticaCancelacion() { return politicaCancelacion; }

    public void setPoliticaCancelacion(PoliticaCancelacion politica) throws ReglaNegocioException {
        if (politica == null) throw new ReglaNegocioException("La política de cancelación es obligatoria.");
        this.politicaCancelacion = politica;
    }


    public void agregarTour(Tour tour) throws ReglaNegocioException {
        if (tour == null) throw new ReglaNegocioException("El tour es obligatorio.");
        if (tours.containsKey(tour.getCodigo())) {
            throw new ReglaNegocioException("Ya existe un tour con el código " + tour.getCodigo() + ".");
        }
        tours.put(tour.getCodigo(), tour);
    }

    public Proveedor registrarProveedor(String nombre, String cuit, String contacto, String servicio)
            throws ReglaNegocioException {
        Proveedor nuevo = new Proveedor(nombre, cuit, contacto, servicio);
        if (proveedores.containsKey(nuevo.getCuit())) {
            throw new ReglaNegocioException("El CUIT ya está registrado: " + proveedores.get(nuevo.getCuit()));
        }
        proveedores.put(nuevo.getCuit(), nuevo);
        return nuevo;
    }

    public Guia registrarGuia(Guia guia) throws ReglaNegocioException {
        if (guia == null) throw new ReglaNegocioException("El guía es obligatorio.");
        if (guia.getCategoriasHabilitadas().isEmpty()) {
            throw new ReglaNegocioException("El guía debe estar habilitado para al menos una categoría de tour.");
        }
        registrarEmpleado(guia);
        return guia;
    }

    public Empleado registrarEmpleado(Empleado empleado) throws ReglaNegocioException {
        if (empleado == null) throw new ReglaNegocioException("El empleado es obligatorio.");
        if (personal.containsKey(empleado.getLegajo())) {
            throw new ReglaNegocioException("El legajo ya está registrado: " + personal.get(empleado.getLegajo()));
        }
        personal.put(empleado.getLegajo(), empleado);
        return empleado;
    }

    public Vehiculo registrarVehiculo(String patente, int capacidad, TipoVehiculo tipo) throws ReglaNegocioException {
        Vehiculo nuevo = new Vehiculo(patente, capacidad, tipo);
        if (vehiculos.containsKey(nuevo.getPatente())) {
            throw new ReglaNegocioException("La patente ya está registrada: " + vehiculos.get(nuevo.getPatente()));
        }
        vehiculos.put(nuevo.getPatente(), nuevo);
        return nuevo;
    }

    public ServicioAdicional registrarServicio(String descripcion, double precio, TipoServicio tipo)
            throws ReglaNegocioException {
        ServicioAdicional s = new ServicioAdicional(proximoServicio, descripcion, precio, tipo);
        proximoServicio++;
        servicios.add(s);
        return s;
    }

    public void eliminarGuia(int legajo) throws ReglaNegocioException {
        Empleado e = personal.get(legajo);
        if (!(e instanceof Guia)) throw new ReglaNegocioException("No existe un guía con legajo " + legajo + ".");
        for (Salida s : salidas) {
            if (s.getGuiaAsignado() != null && s.getGuiaAsignado().getLegajo() == legajo && salidaActiva(s)) {
                throw new ReglaNegocioException("El guía está asignado a la salida #" + s.getIdSalida() + " (" + s.getEstado() + ").");
            }
        }
        personal.remove(legajo);
    }

    public void eliminarVehiculo(String patente) throws ReglaNegocioException {
        String clave = patente == null ? "" : patente.trim().toUpperCase();
        if (!vehiculos.containsKey(clave)) throw new ReglaNegocioException("No existe un vehículo con patente " + clave + ".");
        for (Salida s : salidas) {
            if (s.getVehiculo() != null && s.getVehiculo().getPatente().equals(clave) && salidaActiva(s)) {
                throw new ReglaNegocioException("El vehículo está asignado a la salida #" + s.getIdSalida() + " (" + s.getEstado() + ").");
            }
        }
        vehiculos.remove(clave);
    }

    public void eliminarProveedor(String cuit) throws ReglaNegocioException {
        String clave = Proveedor.normalizarCuit(cuit);
        if (!proveedores.containsKey(clave)) throw new ReglaNegocioException("No existe un proveedor con ese CUIT.");
        for (Tour t : tours.values()) {
            for (Actividad a : t.getItinerario()) {
                if (a.getProveedor() != null && a.getProveedor().getCuit().equals(clave)) {
                    throw new ReglaNegocioException("El proveedor está asociado a una actividad del tour " + t.getCodigo() + ".");
                }
            }
        }
        proveedores.remove(clave);
    }

    private boolean salidaActiva(Salida s) {
        return s.getEstado() != EstadoSalida.FINALIZADA && s.getEstado() != EstadoSalida.CANCELADA;
    }


    public Salida programarSalida(String codigoTour, LocalDate inicio, LocalDate fin, int cupoMaximo)
            throws ReglaNegocioException {
        Tour tour = buscarTour(codigoTour);
        if (tour == null) throw new ReglaNegocioException("No existe el tour " + codigoTour + ".");
        Salida nueva = new Salida(proximaSalida, inicio, fin, cupoMaximo, tour, hoy());
        proximaSalida++;
        salidas.add(nueva);
        return nueva;
    }


    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String pais)
            throws ReglaNegocioException {
        Cliente nuevo = new Cliente(proximoCliente, nombre, documento, telefono, email, pais);
        if (clientes.containsKey(nuevo.getDocumento())) {
            throw new ReglaNegocioException("Ya existe un cliente con ese documento: " + clientes.get(nuevo.getDocumento()));
        }
        proximoCliente++;
        clientes.put(nuevo.getDocumento(), nuevo);
        return nuevo;
    }

    public int getProximoNumeroReserva() { return proximaReserva; }

    public void avanzarNumeroReserva() { proximaReserva++; }


    public List<String> procesarVencimientos(LocalDate hoy) throws ReglaNegocioException {
        List<String> log = new ArrayList<>();
        for (Salida s : salidas) {
            if (!salidaActiva(s) || s.getEstado() == EstadoSalida.EN_CURSO) continue;
            for (Reserva r : s.getReservas()) {
                if (r.anularSiVencido(hoy)) {
                    log.add("Reserva #" + r.getNumero() + " ANULADA por saldo impago (faltan menos de "
                            + Reserva.DIAS_LIMITE_SALDO + " días para la salida #" + s.getIdSalida() + ").");
                }
            }
            if (s.cancelarSiNoAlcanzaCupoMinimo(hoy)) {
                log.add("Salida #" + s.getIdSalida() + " CANCELADA por no alcanzar el cupo mínimo; se devolvió el 100% a sus reservas.");
            }
        }
        return log;
    }


    public List<Salida> salidasPendientesDeLiquidar(Guia guia) {
        return salidas.stream()
                .filter(s -> s.getEstado() == EstadoSalida.FINALIZADA && !s.isLiquidada() && s.getGuiaAsignado() != null)
                .filter(s -> guia == null || s.getGuiaAsignado().getLegajo() == guia.getLegajo())
                .collect(Collectors.toList());
    }

    public double calcularHonorarioSalida(Salida s) {
        return s.getGuiaAsignado().calcularHonorario(s.getTour().getDuracionDias(), s.calcularTotalAdicionales());
    }

    public LiquidacionGuia liquidarHonorarios(Guia guia, List<Salida> aLiquidar, LocalDate hoy) throws ReglaNegocioException {
        if (guia == null) throw new ReglaNegocioException("El guía es obligatorio.");
        if (aLiquidar == null || aLiquidar.isEmpty()) throw new ReglaNegocioException("Debe elegir al menos una salida a liquidar.");
        if (guia.getHonorarioPorDia() <= 0) {
            throw new ReglaNegocioException("El guía no tiene honorario diario definido: complételo antes de liquidar.");
        }
        for (Salida s : aLiquidar) {   // se valida todo antes de marcar nada
            if (s.getEstado() != EstadoSalida.FINALIZADA) {
                throw new ReglaNegocioException("La salida #" + s.getIdSalida() + " no está FINALIZADA.");
            }
            if (s.isLiquidada()) {
                throw new ReglaNegocioException("La salida #" + s.getIdSalida() + " ya fue liquidada el " + s.getFechaLiquidacion() + ".");
            }
            if (s.getGuiaAsignado() == null || s.getGuiaAsignado().getLegajo() != guia.getLegajo()) {
                throw new ReglaNegocioException("La salida #" + s.getIdSalida() + " no fue guiada por " + guia.getNombre() + ".");
            }
        }
        LiquidacionGuia liq = new LiquidacionGuia(proximaLiquidacion, hoy, guia);
        for (Salida s : aLiquidar) {
            double adicionales = s.calcularTotalAdicionales();
            int dias = s.getTour().getDuracionDias();
            liq.agregarItem(new LiquidacionGuia.Item(s.getIdSalida(), s.getTour().getNombre(), dias,
                    guia.getHonorarioPorDia(), adicionales, guia.calcularHonorario(dias, adicionales)));
            s.marcarLiquidada(hoy);
        }
        proximaLiquidacion++;
        liquidaciones.add(liq);
        return liq;
    }


    public Tour buscarTour(String codigo) { return codigo == null ? null : tours.get(codigo.trim().toUpperCase()); }

    public Salida buscarSalida(int id) {
        return salidas.stream().filter(s -> s.getIdSalida() == id).findFirst().orElse(null);
    }

    public Cliente buscarCliente(String documento) { return documento == null ? null : clientes.get(documento.trim()); }

    public Reserva buscarReserva(int numero) {
        return getReservas().stream().filter(r -> r.getNumero() == numero).findFirst().orElse(null);
    }

    public List<Reserva> getReservas() {
        List<Reserva> todas = new ArrayList<>();
        for (Salida s : salidas) todas.addAll(s.getReservas());
        todas.sort(Comparator.comparingInt(Reserva::getNumero));
        return todas;
    }

    public ServicioAdicional getSeguroViaje() throws ReglaNegocioException {
        return servicios.stream().filter(ServicioAdicional::isSeguroViaje).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("No hay un seguro de viaje definido en el catálogo de servicios."));
    }

    public List<Tour> getTours() { return new ArrayList<>(tours.values()); }
    public List<Salida> getSalidas() { return new ArrayList<>(salidas); }
    public List<Proveedor> getProveedores() { return new ArrayList<>(proveedores.values()); }
    public List<Vehiculo> getVehiculos() { return new ArrayList<>(vehiculos.values()); }
    public List<ServicioAdicional> getServicios() { return new ArrayList<>(servicios); }
    public List<LiquidacionGuia> getLiquidaciones() { return new ArrayList<>(liquidaciones); }

    public List<Cliente> getClientes() {
        List<Cliente> lista = new ArrayList<>(clientes.values());
        lista.sort(Comparator.comparing(Cliente::getNombre, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    public List<Empleado> getPersonal() { return new ArrayList<>(personal.values()); }

    public List<Guia> getGuias() {
        return personal.values().stream().filter(e -> e instanceof Guia).map(e -> (Guia) e).collect(Collectors.toList());
    }

    public List<Vendedor> getVendedores() {
        return personal.values().stream().filter(e -> e instanceof Vendedor).map(e -> (Vendedor) e).collect(Collectors.toList());
    }
}
