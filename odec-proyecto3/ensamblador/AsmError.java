/*
   Error de traducción detectado en una línea del archivo .asm
   Guarda el número de línea (contando desde 1, incluyendo comentarios y
   líneas en blanco) para imprimir: ERROR *** Línea N: mensaje
*/
public class AsmError extends Exception {

    private final int line;

    public AsmError(int line, String message) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}