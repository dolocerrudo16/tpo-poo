package modelo;

public class PromocionAnticipada implements Promocion {
    private static final long serialVersionUID = 1L;
    private static final int DIAS_MINIMOS = 60;
    private static final double DESCUENTO = 0.15;

    @Override
    public String getNombre() { return "Reserva anticipada (15%)"; }

    @Override
    public double calcularDescuento(int cantidadPasajeros, long diasAnticipacion) {
        return diasAnticipacion > DIAS_MINIMOS ? DESCUENTO : 0.0;
    }
}
