import modelo.estructura.Agencia;
import persistencia.GestorPersistencia;
import presentacion.controladores.ControladorAdministracion;
import presentacion.controladores.ControladorOperaciones;
import presentacion.controladores.ControladorReserva;
import presentacion.vistas.VistaPrincipal;

import javax.swing.SwingUtilities;

public class MainGUI {
    public static void main(String[] args) {

        GestorPersistencia gestor = new GestorPersistencia();
        Agencia agencia = gestor.cargar("sistema_agencia.dat");

        ControladorReserva ctrlReserva = new ControladorReserva(agencia);
        ControladorOperaciones ctrlOperaciones = new ControladorOperaciones(agencia);
        ControladorAdministracion ctrlAdministracion = new ControladorAdministracion(agencia);

        SwingUtilities.invokeLater(() -> {
            VistaPrincipal menu = new VistaPrincipal(ctrlReserva, ctrlOperaciones, ctrlAdministracion);
            menu.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gestor.guardar(agencia, "sistema_agencia.dat");
        }));
    }
}