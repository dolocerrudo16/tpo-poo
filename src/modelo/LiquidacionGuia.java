package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class LiquidacionGuia implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDate fecha;

    public LiquidacionGuia(int id, LocalDate fecha) {
        this.id = id;
        this.fecha = fecha;
    }

    public double getTotal() {
        return 0.0;
    }

    public int getId() { return id; }
    public LocalDate getFecha() { return fecha; }
}