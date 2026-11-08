//Es el empleado que hace reservas y calcula su comisión (5%) sobre el total vendido.
//GRASP (Experto en informacion)
package modelo.personasRoles;

import excepciones.ReglaNegocioException;
import modelo.reservas.Dinero;
import modelo.clasesAbstractas.Empleado;

public class Vendedor extends Empleado {

    private static final long serialVersionUID = 1L;
    private double porcentajeComision = 0.05;

    public Vendedor(int legajo, String nombre, String documento, String telefono, double porcentajeComision)
            throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
        if (porcentajeComision < 0 || porcentajeComision > 1) {
            throw new ReglaNegocioException("La comisión debe estar entre 0 y 1 (por ejemplo 0.03 para 3%).");
        }
        this.porcentajeComision = porcentajeComision;
    }

    public Vendedor(int legajo, String nombre, String documento, String telefono)
            throws ReglaNegocioException {
        this(legajo, nombre, documento, telefono, 0.05);
    }

    public double calcularRemuneracionVariable(double base) {

        return Dinero.redondear(base * porcentajeComision);
    }

    public double getPorcentajeComision() {
        return porcentajeComision;
    }
}
