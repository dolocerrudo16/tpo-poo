package modelo;

public final class Dinero {
    private Dinero() { }

    public static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public static String formatear(double valor) {
        return String.format(java.util.Locale.ROOT, "$%.2f", valor);
    }
}
