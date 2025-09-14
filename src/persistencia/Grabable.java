package persistencia;

/**
 * Indica el comportamiento que debe ser capaz de mostrar un objeto que vaya a
 * ser grabado en un Archivo.
 *
 */
import java.io.*;

public interface Grabable {

    /**
     * Calcula el tamaño en bytes del objeto, tal como será grabado
     *
     * @return el tamaño en bytes del objeto
     */
    int tamRegistro();

    /**
     * Obtiene la cantidad de registros que se desea en el archivo
     *
     * @return la cantidad de registros
     */
    int tamArchivo();

    /**
     * Indica cómo grabar un objeto
     *
     * @param el archivo donde será grabado el objeto
     */
    void grabar(RandomAccessFile a);

    /**
     * Indica cómo leer un objeto
     *
     * @param a el archivo donde se hará la lectura
     */
    void leer(RandomAccessFile a);
    
    /**
     * Muestra un registro por consola estandar
     */
    void mostrarRegistro();
    
    /**
     * Carga datos de un registro por consola estandar
     */
    void cargarDatos();

    
}
