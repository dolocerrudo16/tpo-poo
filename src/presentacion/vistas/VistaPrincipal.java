package presentacion.vistas;

import presentacion.controladores.ControladorAdministracion;
import presentacion.controladores.ControladorOperaciones;
import presentacion.controladores.ControladorReserva;
import javax.swing.*;
import java.awt.*;

public class VistaPrincipal extends JFrame {
    private ControladorReserva ctrlReserva;
    private ControladorOperaciones ctrlOperaciones;
    private ControladorAdministracion ctrlAdministracion;

    public VistaPrincipal(ControladorReserva ctrlReserva, ControladorOperaciones ctrlOperaciones, ControladorAdministracion ctrlAdmin) {
        this.ctrlReserva = ctrlReserva;
        this.ctrlOperaciones = ctrlOperaciones;
        this.ctrlAdministracion = ctrlAdmin;

        setTitle("Agencia Rumbo Sur - Sistema de Gestión");
        setSize(400, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel pnlBotones = new JPanel(new GridLayout(6, 1, 10, 10));
        pnlBotones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnRegistrarCliente = new JButton("Registrar Cliente");
        JButton btnProgramarSalida = new JButton("Programar Salida");
        JButton btnCrearReserva = new JButton("Crear Reserva");
        JButton btnGestionarReservas = new JButton("Ver / Cancelar Reservas");
        JButton btnReportes = new JButton("Ver Reportes (Consola)");
        JButton btnSalir = new JButton("Salir del Sistema");

        btnRegistrarCliente.addActionListener(e -> {
            VistaRegistrarCliente vistaCliente = new VistaRegistrarCliente(this, ctrlReserva);
            vistaCliente.setVisible(true);
        });

        btnProgramarSalida.addActionListener(e -> {
            VistaProgramarSalida vistaSalida = new VistaProgramarSalida(this, ctrlOperaciones);
            vistaSalida.setVisible(true);
        });

        btnCrearReserva.addActionListener(e -> {
            VistaCrearReserva vistaReserva = new VistaCrearReserva(this, ctrlReserva);
            vistaReserva.setVisible(true);
        });

        btnGestionarReservas.addActionListener(e -> {
            VistaGestionarReservas vistaGestion = new VistaGestionarReservas(this, ctrlReserva);
            vistaGestion.setVisible(true);
        });

        btnReportes.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Los reportes se han impreso en la consola.", "Reportes", JOptionPane.INFORMATION_MESSAGE);
            ctrlAdministracion.getReportes().ocupacionPorSalida(java.time.LocalDate.of(2026, 1, 1), java.time.LocalDate.of(2027, 12, 31), null);
            ctrlAdministracion.getReportes().cancelaciones(java.time.LocalDate.of(2026, 1, 1), java.time.LocalDate.of(2027, 12, 31), null);
        });

        btnSalir.addActionListener(e -> System.exit(0));

        pnlBotones.add(btnRegistrarCliente);
        pnlBotones.add(btnProgramarSalida);
        pnlBotones.add(btnCrearReserva);
        pnlBotones.add(btnGestionarReservas);
        pnlBotones.add(btnReportes);
        pnlBotones.add(btnSalir);

        add(pnlBotones);
    }
}