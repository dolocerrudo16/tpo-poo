package modelo;

import excepciones.ReglaNegocioException;
import java.time.LocalDate;

/** Tour de aventura: exige edad mínima, indica nivel de dificultad y requiere seguro de viaje. */
public class TourAventura extends Tour {
    private static final long serialVersionUID = 1L;

    private final String nivelDificultad;
    private final int edadMinima;

    public TourAventura(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo,
                        int cupoMaximo, String nivelDificultad, int edadMinima) throws ReglaNegocioException {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
        // CU01 (flujo 3a): en aventura la edad mínima y la dificultad son obligatorias.
        this.nivelDificultad = Validaciones.texto(nivelDificultad, "nivel de dificultad");
        if (edadMinima < 1) throw new ReglaNegocioException("La edad mínima de un tour de aventura debe ser de al menos 1 año.");
        this.edadMinima = edadMinima;
    }

    /** Cumple si tiene la edad mínima a la fecha de la salida. Devuelve false (nunca lanza excepción). */
    @Override
    public boolean validarPasajero(Pasajero p, LocalDate fechaSalida) {
        if (p == null || fechaSalida == null) return false;
        return p.calcularEdad(fechaSalida) >= edadMinima;
    }

    @Override
    public CategoriaTour getCategoria() { return CategoriaTour.AVENTURA; }

    @Override
    public boolean requiereSeguro() { return true; }

    public String getNivelDificultad() { return nivelDificultad; }
    public int getEdadMinima() { return edadMinima; }

    @Override
    public String toString() { return super.toString() + " - dificultad " + nivelDificultad + ", edad mínima " + edadMinima; }
}
