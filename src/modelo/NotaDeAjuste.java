package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class NotaDeAjuste implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final double montoDevuelto;
    private final double montoRetenido;
    private final LocalDate fecha;
    private final String motivo;
    private final String mediosDevolucion;

    public NotaDeAjuste(int id, double montoDevuelto, double montoRetenido, LocalDate fecha, String motivo,
                        String mediosDevolucion) {
        this.id = id;
        this.montoDevuelto = montoDevuelto;
        this.montoRetenido = montoRetenido;
        this.fecha = fecha;
        this.motivo = motivo;
        this.mediosDevolucion = mediosDevolucion;
    }

    public int getId() { return id; }
    public double getMontoDevuelto() { return montoDevuelto; }
    public double getMontoRetenido() { return montoRetenido; }
    public LocalDate getFecha() { return fecha; }
    public String getMotivo() { return motivo; }
    public String getMediosDevolucion() { return mediosDevolucion; }

    @Override
    public String toString() {
        return "Nota de ajuste #" + id + " (" + fecha + "): devuelve " + Dinero.formatear(montoDevuelto) + ", retiene " + Dinero.formatear(montoRetenido)
                + " [" + mediosDevolucion + "] - " + motivo;
    }
}
