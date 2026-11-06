package modelo.viajesServicios;

import excepciones.ReglaNegocioException;
import modelo.Validaciones;
import modelo.enums.Temporada;

import java.io.Serializable;
import java.time.LocalDate;

public class TarifaTemporada implements Serializable {
    private static final long serialVersionUID = 1L;

    private Temporada temporada;
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta;
    private double valor;

    public TarifaTemporada(Temporada temporada, LocalDate vigenteDesde, LocalDate vigenteHasta, double valor)
            throws ReglaNegocioException {
        if (temporada == null) throw new ReglaNegocioException("La temporada es obligatoria.");
        if (vigenteDesde == null || vigenteHasta == null || vigenteHasta.isBefore(vigenteDesde)) {
            throw new ReglaNegocioException("La vigencia de la tarifa es inválida.");
        }
        Validaciones.positivo(valor, "valor de la tarifa");

        this.temporada = temporada;
        this.vigenteDesde = vigenteDesde;
        this.vigenteHasta = vigenteHasta;
        this.valor = valor;
    }

    // Métodos internos (package-private) para que Tour los use sin figurar en el UML público
    public boolean cubre(LocalDate fecha) {
        return !fecha.isBefore(vigenteDesde) && !fecha.isAfter(vigenteHasta);
    }

    public boolean seSuperponeCon(TarifaTemporada otra) {
        return !vigenteDesde.isAfter(otra.vigenteHasta) && !vigenteHasta.isBefore(otra.vigenteDesde);
    }

    // Getters indispensables
    public Temporada getTemporada() { return temporada; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public double getValor() { return valor; }
}