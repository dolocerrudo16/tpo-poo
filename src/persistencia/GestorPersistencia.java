package persistencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import modelo.Agencia;

/**
 * Entrada/salida de datos: guarda y recupera toda la agencia en un archivo (serialización de objetos),
 * de modo que la información se conserve entre ejecuciones.
 */
public class GestorPersistencia {

    private GestorPersistencia() { }

    /** Guarda en un archivo temporal y luego lo mueve: si algo falla a mitad de camino no se corrompe el archivo anterior. */
    public static void guardar(Agencia agencia, Path archivo) throws IOException {
        Path padre = archivo.toAbsolutePath().getParent();
        if (padre != null) Files.createDirectories(padre);
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temporal))) {
            out.writeObject(agencia);
        } catch (IOException | RuntimeException e) {
            Files.deleteIfExists(temporal);   // no dejar un temporal a medias
            throw e;
        }
        Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
    }

    /** Carga la agencia guardada; si el archivo no existe devuelve una agencia nueva y vacía. */
    public static Agencia cargar(Path archivo) throws IOException, ClassNotFoundException {
        if (!Files.exists(archivo)) return new Agencia();
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(archivo))) {
            Object leido = in.readObject();
            if (!(leido instanceof Agencia)) {
                throw new IOException("El archivo no contiene datos de la agencia.");
            }
            return (Agencia) leido;
        }
    }
}
