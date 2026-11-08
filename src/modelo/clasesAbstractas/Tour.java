//CU01 (Administrar Catálogo de Tours y Tarifas)
//Guarda la ficha técnica de un viaje (duración, cupos mínimo y máximo, itinerario y tarifas según temporada).
//SOLID (OCP)
//GRASP (LSP)
package modelo.clasesAbstractas;

import modelo.utilidades.Validaciones;
import modelo.enums.CategoriaTour;
import modelo.personasRoles.Pasajero;
import modelo.viajesServicios.Actividad;
import modelo.viajesServicios.TarifaTemporada;
import excepciones.ReglaNegocioException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Tour implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String codigo;
    protected String nombre;
    protected String descripcion;
    protected int duracionDias;
    protected int cupoMinimo;
    protected int cupoMaximo;
    protected final List<Actividad> itinerario = new ArrayList<>();
    protected final List<TarifaTemporada> tarifas = new ArrayList<>();

    protected Tour(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo, int cupoMaximo)
            throws ReglaNegocioException {
        this.codigo = Validaciones.texto(codigo, "código").toUpperCase();
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.descripcion = Validaciones.texto(descripcion, "descripción");
        if (duracionDias < 1) throw new ReglaNegocioException("La duración debe ser de al menos 1 día.");
        if (cupoMinimo < 1) throw new ReglaNegocioException("El cupo mínimo debe ser de al menos 1.");
        if (cupoMaximo < cupoMinimo) throw new ReglaNegocioException("El cupo máximo no puede ser menor al cupo mínimo.");
        this.duracionDias = duracionDias;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
    }

    public abstract boolean validarPasajero(Pasajero p, LocalDate fechaSalida);

    public abstract CategoriaTour getCategoria();

    // RN: el seguro de viaje es obligatorio en los tours que lo requieran (aventura).
    public abstract boolean requiereSeguro();

    public void agregarActividad(Actividad a) throws ReglaNegocioException {
        if (a == null) throw new ReglaNegocioException("La actividad es obligatoria.");
        if (a.getDia() > duracionDias) {
            throw new ReglaNegocioException("El día " + a.getDia() + " excede la duración del tour (" + duracionDias + " días).");
        }
        itinerario.add(a);
        java.util.Collections.sort(itinerario, new Comparator<Actividad>() {
            @Override
            public int compare(Actividad a1, Actividad a2) {
                if (a1.getDia() != a2.getDia()) {
                    return Integer.compare(a1.getDia(), a2.getDia());
                }
                return Integer.compare(a1.getOrden(), a2.getOrden());
            }
        });    }

    //CU07 a cada fecha le corresponde una única tarifa.
    public void agregarTarifa(TarifaTemporada t) throws ReglaNegocioException {
        if (t == null) throw new ReglaNegocioException("La tarifa es obligatoria.");
        for (TarifaTemporada existente : tarifas) {
            if (existente.seSuperponeCon(t)) {
                throw new ReglaNegocioException("La vigencia se superpone con la tarifa existente: " + existente);
            }
        }
        tarifas.add(t);
        java.util.Collections.sort(tarifas, new Comparator<TarifaTemporada>() {
            @Override
            public int compare(TarifaTemporada t1, TarifaTemporada t2) {
                return t1.getVigenteDesde().compareTo(t2.getVigenteDesde());
            }
        });    }

    // CU07 (flujo 1a): si no hay tarifa vigente para la fecha de la salida, no se puede cotizar.
    public TarifaTemporada obtenerTarifaVigente(LocalDate fechaSalida) throws ReglaNegocioException {
        for (TarifaTemporada t : tarifas) {
            if (t.cubre(fechaSalida)) {
                return t;
            }
        }
        throw new ReglaNegocioException(
                "El tour " + codigo + " no tiene una tarifa vigente para la fecha " + fechaSalida + ".");
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getDuracionDias() { return duracionDias; }
    public int getCupoMinimo() { return cupoMinimo; }
    public int getCupoMaximo() { return cupoMaximo; }
    public List<Actividad> getItinerario() { return new ArrayList<>(itinerario); }
    public List<TarifaTemporada> getTarifas() { return new ArrayList<>(tarifas); }

    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - " + getCategoria() + " - " + duracionDias + " día(s) - cupo "
                + cupoMinimo + " a " + cupoMaximo;
    }
}