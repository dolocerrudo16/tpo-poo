package modelo.politicasPromos;

import modelo.interfaces.PoliticaCancelacion;

import java.io.Serializable;

//RN13: si la cancelación la provoca la agencia, se devuelve siempre el 100% de lo pagado.

public class CancelacionPorAgencia implements PoliticaCancelacion, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDevolucion(double totalPagado, long diasAnticipacion) {
        return totalPagado;
    }
}
