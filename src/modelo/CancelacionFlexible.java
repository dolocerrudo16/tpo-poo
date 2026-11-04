package modelo;

/**
 * Escala del enunciado (RN12): 90% con 30 días o más, 50% entre 29 y 15 días, 20% entre 14 y 7 días
 * y sin devolución con menos de 7 días.
 * Supuesto S08: el día 30 exacto se incluye en el 90% ("hasta 30 días antes", según la consigna).
 */
public class CancelacionFlexible implements PoliticaCancelacion {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularDevolucion(double totalPagado, long diasAnticipacion) {
        if (diasAnticipacion >= 30) return totalPagado * 0.90;
        if (diasAnticipacion >= 15) return totalPagado * 0.50;
        if (diasAnticipacion >= 7) return totalPagado * 0.20;
        return 0.0;
    }

    @Override
    public String getNombre() { return "Flexible (90% / 50% / 20% / 0%)"; }
}
