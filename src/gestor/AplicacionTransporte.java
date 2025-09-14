package gestor;

import datos.*;
import entradaSalida.*;
import java.io.File;
import persistencia.*;

public class AplicacionTransporte {

    private static Archivo archivoConductor;
    private static Archivo archivoTransporte;
    private static final String RUTA_ARCHIVO_CONDUCTOR = "CONDUCTOR.dat";
    private static final String RUTA_ARCHIVO_TRANSPORTE = "TRANSPORTE.dat";
    private static final int LIMITE_CARACTERES = 120;
    private static int contador = 0;

    public static void inicializarArchivoConductor() {
        try {
            archivoConductor = new Archivo(RUTA_ARCHIVO_CONDUCTOR, new Conductor());
            if (!archivoConductor.getFd().exists()) {
                archivoConductor.crearArchivoVacio(new Registro(new Conductor(), 0));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error al crear los descriptores de archivos: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void inicializarArchivoTransporte() {
        try {
            archivoTransporte = new Archivo(RUTA_ARCHIVO_TRANSPORTE, new TransporteBase());
            if (!archivoTransporte.getFd().exists()) {
                archivoTransporte.crearArchivoVacio(new Registro(new TransporteBase(), 0));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error al crear los descriptores de archivos: " + e.getMessage());
            System.exit(1);
        }
    }

    public void cargarConductores() {
        archivoConductor.abrirParaLeerEscribir();

        try {
            Conductor dato = new Conductor();
            dato.cargarNroOrd(obtenerNroOrdenParaNuevo(archivoConductor));
            do {
                dato.cargarDNI();
            } while (buscarPorDni(dato.getDni(), archivoConductor));
            dato.cargarDatos();
            Registro reg = new Registro(dato, dato.getNroOrd());
            archivoConductor.cargarUnRegistro(reg);
        } catch (Exception e) {
            ConsolaS.mostrarAdvertencia(e.getMessage());
            System.exit(1);
        }
        archivoConductor.cerrarArchivo();
        ConsolaS.mostrarConfirmacion("Conductor cargado con exito!");
    }

    private int obtenerNroOrdenParaNuevo(Archivo a) {
        a.irPrincipioArchivo();
        int ultimoNroOrden = -1;  // valor inicial para ir guardando el mayor nroOrden
        while (!a.eof()) {
            Registro reg = a.leerRegistro();
            // Voy actualizando el ULtimo nroOrden visto
            ultimoNroOrden = reg.getNroOrden();
            // Si encuentro un hueco, reutilizo y termino
            if (!reg.getActivo()) {
                return reg.getNroOrden();
            }
        }
        // Si no habia huecos, devuelvo el ultimo + 1
        return ultimoNroOrden + 1;
    }

    public void listadoDeSueldo() {
        archivoConductor.abrirParaLectura();
        archivoConductor.irPrincipioArchivo();
        Conductor dato = new Conductor();
        ConsolaS.generarTitulosColumnas(dato.getClass(), LIMITE_CARACTERES);
        while (!archivoConductor.eof()) {
            Registro reg = archivoConductor.leerRegistro();
            if (reg.getActivo()) {
                dato = (Conductor) reg.getDatos();
                ConsolaS.mostrarTabulado(dato.getClass(), dato, LIMITE_CARACTERES);
            }
        }
        archivoConductor.cerrarArchivo();
    }

    public void ver() {
        archivoTransporte.abrirParaLectura();
        archivoTransporte.irPrincipioArchivo();
        Trasnporte dato;

        while (!archivoTransporte.eof()) {
            Registro reg = archivoTransporte.leerRegistro();
            if (reg.getActivo()) {
                dato = (Trasnporte) reg.getDatos();
                if (dato instanceof TransportePersonas p) {
                    p.mostrarRegistro();
                } else if (dato instanceof TransporteMercaderia p) {
                    p.mostrarRegistro();
                }
            }
        }
        archivoTransporte.cerrarArchivo();
    }

    public void listarTransporte() {

    }

    public boolean buscarPorDni(long dniBuscado, Archivo a) {
        a.abrirParaLectura();
        boolean encontrado = false;
        Conductor dato;
//        int contador = 0;
        a.irPrincipioArchivo();

        while (!a.eof() && !encontrado) {
//            System.out.println(contador++);
            Registro reg = a.leerRegistro();
            if (reg.getActivo()) {
                dato = (Conductor) reg.getDatos();
                if (dniBuscado == dato.getDni()) {
                    encontrado = true;
                }
            }
        }

        if (encontrado) {
            ConsolaS.mostrarAdvertencia("DNI existente");
        } else {
            ConsolaS.mostrarAdvertencia("DNI no existente en archivo CONDUCTORES.dat");
        }
        a.cerrarArchivo();
        return encontrado;
    }

    public void actualizarTrasporte() {
        int op = -1;
        do {
            Menu.subMenu();
            op = Menu.ejecutar(3);
            switch (op) {
                case 1:
                    altaTransporte();
                    break;
            }
        } while (op != 0);
    }

    public void altaTransporte() {
        long dni;
        archivoTransporte.abrirParaLeerEscribir();
        do {
            try {
                Trasnporte t = Trasnporte.cargarTipoT();
                t.cargarCodT(obtenerNroOrdenParaNuevo(archivoTransporte));
                do {
                    ConsolaS.mostrarlinea("Ingrese el dni del conductor: ");
                    dni = ConsolaE.leerLong();
                } while (!buscarPorDni(dni, archivoConductor));
                t.cargarDniConductor(dni);
                t.cargarDatos();
                Registro reg = new Registro(t, t.getCodT());
                archivoTransporte.cargarUnRegistro(reg);
                ConsolaS.mostrarConfirmacion("Datos cargados con exito!");
            } catch (Exception e) {
                ConsolaS.mostrarAdvertencia(e.getMessage());
                System.exit(1);
            }
        } while (ConsolaE.confirmar("Continuar cargando?"));
        archivoTransporte.cerrarArchivo();
    }

    public void ejecutarAplicacion() {
        inicializarArchivoTransporte();
        inicializarArchivoConductor();
        int op;
        Menu miMenu = new Menu();
        File f = new File("CONDUCTORES.dat");
        System.out.println("Tamaño del archivo: " + f.length() + " bytes");

        do {
            miMenu.verItems("Gestion de transporte");
            op = Menu.ejecutar(4);

            switch (op) {
                case 1:
                    cargarConductores();
                    break;
                case 2:
                    actualizarTrasporte();
                    break;
                case 3:
                    listadoDeSueldo();
                    break;
                case 4:
                    ver();
                    break;
            }

        } while (op != 0);

    }
}
