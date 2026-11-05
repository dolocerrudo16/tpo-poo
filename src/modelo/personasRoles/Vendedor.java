package modelo.personasRoles;

import excepciones.ReglaNegocioException;
import modelo.reservas.Dinero;
import modelo.clasesAbstractas.Empleado;

public class Vendedor extends Empleado {

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
    public double calcularRemuneracionVariable(double base) {
        return Dinero.redondear(base * porcentajeComision);
    }
}
