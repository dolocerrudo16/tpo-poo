package modelo;

import java.io.Serializable;

public class PromocionGrupo implements Promocion, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDescuento(int cantPasajeros, long diasAnticipacion) {
        // RN08: 6 o más pasajeros, 10% de descuento
        return cantPasajeros >= 6 ? 0.10 : 0.0;
    }
}

