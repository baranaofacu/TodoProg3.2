package datos;

import entradaSalida.*;
import excepcionesPersonalizadas.DatosInvalidosException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TransporteMercaderia extends Transporte {

    private double tonelada; //8 bytes
    private boolean esPeligroso; //1 byte
    //26 + 8 + 1 = 35

    private static final int TAMAREG = 35;
    private static final int TAMARCHIVO = 100;

    public TransporteMercaderia() {
        super();
        this.tonelada = 0;
        this.esPeligroso = false;
        setTipo('M');
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
            super.grabar(a);
            a.writeDouble(tonelada);
            a.writeBoolean(esPeligroso);
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al grabar el registro: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void leer(RandomAccessFile a) {
        try {
            super.leer(a);
            tonelada = a.readDouble();
            esPeligroso = a.readBoolean();
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al leer el registro: " + e.getMessage());
        }
    }

    @Override
    public void cargarDatos() {
        super.cargarDatos();
        cargarTonelada();
        cargaEsPeligroso();
    }

    public void cargarTonelada() {
        double t = 0;
        boolean valido = false;
        do {
            try {
                ConsolaS.mostrarlinea("Ingrese la cantidad de toneladas: ");
                t = ConsolaE.leerDouble();
                valido = validar(t);
            } catch (DatosInvalidosException e) {
                ConsolaS.mostrarAdvertencia(e.getMessage());
            }
        } while (!valido);
        setTonelada(t);
    }

    public void cargaEsPeligroso() {
        boolean resp;
        resp = ConsolaE.confirmar("¿Es transporte peligroso?");
        setEsPeligroso(resp);
    }
    
    

    private void setTonelada(double toneladas) {
        this.tonelada = toneladas;
    }

    private void setEsPeligroso(boolean esPeligroso) {
        this.esPeligroso = esPeligroso;
    }

    public double getTonelada() {
        return tonelada;
    }

    public boolean isEsPeligroso() {
        return esPeligroso;
    }

    @Override
    public double calcularExtra() {
        return 0;
    }

}
