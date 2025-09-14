package gestor;

import entradaSalida.*;

public class Menu {

    private String[] item;
    private int cont;
    private final int N = 4;

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
            ConsolaS.mostrarTitulo(" "+titulo+" ", 60, '*', '*');
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
    
    public static void subMenu(){
        ConsolaS.mostrarSeparador(60, '=');
        ConsolaS.mostrarTitulo("Actualizacion transporte", 60, ' ', ' ');
        ConsolaS.mostrarLinea("\t1) Altas \n"
                + "\t2) Bajas \n"
                + "\t3) Modificaciones \n"
                + "\t0) Salir");
    }
    
}
