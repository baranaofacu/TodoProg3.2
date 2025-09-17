package gestor;

import datos.*;
import entradaSalida.*;
import excepcionesPersonalizadas.TrasporteDuplicadoException;
import persistencia.*;

public class AplicacionTransporte {

    private static Archivo archivoConductor;
    private static final String RUTA_ARCHIVO_CONDUCTOR = "CONDUCTORES.dat";
    private static Archivo archivoTransporte;
    private static final String RUTA_ARCHIVO_TRANSPORTE = "TRANSPORTE.dat";
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

    public static void inicializarArchivoTransporte(Transporte p) {
        try {
            archivoTransporte = new Archivo(RUTA_ARCHIVO_TRANSPORTE, p);
            if (!archivoTransporte.getFd().exists()) {
                archivoTransporte.crearArchivoVacio(new Registro(p, 0));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error al crear los descriptores de archivos: " + e.getMessage());
            System.exit(1);
        }
    }

    public void inicializarArchivos() {
        inicializarArchivoConductor();
        inicializarArchivoTransporte(new TransporteMercaderia());
    }

    public void cargarConductores() {
        boolean valido;
        archivoConductor.abrirParaLeerEscribir();
        try {
            Conductor dato = new Conductor();
            dato.cargarNroOrd(obtenerNroOrdenParaNuevo(archivoConductor));
            do {
                dato.cargarDNI();
                valido = buscarPorDni(dato.getDni(), archivoConductor, 0);
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
        int ultimoNroOrden = 0;  // valor inicial para ir guardando el mayor nroOrden
        while (!a.eof()) {
            Registro reg = a.leerRegistro();
            // Voy actualizando el ULtimo nroOrden visto
            ultimoNroOrden = reg.getNroOrden();
            // si encuentro un hueco reutilizo y termino
            if (!reg.getActivo() && ultimoNroOrden != 0) {
                return reg.getNroOrden();
            }
        }
        // si no habia huecos devuelvo el ultimo + 1
        return ultimoNroOrden + 1;
    }

    public void listadoDeSueldo() {
        int cantHoras = 0;
        double extra = 0;
        archivoTransporte.abrirParaLectura();
        archivoConductor.abrirParaLectura();
        archivoConductor.irPrincipioArchivo();
        Conductor datoC = new Conductor();

        Menu.mostrarCabeceraSueldo();

        while (!archivoConductor.eof()) {
            cantHoras = 0;
            extra = 0;
            Registro regC = archivoConductor.leerRegistro();
            datoC = (Conductor) regC.getDatos();
            if (regC.getActivo()) {
                archivoTransporte.irPrincipioArchivo();
                while (!archivoTransporte.eof()) {
                    Registro regT = archivoTransporte.leerRegistro();
                    Transporte datoT = (Transporte) regT.getDatos();
                    if (datoC.getDni() == datoT.getDniConductor() && regT.getActivo()) {
                        cantHoras = datoT.getHoras() + cantHoras;
                        extra = datoT.getExtra() + extra;
                    }
                }
                double sueldoFinal = Conductor.calcularSueldoFinal(cantHoras, extra);
                datoC.mostrarNombreDniSueldoFinal(sueldoFinal);
            }
        }
                archivoTransporte.cerrarArchivo();
        archivoConductor.cerrarArchivo();
    }

    public void ver(Archivo a) {
        a.abrirParaLectura();
        a.irPrincipioArchivo();

        while (!a.eof()) {
            Registro reg = a.leerRegistro();
            if (reg.getActivo()) {
                Transporte dato = (Transporte) reg.getDatos();
                if (dato.getTipo() == 'P' && dato instanceof TransportePersonas) {
                    ConsolaS.mostrarlinea(" " + dato.getCodT());
                    buscarPorDni(dato.getDniConductor(), archivoConductor, 1);
                    dato = (Transporte) reg.getDatos();
                    dato.mostrarRegistro();
                } else if (dato.getTipo() == 'M' && dato instanceof TransporteMercaderia) {
                    ConsolaS.mostrarlinea(" " + dato.getCodT());
                    dato = (Transporte) reg.getDatos();
                    buscarPorDni(dato.getDniConductor(), archivoConductor, 1);
                    dato.mostrarRegistro();
                }
            }
        }
        ConsolaS.mostrarSeparador(100, '-');
        a.cerrarArchivo();
    }

    public boolean buscarPorDni(long dniBuscado, Archivo a, int mostrarNombre) {
        a.abrirParaLectura();
        boolean encontrado = false;
        Conductor dato;
        a.irPrincipioArchivo();
        while (!a.eof() && !encontrado) {
            Registro reg = a.leerRegistro();
            if (reg.getActivo()) {
                dato = (Conductor) reg.getDatos();
                if (dniBuscado == dato.getDni()) {
                    if (mostrarNombre == 1) {
                        System.out.printf("\t%-16s", dato.getApe_Nom());
                    }
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
                    inicializarArchivoTransporte(new TransportePersonas());
                    altaTransporte(archivoTransporte, new TransportePersonas());
                    break;
                case 2:
                    inicializarArchivoTransporte(new TransporteMercaderia());
                    altaTransporte(archivoTransporte, new TransporteMercaderia());
                    break;
                default:
                    ConsolaS.mostrarAdvertencia("Ingresa valores validos.");
            }
        } while (opc != 1 && opc != 2);
    }

    public void menuBajaRegistros() {
        boolean repetir;
        int nroOrden;

        do {
            ConsolaS.mostrarlinea("Codigo del transporte a dar de baja(presione 0 para salir): ");
            nroOrden = ConsolaE.leerEntero();
            if (nroOrden == 0) {
                break;
            }
            inicializarArchivoTransporte(new TransporteMercaderia());
            repetir = bajaDatos(archivoTransporte, 'M', nroOrden);
            if (repetir) {
                inicializarArchivoTransporte(new TransportePersonas());
                bajaDatos(archivoTransporte, 'P', nroOrden);
            }
        } while (nroOrden != 0);
    }

    public static boolean bajaDatos(Archivo a, char tipo, int nroOrden) {
        boolean repite = true; //esto me sirve para saber si necesito volver a recorrer el archivo inicializado con otra clase

        a.abrirParaLectura();
        a.buscarRegistro(nroOrden);
        Registro reg = a.leerRegistro();

        if (reg != null && reg.getActivo()) {
            Transporte dato = (Transporte) reg.getDatos();
            if (dato.getTipo() == tipo) {
                Menu.mostrarCabezera(dato);
                dato.mostrarRegistro();
                repite = false;
                if (ConsolaE.confirmar("¿Borrar registro?")) {
                    a.bajaRegistro(reg);
                    ConsolaS.mostrarConfirmacion("Registro borrado con exito");
                } else {
                    ConsolaS.mostrarAdvertencia("El registro no se borro");
                }
            }
        } else {
            ConsolaS.mostrarAdvertencia("Ese registro no existe o esta inactivo.");
        }
        return repite;
    }

    public void menuModificarRegistros() {
        boolean repetir;
        int nroOrden;
        do {
            ConsolaS.mostrarlinea("Ingrese el codigo del registro a modificar(presione 0 para salir): ");
            nroOrden = ConsolaE.leerEntero();
            if (nroOrden == 0) {
                break;
            }
            inicializarArchivoTransporte(new TransporteMercaderia());
            repetir = modificarDatos(archivoTransporte, nroOrden, 'M');
            if (repetir) {
                inicializarArchivoTransporte(new TransportePersonas());
                modificarDatos(archivoTransporte, nroOrden, 'P');
            }
        } while (nroOrden != 0);

    }

    public boolean modificarDatos(Archivo a, int nroOrden, char tipo) {
        boolean valido;
        long dni;

        a.abrirParaLeerEscribir();
        a.buscarRegistro(nroOrden);
        Registro reg = a.leerRegistro();
        Transporte dato = (Transporte) reg.getDatos();
        if (a.eof() || !reg.getActivo() || dato.getTipo() != tipo) {
            return true;
        }
        ConsolaS.mostrarLinea("Registro a modificar:  ");
        Menu.mostrarCabezera(dato);
        reg.mostrarRegistro();
        do {
            ConsolaS.mostrarlinea("Ingrese el dni del conductor: ");
            dni = ConsolaE.leerLong();
            valido = buscarPorDni(dni, archivoConductor, 0);
            if (!valido) {
                ConsolaS.mostrarAdvertencia("El DNI debe existir en archivo conductores");
            }
        } while (!valido);
        dato.modificarDatos(dni);
        reg.setDatos(dato);
        a.cargarUnRegistro(reg); // Sobreescribe el registro ya existente

        a.cerrarArchivo();

        ConsolaS.mostrarConfirmacion("Registro modificado");
        return false;
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

    public void altaTransporte(Archivo a, Transporte tipo) {
        long dni;
        boolean valido;
        do {
            a.abrirParaLeerEscribir();
            try {
                Transporte t = tipo;
                t.cargarCodT(obtenerNroOrdenParaNuevo(a));
                do {
                    ConsolaS.mostrarlinea("Ingrese el dni del conductor: ");
                    dni = ConsolaE.leerLong();
                    valido = buscarPorDni(dni, archivoConductor, 0);
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

    public void listadoTransportePorConductor() {
        inicializarArchivoTransporte(new TransportePersonas());
        ConsolaS.mostrarSeparador(90, '-');
        ConsolaS.mostrarTitulo("Transporte Personas", 100, '·', '·');
        System.out.printf("%-10s %-23s %-11s %-8s %-8s %-10s %-15s%n",
                "CodT", "Nombre", "DNI", "Horas", "Tipo", "Extra", "Cant.Pasajeros");

        ver(archivoTransporte);

        inicializarArchivoTransporte(new TransporteMercaderia());
        ConsolaS.mostrarTitulo("Transporte Mercaderias", 100, '·', '·');
        System.out.printf("%-10s %-23s %-11s %-8s %-8s %-10s %8s %10s%n",
                "CodT", "Nombre", "DNI", "Horas", "Tipo", "Extra", "Cant.Toneladas ", "EsPeligroso");
        ver(archivoTransporte);
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
                    listadoTransportePorConductor();
                    break;
            }

        } while (op != 0);
    }
}
