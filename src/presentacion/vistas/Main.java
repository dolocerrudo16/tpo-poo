package presentacion.vistas;

import excepciones.ReglaNegocioException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modelo.*;
import persistencia.GestorPersistencia;
import presentacion.controladores.ControladorAdministracion;
import presentacion.controladores.ControladorOperaciones;
import presentacion.controladores.ControladorReserva;
import reportes.Reportes;

/**
 * Interfaz por consola con menú. Un menú por rol (Administrador, Coordinador de operaciones y Vendedor)
 * más utilidades. Los datos se guardan automáticamente en datos/agencia.dat después de cada operación.
 * Supuesto: no hay autenticación; el rol se elige desde el menú principal.
 */
public class Main {
    private static final Path ARCHIVO = Paths.get("datos", "agencia.dat");

    /** Operación de menú que puede fallar por una regla de negocio. */
    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws ReglaNegocioException;
    }

    private final Consola consola = new Consola();
    private Agencia agencia;
    private ControladorReserva ventas;
    private ControladorOperaciones operaciones;
    private ControladorAdministracion administracion;

    public static void main(String[] args) {
        new Main().iniciar();
    }

    private void iniciar() {
        agencia = cargarDatos();
        ventas = new ControladorReserva(agencia);
        operaciones = new ControladorOperaciones(agencia);
        administracion = new ControladorAdministracion(agencia);
        try {
            menuPrincipal();
        } catch (FinDeEntradaException e) {
            consola.info("\nSe cerró la entrada de datos. Saliendo...");
        } finally {
            guardar();
        }
    }

    // ------------------------------------------------------------------ persistencia

    private Agencia cargarDatos() {
        try {
            Agencia cargada = GestorPersistencia.cargar(ARCHIVO);
            if (Files.exists(ARCHIVO)) consola.info("Datos cargados desde " + ARCHIVO + ".");
            return cargada;
        } catch (IOException | ClassNotFoundException e) {
            consola.error("No se pudo leer " + ARCHIVO + " (" + e.getMessage() + ").");
            try {   // se conserva una copia del archivo ilegible para no perderlo al guardar de nuevo
                Path copia = Paths.get(ARCHIVO + ".corrupto");
                Files.move(ARCHIVO, copia, StandardCopyOption.REPLACE_EXISTING);
                consola.info("Se guardó una copia en " + copia + " y se inicia con datos vacíos.");
            } catch (IOException ex) {
                consola.error("No se pudo respaldar el archivo: " + ex.getMessage());
            }
            return new Agencia();
        }
    }

    private void guardar() {
        try {
            GestorPersistencia.guardar(agencia, ARCHIVO);
        } catch (IOException e) {
            consola.error("No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    /** Ejecuta una operación mostrando los errores de negocio y guarda los datos al terminar. */
    private void correr(Accion accion) {
        try {
            accion.ejecutar();
        } catch (ReglaNegocioException e) {
            consola.error(e.getMessage());
        } catch (FinDeEntradaException e) {
            throw e;
        } catch (RuntimeException e) {
            consola.error("Error inesperado: " + e);
        } finally {
            guardar();
        }
    }

    // ------------------------------------------------------------------ menú principal

    private void menuPrincipal() {
        while (true) {
            consola.titulo("AGENCIA RUMBO SUR  -  fecha del sistema: " + agencia.hoy().format(Consola.FORMATO_FECHA)
                    + (agencia.getReloj().estaSimulada() ? " (simulada)" : ""));
            consola.info("1. Administrador\n2. Coordinador de operaciones\n3. Vendedor\n4. Utilidades (fecha, datos de prueba)\n0. Guardar y salir");
            switch (consola.leerEntero("Opción", 0, 4)) {
                case 1: menuAdministrador(); break;
                case 2: menuCoordinador(); break;
                case 3: menuVendedor(); break;
                case 4: menuUtilidades(); break;
                default: return;
            }
        }
    }

    // ------------------------------------------------------------------ utilidades

    private void menuUtilidades() {
        while (true) {
            consola.titulo("UTILIDADES");
            consola.info("1. Fijar fecha simulada del sistema\n2. Volver a la fecha real\n3. Cargar datos de prueba\n0. Volver");
            switch (consola.leerEntero("Opción", 0, 3)) {
                case 1: correr(() -> {
                    agencia.getReloj().fijar(consola.leerFecha("Nueva fecha del sistema"));
                    consola.info("Fecha del sistema: " + agencia.hoy().format(Consola.FORMATO_FECHA));
                }); break;
                case 2: correr(() -> {
                    agencia.getReloj().usarFechaReal();
                    consola.info("Se usa la fecha real: " + agencia.hoy().format(Consola.FORMATO_FECHA));
                }); break;
                case 3: correr(() -> {
                    if (!agencia.getTours().isEmpty() || !agencia.getPersonal().isEmpty()) {
                        consola.info("Ya hay datos cargados: los datos de prueba solo se cargan en un sistema vacío.");
                        return;
                    }
                    DatosDemo.cargar(agencia);
                    consola.info("Datos de prueba cargados (3 tours, personal, flota, 1 cliente y 2 salidas).");
                }); break;
                default: return;
            }
        }
    }

    // ------------------------------------------------------------------ administrador

    private void menuAdministrador() {
        while (true) {
            consola.titulo("ADMINISTRADOR");
            consola.info("1. Alta de tour (CU01)\n2. Agregar actividad o tarifa a un tour existente (CU01)\n3. Listar tours\n"
                    + "4. Alta de proveedor (CU02)\n5. Alta de guía (CU02)\n6. Alta de vehículo (CU02)\n7. Alta de vendedor (CU02)\n"
                    + "8. Baja de guía / vehículo / proveedor (CU02)\n9. Listar recursos\n10. Liquidar honorarios de guías (CU10)\n"
                    + "11. Reportes de gestión (CU11)\n12. Política de cancelación vigente\n0. Volver");
            switch (consola.leerEntero("Opción", 0, 12)) {
                case 1: correr(this::altaTour); break;
                case 2: correr(this::modificarTour); break;
                case 3: correr(this::listarTours); break;
                case 4: correr(this::altaProveedor); break;
                case 5: correr(this::altaGuia); break;
                case 6: correr(this::altaVehiculo); break;
                case 7: correr(this::altaVendedor); break;
                case 8: correr(this::bajaRecurso); break;
                case 9: correr(this::listarRecursos); break;
                case 10: correr(this::liquidarHonorarios); break;
                case 11: correr(this::menuReportes); break;
                case 12: correr(this::cambiarPolitica); break;
                default: return;
            }
        }
    }

    private void altaTour() throws ReglaNegocioException {
        consola.titulo("ALTA DE TOUR");
        CategoriaTour categoria = consola.elegir("Categoría", Arrays.asList(CategoriaTour.values()), Enum::name);
        if (categoria == null) return;
        String codigo = consola.leerTexto("Código");
        String nombre = consola.leerTexto("Nombre");
        String descripcion = consola.leerTexto("Descripción");
        int dias = consola.leerEntero("Duración en días", 1, 60);
        int cupoMin = consola.leerEntero("Cupo mínimo", 1, 500);
        int cupoMax = consola.leerEntero("Cupo máximo", 1, 500);
        Tour tour;
        switch (categoria) {
            case AVENTURA:
                String dificultad = consola.leerTexto("Nivel de dificultad (obligatorio en aventura)");
                int edad = consola.leerEntero("Edad mínima (obligatoria en aventura)", 0, 99);
                tour = new TourAventura(codigo, nombre, descripcion, dias, cupoMin, cupoMax, dificultad, edad);
                break;
            case CULTURAL:
                tour = new TourCultural(codigo, nombre, descripcion, dias, cupoMin, cupoMax);
                break;
            default:
                tour = new TourNaturaleza(codigo, nombre, descripcion, dias, cupoMin, cupoMax);
        }
        administracion.altaTour(tour);   // valida que el código no exista
        consola.info("Tour creado. Cargue su itinerario y sus tarifas.");
        cargarActividades(tour);
        cargarTarifas(tour);
        consola.info("Tour guardado en el catálogo: " + tour);
    }

    private void modificarTour() throws ReglaNegocioException {
        Tour tour = consola.elegir("Tour a modificar", agencia.getTours(), Tour::toString);
        if (tour == null) return;
        consola.info("1. Agregar actividades\n2. Agregar tarifas\n3. Cambiar descripción");
        switch (consola.leerEntero("Opción", 1, 3)) {
            case 1: cargarActividades(tour); break;
            case 2: cargarTarifas(tour); break;
            default: tour.setDescripcion(consola.leerTexto("Nueva descripción"));
        }
    }

    private void cargarActividades(Tour tour) {
        while (consola.confirmar("¿Agregar una actividad al itinerario?")) {
            try {
                int dia = consola.leerEntero("Día", 1, tour.getDuracionDias());
                int orden = consola.leerEntero("Orden dentro del día", 1, 50);
                String horario = consola.leerTexto("Horario (ej. 09:30)");
                String descripcion = consola.leerTexto("Descripción");
                String lugar = consola.leerTexto("Lugar");
                Proveedor proveedor = null;
                if (!agencia.getProveedores().isEmpty() && consola.confirmar("¿Incluye un proveedor externo?")) {
                    proveedor = consola.elegir("Proveedor", agencia.getProveedores(), Proveedor::toString);
                }
                administracion.agregarActividad(tour, new Actividad(dia, orden, horario, descripcion, lugar, proveedor));
                consola.info("Actividad agregada.");
            } catch (ReglaNegocioException e) {
                consola.error(e.getMessage());
            }
        }
    }

    private void cargarTarifas(Tour tour) {
        while (consola.confirmar("¿Agregar una tarifa por temporada?")) {
            try {
                Temporada temporada = consola.elegir("Temporada", Arrays.asList(Temporada.values()), Enum::name);
                if (temporada == null) continue;
                LocalDate desde = consola.leerFecha("Vigente desde");
                LocalDate hasta = consola.leerFecha("Vigente hasta");
                double valor = consola.leerDecimal("Tarifa base por pasajero", 0.01);
                administracion.agregarTarifa(tour, new TarifaTemporada(temporada, desde, hasta, valor));
                consola.info("Tarifa agregada.");
            } catch (ReglaNegocioException e) {
                consola.error(e.getMessage());
            }
        }
    }

    private void listarTours() {
        consola.titulo("CATÁLOGO DE TOURS");
        if (agencia.getTours().isEmpty()) consola.info("No hay tours cargados.");
        for (Tour t : agencia.getTours()) {
            consola.info(t.toString());
            for (Actividad a : t.getItinerario()) consola.info("    " + a);
            for (TarifaTemporada tf : t.getTarifas()) consola.info("    Tarifa: " + tf);
        }
    }

    private void altaProveedor() throws ReglaNegocioException {
        consola.titulo("ALTA DE PROVEEDOR");
        Proveedor p = administracion.altaProveedor(consola.leerTexto("Nombre"), consola.leerTexto("CUIT (11 dígitos)"),
                consola.leerTexto("Contacto"), consola.leerTexto("Servicio prestado"));
        consola.info("Proveedor registrado: " + p);
    }

    private void altaGuia() throws ReglaNegocioException {
        consola.titulo("ALTA DE GUÍA");
        int legajo = consola.leerEntero("Legajo", 1, 999999);
        String nombre = consola.leerTexto("Nombre");
        String documento = consola.leerTexto("Documento");
        String telefono = consola.leerTexto("Teléfono");
        double honorario = consola.leerDecimal("Honorario por día", 0);
        Guia guia = new Guia(legajo, nombre, documento, telefono, honorario);
        for (String idioma : consola.leerTexto("Idiomas separados por coma").split(",")) guia.agregarIdioma(idioma);
        for (CategoriaTour c : consola.elegirVarios("Categorías para las que está habilitado",
                Arrays.asList(CategoriaTour.values()), Enum::name)) {
            guia.habilitarCategoria(c);
        }
        consola.info("Guía registrado: " + administracion.altaGuia(guia));
    }

    private void altaVehiculo() throws ReglaNegocioException {
        consola.titulo("ALTA DE VEHÍCULO");
        String patente = consola.leerTexto("Patente");
        int capacidad = consola.leerEntero("Capacidad de asientos", 0, 200);
        TipoVehiculo tipo = consola.elegir("Tipo", Arrays.asList(TipoVehiculo.values()), Enum::name);
        if (tipo == null) return;
        consola.info("Vehículo registrado: " + administracion.altaVehiculo(patente, capacidad, tipo));
    }

    private void altaVendedor() throws ReglaNegocioException {
        consola.titulo("ALTA DE VENDEDOR");
        Vendedor v = new Vendedor(consola.leerEntero("Legajo", 1, 999999), consola.leerTexto("Nombre"),
                consola.leerTexto("Documento"), consola.leerTexto("Teléfono"), Vendedor.COMISION_POR_DEFECTO);
        consola.info("Vendedor registrado: " + administracion.altaVendedor(v) + " (comisión estimada 3%)");
    }

    private void bajaRecurso() throws ReglaNegocioException {
        consola.info("1. Guía\n2. Vehículo\n3. Proveedor");
        switch (consola.leerEntero("Opción", 1, 3)) {
            case 1:
                Guia g = consola.elegir("Guía a dar de baja", agencia.getGuias(), Guia::toString);
                if (g != null) { administracion.bajaGuia(g.getLegajo()); consola.info("Guía dado de baja."); }
                break;
            case 2:
                Vehiculo v = consola.elegir("Vehículo a dar de baja", agencia.getVehiculos(), Vehiculo::toString);
                if (v != null) { administracion.bajaVehiculo(v.getPatente()); consola.info("Vehículo dado de baja."); }
                break;
            default:
                Proveedor p = consola.elegir("Proveedor a dar de baja", agencia.getProveedores(), Proveedor::toString);
                if (p != null) { administracion.bajaProveedor(p.getCuit()); consola.info("Proveedor dado de baja."); }
        }
    }

    private void listarRecursos() {
        consola.titulo("RECURSOS");
        consola.info("Personal:");
        agencia.getPersonal().forEach(e -> consola.info("  " + e));
        consola.info("Vehículos:");
        agencia.getVehiculos().forEach(v -> consola.info("  " + v));
        consola.info("Proveedores:");
        agencia.getProveedores().forEach(p -> consola.info("  " + p));
        consola.info("Servicios adicionales:");
        agencia.getServicios().forEach(s -> consola.info("  " + s));
        consola.info("Política de cancelación vigente: " + agencia.getPoliticaCancelacion().getNombre());
    }

    private void cambiarPolitica() throws ReglaNegocioException {
        consola.info("Política vigente: " + agencia.getPoliticaCancelacion().getNombre());
        consola.info("(Se aplica a las reservas que se creen desde ahora; las existentes conservan la suya.)");
        List<PoliticaCancelacion> opciones = Arrays.asList(new CancelacionFlexible(), new CancelacionEstricta());
        PoliticaCancelacion nueva = consola.elegir("Nueva política", opciones, PoliticaCancelacion::getNombre);
        if (nueva != null) {
            administracion.cambiarPoliticaCancelacion(nueva);
            consola.info("Política actualizada.");
        }
    }

    private void liquidarHonorarios() throws ReglaNegocioException {
        consola.titulo("LIQUIDACIÓN DE HONORARIOS DE GUÍAS");
        List<Guia> conPendientes = new ArrayList<>();
        for (Guia g : agencia.getGuias()) {
            if (!administracion.salidasPendientesDeLiquidar(g).isEmpty()) conPendientes.add(g);
        }
        Guia guia = consola.elegir("Guías con salidas finalizadas pendientes de liquidar", conPendientes,
                g -> g.getNombre() + " (" + administracion.salidasPendientesDeLiquidar(g).size() + " salida/s)");
        if (guia == null) return;
        List<Salida> elegidas = consola.elegirVarios("Salidas a liquidar", administracion.salidasPendientesDeLiquidar(guia),
                s -> "Salida #" + s.getIdSalida() + " " + s.getTour().getNombre() + " (" + s.getFechaInicio() + ") honorario "
                        + Consola.dinero(administracion.calcularHonorario(s)));
        if (elegidas.isEmpty()) return;
        if (consola.confirmar("¿Confirma la liquidación?")) {
            consola.info(administracion.liquidar(guia, elegidas).toString());
        }
    }

    private void menuReportes() throws ReglaNegocioException {
        consola.titulo("REPORTES DE GESTIÓN");
        consola.info("1. Ocupación por salida\n2. Ventas por vendedor\n3. Ingresos por tour\n4. Cancelaciones\n0. Volver");
        int tipo = consola.leerEntero("Opción", 0, 4);
        if (tipo == 0) return;
        LocalDate desde = consola.leerFecha("Desde");
        LocalDate hasta = consola.leerFecha("Hasta");
        String tour = consola.leerTexto("Código de tour (vacío = todos)");
        Reportes r = administracion.getReportes();
        switch (tipo) {
            case 1: imprimirOcupacion(r.ocupacionPorSalida(desde, hasta, tour)); break;
            case 2: {
                Vendedor v = null;
                if (consola.confirmar("¿Filtrar por un vendedor?")) {
                    v = consola.elegir("Vendedor", agencia.getVendedores(), Vendedor::toString);
                }
                imprimirVentas(r.ventasPorVendedor(desde, hasta, tour, v));
                break;
            }
            case 3: imprimirIngresos(r.ingresosPorTour(desde, hasta, tour)); break;
            default: imprimirCancelaciones(r.cancelaciones(desde, hasta, tour));
        }
    }

    private void imprimirOcupacion(List<Reportes.FilaOcupacion> filas) {
        if (filas.isEmpty()) { consola.info("No hay resultados para los filtros elegidos."); return; }
        System.out.printf("%-8s %-28s %-12s %-12s %10s %8s%n", "Salida", "Tour", "Inicio", "Estado", "Ocupación", "%");
        for (Reportes.FilaOcupacion f : filas) {
            System.out.printf("#%-7d %-28s %-12s %-12s %5d/%-4d %7.1f%%%n", f.idSalida(), f.tour(), f.fechaInicio(),
                    f.estado(), f.lugaresReservados(), f.cupoMaximo(), f.porcentajeOcupacion());
        }
    }

    private void imprimirVentas(List<Reportes.FilaVentas> filas) {
        if (filas.isEmpty()) { consola.info("No hay resultados para los filtros elegidos."); return; }
        System.out.printf("%-24s %9s %16s %18s%n", "Vendedor", "Reservas", "Monto vendido", "Comisión estimada");
        for (Reportes.FilaVentas f : filas) {
            System.out.printf("%-24s %9d %16s %18s%n", f.vendedor(), f.cantidadReservas(),
                    Consola.dinero(f.montoVendido()), Consola.dinero(f.comisionEstimada()));
        }
    }

    private void imprimirIngresos(List<Reportes.FilaIngresos> filas) {
        if (filas.isEmpty()) { consola.info("No hay resultados para los filtros elegidos."); return; }
        System.out.printf("%-30s %8s %18s%n", "Tour", "Pagos", "Ingresos cobrados");
        double total = 0;
        for (Reportes.FilaIngresos f : filas) {
            System.out.printf("%-30s %8d %18s%n", f.tour(), f.cantidadPagos(), Consola.dinero(f.ingresos()));
            total += f.ingresos();
        }
        consola.info("TOTAL: " + Consola.dinero(total));
    }

    private void imprimirCancelaciones(List<Reportes.FilaCancelacion> filas) {
        if (filas.isEmpty()) { consola.info("No hay resultados para los filtros elegidos."); return; }
        double devuelto = 0;
        double retenido = 0;
        for (Reportes.FilaCancelacion f : filas) {
            consola.info("Reserva #" + f.numeroReserva() + " " + f.estado() + " (" + f.fecha() + ") " + f.tour()
                    + " - devuelto " + Consola.dinero(f.importeDevuelto()) + ", retenido " + Consola.dinero(f.importeRetenido())
                    + " - " + f.motivo());
            devuelto += f.importeDevuelto();
            retenido += f.importeRetenido();
        }
        consola.info("Cantidad: " + filas.size() + " - Total devuelto: " + Consola.dinero(devuelto)
                + " - Total retenido: " + Consola.dinero(retenido));
    }

    // ------------------------------------------------------------------ coordinador

    private void menuCoordinador() {
        while (true) {
            consola.titulo("COORDINADOR DE OPERACIONES");
            consola.info("1. Programar salida (CU03)\n2. Listar salidas\n3. Asignar guía y vehículo (CU04)\n"
                    + "4. Procesar vencimientos del día (reservas impagas y cupo mínimo)\n5. Emitir lista de pasajeros\n"
                    + "6. Iniciar salida (CU09)\n7. Cerrar salida (CU09)\n0. Volver");
            switch (consola.leerEntero("Opción", 0, 7)) {
                case 1: correr(this::programarSalida); break;
                case 2: correr(this::listarSalidas); break;
                case 3: correr(this::asignarRecursos); break;
                case 4: correr(this::procesarVencimientos); break;
                case 5: correr(this::emitirLista); break;
                case 6: correr(() -> { Salida s = elegirSalida(null); if (s != null) { operaciones.iniciarSalida(s); consola.info("La salida #" + s.getIdSalida() + " está EN_CURSO."); } }); break;
                case 7: correr(() -> { Salida s = elegirSalida(null); if (s != null) { operaciones.cerrarSalida(s); consola.info("La salida #" + s.getIdSalida() + " quedó FINALIZADA."); } }); break;
                default: return;
            }
        }
    }

    private Salida elegirSalida(java.util.function.Predicate<Salida> filtro) {
        List<Salida> lista = new ArrayList<>();
        for (Salida s : agencia.getSalidas()) if (filtro == null || filtro.test(s)) lista.add(s);
        return consola.elegir("Salida", lista, Salida::toString);
    }

    private void programarSalida() throws ReglaNegocioException {
        consola.titulo("PROGRAMAR SALIDA");
        Tour tour = consola.elegir("Tour", agencia.getTours(), Tour::toString);
        if (tour == null) return;
        LocalDate inicio = consola.leerFecha("Fecha de inicio");
        LocalDate fin = consola.leerFecha("Fecha de fin (inclusive; un tour de " + tour.getDuracionDias() + " día/s termina el "
                + inicio.plusDays(tour.getDuracionDias() - 1L).format(Consola.FORMATO_FECHA) + ")");
        int cupo = consola.leerEntero("Cupo máximo", 1, 500);
        Salida s = operaciones.programarSalida(tour.getCodigo(), inicio, fin, cupo);
        consola.info("Salida creada en estado PROGRAMADA. Falta asignarle guía y vehículo.\n" + s);
    }

    private void listarSalidas() {
        consola.titulo("SALIDAS");
        if (agencia.getSalidas().isEmpty()) consola.info("No hay salidas programadas.");
        agencia.getSalidas().forEach(s -> consola.info(s.toString()));
    }

    private void asignarRecursos() throws ReglaNegocioException {
        Salida s = elegirSalida(null);
        if (s == null) return;
        Guia guia = consola.elegir("Guía (categoría del tour: " + s.getTour().getCategoria() + ")", agencia.getGuias(),
                g -> g.getNombre() + " - habilitado para " + g.getCategoriasHabilitadas());
        if (guia != null) {
            operaciones.asignarGuia(s, guia);
            consola.info("Guía asignado.");
        }
        Vehiculo v = consola.elegir("Vehículo (la capacidad debe cubrir el cupo de " + s.getCupoMaximo() + ")",
                agencia.getVehiculos(), Vehiculo::toString);
        if (v != null) {
            operaciones.asignarVehiculo(s, v);
            consola.info("Vehículo asignado.");
        }
        consola.info(s.toString());
    }

    private void procesarVencimientos() throws ReglaNegocioException {
        List<String> log = operaciones.procesarVencimientos();
        if (log.isEmpty()) consola.info("No hubo novedades: ninguna reserva vencida ni salida para cancelar.");
        log.forEach(consola::info);
    }

    private void emitirLista() throws ReglaNegocioException {
        Salida s = elegirSalida(null);
        if (s == null) return;
        List<Pasajero> lista = operaciones.emitirListaPasajeros(s);
        consola.titulo("LISTA DE PASAJEROS - Salida #" + s.getIdSalida() + " (versión " + s.getVersionLista() + ")");
        for (Pasajero p : lista) {
            consola.info(p.getNombre() + " | DNI " + p.getDocumento() + " | nac. " + p.getFechaNacimiento().format(Consola.FORMATO_FECHA)
                    + " | " + p.getTipo() + " | emergencia: " + p.getContactoEmergencia() + " | restricciones: " + p.getRestricciones()
                    + " | servicios: " + (p.getServicios().isEmpty() ? "ninguno" : p.getServicios()));
        }
    }

    // ------------------------------------------------------------------ vendedor

    private void menuVendedor() {
        while (true) {
            consola.titulo("VENDEDOR");
            consola.info("1. Registrar o actualizar cliente (CU05)\n2. Crear reserva (CU06)\n3. Registrar pago o seña (CU08)\n"
                    + "4. Reemplazar pasajero (CU12)\n5. Contratar servicio adicional a una reserva\n6. Cancelar reserva (CU13)\n"
                    + "7. Consultar una reserva\n8. Listar reservas (de mayor a menor total)\n9. Listar clientes\n0. Volver");
            switch (consola.leerEntero("Opción", 0, 9)) {
                case 1: correr(this::registrarCliente); break;
                case 2: correr(this::crearReserva); break;
                case 3: correr(this::registrarPago); break;
                case 4: correr(this::reemplazarPasajero); break;
                case 5: correr(this::contratarServicio); break;
                case 6: correr(this::cancelarReserva); break;
                case 7: correr(() -> { Reserva r = elegirReserva(); if (r != null) mostrarReserva(r); }); break;
                case 8: correr(() -> ventas.getReservasOrdenadasPorTotal().forEach(r -> consola.info(r.toString()))); break;
                case 9: correr(() -> agencia.getClientes().forEach(c -> consola.info(c.toString()))); break;
                default: return;
            }
        }
    }

    private void registrarCliente() throws ReglaNegocioException {
        consola.titulo("REGISTRAR CLIENTE");
        String documento = consola.leerTexto("Documento");
        Cliente existente = agencia.buscarCliente(documento);
        if (existente != null) {
            consola.info("Ya existe un cliente con ese documento: " + existente);
            if (consola.confirmar("¿Desea actualizar sus datos?")) {
                ventas.actualizarCliente(existente, consola.leerTexto("Nombre y apellido"), consola.leerTexto("Teléfono"),
                        consola.leerTexto("Correo electrónico"), consola.leerTexto("País de residencia"));
                consola.info("Cliente actualizado: " + existente);
            }
            return;
        }
        Cliente c = ventas.registrarCliente(consola.leerTexto("Nombre y apellido"), documento, consola.leerTexto("Teléfono"),
                consola.leerTexto("Correo electrónico"), consola.leerTexto("País de residencia (vacío = Argentina)"));
        consola.info("Cliente registrado: " + c);
    }

    /** Carga los datos de un pasajero repreguntando hasta que sean válidos para el tour de la salida. */
    private Pasajero cargarPasajero(Salida salida, List<Pasajero> yaCargados) {
        while (true) {
            try {
                String nombre = consola.leerTexto("Nombre y apellido del pasajero");
                String documento = consola.leerTexto("Documento");
                LocalDate nacimiento = consola.leerFecha("Fecha de nacimiento");
                TipoPasajero tipo = consola.elegir("Tipo de pasajero", Arrays.asList(TipoPasajero.values()), Enum::name);
                if (tipo == null) return null;
                String emergencia = consola.leerTexto("Contacto de emergencia");
                String restricciones = consola.leerTexto("Restricciones alimentarias o de salud (vacío = ninguna)");
                Pasajero p = new Pasajero(nombre, documento, nacimiento, tipo, emergencia, restricciones);
                if (!p.esTipoCoherente(salida.getFechaInicio())) {
                    throw new ReglaNegocioException("El tipo " + tipo + " no es coherente con la edad (" + p.calcularEdad(salida.getFechaInicio())
                            + " años). MENOR: menos de 18; ADULTO: 18 o más; JUBILADO: 60 o más.");
                }
                if (!salida.getTour().validarPasajero(p, salida.getFechaInicio())) {
                    throw new ReglaNegocioException("El pasajero no cumple los requisitos del tour (edad mínima).");
                }
                if (yaCargados.contains(p)) throw new ReglaNegocioException("Ese documento ya fue cargado en esta reserva.");
                return p;
            } catch (ReglaNegocioException e) {
                consola.error(e.getMessage());
                if (!consola.confirmar("¿Volver a cargar este pasajero?")) return null;
            }
        }
    }

    private void crearReserva() throws ReglaNegocioException {
        consola.titulo("CREAR RESERVA");
        String documento = consola.leerTexto("Documento del cliente titular");
        Cliente cliente = agencia.buscarCliente(documento);
        if (cliente == null) {
            consola.info("El cliente no está registrado. Se registra ahora (CU05).");
            cliente = ventas.registrarCliente(consola.leerTexto("Nombre y apellido"), documento, consola.leerTexto("Teléfono"),
                    consola.leerTexto("Correo electrónico"), consola.leerTexto("País de residencia (vacío = Argentina)"));
        }
        Salida salida = elegirSalida(s -> s.getEstado() == EstadoSalida.PROGRAMADA || s.getEstado() == EstadoSalida.CONFIRMADA);
        if (salida == null) return;
        Vendedor vendedor = consola.elegir("Vendedor que registra la reserva", agencia.getVendedores(), Vendedor::toString);
        if (vendedor == null) return;
        if (salida.getTour().requiereSeguro()) {
            consola.info("Tour de aventura: edad mínima " + ((TourAventura) salida.getTour()).getEdadMinima()
                    + " años y seguro de viaje obligatorio (el sistema lo agrega a cada pasajero).");
        }
        List<Pasajero> pasajeros = new ArrayList<>();
        do {
            consola.info("--- Pasajero " + (pasajeros.size() + 1) + " ---");
            Pasajero p = cargarPasajero(salida, pasajeros);
            if (p == null) break;
            List<ServicioAdicional> opcionales = new ArrayList<>(agencia.getServicios());
            if (salida.getTour().requiereSeguro()) opcionales.removeIf(ServicioAdicional::isSeguroViaje);
            if (!opcionales.isEmpty() && consola.confirmar("¿Contrata servicios adicionales para este pasajero?")) {
                consola.elegirVarios("Servicios adicionales", opcionales, ServicioAdicional::toString).forEach(p::agregarServicio);
            }
            pasajeros.add(p);
        } while (consola.confirmar("¿Agregar otro pasajero?"));
        Reserva r = ventas.crearReserva(cliente, salida, vendedor, pasajeros);
        consola.info("Reserva #" + r.getNumero() + " creada en estado " + r.getEstado() + " (temporada " + r.getTemporada() + ").");
        consola.info("Total: " + Consola.dinero(r.getTotal()) + " - Seña requerida (30%): " + Consola.dinero(r.getTotal() * Reserva.PORCENTAJE_SENA));
    }

    private Reserva elegirReserva() {
        if (agencia.getReservas().isEmpty()) {
            consola.info("No hay reservas registradas.");
            return null;
        }
        int numero = consola.leerEntero("Número de reserva", 1, 9999999);
        Reserva r = ventas.buscarReserva(numero);
        if (r == null) consola.error("No existe la reserva #" + numero + ".");
        return r;
    }

    private void mostrarReserva(Reserva r) {
        consola.info(r.toString());
        consola.info("  Cliente: " + r.getCliente() + " | Vendedor: " + r.getVendedor().getNombre() + " | Política: " + r.getPoliticaCancelacion().getNombre());
        for (Pasajero p : r.getPasajeros()) {
            consola.info("  Pasajero: " + p + (p.getServicios().isEmpty() ? "" : " - servicios " + p.getServicios()));
        }
        for (Pasajero p : r.getPasajerosReemplazados()) consola.info("  (Reemplazado) " + p);
        for (Pago p : r.getPagos()) consola.info("  " + p);
        for (NotaDeAjuste n : r.getNotasDeAjuste()) consola.info("  " + n);
    }

    private void registrarPago() throws ReglaNegocioException {
        consola.titulo("REGISTRAR PAGO");
        Reserva r = elegirReserva();
        if (r == null) return;
        mostrarReserva(r);
        double monto = consola.leerDecimal("Importe a pagar (saldo " + Consola.dinero(r.getSaldoPendiente()) + ")", 0.01);
        MedioPago medio = consola.elegir("Medio de pago", Arrays.asList(MedioPago.values()), Enum::name);
        if (medio == null) return;
        int cuotas = medio == MedioPago.TARJETA ? consola.leerEntero("Cantidad de cuotas (1 = un solo pago, sin recargo)", 1, 24) : 1;
        Pago pago = ventas.registrarPago(r, monto, medio, cuotas);
        consola.info("COMPROBANTE: " + pago);
        consola.info("Total cobrado al cliente: " + Consola.dinero(pago.getMontoCobrado()));
        consola.info("Estado de la reserva: " + r.getEstado() + " - saldo pendiente " + Consola.dinero(r.getSaldoPendiente()));
    }

    private void reemplazarPasajero() throws ReglaNegocioException {
        consola.titulo("REEMPLAZAR PASAJERO");
        Reserva r = elegirReserva();
        if (r == null) return;
        Pasajero viejo = consola.elegir("Pasajero a reemplazar", r.getPasajeros(), Pasajero::toString);
        if (viejo == null) return;
        consola.info("--- Datos del nuevo pasajero ---");
        List<Pasajero> otros = new ArrayList<>(r.getPasajeros());
        Pasajero nuevo = cargarPasajero(r.getSalida(), otros);
        if (nuevo == null) return;
        double dif = ventas.reemplazarPasajero(r, viejo, nuevo);
        if (dif == 0) consola.info("Reemplazo realizado sin cargo.");
        else if (dif > 0) consola.info("Reemplazo realizado. Diferencia a abonar: " + Consola.dinero(dif) + " (registrarla como pago complementario).");
        else consola.info("Reemplazo realizado. Se generó una nota de ajuste por " + Consola.dinero(-dif) + " a favor del cliente.");
        consola.info("Si la lista de pasajeros ya estaba emitida, deberá emitirse de nuevo.");
    }

    private void contratarServicio() throws ReglaNegocioException {
        Reserva r = elegirReserva();
        if (r == null) return;
        Pasajero p = consola.elegir("Pasajero", r.getPasajeros(), Pasajero::toString);
        if (p == null) return;
        ServicioAdicional s = consola.elegir("Servicio", agencia.getServicios(), ServicioAdicional::toString);
        if (s == null) return;
        ventas.contratarServicio(r, p, s);
        consola.info("Servicio contratado. Nuevo total: " + Consola.dinero(r.getTotal()));
    }

    private void cancelarReserva() throws ReglaNegocioException {
        consola.titulo("CANCELAR RESERVA");
        Reserva r = elegirReserva();
        if (r == null) return;
        mostrarReserva(r);
        long dias = r.diasHastaLaSalida(agencia.hoy());
        double aDevolver = r.getPoliticaCancelacion().calcularDevolucion(r.getTotalNetoPagado(), dias);
        consola.info("Faltan " + dias + " día(s) para la salida. Pagado: " + Consola.dinero(r.getTotalNetoPagado())
                + " - Devolución estimada: " + Consola.dinero(aDevolver));
        if (!consola.confirmar("¿Confirma la cancelación?")) {
            consola.info("La reserva se mantiene sin cambios.");
            return;
        }
        double devuelto = ventas.cancelarReserva(r, consola.leerTexto("Motivo"));
        consola.info("Reserva cancelada (no se borra: queda registrada). Importe devuelto: " + Consola.dinero(devuelto));
        r.getNotasDeAjuste().forEach(n -> consola.info("  " + n));
    }
}
