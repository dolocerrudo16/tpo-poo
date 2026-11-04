package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Reloj implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate fechaFijada;

    public LocalDate hoy() {
        return fechaFijada != null ? fechaFijada : LocalDate.now();
    }

    public void fijar(LocalDate fecha) { this.fechaFijada = fecha; }

    public void usarFechaReal() { this.fechaFijada = null; }

    public boolean estaSimulada() { return fechaFijada != null; }
}
