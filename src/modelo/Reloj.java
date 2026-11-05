package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Reloj implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate fechaActual = LocalDate.now();

    public LocalDate hoy() {
        return fechaActual;
    }

    public void fijar(LocalDate fecha) {
        this.fechaActual = fecha;
    }
}