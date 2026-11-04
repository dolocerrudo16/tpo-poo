package modelo;

import java.io.Serializable;

public interface Promocion extends Serializable {
    String getNombre();

    double calcularDescuento(int cantidadPasajeros, long diasAnticipacion);
}
