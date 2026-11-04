package modelo;

public class PromocionGrupo implements Promocion {
    private static final long serialVersionUID = 1L;
    private static final int PASAJEROS_MINIMOS = 6;
    private static final double DESCUENTO = 0.10;

    @Override
    public String getNombre() { return "Grupo (10%)"; }

    @Override
    public double calcularDescuento(int cantidadPasajeros, long diasAnticipacion) {
        return cantidadPasajeros >= PASAJEROS_MINIMOS ? DESCUENTO : 0.0;
    }
}
