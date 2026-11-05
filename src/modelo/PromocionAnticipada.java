package modelo;

import java.io.Serializable;

public class PromocionAnticipada implements Promocion, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDescuento(int cantPasajeros, long diasAnticipacion) {
        // RN08: >60 días de anticipación, 15% de descuento
        return diasAnticipacion > 60 ? 0.15 : 0.0;
    }
}