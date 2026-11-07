import excepciones.ReglaNegocioException;
import modelo.estructura.*;
import modelo.personasRoles.*;
import modelo.viajesServicios.*;
import modelo.enums.*;
import modelo.reservas.Reserva;
import modelo.reservas.LiquidacionGuia;
import modelo.reservas.Dinero;
import modelo.toursEspecificos.TourAventura;
import modelo.toursEspecificos.TourCultural;
import presentacion.controladores.*;
import persistencia.GestorPersistencia;
import reportes.Reportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PruebaSistema {

    public static void main(String[] args) {
        System.out.println("AGENCIA RUMBO SUR");

        try {
            // 1. INICIALIZACION DEL SISTEMA Y CONTROLADORES (MVC)
            Agencia agencia = new Agencia();
            ControladorAdministracion ctrlAdmin = new ControladorAdministracion(agencia);
            ControladorOperaciones ctrlOper = new ControladorOperaciones(agencia);
            ControladorReserva ctrlRes = new ControladorReserva(agencia);

            Reloj reloj = new Reloj();
            // Fijamos la fecha de referencia para cálculos temporales
            reloj.fijar(LocalDate.of(2026, 10, 1));
            System.out.println("Fecha del sistema (Hoy): " + reloj.hoy() + "\n");

            // BLOQUE 1: ALTA DE CATALOGO, RECURSOS Y PROVEEDORES (CU01 / CU02)
            System.out.println("--- 1. CATALOGO Y RECURSOS (CU01 / CU02) ---");
            Guia guia = new Guia(1, "Germana Mendoza", "22334455", "11-2233", 20000.0);
            guia.habilitarCategoria(CategoriaTour.AVENTURA);
            guia.habilitarCategoria(CategoriaTour.CULTURAL);

            Vehiculo combi = new Vehiculo("AB123CD", 15, TipoVehiculo.COMBI);
            Vendedor vendedor = new Vendedor(2, "Dolores Ventas", "33445566", "11-4455", 0.05);

            TourAventura tourAventura = new TourAventura("T-AV01", "Trekking Glaciar", "Caminata sobre hielo",
                    5, 2, 8, "Alta", 18);
            tourAventura.agregarTarifa(new TarifaTemporada(Temporada.ALTA,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2027, 12, 31), 100000.0));

            System.out.println("[OK] Catálogo y recursos inicializados correctamente.\n");

            // BLOQUE 2: PROGRAMACION DE SALIDA Y RECURSOS (CU03 / CU04)
            System.out.println("--- 2. PROGRAMACION DE SALIDA #1 (CU03 / CU04) ---");
            LocalDate fechaSalida1 = LocalDate.of(2026, 12, 15);
            LocalDate fechaFin1 = fechaSalida1.plusDays(tourAventura.getDuracionDias() - 1);

            Salida salida1 = ctrlOper.programarSalida("T-AV01", fechaSalida1, fechaFin1, 8, tourAventura, reloj.hoy());
            ctrlOper.asignarGuia(salida1, guia);
            ctrlOper.asignarVehiculo(salida1, combi);

            System.out.println("Salida #" + salida1.getIdSalida() + " (" + tourAventura.getNombre() + ") programada para " + fechaSalida1);
            System.out.println("Estado: " + salida1.getEstado() + " | Cupo disponible: " + salida1.calcularCupoDisponible() + " lugares.\n");

            // BLOQUE 3: RESERVA, TIPOS DE PASAJERO Y PROMOCIONES (CU06 / CU07 / RN07)
            System.out.println("--- 3. CREACION DE RESERVA #1 (CU06 / CU07 / RN07) ---");
            Cliente cliente1 = ctrlRes.registrarCliente("Carlos Gomez", "111", "11-1111", "carlos@mail.com", "ARG");

            Pasajero pAdulto = new Pasajero("Carlos Gomez", "111", LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "11-9999", "Ninguna");
            Pasajero pJubilado = new Pasajero("Abuelo Gomez", "222", LocalDate.of(1950, 1, 1), TipoPasajero.JUBILADO, "11-9999", "Hipertensión");

            List<Pasajero> paxReserva1 = new ArrayList<>(List.of(pAdulto, pJubilado));
            Reserva reserva1 = ctrlRes.crearReserva(cliente1, salida1, vendedor, paxReserva1, reloj.hoy());

            System.out.println("Reserva #1 creada para " + paxReserva1.size() + " pasajeros.");
            System.out.println("TOTAL CALCULADO CON CONEXION A CALCULADORA: $" + reserva1.getTotal());
            System.out.println("Estado de Reserva: " + reserva1.getEstado() + "\n");

            // BLOQUE 4: REGISTRO DE PAGOS Y SENA (CU08 / RN07)
            System.out.println("--- 4. REGISTRO DE PAGO Y CONFIRMACION (CU08 / RN07) ---");
            double montoPago = 60000.0;
            modelo.reservas.Pago pago = ctrlRes.registrarPago(reserva1, montoPago, MedioPago.TARJETA, 3, reloj.hoy());

            System.out.println("Pago registrado: $" + montoPago + " en 3 cuotas con Tarjeta.");
            System.out.println("Recargo por tarjeta (7%): $" + pago.getRecargo());
            System.out.println("Estado de Reserva #1 tras superar el 30% de seña: " + reserva1.getEstado() + "\n");

            // BLOQUE 5: CANCELACION DE RESERVA Y REINTEGRO (CU13 / RN12)
            System.out.println("--- 5. CANCELACION DE RESERVA Y LIBERACION DE CUPO (CU13) ---");
            System.out.println("Lugares ocupados antes de cancelar: " + (salida1.getCupoMaximo() - salida1.calcularCupoDisponible()));
            double devuelto = ctrlRes.cancelarReserva(reserva1, "Motivos personales", reloj.hoy());

            System.out.println("Reserva #1 CANCELADA.");
            System.out.println("Devolución calculada (90% por política flexible > 30 días): $" + devuelto);
            System.out.println("Lugares ocupados tras cancelar (cupo liberado): " + (salida1.getCupoMaximo() - salida1.calcularCupoDisponible()) + "\n");

            // BLOQUE 6: CONTROL DE RESTRICCIONES (LSP Y EXCEPCIONES)
            System.out.println("--- 6. CONTROL DE RESTRICCIONES Y REGLAS DE NEGOCIO ---");
            try {
                ctrlRes.registrarCliente("Luis Perez", "333", "11-3333", "luis@mail.com", "ARG");
                Pasajero pMenor = new Pasajero("Niño Perez", "444", LocalDate.of(2016, 1, 1), TipoPasajero.MENOR, "11-8888", "Ninguna");

                System.out.println("Intentando ingresar menor de 10 años en Tour Aventura (exige 18)...");
                if (!tourAventura.validarPasajero(pMenor, fechaSalida1)) {
                    throw new ReglaNegocioException("El pasajero no cumple la edad mínima obligatoria.");
                }
            } catch (ReglaNegocioException e) {
                System.out.println("[EXCEPCION CONTROLADA]: " + e.getMessage() + "\n");
            }

            // BLOQUE 7: OPERACION COMPLETA, REEMPLAZO Y LIQUIDACION (CU09, CU10, CU12)
            System.out.println("--- 7. OPERACION, REEMPLAZO DE PASAJERO Y LIQUIDACION (CU09, CU10, CU12) ---");

            TourCultural tourCultural = new TourCultural("T-CUL01", "Circuito Histórico y Museos", "Recorrido colonial", 3, 1, 10);
            tourCultural.agregarTarifa(new TarifaTemporada(Temporada.MEDIA,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2027, 12, 31), 40000.0));

            LocalDate fechaSalida2 = LocalDate.of(2026, 11, 20);
            LocalDate fechaFin2 = fechaSalida2.plusDays(tourCultural.getDuracionDias() - 1);

            Salida salida2 = ctrlOper.programarSalida("T-CUL01", fechaSalida2, fechaFin2, 10, tourCultural, reloj.hoy());
            ctrlOper.asignarGuia(salida2, guia);
            ctrlOper.asignarVehiculo(salida2, combi);

            Cliente cliente2 = ctrlRes.registrarCliente("Marta Fernandez", "555", "11-5555", "marta@mail.com", "ARG");
            Pasajero paxOriginal = new Pasajero("Marta Fernandez", "555", LocalDate.of(1985, 3, 10), TipoPasajero.ADULTO, "11-0000", "Ninguna");
            Pasajero paxAcompaniante = new Pasajero("Esteban Fernandez", "666", LocalDate.of(1988, 7, 22), TipoPasajero.ADULTO, "11-0000", "Ninguna");

            List<Pasajero> paxReserva2 = new ArrayList<>(List.of(paxOriginal, paxAcompaniante));
            Reserva reserva2 = ctrlRes.crearReserva(cliente2, salida2, vendedor, paxReserva2, reloj.hoy());

            // Confirmamos la reserva con pago en efectivo
            ctrlRes.registrarPago(reserva2, reserva2.getTotal(), MedioPago.EFECTIVO, 1, reloj.hoy());
            System.out.println("[OK] Reserva #2 confirmada por $" + reserva2.getTotal() + " en Salida #" + salida2.getIdSalida());

            // CU12: Reemplazo de pasajero
            Pasajero paxReemplazo = new Pasajero("Julieta Gomez", "777", LocalDate.of(1992, 4, 15), TipoPasajero.ADULTO, "11-2222", "Ninguna");
            ctrlRes.reemplazarPasajero(reserva2, paxAcompaniante, paxReemplazo, reloj.hoy());
            System.out.println("[OK CU12] Pasajero reemplazado con éxito (Esteban -> Julieta).");

            // CU09: Emisión de lista ordenada de pasajeros (Comparable) e inicio
            List<Pasajero> listaEmitida = ctrlOper.emitirListaPasajeros(salida2);
            System.out.println("[OK CU09] Lista de pasajeros emitida y ordenada alfabéticamente:");
            for (Pasajero p : listaEmitida) {
                System.out.println("     * " + p);
            }

            ctrlOper.iniciarSalida(salida2, fechaSalida2);
            System.out.println("Estado de Salida #2 al iniciar: " + salida2.getEstado());

            ctrlOper.cerrarSalida(salida2, fechaFin2);
            System.out.println("Estado de Salida #2 al finalizar: " + salida2.getEstado());

            // CU10: Liquidación de honorarios al guía
            LiquidacionGuia liq = ctrlAdmin.liquidar(guia, agencia.getSalidas());
            System.out.println("[OK CU10] Liquidación generada para " + guia.getNombre() + ": " + Dinero.formatear(liq.getTotal()) + "\n");

            // BLOQUE 8: REPORTES GERENCIALES (CU11)
            System.out.println("--- 8. REPORTES GERENCIALES COMPLETOS (CU11) ---");
            Reportes reportes = ctrlAdmin.getReportes();
            LocalDate inicioFiltro = LocalDate.of(2026, 1, 1);
            LocalDate finFiltro = LocalDate.of(2026, 12, 31);

            reportes.ocupacionPorSalida(inicioFiltro, finFiltro, null);
            reportes.cancelaciones(inicioFiltro, finFiltro, null);
            reportes.ventasPorVendedor(inicioFiltro, finFiltro, null, vendedor);
            reportes.ingresosPorTour(inicioFiltro, finFiltro, null);
            System.out.println();

            // BLOQUE 9: PERSISTENCIA EN ARCHIVO
            System.out.println("--- 9. PERSISTENCIA DE OBJETOS ---");
            GestorPersistencia gestor = new GestorPersistencia();
            gestor.guardar(agencia, "sistema_agencia.dat");
            Agencia agenciaRecuperada = gestor.cargar("sistema_agencia.dat");
            System.out.println("Sistema guardado y recuperado. Salidas activas en archivo: " + agenciaRecuperada.getSalidas().size());

            System.out.println("\n>>> LA SIMULACION FINALIZO CON EXITO (TODOS LOS CASOS DE USO CUBIERTOS) <<<");

        } catch (Exception e) {
            System.err.println("ERROR CRITICO NO CONTROLADO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}