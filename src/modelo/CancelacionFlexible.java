package modelo;

import modelo.interfaces.PoliticaCancelacion;

import java.io.Serializable;

public class CancelacionFlexible implements PoliticaCancelacion, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDevolucion(double totalPagado, long diasAnticipacion) {
        if (diasAnticipacion >= 30) return totalPagado * 0.90;
        if (diasAnticipacion >= 15) return totalPagado * 0.50;
        if (diasAnticipacion >= 7) return totalPagado * 0.20;
        return 0.0;
    }
}