package presentacion.vistas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/** Utilidades de entrada y salida por consola, con validación y repregunta ante datos inválidos. */
public class Consola {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Scanner scanner = new Scanner(System.in, "UTF-8");

    public String leerLinea(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) throw new FinDeEntradaException();
        return scanner.nextLine().trim();
    }

    /** Texto que puede quedar vacío. */
    public String leerTexto(String mensaje) { return leerLinea(mensaje + ": "); }

    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            String linea = leerLinea(mensaje + " [" + min + "-" + max + "]: ");
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) return valor;
            } catch (NumberFormatException e) {
                // se repregunta abajo
            }
            error("Ingrese un número entero entre " + min + " y " + max + ".");
        }
    }

    public double leerDecimal(String mensaje, double minimo) {
        while (true) {
            String linea = leerLinea(mensaje + ": ").replace(',', '.');
            try {
                double valor = Double.parseDouble(linea);
                if (valor >= minimo) return valor;
            } catch (NumberFormatException e) {
                // se repregunta abajo
            }
            error("Ingrese un número válido (mínimo " + minimo + ").");
        }
    }

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            String linea = leerLinea(mensaje + " (dd/mm/aaaa): ");
            try {
                return LocalDate.parse(linea, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha inválida. Use el formato dd/mm/aaaa (por ejemplo 15/03/2027).");
            }
        }
    }

    public boolean confirmar(String mensaje) {
        while (true) {
            String r = leerLinea(mensaje + " (s/n): ").toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("sí")) return true;
            if (r.equals("n") || r.equals("no")) return false;
        }
    }

    /** Muestra una lista numerada y devuelve el elemento elegido, o null si se elige 0 o no hay opciones. */
    public <T> T elegir(String titulo, List<T> opciones, Function<T, String> descripcion) {
        if (opciones.isEmpty()) {
            info("No hay opciones disponibles para: " + titulo);
            return null;
        }
        System.out.println(titulo);
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + descripcion.apply(opciones.get(i)));
        }
        System.out.println("  0. Cancelar / volver");
        int n = leerEntero("Opción", 0, opciones.size());
        return n == 0 ? null : opciones.get(n - 1);
    }

    /** Elige varios elementos escribiendo sus números separados por coma (o "todas"). Lista vacía si no elige ninguno. */
    public <T> List<T> elegirVarios(String titulo, List<T> opciones, Function<T, String> descripcion) {
        List<T> elegidos = new ArrayList<>();
        if (opciones.isEmpty()) {
            info("No hay opciones disponibles para: " + titulo);
            return elegidos;
        }
        System.out.println(titulo);
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + descripcion.apply(opciones.get(i)));
        }
        while (true) {
            String linea = leerLinea("Números separados por coma, 'todas' o vacío para ninguna: ").toLowerCase();
            if (linea.isEmpty()) return elegidos;
            if (linea.equals("todas")) return new ArrayList<>(opciones);
            try {
                List<T> tentativa = new ArrayList<>();
                for (String parte : linea.split(",")) {
                    int n = Integer.parseInt(parte.trim());
                    if (n < 1 || n > opciones.size()) throw new NumberFormatException();
                    T item = opciones.get(n - 1);
                    if (!tentativa.contains(item)) tentativa.add(item);
                }
                return tentativa;
            } catch (NumberFormatException e) {
                error("Entrada inválida. Ejemplo: 1,3");
            }
        }
    }

    public void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    public void info(String texto) { System.out.println(texto); }

    public void error(String texto) { System.out.println("ERROR: " + texto); }

    public static String dinero(double valor) { return String.format("$%,.2f", valor); }
}
