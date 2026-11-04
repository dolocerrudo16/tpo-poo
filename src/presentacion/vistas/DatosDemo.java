package presentacion.vistas;

import excepciones.ReglaNegocioException;
import java.time.LocalDate;
import modelo.Actividad;
import modelo.Administrador;
import modelo.Agencia;
import modelo.CategoriaTour;
import modelo.CoordinadorOperaciones;
import modelo.Guia;
import modelo.Proveedor;
import modelo.TarifaTemporada;
import modelo.Temporada;
import modelo.TipoVehiculo;
import modelo.Tour;
import modelo.TourAventura;
import modelo.TourCultural;
import modelo.TourNaturaleza;
import modelo.Vendedor;

/** Carga datos de prueba (catálogo, personal, flota, un cliente y dos salidas) para poder probar el sistema. */
public final class DatosDemo {
    private DatosDemo() { }

    public static void cargar(Agencia agencia) throws ReglaNegocioException {
        LocalDate hoy = agencia.hoy();

        Proveedor hotel = agencia.registrarProveedor("Hotel Los Andes", "30-71234567-8", "reservas@losandes.com", "Alojamiento");
        Proveedor resto = agencia.registrarProveedor("Restaurante El Fogón", "30-70123456-9", "11-5555-0101", "Comidas");
        agencia.registrarProveedor("Transportes Patagonia", "30-69876543-2", "11-5555-0202", "Traslados");

        agencia.registrarEmpleado(new Administrador(1, "Administrador General", "20111111", "11-4000-0001"));
        agencia.registrarEmpleado(new CoordinadorOperaciones(2, "Coordinadora de Operaciones", "20222222", "11-4000-0002"));
        agencia.registrarEmpleado(new Vendedor(201, "Camila Suárez", "30333333", "11-4000-0201", Vendedor.COMISION_POR_DEFECTO));
        agencia.registrarEmpleado(new Vendedor(202, "Julián Ortiz", "30444444", "11-4000-0202", Vendedor.COMISION_POR_DEFECTO));

        Guia martin = new Guia(101, "Martín Pereyra", "28555555", "11-4000-0101", 25000);
        martin.habilitarCategoria(CategoriaTour.AVENTURA);
        martin.habilitarCategoria(CategoriaTour.NATURALEZA);
        martin.agregarIdioma("Español");
        martin.agregarIdioma("Inglés");
        agencia.registrarGuia(martin);
        Guia lucia = new Guia(102, "Lucía Benítez", "29666666", "11-4000-0102", 22000);
        lucia.habilitarCategoria(CategoriaTour.CULTURAL);
        lucia.habilitarCategoria(CategoriaTour.NATURALEZA);
        lucia.agregarIdioma("Español");
        agencia.registrarGuia(lucia);

        agencia.registrarVehiculo("AB123CD", 40, TipoVehiculo.MICRO);
        agencia.registrarVehiculo("AC456EF", 15, TipoVehiculo.COMBI);
        agencia.registrarVehiculo("AD789GH", 6, TipoVehiculo.CAMIONETA);

        Tour rafting = new TourAventura("AV-RAFT", "Rafting en el Río Manso", "Descenso en balsa con almuerzo", 3, 2, 12, "Media", 14);
        rafting.agregarActividad(new Actividad(1, 1, "09:00", "Traslado y charla de seguridad", "Base Manso", null));
        rafting.agregarActividad(new Actividad(2, 1, "10:00", "Descenso en balsa", "Río Manso", null));
        rafting.agregarActividad(new Actividad(2, 2, "14:00", "Almuerzo en la costa", "Costa del río", resto));
        rafting.agregarActividad(new Actividad(3, 1, "09:30", "Trekking al mirador", "Cerro Cuevas", null));
        Tour bodegas = new TourCultural("CU-BOD", "Bodegas de Mendoza", "Circuito de bodegas con degustación", 2, 3, 20);
        bodegas.agregarActividad(new Actividad(1, 1, "10:00", "Visita a bodega y degustación", "Luján de Cuyo", null));
        bodegas.agregarActividad(new Actividad(1, 2, "20:00", "Alojamiento", "Hotel Los Andes", hotel));
        Tour iguazu = new TourNaturaleza("NA-IGU", "Cataratas del Iguazú", "Parque Nacional y avistaje de fauna", 4, 4, 25);
        iguazu.agregarActividad(new Actividad(1, 1, "08:00", "Ingreso al Parque Nacional", "Iguazú", null));

        double[] bases = {60000, 45000, 80000};
        Tour[] tours = {rafting, bodegas, iguazu};
        double[] factores = {1.0, 1.25, 1.5};    // baja, media, alta
        for (int t = 0; t < tours.length; t++) {
            // 6 bloques de 4 meses consecutivos: cubren los próximos 2 años rotando las temporadas
            for (int i = 0; i < 6; i++) {
                LocalDate desde = hoy.plusMonths(4L * i);
                tours[t].agregarTarifa(new TarifaTemporada(Temporada.values()[i % 3], desde,
                        desde.plusMonths(4).minusDays(1), bases[t] * factores[i % 3]));
            }
            agencia.agregarTour(tours[t]);
        }

        agencia.registrarCliente("Ana Gómez", "30111222", "11-5000-1111", "ana.gomez@mail.com", "Argentina");

        LocalDate inicioBodegas = hoy.plusDays(45);
        agencia.programarSalida("CU-BOD", inicioBodegas, inicioBodegas.plusDays(1), 10);
        LocalDate inicioRafting = hoy.plusDays(80);
        agencia.programarSalida("AV-RAFT", inicioRafting, inicioRafting.plusDays(2), 8);
    }
}
