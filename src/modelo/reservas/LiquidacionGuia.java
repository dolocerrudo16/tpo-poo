//Registra el recibo del sueldo liquidado a un guía por una salida ya finalizada.
package modelo.reservas;

import java.io.Serializable;
import java.time.LocalDate;

public class LiquidacionGuia implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDate fecha;
    private double totalAbonado;

    public LiquidacionGuia(int id, LocalDate fecha, double totalAbonado) {
        this.id = id;
        this.fecha = fecha;
        this.totalAbonado = Dinero.redondear(totalAbonado);
    }

    public double getTotal() {
        return totalAbonado;
    }

    public int getId() { return id; }
    public LocalDate getFecha() { return fecha; }
}