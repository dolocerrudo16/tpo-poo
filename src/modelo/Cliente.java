package modelo;

import excepciones.ReglaNegocioException;
import java.io.Serializable;
import java.util.regex.Pattern;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final int id;
    private String nombre;
    private final String documento;
    private String telefono;
    private String email;
    private String paisResidencia;

    public Cliente(int id, String nombre, String documento, String telefono, String email, String paisResidencia)
            throws ReglaNegocioException {
        this.id = id;
        this.documento = Validaciones.texto(documento, "documento");
        this.nombre = Validaciones.texto(nombre, "nombre y apellido");
        this.telefono = Validaciones.texto(telefono, "teléfono");
        this.email = validarEmail(email);
        this.paisResidencia = (paisResidencia == null || paisResidencia.isBlank()) ? "Argentina" : paisResidencia.trim();
    }

    private static String validarEmail(String email) throws ReglaNegocioException {
        String limpio = Validaciones.texto(email, "correo electrónico");
        if (!EMAIL.matcher(limpio).matches()) {
            throw new ReglaNegocioException("El correo electrónico no tiene un formato válido: " + limpio);
        }
        return limpio;
    }

    /** CU05 (flujo 3a): permite actualizar los datos de un cliente ya registrado. */
    public void actualizarDatos(String nombre, String telefono, String email, String paisResidencia)
            throws ReglaNegocioException {
        String nuevoNombre = Validaciones.texto(nombre, "nombre y apellido");
        String nuevoTelefono = Validaciones.texto(telefono, "teléfono");
        String nuevoEmail = validarEmail(email);
        this.nombre = nuevoNombre;
        this.telefono = nuevoTelefono;
        this.email = nuevoEmail;
        if (paisResidencia != null && !paisResidencia.isBlank()) this.paisResidencia = paisResidencia.trim();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getPaisResidencia() { return paisResidencia; }

    @Override
    public String toString() {
        return "Cliente #" + id + " - " + nombre + " (DNI " + documento + ") <" + email + ">";
    }
}
