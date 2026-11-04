package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pasajero implements Serializable, Comparable<Pasajero> {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String documento;
    private final LocalDate fechaNacimiento;
    private final TipoPasajero tipo;
    private final String contactoEmergencia;
    private final String restricciones;
    private final List<ServicioAdicional> servicios = new ArrayList<>();

    public Pasajero(String nombre, String documento, LocalDate fechaNacimiento, TipoPasajero tipo,
                    String contactoEmergencia, String restricciones) throws ReglaNegocioException {
        this.nombre = Validaciones.texto(nombre, "nombre del pasajero");
        this.documento = Validaciones.texto(documento, "documento del pasajero");
        if (fechaNacimiento == null) {
            throw new ReglaNegocioException("La fecha de nacimiento del pasajero es obligatoria.");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser futura.");
        }
        this.fechaNacimiento = fechaNacimiento;
        if (tipo == null) {
            throw new ReglaNegocioException("El tipo de pasajero es obligatorio.");
        }
        this.tipo = tipo;
        this.contactoEmergencia = Validaciones.texto(contactoEmergencia, "contacto de emergencia");
        this.restricciones = (restricciones == null || restricciones.isBlank()) ? "Ninguna" : restricciones.trim();
    }

    public int calcularEdad(LocalDate fecha) {
        return Period.between(fechaNacimiento, fecha).getYears();
    }

    public boolean esTipoCoherente(LocalDate fechaViaje) {
        return tipo.esCoherenteConEdad(calcularEdad(fechaViaje));
    }

    public void agregarServicio(ServicioAdicional servicio) {
        if (servicio == null) return;
        if (servicio.isSeguroViaje() && tieneSeguro()) return;
        servicios.add(servicio);
    }

    public boolean quitarServicio(ServicioAdicional servicio) { return servicios.remove(servicio); }

    public boolean tieneSeguro() { return servicios.stream().anyMatch(ServicioAdicional::isSeguroViaje); }

    public double getTotalServicios() { return servicios.stream().mapToDouble(ServicioAdicional::getPrecio).sum(); }

    public List<ServicioAdicional> getServicios() { return new ArrayList<>(servicios); }

    @Override
    public int compareTo(Pasajero otro) {
        int porNombre = this.nombre.compareToIgnoreCase(otro.nombre);
        return porNombre != 0 ? porNombre : this.documento.compareTo(otro.documento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pasajero)) return false;
        return documento.equals(((Pasajero) o).documento);
    }

    @Override
    public int hashCode() { return Objects.hash(documento); }

    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public TipoPasajero getTipo() { return tipo; }
    public String getContactoEmergencia() { return contactoEmergencia; }
    public String getRestricciones() { return restricciones; }

    @Override
    public String toString() { return nombre + " (DNI " + documento + ", " + tipo + ")"; }
}
