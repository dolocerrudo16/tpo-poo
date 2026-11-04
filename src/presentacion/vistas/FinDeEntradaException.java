package presentacion.vistas;

/** Se lanza cuando se cierra la entrada estándar (por ejemplo Ctrl+D) para salir del menú de forma ordenada. */
public class FinDeEntradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FinDeEntradaException() {
        super("Fin de la entrada de datos.");
    }
}
