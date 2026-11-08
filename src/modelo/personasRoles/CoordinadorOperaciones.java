//Representa al rol interno encargado de armar las salidas y asignar vehículos y guías.
package modelo.personasRoles;

import excepciones.ReglaNegocioException;
import modelo.clasesAbstractas.Empleado;

public class CoordinadorOperaciones extends Empleado {

    public CoordinadorOperaciones(int legajo, String nombre, String documento, String telefono)
            throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
    }

}
