import modelo.*;
import modelo.enums.*;
import modelo.estructura.Agencia;
import modelo.estructura.Reloj;
import modelo.personasRoles.*;
import modelo.reservas.LiquidacionGuia;
import modelo.reservas.Pago;
import modelo.reservas.Reserva;
import modelo.toursEspecificos.TourAventura;
import modelo.toursEspecificos.TourCultural;
import modelo.toursEspecificos.TourNaturaleza;
import modelo.viajesServicios.*;
import presentacion.controladores.*;
import reportes.*;
import persistencia.*;
import excepciones.ReglaNegocioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== INICIALIZACION DEL SISTEMA ===");

            // 1. Reloj y fechas
            Reloj reloj = new Reloj();
            LocalDate hoy = reloj.hoy();
            System.out.println("\n--- 1. Reloj y Fechas ---");
            System.out.println("Fecha actual del sistema: " + hoy);

            // 2. Instanciacion de la raiz del modelo y persistencia
            Agencia agencia = new Agencia();
            GestorPersistencia gestorPersistencia = new GestorPersistencia();
            System.out.println("\n--- 2. Entorno y Persistencia ---");
            System.out.println("Agencia y Gestor de Persistencia instanciados correctamente.");

            // 3. Controladores
            ControladorAdministracion ctrlAdmin = new ControladorAdministracion(agencia);
            ControladorOperaciones ctrlOp = new ControladorOperaciones(agencia);
            ControladorReserva ctrlRes = new ControladorReserva(agencia);
            System.out.println("\n--- 3. Controladores ---");
            System.out.println("Controladores (Administración, Operaciones, Reservas) vinculados.");

            // 4. Personas: Empleados y Clientes
            Vendedor vendedor = new Vendedor(1, "Carlos Gomez", "30111222", "11223344", 0.03);
            Guia guia = new Guia(2, "Maria Lopez", "28333444", "55667788", 15000.0);
            Administrador admin = new Administrador(3, "Roberto Jefe", "20111999", "11889900");
            CoordinadorOperaciones coord = new CoordinadorOperaciones(4, "Lucia Opera", "27333222", "11445566");

            Cliente cliente = new Cliente(1, "Ana Ruiz", "25444555", "11998877", "ana.ruiz@email.com", "Argentina");
            Pasajero p1 = new Pasajero("Pedro Ruiz", "40555666", hoy.minusYears(25), TipoPasajero.ADULTO, "11998877", "Ninguna");
            Pasajero p2 = new Pasajero("Sofia Ruiz", "48555666", hoy.minusYears(15), TipoPasajero.MENOR, "11998877", "Vegetariana");

            System.out.println("\n--- 4. Empleados, Clientes y Pasajeros ---");
            System.out.println("Vendedor: " + vendedor);
            System.out.println("Guía: " + guia);
            System.out.println("Administrador: " + admin);
            System.out.println("Coordinador: " + coord);
            System.out.println("Cliente registrado: " + cliente);
            System.out.println("Pasajeros listos: " + p1 + " | " + p2);

            // 5. Proveedor y Actividades
            Proveedor hotel = new Proveedor("Hotel Cordillera", "20300100203", "Juan Perez", "Alojamiento");
            Actividad act1 = new Actividad(1, 1, "09:00", "Ascenso Base", "Cerro Catedral", hotel);
            Actividad act2 = new Actividad(2, 1, "10:30", "Trekking Glaciar", "Tronador", hotel);

            System.out.println("\n--- 5. Proveedores y Actividades ---");
            System.out.println("Proveedor: " + hotel);
            System.out.println("Actividad 1: Día " + act1.getDia() + " - " + act1.getDescripcion() + " (" + act1.getLugar() + " a las " + act1.getHorario() + ")");
            System.out.println("Actividad 2: Día " + act2.getDia() + " - " + act2.getDescripcion() + " (" + act2.getLugar() + " a las " + act2.getHorario() + ")");

            // 6. Tours, Tarifas y Adicionales
            TourAventura tourAventura = new TourAventura("ADV-01", "Aventura Bariloche", "Rutas y trekking", 3, 2, 20, "Media", 12);
            tourAventura.agregarActividad(act1);
            tourAventura.agregarActividad(act2);

            TourCultural tourCultural = new TourCultural("CUL-01", "Casco Historico", "Museos e historia", 1, 1, 30);
            TourNaturaleza tourNaturaleza = new TourNaturaleza("NAT-01", "Bosque de Arrayanes", "Flora y fauna", 2, 2, 25);

            TarifaTemporada tarifa = new TarifaTemporada(Temporada.ALTA, hoy.minusDays(5), hoy.plusMonths(2), 75000.0);
            tourAventura.agregarTarifa(tarifa);

            ServicioAdicional seguro = new ServicioAdicional(1, "Seguro Medico Aventura", 5000.0, TipoServicio.SEGURO_VIAJE);
            ServicioAdicional traslado = new ServicioAdicional(2, "Traslado Aeropuerto", 3500.0, TipoServicio.TRASLADO);

            System.out.println("\n--- 6. Tours, Tarifas y Servicios Adicionales ---");
            System.out.println("Tour Creado: " + tourAventura + " [Dificultad: " + tourAventura.getNivelDificultad() + ", Edad mínima: " + tourAventura.getEdadMinima() + "]");
            System.out.println("Tour Creado: " + tourCultural);
            System.out.println("Tour Creado: " + tourNaturaleza);
            System.out.println("Tarifa configurada: Temporada " + tarifa.getTemporada() + " ($" + tarifa.getValor() + ") vigencia: " + tarifa.getVigenteDesde() + " al " + tarifa.getVigenteHasta());
            System.out.println("Servicio Adicional: " + seguro.getDescripcion() + " ($" + seguro.getPrecio() + ")");
            System.out.println("Servicio Adicional: " + traslado.getDescripcion() + " ($" + traslado.getPrecio() + ")");

            // 7. Vehiculos y Salidas
            Vehiculo combi = new Vehiculo("AA123BB", 15, TipoVehiculo.COMBI);
            Vehiculo micro = new Vehiculo("AC456DD", 45, TipoVehiculo.MICRO);

            LocalDate inicioSalida = hoy.plusDays(15);
            LocalDate finSalida = inicioSalida.plusDays(tourAventura.getDuracionDias() - 1L);
            Salida salida = new Salida(101, inicioSalida, finSalida, 15, tourAventura, hoy);

            System.out.println("\n--- 7. Vehículos y Salidas ---");
            System.out.println("Vehículo 1: " + combi);
            System.out.println("Vehículo 2: " + micro);
            System.out.println("Salida #" + salida.getIdSalida() + " programada desde " + inicioSalida + " hasta " + finSalida + " (Estado: " + salida.getEstado() + ")");

            // 8. Flujo mediante Controladores
            ctrlAdmin.altaTour(tourAventura);
            ctrlAdmin.altaTour(tourCultural);
            ctrlAdmin.altaTour(tourNaturaleza);
            ctrlAdmin.altaProveedor("Restaurante Los Alerces", "30708090104", "Recepcion", "Gastronomia");

            ctrlOp.asignarVehiculo(salida, combi);
            ctrlOp.asignarGuia(salida, guia);

            List<Pasajero> listaPasajeros = new ArrayList<>();
            listaPasajeros.add(p1);
            listaPasajeros.add(p2);

            Reserva reserva = ctrlRes.crearReserva(cliente, salida, vendedor, listaPasajeros);
            Pago pago = ctrlRes.registrarPago(reserva, 150000.0, MedioPago.EFECTIVO, 1);

            List<Salida> salidasGuia = new ArrayList<>();
            salidasGuia.add(salida);
            LiquidacionGuia liquidacion = ctrlAdmin.liquidar(guia, salidasGuia);

            System.out.println("\n--- 8. Ejecución mediante Controladores ---");
            System.out.println("Alta de tours y proveedores completada en Administración.");
            System.out.println("Guía y vehículo asignados a la salida mediante ControladorOperaciones.");
            System.out.println("Reserva creada mediante ControladorReserva: " + reserva);
            System.out.println("Pago registrado mediante ControladorReserva: " + pago);
            System.out.println("Liquidación de guía generada: " + liquidacion);

            // 9. Reportes
            System.out.println("\n--- 9. Reportes ---");
            Reportes reportes = ctrlAdmin.getReportes();
            reportes.ingresosPorTour(hoy.minusDays(30), hoy.plusDays(30), tourAventura);
            reportes.ocupacionPorSalida(hoy, hoy.plusDays(60), tourAventura);
            reportes.ventasPorVendedor(hoy.minusDays(30), hoy.plusDays(30), tourAventura, vendedor);
            reportes.cancelaciones(hoy.minusDays(30), hoy.plusDays(30), tourAventura);
            System.out.println("Reportes ejecutados exitosamente.");

            // 10. Persistencia
            System.out.println("\n--- 10. Persistencia ---");
            gestorPersistencia.guardar(agencia, "agencia_datos.dat");
            System.out.println("Estado de la agencia guardado en 'agencia_datos.dat'.");
            Agencia agenciaRecuperada = gestorPersistencia.cargar("agencia_datos.dat");
            System.out.println("Estado de la agencia recuperado desde archivo: " + agenciaRecuperada);

            System.out.println("\n=== EJECUCION FINALIZADA SIN ERRORES ===");

        } catch (ReglaNegocioException e) {
            System.err.println("Regla de negocio no superada: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
}