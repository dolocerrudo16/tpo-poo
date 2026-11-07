package modelo.personasRoles;

import excepciones.ReglaNegocioException;
import modelo.reservas.Dinero;
import modelo.Validaciones;
import modelo.clasesAbstractas.Empleado;
import modelo.enums.CategoriaTour;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Guia extends Empleado {

    private double honorarioPorDia;
    private List<String> idiomas = new ArrayList<>();
    private Set<CategoriaTour> categoriasHabilitadas = new HashSet<>();
    public Guia(int legajo, String nombre, String documento, String telefono, double honorarioPorDia)
            throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
        Validaciones.noNegativo(honorarioPorDia, "honorario por día");
        this.honorarioPorDia = honorarioPorDia;
    }


    public double calcularRemuneracionVariable(double base) {
        return Dinero.redondear(base * 0.05); // 5% de los adicionales
    }

    public double calcularHonorario(int dias, double adicionalesVendidos) {
        return Dinero.redondear((honorarioPorDia * dias) + calcularRemuneracionVariable(adicionalesVendidos));
    }

    public boolean estaHabilitadoPara(CategoriaTour categoria) {
        return categoriasHabilitadas != null && categoriasHabilitadas.contains(categoria);
    }

    public void habilitarCategoria(CategoriaTour categoria) {
        if (categoria != null) {
            categoriasHabilitadas.add(categoria);
        }
    }
}
