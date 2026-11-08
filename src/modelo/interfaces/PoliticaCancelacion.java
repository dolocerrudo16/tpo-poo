//SOLID (OCP)
//SOLID (DIP)
//GRASP(Variaciones protegidas)
package modelo.interfaces;

public interface PoliticaCancelacion {
    double calcularDevolucion(double totalPagado, long diasAnticipacion);
}
