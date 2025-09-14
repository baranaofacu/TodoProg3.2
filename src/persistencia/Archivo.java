package persistencia;

/**
 * Una clase para representar un archivo binario, cuyo contenido son registros
 * uniformes (del mismo tipo) y de la misma longitud. El archivo no permite
 * grabar objetos cuyo tipo y tamaño no coincida con los que se indicaron en el
 * constructor.
 */
import java.io.*;

public class Archivo {

    private File fd; // descriptor del archivo para acceder a sus propiedades externas
    private RandomAccessFile maestro; // objeto para acceder al contenido del archivo
    private Grabable tipo; // representa al tipo de objeto que se puede grabar en el archivo

    /**
     * Crea un manejador para el Archivo, asociando al mismo un nombre a modo de
     * file descriptor, y un tipo de contenido al que quedará fijo. El segundo
     * parámetro se usa para fijar el tipo de registro que será aceptado para
     * grabar en el archivo. No se crea el archivo en disco, ni se abre. Sólo se
     * crea un descriptor general para él. La apertura y eventual creación, debe
     * hacerse con el método abrirParaLeerEscribir().
     *
     * @param nombre es el nombre físico del archivo a crear
     * @param r una instancia de la clase a la que pertenecen los objetos cuyos
     * datos serán grabados. La instancia referida por r NO será grabada en el
     * archivo
     * @throws ClassNotFoundException si no se informa correctamente el tipo de
     * registro a grabar
     */
    public Archivo(String nombre, Grabable r) throws ClassNotFoundException {
        if (r == null) {
            throw new ClassNotFoundException("Clase incorrecta o inexistente para el tipo de registro");
        }
        tipo = r;
        fd = new File(nombre);
    }

    /**
     * Acceso al descriptor del archivo
     *
     * @return un objeto de tipo File con las propiedades de file system del
     * archivo
     */
    public File getFd() {
        return fd;
    }

    /**
     * Acceso al manejador del archivo binario
     *
     * @return un objeto de tipo RandomAccessFile que permite acceder al bloque
     * físico de datos en disco, en forma directa
     */
    public RandomAccessFile getMaestro() {
        return maestro;
    }

    /**
     * Acceso al descriptor de la clase del registro que se graba en el archivo
     *
     * @return una cadena con el nombre de la clase del registro usado en el
     * archivo
     */
    public String getTipoRegistro() {
        return tipo.getClass().getName();
    }

    /**
     * crea el Archivo vacio
     */
    public void crearArchivoVacio(Registro reg) {
        abrirParaLeerEscribir();
        try {
            for (int i = 0; i < reg.tamArchivo(); i++) {
                reg.setNroOrden(i);
                reg.setActivo(false);

                buscarRegistro(i);
                grabarRegistro(reg);
            }
        } catch (Exception e) {
            System.out.println("Error al crear el archivo vacio: " + e.getMessage());
            System.exit(1);
        }
        cerrarArchivo();
    }

    /**
     * Borra el Archivo del disco
     */
    public void borrarArchivo() {
        fd.delete();
    }

    /**
     * Cambia el nombre del archivo
     *
     * @param nuevo otro Archivo, cuyo nombre (o file descriptor) será dado al
     * actual
     */
    public void renombrarArchivo(Archivo nuevo) {
        fd.renameTo(nuevo.fd);
    }

