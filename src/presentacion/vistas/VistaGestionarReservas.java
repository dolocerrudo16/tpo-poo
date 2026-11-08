package presentacion.vistas;

import excepciones.ReglaNegocioException;
import modelo.enums.EstadoReserva;
import modelo.enums.MedioPago;
import modelo.reservas.Pago;
import modelo.reservas.Reserva;
import presentacion.controladores.ControladorReserva;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VistaGestionarReservas extends JDialog {
    private JComboBox<Reserva> cmbReservas;
    private JTextArea areaDetalle;
    private JTextField txtMotivo;
    private ControladorReserva controlador;

    public VistaGestionarReservas(JFrame parent, ControladorReserva controlador) {
        super(parent, "Ver y Cancelar Reservas (CU08 / CU13)", true);
        this.controlador = controlador;

        setSize(520, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Panel superior: selector de reserva
        JPanel pnlSuperior = new JPanel(new BorderLayout(5, 5));
        pnlSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        pnlSuperior.add(new JLabel("Seleccionar Reserva:"), BorderLayout.NORTH);

        cmbReservas = new JComboBox<>();
        cmbReservas.addActionListener(e -> actualizarDetalle());
        pnlSuperior.add(cmbReservas, BorderLayout.CENTER);

        // Panel central: detalle completo de la reserva seleccionada
        areaDetalle = new JTextArea();
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(areaDetalle);
        scroll.setBorder(BorderFactory.createTitledBorder("Estado y Detalle de la Reserva"));

        // Panel inferior: motivo de cancelación y botones de acción
        JPanel pnlInferior = new JPanel(new BorderLayout(5, 10));
        pnlInferior.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JPanel pnlMotivo = new JPanel(new BorderLayout(5, 5));
        pnlMotivo.add(new JLabel("Motivo de cancelación:"), BorderLayout.WEST);
        txtMotivo = new JTextField("Motivos personales");
        pnlMotivo.add(txtMotivo, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnPagarSena = new JButton("Pagar Seña 50% (CU08)");
        JButton btnCancelarReserva = new JButton("Cancelar Reserva (CU13)");
        JButton btnCerrar = new JButton("Cerrar");

        btnPagarSena.addActionListener(e -> pagarSenaDemo());
        btnCancelarReserva.addActionListener(e -> cancelarReserva());
        btnCerrar.addActionListener(e -> dispose());

        pnlBotones.add(btnPagarSena);
        pnlBotones.add(btnCancelarReserva);
        pnlBotones.add(btnCerrar);

        pnlInferior.add(pnlMotivo, BorderLayout.NORTH);
        pnlInferior.add(pnlBotones, BorderLayout.SOUTH);

        add(pnlSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(pnlInferior, BorderLayout.SOUTH);

        recargarCombo();
    }

    private void recargarCombo() {
        cmbReservas.removeAllItems();
        List<Reserva> reservas = controlador.getReservas();
        for (Reserva r : reservas) {
            cmbReservas.addItem(r);
        }
        actualizarDetalle();
    }

    private void actualizarDetalle() {
        Reserva r = (Reserva) cmbReservas.getSelectedItem();
        if (r == null) {
            areaDetalle.setText("No hay reservas registradas en el sistema.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva #").append(r.getNumero()).append("\n");
        sb.append("Estado actual:    ").append(r.getEstado()).append("\n");
        sb.append("Cliente Titular:  ").append(r.getCliente().getNombre()).append("\n");
        sb.append("Tour / Salida:    ").append(r.getSalida().getTour().getNombre())
                .append(" (Inicio: ").append(r.getSalida().getFechaInicio()).append(")\n");
        sb.append("Pasajeros:        ").append(r.getPasajeros().size()).append("\n");
        sb.append("Total Reserva:    $").append(r.getTotal()).append("\n");
        sb.append("Pagos realizados: ").append(r.getPagos().size()).append("\n");
        sb.append("Cupo libre Salida:").append(r.getSalida().calcularCupoDisponible()).append(" lugares\n");
        areaDetalle.setText(sb.toString());
    }

    private void pagarSenaDemo() {
        Reserva r = (Reserva) cmbReservas.getSelectedItem();
        if (r == null) return;

        try {
            double montoSena = r.getTotal() * 0.50;
            Pago pago = controlador.registrarPago(r, montoSena, MedioPago.EFECTIVO, 1, LocalDate.now());
            JOptionPane.showMessageDialog(this,
                    "Pago registrado por $" + pago.getMonto() + "\n" +
                            "Nuevo estado de la reserva: " + r.getEstado(),
                    "Pago Exitoso (CU08)", JOptionPane.INFORMATION_MESSAGE);
            actualizarDetalle();
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Pagar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarReserva() {
        Reserva r = (Reserva) cmbReservas.getSelectedItem();
        if (r == null) return;

        if (r.getEstado() == EstadoReserva.CANCELADA || r.getEstado() == EstadoReserva.ANULADA) {
            JOptionPane.showMessageDialog(this, "La reserva ya se encuentra " + r.getEstado() + ".", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String motivo = txtMotivo.getText().trim().isEmpty() ? "Cancelación solicitada" : txtMotivo.getText().trim();
            double devuelto = controlador.cancelarReserva(r, motivo, LocalDate.now());

            JOptionPane.showMessageDialog(this,
                    "¡Reserva #" + r.getNumero() + " CANCELADA con éxito!\n" +
                            "Motivo: " + motivo + "\n" +
                            "Reintegro calculado según política: $" + devuelto + "\n" +
                            "Cupo disponible en la salida ahora: " + r.getSalida().calcularCupoDisponible(),
                    "Cancelación Confirmada (CU13)", JOptionPane.INFORMATION_MESSAGE);
            actualizarDetalle();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Cancelar", JOptionPane.ERROR_MESSAGE);
        }
    }
}