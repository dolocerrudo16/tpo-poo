//SOLID: Liskov (LSP)
//Bajo acoplamiento (GRASP)
package excepciones;

public class CupoExcedidoException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public CupoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
