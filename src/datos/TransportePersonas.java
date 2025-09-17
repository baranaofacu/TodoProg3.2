package datos;

import entradaSalida.*;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TransportePersonas extends Transporte {

    private int cantPasajeros;//4bytes
    //26+ 4 = 30

    private int relleno1;         // 4 bytes
    private boolean relleno2; // 1 byte
    //forzamos que el TAMREG sea de 35 para evitar errores de archivos 
    private static final int TAMAREG = 35;
    private static final int TAMARCHIVO = 100;

    public TransportePersonas() {
        super();
        this.cantPasajeros = 0;
        this.relleno1 = 1;
        this.relleno2 = false;
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
    public void cargarDatos() {
        super.cargarDatos();
        cargarCantPasajeros();
        setTipo('P');
        setExtra(calcularExtra());
    }

    @Override
    public void grabar(RandomAccessFile a) {
        try {
            super.grabar(a);
            a.writeInt(cantPasajeros);
            a.writeInt(relleno1);
            a.writeBoolean(relleno2);
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al grabar el registro: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void leer(RandomAccessFile a) {
        try {
            super.leer(a);
            cantPasajeros = a.readInt();
            relleno1 = a.readInt();
            relleno2 = a.readBoolean();
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al leer el registro: " + e.getMessage());
        }
    }

    public void cargarCantPasajeros() {
        int cant;
        do {
            ConsolaS.mostrarlinea("Ingresa la cantidad de pasajeros: ");
            cant = ConsolaE.leerEntero();
            if (cant < 0 || cant > 60) {
                ConsolaS.mostrarAdvertencia("Ingrese un numero valido (0-60)");
            }
        } while (cant < 0 || cant > 60);
        setCantPasajeros(cant);
    }

    @Override
    public void mostrarRegistro() {
        ConsolaS.mostrarLinea(super.toString() + toString());
    }

    @Override
    public String toString() {
        return String.format("%9d", cantPasajeros);
    }

    private void setCantPasajeros(int cantPasajeros) {
        this.cantPasajeros = cantPasajeros;
    }

    public int getCantPasajeros() {
        return cantPasajeros;
    }

    @Override
    public double calcularExtra() {
        double resul;
        if (cantPasajeros > 9) {
            resul = 5500 * getHoras();
        }else{
            resul = 3000 * getHoras();
        }
        return resul;
    }

    @Override
    public void modificarDatos(long dni) {
        super.cargarDniConductor(dni);
        cargarDatos();
        super.cargarExtra();
    }
}
