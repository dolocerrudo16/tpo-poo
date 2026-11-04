package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;

public class Pago implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double RECARGO_TARJETA_CUOTAS = 0.07;

    private final int id;
    private final double monto;
    private final LocalDate fecha;
    private final MedioPago medioPago;
    private final int cuotas;

    public Pago(int id, double monto, LocalDate fecha, MedioPago medioPago, int cuotas) throws ReglaNegocioException {
        if (monto <= 0) throw new ReglaNegocioException("El importe del pago debe ser mayor a cero.");
        if (medioPago == null) throw new ReglaNegocioException("Debe indicar el medio de pago.");
        if (fecha == null) throw new ReglaNegocioException("La fecha del pago es obligatoria.");
        if (cuotas < 1) throw new ReglaNegocioException("La cantidad de cuotas debe ser al menos 1.");
        if (medioPago != MedioPago.TARJETA && cuotas != 1) {
            throw new ReglaNegocioException("Solo los pagos con tarjeta pueden hacerse en cuotas.");
        }
        this.id = id;
        this.monto = Dinero.redondear(monto);
        this.fecha = fecha;
        this.medioPago = medioPago;
        this.cuotas = cuotas;
    }

    public double getRecargo() {
        return (medioPago == MedioPago.TARJETA && cuotas >= 2) ? Dinero.redondear(monto * RECARGO_TARJETA_CUOTAS) : 0.0;
    }

    public double getMontoCobrado() { return Dinero.redondear(monto + getRecargo()); }

    public int getId() { return id; }
    public double getMonto() { return monto; }
    public LocalDate getFecha() { return fecha; }
    public MedioPago getMedioPago() { return medioPago; }
    public int getCuotas() { return cuotas; }

    @Override
    public String toString() {
        return "Pago #" + id + " " + fecha + " " + Dinero.formatear(monto) + " " + medioPago
                + (cuotas > 1 ? " en " + cuotas + " cuotas (recargo " + Dinero.formatear(getRecargo()) + ")" : "");
    }
}
