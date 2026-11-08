// Representa al directivo que liquida sueldos y consulta las estadísticas del negocio.
package modelo.personasRoles;

import excepciones.ReglaNegocioException;
import modelo.clasesAbstractas.Empleado;

public class Administrador extends Empleado {

    public Administrador(int legajo, String nombre, String documento, String telefono) throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
    }

}