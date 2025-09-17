package gestor;

import datos.Transporte;
import entradaSalida.*;

public class Menu {

    private String[] item;
    private int cont;
    private final int N = 4;
    
    private static final String cabezeraTransportePersona = "\t  %-11s %-10s %-8s %-10s %-15s%n";
    private static final String cabezeraTransporteMercaderia = "\t   %-11s %-8s %-8s %-11s %-15s %-12s%n";

    public Menu() {
        this.item = new String[N];
        this.cont = 0;

        item[this.cont++] = "Cargar conductores";
        item[this.cont++] = "Actualizacion de transporte";
        item[this.cont++] = "Listado de sueldo";
        item[this.cont++] = "Listado de transporte por conductor";
    }

    public void verItems(String titulo) {
        if (this.cont != 0) {
            ConsolaS.mostrarTitulo(" " + titulo + " ", 60, '*', '*');
            ConsolaS.mostrarSeparador(60, '=');
            ConsolaS.mostrarTitulo("Menu principal", 60, ' ', ' ');
            for (int i = 0; i < this.cont; i++) {
                ConsolaS.mostrarLinea("\t" + (i + 1) + ") " + item[i]);
            }
            ConsolaS.mostrarLinea("\t0) Salir");
        } else {
            ConsolaS.mostrarLinea("No hay ningun item cargado");
        }
    }

    public static int ejecutar(int cantOpciones) {
        int opcion = -1;
        if (cantOpciones != 0) {
            ConsolaS.mostrarlinea("\tIngrese una opcion: ");
            opcion = ConsolaE.leerEntero();

            while (opcion < 0 || opcion > cantOpciones) {
                ConsolaS.mostrarLinea("Opcion invalida. Ingrese un numero entre 0 y " + cantOpciones + ": ");
                opcion = ConsolaE.pedirOpcion();
            }
        } else {
            ConsolaS.mostrarLinea("No hay items cargados");
        }
        return opcion;
    }

    public static void subMenu() {
        ConsolaS.mostrarSeparador(60, '=');
        ConsolaS.mostrarTitulo("Actualizacion transporte", 60, ' ', ' ');
        ConsolaS.mostrarLinea("\t1) Altas \n"
                + "\t2) Bajas \n"
                + "\t3) Modificaciones \n"
                + "\t0) Salir");
    }

    public static void mostrarCabezera(Transporte dato) {
        if (dato.getTipo() == 'M') {
            System.out.printf(cabezeraTransporteMercaderia,
                    "DNI", "Horas", "Tipo", "Extra", "Cant.Toneladas", "EsPeligroso");
        } else {
            System.out.printf(cabezeraTransportePersona,
                    "DNI", "Horas", "Tipo", "Extra", "Cant.Pasajeros");
        }
    }

}
