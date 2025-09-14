package datos;

import entradaSalida.*;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TransportePersonas extends Transporte {

    private int cantPasajeros;//4bytes
    //26+ 4 = 29
    //forzamos que el TAMREG sea de 34 para evitar errores de archivos 

    private static final int TAMAREG = 35;
    private static final int TAMARCHIVO = 100;

    public TransportePersonas() {
        super();
        this.cantPasajeros = 0;
        setTipo('P');
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
    }

    @Override
    public void grabar(RandomAccessFile a) {
        try {
            super.grabar(a);
            a.writeInt(cantPasajeros);
            a.writeBoolean(false); // 1 byte
            a.writeBoolean(false); // 1 byte
            a.writeInt(0); //5 byte
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
            a.readBoolean(); // descartar
            a.readBoolean(); // descartar
            a.readInt();     // descartar
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

    private void setCantPasajeros(int cantPasajeros) {
        this.cantPasajeros = cantPasajeros;
    }

    public int getCantPasajeros() {
        return cantPasajeros;
    }

    @Override
    public double calcularExtra() {
        return 0;
    }
}
