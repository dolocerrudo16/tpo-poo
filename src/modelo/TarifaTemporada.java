package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Tarifa base por pasajero de un tour para una temporada, con su período de vigencia (ambos extremos incluidos).
 * La tarifa que corresponde a una salida es la que cubre su fecha de inicio.
 */
public class TarifaTemporada implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Temporada temporada;
    private final LocalDate vigenteDesde;
    private final LocalDate vigenteHasta;
    private final double valor;

    public TarifaTemporada(Temporada temporada, LocalDate vigenteDesde, LocalDate vigenteHasta, double valor)
            throws ReglaNegocioException {
        if (temporada == null) throw new ReglaNegocioException("La temporada es obligatoria.");
        if (vigenteDesde == null || vigenteHasta == null || vigenteHasta.isBefore(vigenteDesde)) {
            throw new ReglaNegocioException("La vigencia de la tarifa es inválida (la fecha hasta no puede ser anterior a la desde).");
        }
        Validaciones.positivo(valor, "valor de la tarifa");
        this.temporada = temporada;
        this.vigenteDesde = vigenteDesde;
        this.vigenteHasta = vigenteHasta;
        this.valor = valor;
    }

    public boolean cubre(LocalDate fecha) {
        return !fecha.isBefore(vigenteDesde) && !fecha.isAfter(vigenteHasta);
    }

    public boolean seSuperponeCon(TarifaTemporada otra) {
        return !vigenteDesde.isAfter(otra.vigenteHasta) && !vigenteHasta.isBefore(otra.vigenteDesde);
    }

    public Temporada getTemporada() { return temporada; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public double getValor() { return valor; }

    @Override
    public String toString() { return temporada + " " + Dinero.formatear(valor) + " (" + vigenteDesde + " a " + vigenteHasta + ")"; }
}
