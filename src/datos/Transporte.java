package datos;

import interfaces.ICalculable;
import interfaces.Validable;
import entradaSalida.*;
import excepcionesPersonalizadas.DatosInvalidosException;
import java.io.IOException;
import java.io.RandomAccessFile;
import persistencia.Grabable;

public abstract class Transporte implements Grabable, ICalculable, Validable {

    private int codT; // 4bytes
    private char tipoTransporte; //2  bytes
    private int horas; //4 bytes
    private long dniConductor; //8 bytes
    private double extra; //8 bytes
    //4 + 2 + 4 +8 + 8 = 26
    private static int TAMAREG = 26;
    private static int TAMARCHIVO = 100;

    public Transporte() {
        this.codT = 0;
        this.tipoTransporte = ' ';
        this.horas = 0;
        this.dniConductor = 0;
        this.extra = 0;
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
            a.writeInt(codT);
            a.writeChar(tipoTransporte);
            a.writeInt(horas);
            a.writeLong(dniConductor);
            a.writeDouble(extra);
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al grabar el archivo: " + e.getMessage());
        }
    }

    @Override
    public void leer(RandomAccessFile a) {
        try {
            codT = a.readInt();
            tipoTransporte = a.readChar();
            horas = a.readInt();
            dniConductor = a.readLong();
            extra = a.readDouble();
        } catch (IOException e) {
            ConsolaS.mostrarAdvertencia("Error al leer registro: " + e.getMessage());
        }
    }

    @Override
    public void mostrarRegistro() {
        ConsolaS.mostrarLinea(toString());
    }

    @Override
    public String toString() {
        return String.format("\t%-11d \t%-8d %-8c %-10.2f",
                 dniConductor, horas, tipoTransporte, extra);
    }

    @Override
    public void cargarDatos() {
        cargarHoras();
        cargarExtra();
    }

    public abstract void modificarDatos(long dni);

    public void cargarCodT(int cont) throws Exception {
        if (cont < TAMARCHIVO) {
            setCodT(cont++);
            ConsolaS.mostrarLinea("Nro de transporte asignado: " + getCodT());
        } else {
            throw new Exception("No se pueden crear mas registros");
        }
    }

    public static Transporte cargarTipoT(int tipo) {
        Transporte t = null;
//        char tipo = ' ';
//        boolean valido = false;
//        do {
//            ConsolaS.mostrarlinea("Ingrese el tipo de transporte (P = personas | M = mercaderias): ");
//            tipo = ConsolaE.leerCaracter();
//            tipo = Character.toUpperCase(tipo);
//            valido = tipo == 'P' || tipo == 'M';
//            if (!valido) {
//                ConsolaS.mostrarLinea("Ingresa un valor valido");
//            }
//        } while (!valido);

        switch (tipo) {
            case 1:
                t = new TransportePersonas();
                break;
            case 2:
                t = new TransporteMercaderia();
                break;
        }
        return t;
    }

    public void cargarHoras() {
        int h = 0;
        boolean valido = false;
        do {
            try {
                ConsolaS.mostrarlinea("Ingrese la cantidad de horas: ");
                h = ConsolaE.leerEntero();
                if (h > 0 && h < 1000) {
                    valido = true;
                } else {
                    throw new DatosInvalidosException("Las horas deben estar entre 1 y 999");
                }
            } catch (DatosInvalidosException e) {
                ConsolaS.mostrarAdvertencia(e.getMessage());
            }
        } while (!valido);
        setHoras(h);
    }

    @Override
    public abstract double calcularExtra();

    @Override
    public boolean validar(Object e) throws DatosInvalidosException {
        boolean resul = false;
        if (e instanceof Integer) {
            int entero = (int) e;
            if (entero > 0 && entero < 100) {
                resul = true;
            } else {
                throw new DatosInvalidosException("Ingresar un numero del 1 al 100");
            }

        } else if (e instanceof Character) {
            char ch = (Character) e;
            if (ch == 'P' || ch == 'M') {
                resul = true;
            } else {
                throw new DatosInvalidosException("Ingresar un caracter valido");
            }
        } else if (e instanceof Double) {
            double d = (double) e;
            if (d >= 0 && d <= 11.5) {
                resul = true;
            } else if (d < 0) {
                throw new DatosInvalidosException("Ingrese un valor positivo");
            } else {
                throw new DatosInvalidosException("No puede superar la capacidad maxima (11.5T)");
            }
        }
        return resul;
    }

    public void cargarExtra() {
        setExtra(calcularExtra());
    }

    public void cargarDniConductor(long dni) {
        setDniConductor(dni);
    }

    private void setCodT(int codT) {
        this.codT = codT;
    }

    protected void setTipo(char tipo) {
        this.tipoTransporte = tipo;
    }

    private void setHoras(int horas) {
        this.horas = horas;
    }

    private void setDniConductor(long dniConductor) {
        this.dniConductor = dniConductor;
    }

    protected void setExtra(double extra) {
        this.extra = extra;
    }

    public int getCodT() {
        return codT;
    }

    public char getTipo() {
        return tipoTransporte;
    }

    public int getHoras() {
        return horas;
    }

    public long getDniConductor() {
        return dniConductor;
    }

    public double getExtra() {
        return extra;
    }
}
