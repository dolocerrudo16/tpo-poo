package modelo;

import java.io.Serializable;

/**
 * Estrategia de devolución ante una cancelación (polimorfismo GRASP / variaciones protegidas).
 * Reserva invoca calcularDevolucion sin conocer la política concreta.
 */
public interface PoliticaCancelacion extends Serializable {
    /**
     * @param totalPagado       importe efectivamente pagado por el cliente (sin recargos financieros)
     * @param diasAnticipacion  días que faltan para el inicio de la salida
     * @return importe a devolver al cliente
     */
    double calcularDevolucion(double totalPagado, long diasAnticipacion);

    String getNombre();
}
