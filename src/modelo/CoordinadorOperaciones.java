package modelo;

import excepciones.ReglaNegocioException;

public class CoordinadorOperaciones extends Empleado {
    private static final long serialVersionUID = 1L;

    public CoordinadorOperaciones(int legajo, String nombre, String documento, String telefono)
            throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
    }

    @Override
    public double calcularRemuneracionVariable(double baseCalculo) { return 0.0; }

    @Override
    public String getRol() { return "Coordinador de operaciones"; }
}
