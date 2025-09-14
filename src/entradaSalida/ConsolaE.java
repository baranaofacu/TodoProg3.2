package entradaSalida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Clase para facilitar operaciones de carga por teclado en consola estándar
 */
public class ConsolaE {

    /**
     * Lee un string desde teclado. El string termina con un salto de linea
     *
     * @return el string leido (sin el salto de linea)
     */
    public static String leerString() {

        int ch;
        String r = "";
        boolean done = false;
        while (!done) {
            try {
                ch = System.in.read();
                if (ch < 0 || (char) ch == '\n') {
                    done = true;
                } else {
                    if ((char) ch != '\r') {
                        r = r + (char) ch;
                    }
                }
            } catch (java.io.IOException e) {
                done = true;
            }
        }
        return r;
    }

    /**
     * Lee un integer desde teclado. La entrada termina con un salto de linea
     *
     * @return el valor cargado, como un int
     */
    public static int leerEntero() {

        while (true) {
            try {
                return Integer.parseInt(leerString().trim());
            } catch (NumberFormatException e) {
                System.out.print("ERROR! Escriba nuevamente: ");
//                e.printStackTrace();
            }
        }
    }

    /**
     * Lee un Integer desde teclado
     *
     * @return el Integer leido
     */
    public static Integer leerInteger() {

        while (true) {
            try {
                return Integer.parseInt(leerDato());
            } catch (NumberFormatException e) {
                System.out.print("ERROR! Escriba nuevamente: ");
            }
        }
    }

    /**
     * Lee un flotante desde teclado
     *
     * @return el flotante leido
     */
    public static float leerFlotante() {
        while (true) {
            try {
                return Float.parseFloat(leerString().trim());
            } catch (NumberFormatException e) {
                System.out.print("ERROR! Intente nuevamente: ");
            }
        }
    }

    /**
     * Lee un double desde teclado. La entrada termina con un salto de linea
     *
     * @return el valor cargado, como un double
     */
    public static double leerDouble() {

        while (true) {
            try {
                return Double.parseDouble(leerString().trim());
            } catch (NumberFormatException e) {
                System.out.print("ERROR! Intente nuevamente: ");
            }
        }
    }

    /**
     * Lee un long desde teclado. La entrada termina con un salto de linea
     *
     * @return el valor cargado, como un double
     */
    public static long leerLong() {

        while (true) {
            try {
                return Long.parseLong(leerString().trim());
            } catch (NumberFormatException e) {
                System.out.print("ERROR! Intente nuevamente: ");
            }
        }
    }

    /**
     * Lee un caracter desde teclado.
     *
     * @return el caracter leido (sin el salto de linea)
     */
    public static char leerCaracter() {
        while (true) {
            try {
                return (char) leerDato().charAt(0);
            } catch (NumberFormatException e) {
                System.out.print("ERROR! Intente nuevamente: ");
            }
        }
    }

    private static String leerDato() {
        String dato = "";
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader flujoE = new BufferedReader(isr);

        try {
            dato = flujoE.readLine();
        } catch (IOException e) {
            System.err.println("Error " + e.getMessage());
        }

        return dato;
    }

    /**
     * Metodo util para cuando se necesite confirmar una opcion con Si y No.
     *
     * @return un booleano verdadero si se ingreso
     */
    public static boolean confirmar(String mensaje) {
        char op = ' ';
        while (op != 'n' && op != 'N' && op != 's' && op != 'S') {
            System.out.print(mensaje + " s/n: ");
            op = leerCaracter();
        }
        return (op != 'n' && op != 'N');
    }

    /**
     * Metodo para pedir una opcion numerica por teclado.
     *
     * @return un entero que representa una opcion.
     */
    public static int pedirOpcion() {

        System.out.print("--> ");
        int opcion = ConsolaE.leerEntero();
        return opcion;
    }

    /**
     * Metodo para pedir una opcion numerica por teclado.
     *
     * @return un entero que representa una opcion.
     */
    public static int pedirOpcion(String s) {

        System.out.print(s + ": ");
        int opcion = ConsolaE.leerEntero();
        return opcion;
    }

    /**
     * Shows a message on the console and waits for the user to press ENTER.
     */
    public static void pausar() {
        System.out.print("Pulse ENTER para continuar...");
        leerString();
    }

}
