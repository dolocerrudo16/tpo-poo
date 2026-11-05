package modelo;

import excepciones.ReglaNegocioException;
import modelo.enums.CategoriaTour;

import java.time.LocalDate;

public class TourAventura extends Tour {

    private String nivelDificultad;
    private int edadMinima;

    public TourAventura(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo, int cupoMaximo, String nivelDificultad, int edadMinima) throws ReglaNegocioException {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
        this.nivelDificultad = Validaciones.texto(nivelDificultad, "nivel de dificultad");
        if (edadMinima < 1) throw new ReglaNegocioException("La edad mínima debe ser de al menos 1 año.");
        this.edadMinima = edadMinima;
    }

    @Override
    public boolean validarPasajero(Pasajero p, LocalDate fechaSalida) {
        if (p == null || fechaSalida == null) return false;
        return p.calcularEdad(fechaSalida) >= edadMinima;
    }

    @Override
    public CategoriaTour getCategoria() {
        return CategoriaTour.AVENTURA;
    }

    @Override
    public boolean requiereSeguro() {
        return true;
    }

    public String getNivelDificultad() { return nivelDificultad; }
    public int getEdadMinima() { return edadMinima; }
}
