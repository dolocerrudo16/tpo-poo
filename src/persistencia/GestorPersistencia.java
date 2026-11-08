//Fabricacion Pura(GRASP) clases de dominio  delegando la persistencia a GestorPersistencia.
//SOLID (SRP) pesistencia aislada, la agencia no sabe ni abrir archivos
package persistencia;

import modelo.estructura.Agencia;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class GestorPersistencia {

    // Guarda el estado del sistema
    public void guardar(Agencia agencia, String archivo) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(archivo); //crea conexion con el archivo
            oos = new ObjectOutputStream(fos); //convierte objeto en bytes
            oos.writeObject(agencia);
            System.out.println("Datos guardados exitosamente en: " + archivo);
        } catch (IOException e) { //fallas de disco
            System.err.println("Error al guardar los datos: " + e.getMessage());
        } finally { //cierra el archivo
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                System.err.println("Error al cerrar los flujos de escritura: " + e.getMessage());
            }
        }
    }

    // Método para cargar el estado del sistema al iniciar el programa
    public Agencia cargar(String archivo) {
        FileInputStream fis = null; //canal de lectura hacia el archivo
        ObjectInputStream ois = null;
        Agencia agenciaCargada = null;
        try {
            fis = new FileInputStream(archivo);
            ois = new ObjectInputStream(fis);
            agenciaCargada = (Agencia) ois.readObject();
            System.out.println("Datos cargados exitosamente desde: " + archivo);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se encontró un archivo previo o hubo un error al cargar. Se iniciará un sistema vacío.");
            // Si hay error o no existe el archivo, retornamos una nueva instancia vacía
            agenciaCargada = new Agencia();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                System.err.println("Error al cerrar los flujos de lectura: " + e.getMessage());
            }
        }
        return agenciaCargada;
    }
}