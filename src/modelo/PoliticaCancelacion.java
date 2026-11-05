package modelo;

public interface PoliticaCancelacion {
    double calcularDevolucion(double totalPagado, long diasAnticipacion);
}
