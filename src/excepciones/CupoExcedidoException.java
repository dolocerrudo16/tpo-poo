package excepciones;

/** Se lanza cuando una reserva pide más lugares de los que tiene disponibles la salida. */
public class CupoExcedidoException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public CupoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
