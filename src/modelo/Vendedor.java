package modelo;

import excepciones.ReglaNegocioException;

public class Vendedor extends Empleado {
    private static final long serialVersionUID = 1L;
    public static final double COMISION_POR_DEFECTO = 0.03;

    private double porcentajeComision;

    public Vendedor(int legajo, String nombre, String documento, String telefono, double porcentajeComision)
            throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
        if (porcentajeComision < 0 || porcentajeComision > 1) {
            throw new ReglaNegocioException("La comisión debe estar entre 0 y 1 (por ejemplo 0.03 para 3%).");
        }
        this.porcentajeComision = porcentajeComision;
    }

    @Override
    public double calcularRemuneracionVariable(double montoVentasConfirmadas) {
        return Dinero.redondear(montoVentasConfirmadas * porcentajeComision);
    }

    @Override
    public String getRol() { return "Vendedor"; }

    public double getPorcentajeComision() { return porcentajeComision; }
}
