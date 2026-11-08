//Ventana emergente con campos de texto para cargar los datos de un nuevo cliente y guardarlo.
package presentacion.vistas;

import excepciones.ReglaNegocioException;
import presentacion.controladores.ControladorReserva;

import javax.swing.*;
import java.awt.*;

public class VistaRegistrarCliente extends JDialog {
    private JTextField txtNombre, txtDocumento, txtTelefono, txtEmail, txtPais;
    private ControladorReserva controlador;

    public VistaRegistrarCliente(JFrame parent, ControladorReserva controlador) {
        super(parent, "Registrar Nuevo Cliente", true);
        this.controlador = controlador;

        setSize(350, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel pnlFormulario = new JPanel(new GridLayout(5, 2, 5, 15));
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pnlFormulario.add(new JLabel("Nombre y Apellido:"));
        txtNombre = new JTextField();
        pnlFormulario.add(txtNombre);

        pnlFormulario.add(new JLabel("Documento:"));
        txtDocumento = new JTextField();
        pnlFormulario.add(txtDocumento);

        pnlFormulario.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        pnlFormulario.add(txtTelefono);

        pnlFormulario.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pnlFormulario.add(txtEmail);

        pnlFormulario.add(new JLabel("País:"));
        txtPais = new JTextField("Argentina");
        pnlFormulario.add(txtPais);

        JPanel pnlBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar Cliente");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardarCliente());
        btnCancelar.addActionListener(e -> dispose());

        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnCancelar);

        add(pnlFormulario, BorderLayout.CENTER);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    private void guardarCliente() {
        try {
            controlador.registrarCliente(
                    txtNombre.getText(),
                    txtDocumento.getText(),
                    txtTelefono.getText(),
                    txtEmail.getText(),
                    txtPais.getText()
            );
            JOptionPane.showMessageDialog(this, "¡Cliente registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }
}