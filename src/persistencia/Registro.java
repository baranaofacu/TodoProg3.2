package persistencia;

import java.io.*;
import entradaSalida.*;

public class Registro implements Grabable {

    private int nroOrden; // indica la posicion del registro en el archivo 4 bytes
    private boolean estado; // marca de registro activo. Ocupa 1 byte en disco
    private Grabable dato; // los datos que seran grabados en el registro
    // para utilizar la clase Grabable sera necesario que la clase de los datos
    // herede de la interface

    /**
     * Crea un Registro sin datos, marcándolo como inactivo
     */
    public Registro() {
        estado = false;
        dato = null;
    }

    /**
     * Crea un Registro con datos, marcándolo como activo
     */
    public Registro(Grabable d, int orden) {
        nroOrden = orden;
        estado = true;
        dato = d;
    }

    /**
     * Accede al valor del nroOrden
     *
     * @return el valor del atributo nroOrden
     */
    public int getNroOrden() {
        return nroOrden;
    }

    /**
     * Accede al valor del nroOrden
     *
     * @return el valor del atributo nroOrden
     */
    public void setNroOrden(int nroOrden) {
        this.nroOrden = nroOrden;
    }

    /**
     * Determina si el registro es activo o no
     *
     * @return true si es activo, false si no.
     */
    public boolean getActivo() {
        return estado;
    }

    /**
     * Cambia el estado del registro, en memoria
     *
     * @param x el nuevo estado
     */
    public void setActivo(boolean x) {
        estado = x;
    }

    /**
     * Cambia los datos del registro en memoria
     *
     * @param d los nuevos datos
     */
    public void setDatos(Grabable d) {
        dato = d;
    }

    /**
     * Accede a los datos del registro en memoria
     * devuelve el tipo Grabable ya que en el archivo se tratan a los datos con ese
     * tipo
     * 
     * @return una referencia a los datos del registro
     */
    public Grabable getDatos() {
        return dato;
    }

    /**
     * Carga un Nro.Orden de Articulo por teclado
     */
    public void cargarNroOrden() {
        System.out.print("Codigo: ");
        int orden = ConsolaE.leerEntero();
        nroOrden = orden;
    }

    /**
     * Calcula el tamaño en bytes del objeto, tal como será grabado en disco.
     * Pedido por Grabable.
     *
     * @return el tamaño en bytes del objeto como será grabado.
     */
    @Override
    public int tamRegistro() {
        return dato.tamRegistro() + 5; // suma 5, cantidad de bytes de activo y nroOrden
    }

    /**
     * Devuelve cantidad de registros.
     * Pedido por Grabable.
     *
     * @return cantidad de registros.
     */
    @Override
    public int tamArchivo() {
        return dato.tamArchivo();
    }

    /**
     * Especifica cómo se graba un Registro en disco. Pedido por Grabable.
     *
     * @param a el manejador del archivo de disco donde se hará la grabación
     */
    @Override
    public void grabar(RandomAccessFile a) {
        try {
            a.writeInt(nroOrden);
            a.writeBoolean(estado);
            dato.grabar(a);
        } catch (IOException e) {
            System.out.println("Error al grabar los datos del registro: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Especifica cómo se lee un Registro desde disco. Pedido por Grabable.
     *
     * @param a el manejador del archivo de disco desde donde se hará la lectura
     */
    @Override
    public void leer(RandomAccessFile a) {
        try {
            nroOrden = a.readInt();
            estado = a.readBoolean();
            dato.leer(a); // convoca al metodo de lectura especifico a los datos del registro
        } catch (IOException e) {
            // print stack trace
            System.err.println("Error al leer los datos del registro.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Lee desde un archivo un String de "tam" caracteres. Se declara static
     * para que pueda ser usado en forma global por cualquier clase que requiera
     * leer una cadena de longitud fija desde un archivo.
     *
     * @param arch el archivo desde el cual se lee
     * @param tam  la cantidad de caracteres a leer
     * @return el String leido
     */
    public static final String readString(RandomAccessFile arch, int tam) {
        String cad = "";

        try {
            char vector[] = new char[tam];
            for (int i = 0; i < tam; i++) {
                vector[i] = arch.readChar();
            }
            cad = new String(vector, 0, tam);
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("Error al leer una cadena: " + e.getMessage());
            System.exit(1);
        }
        return cad;
    }

    /**
     * Graba en un archivo un String de "tam" caracteres. Se declara static y
     * publico para
     * que pueda ser usado en forma global por cualquier clase que requiera
     * grabar una cadena de longitud fija en un archivo.
     *
     * @param arch el archivo en el cual se graba
     * @param cad  la cadena a a grabar
     * @param tam  la cantidad de caracteres a grabar
     */
    public static final void writeString(RandomAccessFile arch, String cad, int tam) {
        try {
            int i;
            char vector[] = new char[tam];
            for (i = 0; i < tam; i++) {
                vector[i] = ' ';
            }
            cad.getChars(0, cad.length(), vector, 0);
            for (i = 0; i < tam; i++) {
                arch.writeChar(vector[i]);
            }
        } catch (IOException e) {
            System.out.println("Error al grabar una cadena: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Muestra un registro por consola estandar
     */
    @Override
    public void mostrarRegistro() {
//        System.out.println("Numero de orden: " + nroOrden);
        dato.mostrarRegistro();
//        System.out.println("Activo: " + getActivo());
//        System.out.println("\t");
    }

    /**
     * Muestra un registro por consola estandar
     */
    @Override
    public void cargarDatos() {
        dato.cargarDatos();
    }

}