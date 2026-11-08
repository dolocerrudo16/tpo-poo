//Es el comprobante emitido cuando se cancela una reserva; registra cuánta plata se le devolvió al cliente y cuánto retuvo la agencia como penalidad.
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

    public int getId() { return id; }
    public double getMontoDevuelto() { return montoDevuelto; }
    public double getMontoRetenido() { return montoRetenido; }
    public LocalDate getFecha() { return fecha; } 
    public String getMotivo() { return motivo; }
    public String getMediosDevolucion() { return mediosDevolucion; }
}