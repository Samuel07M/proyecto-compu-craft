/*
   Error detectado en una línea del archivo .hack
   Guarda el número de línea (desde 1) para imprimir: ERROR *** Línea N: mensaje
*/
public class DisError extends Exception {

    private final int line;

    public DisError(int line, String message) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}