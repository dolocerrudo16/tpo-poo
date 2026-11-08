package modelo.politicasPromos;

import modelo.interfaces.PoliticaCancelacion;

import java.io.Serializable;

public class CancelacionPorAgencia implements PoliticaCancelacion, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDevolucion(double totalPagado, long diasAnticipacion) {
        return totalPagado;
    }
}
