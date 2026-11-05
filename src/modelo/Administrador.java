package modelo;

import excepciones.ReglaNegocioException;
import modelo.clasesAbstractas.Empleado;

public class Administrador extends Empleado {

    public Administrador(int legajo, String nombre, String documento, String telefono) throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
    }

    @Override
    public double calcularRemuneracionVariable(double base) {
        return 0.0;
    }
}