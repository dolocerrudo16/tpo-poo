package modelo.personasRoles;

import excepciones.ReglaNegocioException;
import modelo.reservas.Dinero;
import modelo.Validaciones;
import modelo.clasesAbstractas.Empleado;
import modelo.enums.CategoriaTour;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Guia extends Empleado {

    private double honorarioPorDia;
    private List<String> idiomas = new ArrayList<>();
    private Set<CategoriaTour> categoriasHabilitadas = EnumSet.noneOf(CategoriaTour.class);

    public Guia(int legajo, String nombre, String documento, String telefono, double honorarioPorDia)
            throws ReglaNegocioException {
        super(legajo, nombre, documento, telefono);
        Validaciones.noNegativo(honorarioPorDia, "honorario por día");
        this.honorarioPorDia = honorarioPorDia;
    }

    @Override
    public double calcularRemuneracionVariable(double base) {
        return Dinero.redondear(base * 0.05); // 5% de los adicionales
    }

    public double calcularHonorario(int dias, double adicionalesVendidos) {
        return Dinero.redondear((honorarioPorDia * dias) + calcularRemuneracionVariable(adicionalesVendidos));
    }
}
