package presentacion.vistas;

import excepciones.ReglaNegocioException;
import modelo.enums.Temporada;
import modelo.enums.TipoPasajero;
import modelo.personasRoles.Cliente;
import modelo.personasRoles.Pasajero;
import modelo.personasRoles.Vendedor;
import modelo.reservas.Reserva;
import modelo.viajesServicios.Salida;
import modelo.viajesServicios.TarifaTemporada;
import presentacion.controladores.ControladorReserva;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VistaCrearReserva extends JDialog {
    private JComboBox<Cliente> cmbClientes;
    private JComboBox<Salida> cmbSalidas;
    private JTextField txtNombrePax, txtDocPax, txtFechaNacPax, txtContactoPax;
    private JComboBox<TipoPasajero> cmbTipoPax;
    private ControladorReserva controlador;
    private Vendedor vendedorDemo;

    public VistaCrearReserva(JFrame parent, ControladorReserva controlador) {
        super(parent, "Crear Nueva Reserva (CU06)", true);
        this.controlador = controlador;

        try {
            this.vendedorDemo = new Vendedor(1, "Dolores Ventas", "33445566", "11-4455", 0.05);
        } catch (Exception ignored) {}

        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel pnlFormulario = new JPanel(new GridLayout(7, 2, 5, 12));
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pnlFormulario.add(new JLabel("Cliente Titular:"));
        cmbClientes = new JComboBox<>(controlador.getClientes().toArray(new Cliente[0]));
        pnlFormulario.add(cmbClientes);

        pnlFormulario.add(new JLabel("Salida Disponible:"));
        cmbSalidas = new JComboBox<>(controlador.getSalidas().toArray(new Salida[0]));
        pnlFormulario.add(cmbSalidas);

        pnlFormulario.add(new JLabel("Nombre Pasajero:"));
        txtNombrePax = new JTextField();
        pnlFormulario.add(txtNombrePax);

        pnlFormulario.add(new JLabel("DNI Pasajero:"));
        txtDocPax = new JTextField();
        pnlFormulario.add(txtDocPax);

        pnlFormulario.add(new JLabel("Nacimiento (YYYY-MM-DD):"));
        txtFechaNacPax = new JTextField("1995-05-15");
        pnlFormulario.add(txtFechaNacPax);

        pnlFormulario.add(new JLabel("Tipo de Pasajero:"));
        cmbTipoPax = new JComboBox<>(TipoPasajero.values());
        pnlFormulario.add(cmbTipoPax);

        pnlFormulario.add(new JLabel("Contacto Emergencia:"));
        txtContactoPax = new JTextField("11-9999-8888");
        pnlFormulario.add(txtContactoPax);

        JPanel pnlBotones = new JPanel();
        JButton btnGuardar = new JButton("Confirmar Reserva");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> crearReserva());
        btnCancelar.addActionListener(e -> dispose());

        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnCancelar);

        add(pnlFormulario, BorderLayout.CENTER);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    private void crearReserva() {
        try {
            Cliente cliente = (Cliente) cmbClientes.getSelectedItem();
            Salida salida = (Salida) cmbSalidas.getSelectedItem();

            if (cliente == null || salida == null) {
                JOptionPane.showMessageDialog(this, "Primero debes registrar al menos un Cliente y programar una Salida.", "Datos Faltantes", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Aseguramos que el tour de la salida tenga tarifa vigente para que CalculadoraTarifa no falle
            if (salida.getTour().getTarifas().isEmpty()) {
                salida.getTour().agregarTarifa(new TarifaTemporada(
                        Temporada.ALTA,
                        LocalDate.now().minusDays(10),
                        LocalDate.now().plusYears(2),
                        100000.0
                ));
            }

            LocalDate fechaNac = LocalDate.parse(txtFechaNacPax.getText().trim());
            TipoPasajero tipo = (TipoPasajero) cmbTipoPax.getSelectedItem();

            Pasajero pasajero = new Pasajero(
                    txtNombrePax.getText(),
                    txtDocPax.getText(),
                    fechaNac,
                    tipo,
                    txtContactoPax.getText(),
                    "Ninguna"
            );

            List<Pasajero> pasajeros = new ArrayList<>();
            pasajeros.add(pasajero);

            Reserva nueva = controlador.crearReserva(cliente, salida, vendedorDemo, pasajeros, LocalDate.now());

            JOptionPane.showMessageDialog(this,
                    "¡Reserva #" + nueva.getNumero() + " creada con éxito!\n" +
                            "Total calculado: $" + nueva.getTotal() + "\n" +
                            "Estado inicial: " + nueva.getEstado() + "\n" +
                            "Cupo restante en la salida: " + salida.calcularCupoDisponible(),
                    "Reserva Registrada",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "El formato de fecha de nacimiento debe ser YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Regla de Negocio", JOptionPane.ERROR_MESSAGE);
        }
    }
}