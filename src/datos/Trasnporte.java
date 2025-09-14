package datos;

import interfaces.ICalculable;
import interfaces.Validable;
import entradaSalida.*;
import excepcionesPersonalizadas.DatosInvalidosException;
import java.io.IOException;
import java.io.RandomAccessFile;
import persistencia.Grabable;

public abstract class Trasnporte implements Grabable, ICalculable, Validable {

    private int codT; // 4bytes
    private char tipoTransporte; //1  bytes
    private int horas; //4 bytes
    private long dniConductor; //8 bytes
    private double extra; //8 bytes
    //4 + 1 + 4 +8 + 8 = 25
    private final double sueldoBase = 400000.0;
    private static int TAMAREG = 25;
    private static int TAMARCHIVO = 100;

    public Trasnporte() {
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
        return "Trasnporte{" + "codT=" + codT + ", tipo=" + tipoTransporte + ", horas=" + horas
                + ", dniConductor=" + dniConductor + ", extra=" + extra + '}';
    }

    @Override
    public void cargarDatos() {
        cargarHoras();
        cargarExtra();
    }

    public void cargarCodT(int cont) throws Exception {
        if (cont < TAMARCHIVO) {
            setCodT(cont++);
            ConsolaS.mostrarLinea("Nro de transporte asignado: " + getCodT());
        } else {
            throw new Exception("No se pueden crear mas registros");
        }
    }

    public static Trasnporte cargarTipoT() {
        Trasnporte t = null;
        char tipo = ' ';
        boolean valido = false;
        do {
            ConsolaS.mostrarlinea("Ingrese el tipo de transporte (P = personas | M = mercaderias): ");
            tipo = ConsolaE.leerCaracter();
            tipo = Character.toUpperCase(tipo);
            valido = tipo == 'P' || tipo == 'M';
            if (!valido) {
                ConsolaS.mostrarLinea("Ingresa un valor valido");
            }
        } while (!valido);

        switch (tipo) {
            case 'P':
                t = new TransportePersonas();
                break;
            case 'M':
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
        if (e instanceof Integer entero) {
            if (entero > 0 && entero < 100) {
                resul = true;
            } else {
                throw new DatosInvalidosException("Ingresar un numero del 1 al 100");
            }

        } else if (e instanceof Character ch) {
            if (ch == 'P' || ch == 'M') {
                resul = true;
            } else {
                throw new DatosInvalidosException("Ingresar un caracter valido");
            }
        } else if (e instanceof Double d) {
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

    private void setExtra(double extra) {
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

    public double getSueldoBase() {
        return sueldoBase;
    }
}
