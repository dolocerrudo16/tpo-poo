//SOLID (OCP)
//SOLID (ISP)
//GRASP (Bajo acoplamiento)
package modelo.interfaces;

public interface Promocion {
    double calcularDescuento(int cantPasajeros, long diasAnticipacion);
}