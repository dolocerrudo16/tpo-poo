package modelo;

/** RN13: si la cancelación la provoca la agencia, se devuelve siempre el 100% de lo pagado. */
public class CancelacionPorAgencia implements PoliticaCancelacion {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDevolucion(double totalPagado, long diasAnticipacion) { return totalPagado; }

    @Override
    public String getNombre() { return "Cancelación por la agencia (100%)"; }
}
