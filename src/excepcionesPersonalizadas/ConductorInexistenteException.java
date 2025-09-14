package excepcionesPersonalizadas;

public class ConductorInexistenteException extends DatosInvalidosException{
    
    public ConductorInexistenteException(String message) {
        super(message);
    }
}
