package modelo;

import excepciones.ReglaNegocioException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Guia extends Empleado {
    private static final long serialVersionUID = 1L;
    public static final double PORCENTAJE_ADICIONALES = 0.05;

    private double honorarioPorDia;
    private final List<String> idiomas = new ArrayList<>();
    private final Set<CategoriaTour> categoriasHabilitadas = EnumSet.noneOf(CategoriaTour.class);

    public Guia(int legajo, String nombre, String documento, String telefono, double honorarioPorDia)
            throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
        Validaciones.noNegativo(honorarioPorDia, "honorario por día");
        this.honorarioPorDia = honorarioPorDia;
    }

    public void agregarIdioma(String idioma) {
        if (idioma != null && !idioma.isBlank() && !idiomas.contains(idioma.trim())) {
            idiomas.add(idioma.trim());
        }
    }

    public void habilitarCategoria(CategoriaTour categoria) {
        if (categoria != null) categoriasHabilitadas.add(categoria);
    }

    public boolean estaHabilitadoPara(CategoriaTour categoria) {
        return categoria != null && categoriasHabilitadas.contains(categoria);
    }

    @Override
    public double calcularRemuneracionVariable(double adicionalesVendidos) {
        return adicionalesVendidos * PORCENTAJE_ADICIONALES;
    }

    public double calcularHonorario(int dias, double adicionalesVendidos) {
        return Dinero.redondear(honorarioPorDia * dias + calcularRemuneracionVariable(adicionalesVendidos));
    }

    @Override
    public String getRol() { return "Guía"; }

    public double getHonorarioPorDia() { return honorarioPorDia; }

    public void setHonorarioPorDia(double honorarioPorDia) throws ReglaNegocioException {
        Validaciones.noNegativo(honorarioPorDia, "honorario por día");
        this.honorarioPorDia = honorarioPorDia;
    }

    public List<String> getIdiomas() { return new ArrayList<>(idiomas); }

    public Set<CategoriaTour> getCategoriasHabilitadas() {
        return categoriasHabilitadas.isEmpty() ? EnumSet.noneOf(CategoriaTour.class) : EnumSet.copyOf(categoriasHabilitadas);
    }
}
