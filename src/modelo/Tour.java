package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Tour del catálogo (clase abstracta). Cada categoría define sus propias reglas de validación de
 * pasajeros sobrescribiendo {@link #validarPasajero(Pasajero, LocalDate)} (principios OCP y LSP).
 */
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

    /** Un pasajero es válido para el tour si cumple los requisitos de su categoría. Nunca lanza excepciones (LSP). */
    public abstract boolean validarPasajero(Pasajero p, LocalDate fechaSalida);

    public abstract CategoriaTour getCategoria();

    /** RN: el seguro de viaje es obligatorio en los tours que lo requieran (aventura). */
    public boolean requiereSeguro() { return false; }

    public void agregarActividad(Actividad a) throws ReglaNegocioException {
        if (a == null) throw new ReglaNegocioException("La actividad es obligatoria.");
        if (a.getDia() > duracionDias) {
            throw new ReglaNegocioException("El día " + a.getDia() + " excede la duración del tour (" + duracionDias + " días).");
        }
        itinerario.add(a);
        itinerario.sort(Comparator.comparingInt(Actividad::getDia).thenComparingInt(Actividad::getOrden));
    }

    /** No se permiten tarifas con vigencias superpuestas: a cada fecha le corresponde una única tarifa. */
    public void agregarTarifa(TarifaTemporada t) throws ReglaNegocioException {
        if (t == null) throw new ReglaNegocioException("La tarifa es obligatoria.");
        for (TarifaTemporada existente : tarifas) {
            if (existente.seSuperponeCon(t)) {
                throw new ReglaNegocioException("La vigencia se superpone con la tarifa existente: " + existente);
            }
        }
        tarifas.add(t);
        tarifas.sort(Comparator.comparing(TarifaTemporada::getVigenteDesde));
    }

    /** CU07 (flujo 1a): si no hay tarifa vigente para la fecha de la salida, no se puede cotizar. */
    public TarifaTemporada obtenerTarifaVigente(LocalDate fechaSalida) throws ReglaNegocioException {
        return tarifas.stream()
                .filter(t -> t.cubre(fechaSalida))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "El tour " + codigo + " no tiene una tarifa vigente para la fecha " + fechaSalida + "."));
    }

    public void setDescripcion(String descripcion) throws ReglaNegocioException {
        this.descripcion = Validaciones.texto(descripcion, "descripción");
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
