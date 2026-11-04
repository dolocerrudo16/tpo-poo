package modelo;

/** Política alternativa más rígida que la agencia puede configurar: 50% con más de 30 días, luego nada. */
public class CancelacionEstricta implements PoliticaCancelacion {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDevolucion(double totalPagado, long diasAnticipacion) {
        return diasAnticipacion > 30 ? totalPagado * 0.50 : 0.0;
    }

    @Override
    public String getNombre() { return "Estricta (50% con más de 30 días, luego 0%)"; }
}
