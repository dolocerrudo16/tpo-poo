import excepciones.ReglaNegocioException;
import modelo.estructura.*;
import modelo.personasRoles.*;
import modelo.politicasPromos.CancelacionFlexible;
import modelo.reservas.CalculadoraTarifa;
import modelo.reservas.Pago;
import modelo.reservas.Reserva;
import modelo.toursEspecificos.TourAventura;
import modelo.viajesServicios.*;
import modelo.enums.*;
import presentacion.controladores.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("INICIANDO PRUEBAS DE INTEGRACIÓN - AGENCIA DE TURISMO");

        try {
            // 1. Inicialización de la Agencia y Controladores
            Agencia agencia = new Agencia();
            ControladorOperaciones ctrlOperaciones = new ControladorOperaciones(agencia);
            ControladorReserva ctrlReserva = new ControladorReserva(agencia);
            ControladorAdministracion ctrlAdmin = new ControladorAdministracion(agencia);
            Reloj reloj = new Reloj(); // Simulador de fecha actual

            System.out.println("\n[OK] Controladores y Agencia inicializados.");

            // 2. Alta de Personal (Uso de Herencia: Vendedor y Guia heredan de Empleado)
            Vendedor vendedor = new Vendedor(101, "Ana Vendedora", "11223344", "11-5555-1234", 0.03);
            Guia guia = new Guia(201, "Carlos Guia", "22334455", "11-4444-5678", 15000.0);
            System.out.println("[OK] Personal registrado: " + vendedor.getNombre() + " y " + guia.getNombre());

            // 3. Alta de Vehículo y Proveedor
            Vehiculo combi = new Vehiculo("AF123CD", 15, TipoVehiculo.COMBI);
            Proveedor provHotel = new Proveedor("Hotel Patagonia", "30112233445", "contacto@hotel.com", "Alojamiento");
            System.out.println("Vehículo y Proveedor registrados.");

            // 4. Creación de un Tour con Polimorfismo (TourAventura hereda de Tour)
            TourAventura tourSur = new TourAventura("T-SUR", "Expedición Glaciar", "Trekking en hielo",
                    5, 2, 15, "Alta", 18);

            // Agregando Actividad y Tarifa al Tour (Manejo de colecciones internas)
            Actividad act1 = new Actividad(1, 1, "08:00", "Llegada y check-in", "Calafate", provHotel);
            tourSur.agregarActividad(act1);

            TarifaTemporada tarifaAlta = new TarifaTemporada(Temporada.ALTA,
                    LocalDate.of(2026, 12, 1),
                    LocalDate.of(2027, 3, 31),
                    250000.0);
            tourSur.agregarTarifa(tarifaAlta);
            System.out.println("Tour creado y configurado: " + tourSur.toString());

            // 5. Programación de una Salida
            LocalDate fechaInicio = LocalDate.of(2026, 12, 15);
            LocalDate fechaFin = fechaInicio.plusDays(tourSur.getDuracionDias() - 1);
            Salida salidaDic = new Salida(1, fechaInicio, fechaFin, 10, tourSur, reloj.hoy());

            // Asignación de recursos a la salida
            salidaDic.asignarGuia(guia, new ArrayList<>());
            salidaDic.asignarVehiculo(combi, new ArrayList<>());
            System.out.println("Salida programada y recursos asignados. Estado: " + salidaDic.getEstado());

            // 6. Registro de Cliente y Pasajeros
            Cliente cliente = new Cliente(1, "Juan Perez", "30444555", "11-3333-2222", "juan@mail.com", "Argentina");

            Pasajero p1 = new Pasajero("Juan Perez", "30444555", LocalDate.of(1990, 5, 10),
                    TipoPasajero.ADULTO, "Maria: 11-2222-3333", "Vegetariano");
            Pasajero p2 = new Pasajero("Laura Perez", "32111222", LocalDate.of(1995, 8, 20),
                    TipoPasajero.ADULTO, "Maria: 11-2222-3333", "Ninguna");
            List<Pasajero> listaPasajeros = new ArrayList<>(List.of(p1, p2));
            System.out.println("Cliente y " + listaPasajeros.size() + " pasajeros registrados.");

            // 7. Gestión de Reserva (Simulación de la capa de Controladores)
            // Nota: Se instancian las dependencias requeridas por el constructor de Reserva según tu modelo
            Reserva reserva = new Reserva(1001, cliente, salidaDic, vendedor,
                    new CancelacionFlexible(), new CalculadoraTarifa(), reloj.hoy());
            reserva.agregarPasajero(p1);
            reserva.agregarPasajero(p2);
            salidaDic.agregarReserva(reserva, reloj.hoy());
            System.out.println("Reserva creada exitosamente. Número: " + reserva.getNumero());

            // 8. Registro de Pago
            Pago pagoSena = reserva.registrarPago(150000.0, MedioPago.TRANSFERENCIA, 1, reloj.hoy());
            System.out.println("[OK] Pago registrado: $" + pagoSena.getMonto() + " mediante " + pagoSena.getMedioPago());

            // 9. Emisión de Lista de Pasajeros y Cambio de Estado (Operaciones)
            List<Pasajero> emitidos = salidaDic.emitirListaPasajeros();
            System.out.println("Lista de pasajeros emitida. Cantidad confirmada: " + emitidos.size());

            salidaDic.iniciar(reloj.hoy());
            System.out.println("Salida iniciada. Nuevo estado: " + salidaDic.getEstado());

            // 10. Prueba de Excepciones (Reglas de Negocio)
            System.out.println("\nPRUEBA DE REGLAS DE NEGOCIO (EXCEPCIONES)");
            try {
                // Forzamos un error: Edad menor a la permitida para Tour de Aventura
                Pasajero pInvalido = new Pasajero("Niño", "50111222", LocalDate.of(2018, 1, 1),
                        TipoPasajero.MENOR, "Madre", "");
                if (!tourSur.validarPasajero(pInvalido, fechaInicio)) {
                    throw new ReglaNegocioException("El pasajero no cumple la edad mínima para el Tour de Aventura.");
                }
            } catch (ReglaNegocioException e) {
                System.out.println("[EXCEPCIÓN CAPTURADA CORRECTAMENTE]: " + e.getMessage());
            }

        } catch (ReglaNegocioException e) {
            System.err.println("Fallo inesperado en la inicialización: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general del sistema: " + e.getMessage());
        }

        System.out.println("\nFIN DE LAS PRUEBAS ");
    }
}