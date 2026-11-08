//si una reserva se contrata bajo condiciones promocionales
package modelo.politicasPromos;

import modelo.interfaces.PoliticaCancelacion;

import java.io.Serializable;

public class CancelacionEstricta implements PoliticaCancelacion, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDevolucion(double totalPagado, long diasAnticipacion) {
        return diasAnticipacion > 30 ? totalPagado * 0.50 : 0.0;
    }
}