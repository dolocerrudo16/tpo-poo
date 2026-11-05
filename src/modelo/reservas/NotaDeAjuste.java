package modelo.reservas;

import java.io.Serializable;
import java.time.LocalDate;

public class NotaDeAjuste implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private double montoDevuelto;
    private double montoRetenido;
    private LocalDate fecha;
    private String motivo;
    private String mediosDevolucion;

    public NotaDeAjuste(int id, double montoDevuelto, double montoRetenido, LocalDate fecha, String motivo, String mediosDevolucion) {
        this.id = id;
        this.montoDevuelto = montoDevuelto;
        this.montoRetenido = montoRetenido;
        this.fecha = fecha;
        this.motivo = motivo;
        this.mediosDevolucion = mediosDevolucion;
    }
}