    /**
     * Abre el archivo en modo de sólo lectura. El archivo en disco debe existir
     * previamente. Queda posicionado al principio del archivo.
     */
    public void abrirParaLectura() {
        try {
            maestro = new RandomAccessFile(fd, "r");
        } catch (IOException e) {
            System.out.println("Error de apertura archivo " + fd.getName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Abre el archivo en modo de lectura y grabación. Si el archivo en disco no
     * existía, será creado. Si existía, será posicionado al principio del
     * archivo. Mueva el puntero de registro activo con el método seekRegistro()
     * o con buscarByte().
     */
    public void abrirParaLeerEscribir() {
        try {
            maestro = new RandomAccessFile(fd, "rw");
        } catch (IOException e) {
            System.out.println("Error de apertura archivo " + fd.getName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Cierra el archivo
     */
    public void cerrarArchivo() {
        try {
            maestro.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar el archivo " + fd.getName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Ubica el puntero de registro activo en la posición del registro número i.
     * Se supone que los registros grabados son del mismo tipo, y que la
     * longitud de los registros es uniforme.
     *
     * @param i número relativo del registro que se quiere acceder
     */
    public void buscarRegistro(long i) {
        Registro reg = new Registro(tipo, 0);
        try {
            maestro.seek(0);
            maestro.seek(i * reg.tamRegistro());
        } catch (IOException e) {
            System.out.println("Error al posicionar en el registro número " + i);
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Ubica el puntero de registro activo en la posición del byte número b
     *
     * @param b número del byte que se quiere acceder, contando desde el
     * principio del archivo
     * @throws IOException si hubo problema en el posicionamiento
     */
    public void buscarByte(long b) {
        try {
            maestro.seek(b);
        } catch (IOException e) {
            System.out.println("Error al posicionar en el byte número " + b + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Rebobina el archivo: ubica el puntero de registro activo en la posición
     * cero
     */
    public void irPrincipioArchivo() {
        try {
            maestro.seek(0);
        } catch (IOException e) {
            System.out.println("Error al ir al principio del archivo: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Devuelve el número relativo del registro en el cual esta posicionado el
     * archivo en este momento
     *
     * @return el número del registro actual
     */
    public long numeroRegistro() {
        Registro reg = new Registro(tipo, 0);
        try {
            return maestro.getFilePointer() / reg.tamRegistro();
        } catch (IOException e) {
            System.out.println("Error al intentar devolver el número de registro: " + e.getMessage());
            System.exit(1);
        }

        return -1;
    }

    /**
     * Devuelve el número de byte en el cual esta posicionado el archivo en este
     * momento
     *
     * @return el número de byte de posicionamiento actual
     */
    public long numeroByte() {
        try {
            return maestro.getFilePointer();
        } catch (IOException e) {
            System.out.println("Error al intentar devolver el número de byte: " + e.getMessage());
            System.exit(1);
        }

        return -1;
    }

    /**
     * Posiciona el puntero de registro activo al final del archivo
     */
    public void irFinalArchivo() {
        try {
            maestro.seek(maestro.length());
        } catch (IOException e) {
            System.out.println("Error al posicionar al final: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Devuelve la cantidad de registros del archivo en este momento
     *
     * @return el número de registros del archivo
     */
    public long cantidadRegistros() {
        Registro reg = new Registro(tipo, 0);
        try {
            return maestro.length() / reg.tamRegistro();
        } catch (IOException e) {
            System.out.println("Error al calcular el número de registros: " + e.getMessage());
            System.exit(1);
        }

        return 0;
    }

    /**
     * Determina si se ha llegado al final del archivo o no
     *
     * @return true si se llegó al final - false en caso contrario
     * @throws IOException si hubo problema en la operación
     */
    public boolean eof() {
        try {
            if (maestro.getFilePointer() < maestro.length()) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error al determinar el fin de archivo: " + e.getMessage());
            System.exit(1);
        }

        return true;
    }

    /**
     * Graba EL registro en el archivo
     *
     * @param r el registro a grabar
     */
    public void grabarRegistro(Registro r) {
        if (r != null && (r.getDatos().getClass() == tipo.getClass())) {
            try {
                buscarRegistro(r.getNroOrden());
                r.grabar(maestro);
            } catch (Exception e) {
                System.out.println("Error al grabar el registro: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Lee un registro del archivo
     *
     * @return el registro leido
     * @throws IOException si hubo problema en la operación
     */
    public Registro leerRegistro() {
        Registro reg = new Registro(tipo, 0);
        try {
            reg.leer(maestro);
            return reg;
        } catch (Exception e) {
            System.out.println("Error al leer el registro: " + e.getMessage());
            System.exit(1);
        }

        return null;
    }

    /**
     * Agrega un registro en el archivo, controlando que no haya repetición y
     * que la clase del nuevo registro coincida con la clase indicada para el
     * archivo al invocar al constructor. El archivo debe estar abierto en modo
     * de grabación.
     *
     * @param r registro a agregar
     * @return true si fue posible agregar el registro - false si no fue posible
     */
    public boolean altaRegistro(Registro r) {
        boolean resp = false;

        if (r != null && (tipo.getClass() == r.getDatos().getClass())) {
            try {
                buscarRegistro(r.getNroOrden());
                Registro x = leerRegistro();
                if (!x.getActivo()) {
                    grabarRegistro(r);
                } else {
                    System.out.println("El registro que desea grabar ya existe. . . ");
                }
                resp = true;
            } catch (Exception e) {
                System.out.println("Error al grabar el registro: " + e.getMessage());
                System.exit(1);
            }
        }
        return resp;
    }

    /*
       * 
       * /**
       * Carga un solo registro
       *
       * @param r registro a agregar
       * 
       * @return true si fue posible agregar el registro - false si no fue posible
     */

    public void cargarUnRegistro(Registro r) {
        abrirParaLeerEscribir();
        try {
            grabarRegistro(r);
            System.out.println("Pasa por acaaaaa");
        } catch (Exception e) {
            System.out.println("Error al grabar el registro: " + e.getMessage());
            System.exit(1);
        }

        cerrarArchivo();
    }

    /**
     * Borra un registro del archivo. La clase del registro buscado debe
     * coincidir con la clase indicada para el archivo al invocar al
     * constructor. El archivo debe estar abierto en modo de grabación. El
     * registro se marca como borrado, aunque sigue físicamente ocupando lugar
     * en el archivo
     *
     * @param r registro a buscar y borrar
     * @return true si fue posible borrar el registro - false si no fue posible
     */
    public boolean bajaRegistro(Registro r) {
        boolean resp = false;
        Registro reg = new Registro(tipo, 0);

        if (r != null && (tipo.getClass() == r.getDatos().getClass())) {
            abrirParaLeerEscribir();
            try {
                buscarRegistro(r.getNroOrden());
                if (!eof()) {

                    reg = leerRegistro();
                    if (reg.getActivo()) {
                        reg.setActivo(false);
                        irPrincipioArchivo();
                        buscarRegistro(r.getNroOrden());
                        grabarRegistro(reg);
                        resp = true;
                        System.out.println("El registro fue borrado\n");
                    } else {
                        System.out.println("El registro no se encuentra activo\n");
                    }
                } else {
                    System.out.println("El registro no se encuentra \n");
                }
            } catch (Exception e) {
                System.out.println("Error al eliminar el registro: " + e.getMessage());
                System.exit(1);
            }
            cerrarArchivo();
        }
        return resp;
    }

    /**
     * Modifica un registro en el archivo. Reemplaza el registro en una posición
     * dada, por otro tomado como parámetro. La clase del registro buscado debe
     * coincidir con la clase indicada para el archivo al invocar al constructo
     * r. El archivo debe estar abierto en modo de grabación.
     *
     * @param r registro con los nuevos datos
     * @return true si fue posible modificar el registro - false si no fue
     * posible
     */
    public boolean modificarRegistro(Registro r) {
        boolean resp = false;
        Registro reg;

        if (r != null && (tipo.getClass() == r.getDatos().getClass())) {
            abrirParaLeerEscribir();
            try {
                buscarRegistro(r.getNroOrden());
                if (!eof()) {
                    reg = leerRegistro();
                    if (reg.getActivo()) {
                        reg.cargarDatos();
                        irPrincipioArchivo();
                        buscarRegistro(r.getNroOrden());
                        grabarRegistro(reg);
                        resp = true;
                        System.out.println("El registro fue modificado\n");
                    } else {
                        System.out.println("El registro no se encuentra activo\n");
                    }
                } else {
                    System.out.println("El registro no se encuentra \n");
                }
            } catch (Exception e) {
                System.out.println("Error al modificar el registro: " + e.getMessage());
                System.exit(1);
            }
            cerrarArchivo();
        }
        return resp;
    }

    /**
     * Elimina físicamente los registros que estuvieran marcados como borrados.
     * El Archivo queda limpio, pero sale cerrado.
     */
    public void depurar() {
        Registro reg;
        try {
            Archivo temp = new Archivo("temporal.dat", tipo);
            temp.abrirParaLeerEscribir();

            this.abrirParaLectura();
            while (!this.eof()) {
                reg = this.leerRegistro();
                if (reg.getActivo()) {
                    temp.grabarRegistro(reg);
                }
            }
            this.cerrarArchivo();
            temp.cerrarArchivo();
            this.borrarArchivo();
            temp.renombrarArchivo(this);
        } catch (ClassNotFoundException e) {
            System.out.println("Error de tipo de dato con el archivo temporal: " + e.getMessage());
            System.exit(1);
        }
    }
}
