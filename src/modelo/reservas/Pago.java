package modelo.reservas;

import excepciones.ReglaNegocioException;
import modelo.enums.MedioPago;

import java.io.Serializable;
import java.time.LocalDate;

public class Pago implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private double monto;
    private LocalDate fecha;
    private MedioPago medioPago;
    private int cuotas;

    public Pago(int id, double monto, LocalDate fecha, MedioPago medioPago, int cuotas) throws ReglaNegocioException {
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.medioPago = medioPago;
        this.cuotas = cuotas;
    }

    public double getRecargo() {
        return (medioPago == MedioPago.TARJETA && cuotas >= 2) ? monto * 0.07 : 0.0;
    }

    public int getId() { return id; }
    public double getMonto() { return monto; }
    public LocalDate getFecha() { return fecha; }
    public MedioPago getMedioPago() { return medioPago; }
    public int getCuotas() { return cuotas; }
}