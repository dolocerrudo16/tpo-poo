import excepciones.ReglaNegocioException;
import modelo.estructura.*;
import modelo.personasRoles.*;
import modelo.viajesServicios.*;
import modelo.enums.*;
import modelo.reservas.Reserva;
import modelo.toursEspecificos.TourAventura;
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
            // inicia sistema
            Agencia agencia = new Agencia();
            ControladorAdministracion ctrlAdmin = new ControladorAdministracion(agencia);
            ControladorOperaciones ctrlOper = new ControladorOperaciones(agencia);
            ControladorReserva ctrlRes = new ControladorReserva(agencia);

            Reloj reloj = new Reloj();
            // Fijamos la fecha a 1 de Octubre de 2026 para tener previsibilidad en días de anticipación
            reloj.fijar(LocalDate.of(2026, 10, 1));
            System.out.println("Fecha del sistema (Hoy): " + reloj.hoy() + "\n");

            // CU01 y CU02 - CATÁLOGO, RECURSOS Y PROVEEDORES
            System.out.println("ALTA DE CATÁLOGO Y RECURSOS (CU01 / CU02)");
            Guia guia = new Guia(1, "Germana Mendoza", "22334455", "11-2233", 20000.0);
            guia.habilitarCategoria(CategoriaTour.AVENTURA);
            Vehiculo combi = new Vehiculo("AB123CD", 15, TipoVehiculo.COMBI);
            Vendedor vendedor = new Vendedor(2, "Dolores Ventas", "33445566", "11-4455", 0.05);

            // RN: Tour Aventura exige edad mínima y nivel de dificultad
            TourAventura tour = new TourAventura("T-AV01", "Trekking Glaciar", "Caminata sobre hielo",
                    5, 2, 8, "Alta", 18);
            tour.agregarTarifa(new TarifaTemporada(Temporada.ALTA,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2027, 12, 31), 100000.0)); // Tarifa Base: $100.000
            System.out.println("[OK] Catálogo y recursos inicializados correctamente.\n");

            // CU03 y CU04 PROGRAMAR SALIDA Y ASIGNAR RECURSOS
            System.out.println("PROGRAMACIÓN DE SALIDA (CU03 / CU04)");
            // Salida: 15 de Diciembre de 2026 (75 días de anticipación desde hoy)
            LocalDate fechaSalida = LocalDate.of(2026, 12, 15);
            LocalDate fechaFin = fechaSalida.plusDays(tour.getDuracionDias() - 1);

            Salida salida = ctrlOper.programarSalida("T-AV01", fechaSalida, fechaFin, 8, tour, reloj.hoy());
            ctrlOper.asignarGuia(salida, guia);
            ctrlOper.asignarVehiculo(salida, combi);
            System.out.println("Salida " + tour.getNombre() + " programada para " + fechaSalida);
            System.out.println("     Estado actual de Salida: " + salida.getEstado() + " | Cupo: " + salida.calcularCupoDisponible() + " libres\n");

            // CU06, CU07 Y RN07 CÁLCULO DE TARIFAS Y PROMOCIONES
            System.out.println("RESERVA, TIPOS DE PASAJERO Y PROMOCIONES (CU06 / CU07 / RN07)");
            Cliente cliente1 = ctrlRes.registrarCliente("Carlos", "111", "111", "Carlos@mail.com", "ARG");

            // Pasajero Adulto: Paga 100%
            Pasajero pAdulto = new Pasajero("Carlos", "111", LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "-", "-");
            // Pasajero Jubilado: Paga 90% (10% descuento por RN07)
            Pasajero pJubilado = new Pasajero("Abuelo", "222", LocalDate.of(1950, 1, 1), TipoPasajero.JUBILADO, "-", "-");

            List<Pasajero> paxReserva1 = new ArrayList<>(List.of(pAdulto, pJubilado));
            Reserva reserva1 = ctrlRes.crearReserva(cliente1, salida, vendedor, paxReserva1, reloj.hoy());

            System.out.println("Reserva #1 creada para " + paxReserva1.size() + " pasajeros.");
            System.out.println("- Cálculo esperado: Adulto(100k) + Jubilado(90k) = 190k");
            System.out.println("- Promo Anticipada: >60 días de anticipación = 15% OFF sobre tarifa = -28.5k");
            System.out.println("- Seguro Obligatorio (Aventura): 2 seguros de $5.000 (por CU06) = +10k");
            System.out.println("TOTAL CALCULADO: $" + reserva1.getTotal());
            System.out.println("Estado de Reserva: " + reserva1.getEstado() + "\n");

            // CU08 Y RN07 PAGOS, RECARGOS CON TARJETA Y CAMBIO DE ESTADO
            System.out.println("REGISTRO DE PAGOS Y RECARGOS (CU08 / RN07)");
            // Abonamos con tarjeta en 3 cuotas para activar el recargo del 7%
            double montoPago = 60000.0;
            modelo.reservas.Pago pago = ctrlRes.registrarPago(reserva1, montoPago, MedioPago.TARJETA, 3, reloj.hoy());

            System.out.println("Se registró un pago de $" + montoPago + " en 3 cuotas con Tarjeta.");
            System.out.println("Recargo por financiación (7%): $" + pago.getRecargo());
            System.out.println("Estado de Reserva #1 tras superar el 30% de seña: " + reserva1.getEstado() + "\n");

            // CU13 Y RN12-17 POLÍTICA DE CANCELACIÓN
            System.out.println("CANCELACIÓN DE RESERVA Y REINTEGRO (CU13)");
            System.out.println("Lugares ocupados antes de cancelar: " + (salida.getCupoMaximo() - salida.calcularCupoDisponible()));

            // Faltan 75 días, por política flexible (CancelacionFlexible), si faltan >=30 días se devuelve el 90%.
            double devuelto = ctrlRes.cancelarReserva(reserva1, "Motivos personales", reloj.hoy());

            System.out.println("Reserva #1 CANCELADA.");
            System.out.println("Total pagado: $" + montoPago + " | Devolución calculada (90%): $" + devuelto);
            System.out.println("Lugares ocupados tras cancelar (liberación de cupo): " + (salida.getCupoMaximo() - salida.calcularCupoDisponible()) + "\n");

            // BLOQUE 6: REGLAS DE NEGOCIO - CONTROL DE EXCEPCIONES Y RESTRICCIONES
            System.out.println(">>> BLOQUE 6: CONTROL DE RESTRICCIONES (LSP Y EXCEPCIONES)");
            try {
                // Intentamos ingresar a un menor de 10 años a un tour que exige ser mayor de 18
                Cliente cliente2 = ctrlRes.registrarCliente("Luis", "333", "333", "luis@mail.com", "ARG");
                Pasajero pMenor = new Pasajero("Niño", "444", LocalDate.of(2016, 1, 1), TipoPasajero.MENOR, "-", "-");

                System.out.println("Intentando crear reserva con Pasajero Menor (10 años) en Tour Aventura...");
                // Esta línea usa la función validarPasajero sobreescrita en TourAventura (LSP/OCP)
                if (!tour.validarPasajero(pMenor, fechaSalida)) {
                    throw new ReglaNegocioException("El pasajero no cumple la edad mínima obligatoria.");
                }
            } catch (ReglaNegocioException e) {
                System.out.println("EXCEPCIÓN ATRAPADA: " + e.getMessage() + "\n");
            }

            // CU11 REPORTES GERENCIALES
            System.out.println(">>> BLOQUE 7: REPORTES GERENCIALES (CU11)");
            Reportes reportes = ctrlAdmin.getReportes();
            LocalDate inicioFiltro = LocalDate.of(2026, 1, 1);
            LocalDate finFiltro = LocalDate.of(2026, 12, 31);

            // Imprimimos la ocupación (en este caso será 0 porque cancelamos la única reserva)
            reportes.ocupacionPorSalida(inicioFiltro, finFiltro, null);
            // Imprimimos las cancelaciones
            reportes.cancelaciones(inicioFiltro, finFiltro, null);
            System.out.println("");

            // PERSISTENCIA (GUARDADO DE DATOS)
            System.out.println("PERSISTENCIA DE OBJETOS");
            GestorPersistencia gestor = new GestorPersistencia();
            gestor.guardar(agencia, "sistema_agencia.dat");
            Agencia agenciaRecuperada = gestor.cargar("sistema_agencia.dat");
            System.out.println("Sistema serializado y recuperado. Salidas guardadas: " + agenciaRecuperada.getSalidas().size());

            System.out.println(" LA EJECUCIÓN FINALIZÓ SIN ERRORES.");

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO NO CONTROLADO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}