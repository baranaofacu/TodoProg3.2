package gestor;

import datos.*;
import entradaSalida.*;
import persistencia.*;

public class AplicacionTransporte {

    private static Archivo archivoConductor;
    private static final String RUTA_ARCHIVO_CONDUCTOR = "CONDUCTORES.dat";
    private static Archivo archivoTransporteMercaderia;
    private static Archivo archivoTransportePersona;
    private static final String RUTA_ARCHIVO_TRANSPORTE_MERCADERIA = "TRANSPORTE_MERCADERIA.dat";
    private static final String RUTA_ARCHIVO_TRANSPORTE_PERSONAS = "TRANSPORTE_PERSONA.dat";
    private static final int LIMITE_CARACTERES = 120;

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

    public static void inicializarArchivoTransporteMercaderia() {
        try {
            archivoTransporteMercaderia = new Archivo(RUTA_ARCHIVO_TRANSPORTE_MERCADERIA, new TransporteMercaderia());
            if (!archivoTransporteMercaderia.getFd().exists()) {
                archivoTransporteMercaderia.crearArchivoVacio(new Registro(new TransporteMercaderia(), 0));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error al crear los descriptores de archivos: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void inicializarArchivoTransportePersona() {
        try {
            archivoTransportePersona = new Archivo(RUTA_ARCHIVO_TRANSPORTE_PERSONAS, new TransportePersonas());
            if (!archivoTransportePersona.getFd().exists()) {
                archivoTransportePersona.crearArchivoVacio(new Registro(new TransportePersonas(), 0));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error al crear los descriptores de archivos: " + e.getMessage());
            System.exit(1);
        }
    }

    public void inicializarArchivos() {
        inicializarArchivoConductor();
        inicializarArchivoTransporteMercaderia();
        inicializarArchivoTransportePersona();
    }

    public void cargarConductores() {
        boolean valido;
        archivoConductor.abrirParaLeerEscribir();
        try {
            Conductor dato = new Conductor();
            dato.cargarNroOrd(obtenerNroOrdenParaNuevo(archivoConductor));
            do {
                dato.cargarDNI();
                valido = buscarPorDni(dato.getDni(), archivoConductor);
                if (valido) {
                    ConsolaS.mostrarAdvertencia("DNI existente, cargue otro");
                }
            } while (valido);
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

    public void ver(Archivo a) {
        a.abrirParaLectura();
        a.irPrincipioArchivo();
        Transporte dato;

        while (!a.eof()) {
            Registro reg = a.leerRegistro();
            if (reg.getActivo()) {
                dato = (Transporte) reg.getDatos();
                if (dato instanceof TransportePersonas p) {
                    p.mostrarRegistro();
                } else if (dato instanceof TransporteMercaderia p) {
                    p.mostrarRegistro();
                }
            }
        }
        a.cerrarArchivo();
    }

    public boolean buscarPorDni(long dniBuscado, Archivo a) {
        a.abrirParaLectura();
        boolean encontrado = false;
        Conductor dato;
        a.irPrincipioArchivo();
        while (!a.eof() && !encontrado) {
            Registro reg = a.leerRegistro();
            if (reg.getActivo()) {
                dato = (Conductor) reg.getDatos();
                if (dniBuscado == dato.getDni()) {
                    encontrado = true;
                }
            }
        }
        a.cerrarArchivo();
        return encontrado;
    }

    public void menuAltaRegistros() {
        int opc;
        do {
            ConsolaS.mostrarlinea("Ingrese el tipo de transporte (1: Personas | 2: Mercaderia): ");
            opc = ConsolaE.leerEntero();
            switch (opc) {
                case 1:
                    altaTransporte(archivoTransportePersona, 1);
                    break;
                case 2:
                    altaTransporte(archivoTransporteMercaderia, 2);
                    break;
                default:
                    ConsolaS.mostrarAdvertencia("Ingresa valores validos.");
            }
        } while (opc != 1 && opc != 2);
    }

    public void menuBajaRegistros() {
        int opc;
        do {
            ConsolaS.mostrarlinea("Elige el tipo de archivo para la baja(1: Personas | 2: Mercaderia): ");
            opc = ConsolaE.leerEntero();
            switch (opc) {
                case 1:
                    bajaDatos(archivoTransportePersona);
                    break;
                case 2:
                    bajaDatos(archivoTransporteMercaderia);
                    break;
                default:
                    ConsolaS.mostrarAdvertencia("Ingrese un valor valido");
            }
        } while (opc != 1 && opc != 2);
    }

    public static void bajaDatos(Archivo a) {
        ConsolaS.mostrarlinea("Codigo del transporte a dar de baja: ");
        int nroOrden = ConsolaE.leerEntero();

        a.abrirParaLectura();
        a.buscarRegistro(nroOrden);

        Registro reg = a.leerRegistro();
        a.cerrarArchivo();

        if (reg != null && reg.getActivo()) {
            reg.mostrarRegistro();

            if (ConsolaE.confirmar("¿Borrar registro?")) {
                a.bajaRegistro(reg);
            }
        } else {
            ConsolaS.mostrarAdvertencia("Ese registro no existe o esta inactivo.");
        }
    }

    public void menuModificarRegistros() {
        int opc;
        do {
            ConsolaS.mostrarlinea("Elige el tipo de archivo para la baja(1: Personas | 2: Mercaderia): ");
            opc = ConsolaE.leerEntero();
            switch (opc) {
                case 1:
                    modificarDatos(archivoTransportePersona);
                    break;
                case 2:
                    modificarDatos(archivoTransporteMercaderia);
                    break;
                default:
                    ConsolaS.mostrarAdvertencia("Ingrese un valor valido");
            }
        } while (opc != 1 && opc != 2);

    }

    public void modificarDatos(Archivo a) {
        System.out.print("Codigo a modificar: ");
        int codigo = ConsolaE.leerEntero();
        boolean valido;
        long dni;

        a.abrirParaLeerEscribir();
        a.buscarRegistro(codigo);
        if (a.eof()) {
            ConsolaS.mostrarAdvertencia("No existe el registro");
            return;
        }

        Registro reg = a.leerRegistro(); // Lee el registro existente
        reg.mostrarRegistro();
        Transporte dato = (Transporte) reg.getDatos();
        do {
            ConsolaS.mostrarlinea("Ingrese el dni del conductor: ");
            dni = ConsolaE.leerLong();
            valido = buscarPorDni(dni, archivoConductor);
            if (!valido) {
                ConsolaS.mostrarAdvertencia("El DNI debe existir en archivo conductores");
            }
        } while (!valido);
        dato.modificarDatos(dni);
        reg.setDatos(dato);
        a.cargarUnRegistro(reg); // Sobreescribe el registro ya existente

        a.cerrarArchivo();

        ConsolaS.mostrarConfirmacion("Registro modificado");
    }

    public void menuAbmTransporte() {
        int op = -1;
        do {
            Menu.subMenu();
            op = Menu.ejecutar(3);
            switch (op) {
                case 1:
                    menuAltaRegistros();
                    break;
                case 2:
                    menuBajaRegistros();
                    break;
                case 3:
                    menuModificarRegistros();
                    break;
            }
        } while (op != 0);
    }

    public void altaTransporte(Archivo a, int tipo) {
        long dni;
        boolean valido;
        do {
            a.abrirParaLeerEscribir();
            try {
                Transporte t = Transporte.cargarTipoT(tipo);
                t.cargarCodT(obtenerNroOrdenParaNuevo(a));
                do {
                    ConsolaS.mostrarlinea("Ingrese el dni del conductor: ");
                    dni = ConsolaE.leerLong();
                    valido = buscarPorDni(dni, archivoConductor);
                    if (!valido) {
                        ConsolaS.mostrarAdvertencia("Ingrese un DNI que exista en archivo conductores");
                    }
                } while (!valido);
                t.cargarDniConductor(dni);
                t.cargarDatos();
                Registro reg = new Registro(t, t.getCodT());
                a.cargarUnRegistro(reg);
                ConsolaS.mostrarConfirmacion("Datos cargados con exito!");
            } catch (Exception e) {
                ConsolaS.mostrarAdvertencia(e.getMessage());
                System.exit(1);
            }
            a.cerrarArchivo();
        } while (ConsolaE.confirmar("Continuar cargando?"));
    }

    public void ejecutarAplicacion() {
        inicializarArchivos();
        int op;
        Menu miMenu = new Menu();

        do {
            miMenu.verItems("Gestion de transporte");
            op = Menu.ejecutar(4);

            switch (op) {
                case 1:
                    cargarConductores();
                    break;
                case 2:
                    menuAbmTransporte();
                    break;
                case 3:
                    listadoDeSueldo();
                    break;
                case 4:
                    ver(archivoTransporteMercaderia);
                    ver(archivoTransportePersona);
                    break;
            }

        } while (op != 0);
    }
}
