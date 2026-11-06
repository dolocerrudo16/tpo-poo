package excepciones;

//Excepción propia del sistema: se lanza cada vez que una operación viola una regla de negocio (cupo, estados, plazos, datos obligatorios, unicidad, etc.).

public class ReglaNegocioException extends Exception {
    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
