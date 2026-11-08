//Ventana emergente para elegir fecha, cupo y crear una nueva salida operativa.
package presentacion.vistas;

import excepciones.ReglaNegocioException;
import presentacion.controladores.ControladorOperaciones;
import modelo.toursEspecificos.TourAventura;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class VistaProgramarSalida extends JDialog {
    private JTextField txtCodigo, txtInicio, txtFin, txtCupo;
    private ControladorOperaciones controlador;
    private TourAventura tourDemo; // Tour temporal para cumplir con la firma del controlador

    public VistaProgramarSalida(JFrame parent, ControladorOperaciones controlador) {
        super(parent, "Programar Salida", true);
        this.controlador = controlador;

        try {
            this.tourDemo = new TourAventura("T-AV01", "Trekking Demo", "Caminata", 5, 2, 10, "Alta", 18);
        } catch (ReglaNegocioException ignored) {}

        setSize(350, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel pnlFormulario = new JPanel(new GridLayout(4, 2, 5, 15));
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pnlFormulario.add(new JLabel("Código Tour:"));
        txtCodigo = new JTextField("T-AV01");
        pnlFormulario.add(txtCodigo);

        pnlFormulario.add(new JLabel("Inicio (YYYY-MM-DD):"));
        txtInicio = new JTextField(LocalDate.now().plusDays(30).toString());
        pnlFormulario.add(txtInicio);

        pnlFormulario.add(new JLabel("Fin (YYYY-MM-DD):"));
        txtFin = new JTextField(LocalDate.now().plusDays(34).toString());
        pnlFormulario.add(txtFin);

        pnlFormulario.add(new JLabel("Cupo Máximo:"));
        txtCupo = new JTextField("10");
        pnlFormulario.add(txtCupo);

        JPanel pnlBotones = new JPanel();
        JButton btnGuardar = new JButton("Programar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> programarSalida());
        btnCancelar.addActionListener(e -> dispose());

        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnCancelar);

        add(pnlFormulario, BorderLayout.CENTER);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    private void programarSalida() {
        try {
            LocalDate inicio = LocalDate.parse(txtInicio.getText());
            LocalDate fin = LocalDate.parse(txtFin.getText());
            int cupo = Integer.parseInt(txtCupo.getText());

            controlador.programarSalida(txtCodigo.getText(), inicio, fin, cupo, tourDemo, LocalDate.now());

            JOptionPane.showMessageDialog(this, "¡Salida programada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "El formato de fecha debe ser YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El cupo debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Regla de Negocio", JOptionPane.ERROR_MESSAGE);
        }
    }
}