package modelo;

import excepciones.ReglaNegocioException;

public class Cliente {

    private int id;
    private String nombre;
    private String documento;
    private String telefono;
    private String email;
    private String paisResidencia;

    public Cliente(int id, String nombre, String documento, String telefono, String email, String paisResidencia)
            throws ReglaNegocioException {
        this.id = id;
        this.documento = Validaciones.texto(documento, "documento");
        this.nombre = Validaciones.texto(nombre, "nombre y apellido");
        this.telefono = Validaciones.texto(telefono, "teléfono");

        // Validación directa en el constructor para no agregar métodos extra
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ReglaNegocioException("El correo electrónico no tiene un formato válido.");
        }
        this.email = email;

        this.paisResidencia = (paisResidencia == null || paisResidencia.isBlank()) ? "Argentina" : paisResidencia.trim();
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