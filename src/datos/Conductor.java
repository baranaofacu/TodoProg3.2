package datos;

import interfaces.Validable;
import entradaSalida.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import persistencia.*;

public class Conductor implements Grabable, Validable {

    private int nroOrd; //4bytes
    private long dni; // 8bytes
    private String ape_Nom; //40*2
    //4 + 8 + 40*2 = 92

    private static final double sueldoBase = 400000.0;
    private static final int LONGITUD_APENOM = 40;
    private final int TAMAREG = 92;
    private final int TAMARCHIVO = 100;

    public Conductor() {
        this.dni = 0;
        this.ape_Nom = "";
    }

    @Override
    public int tamRegistro() {
        return TAMAREG;
    }

    @Override
    public int tamArchivo() {
        return TAMARCHIVO;
    }

    @Override
    public void grabar(RandomAccessFile a) {
        try {
            a.writeInt(nroOrd);
            a.writeLong(dni);
            Registro.writeString(a, ape_Nom, LONGITUD_APENOM);
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al grabar el archivo: " + e.getMessage());
        }
    }

    @Override
    public void leer(RandomAccessFile a) {
        try {
            nroOrd = a.readInt();
            dni = a.readLong();
            ape_Nom = Registro.readString(a, LONGITUD_APENOM).trim();
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al leer registro: " + e.getMessage());
        }
    }

    @Override
    public void mostrarRegistro() {
        ConsolaS.mostrarLinea(toString());
    }

    @Override
    public void cargarDatos() {
//        cargarDNI();
        cargarApe_Nom();
    }

    public void cargarNroOrd(int cont) throws Exception {
        if (cont < TAMARCHIVO) {
            setNroOrd(cont++);
            ConsolaS.mostrarLinea("Nro de orden asignado: " + getNroOrd());
        } else {
            throw new Exception("No se pueden crear mas registros");
        }
    }

    public void cargarDNI() {
        boolean valido = false;
        long numDni = 0;
        do {
            try {
                ConsolaS.mostrarlinea("Ingrese el dni: ");
                numDni = ConsolaE.leerLong();
                valido = validar(numDni);
            } catch (Exception e) {
                ConsolaS.mostrarAdvertencia(e.getMessage());
            }
        } while (!valido);
        setDni(numDni);
    }

    public void cargarApe_Nom() {
        Scanner sc = new Scanner(System.in);
        boolean valido = false;
        String nom = "";
        do {
            try {
                ConsolaS.mostrarlinea("Ingrese el nombre y apellido: ");
                nom = sc.nextLine();
                valido = validar(nom);
            } catch (Exception e) {
                ConsolaS.mostrarAdvertencia(e.getMessage());
            }
        } while (!valido);
        setApe_Nom(nom);
    }

    @Override
    public boolean validar(Object e) throws Exception {
        if (e instanceof Long) {
            Long dni = (Long) e;
            String dniStr = String.valueOf(dni);
            if (dniStr.length() == 8) {
                return true;
            } else {
                throw new Exception("DNI incorrecto, debe tener 8 dígitos");
            }
        }
        if (e instanceof String) {
            String nom = (String) e;
            if (!nom.isEmpty()) {
                return true;
            } else {
                throw new Exception("El nombre no puede estar vacio");
            }
        }
        return false;
    }

    public static double calcularSueldoFinal(int cantHoras, double extra) {
        double resul;
        resul = cantHoras * 7500;
        resul = sueldoBase + resul + extra;
        return resul;
    }

    public void mostrarNombreDniSueldoFinal(double sueldoFinal) {
        System.out.printf("%-20s %-12d %-15.2f%n",
                getApe_Nom(), getDni(), sueldoFinal);
    }

    @Override
    public String toString() {
        return "Conductor{" + "dni=" + dni + ", ape_Nom=" + ape_Nom + '}';
    }

    private void setDni(long dni) {
        this.dni = dni;
    }

    private void setApe_Nom(String ape_Nom) {
        this.ape_Nom = ape_Nom;
    }

    public void setNroOrd(int nroOrd) {
        this.nroOrd = nroOrd;
    }

    public int getNroOrd() {
        return nroOrd;
    }

    public long getDni() {
        return dni;
    }

    public String getApe_Nom() {
        return ape_Nom;
    }

    public double getSueldoBase() {
        return sueldoBase;
    }
}
