import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser: encapsula el acceso a un archivo .asm de entrada. Quita espacios en
 * blanco y comentarios, y divide cada instrucción en sus campos simbólicos.
 */
public class Parser {

    public enum CommandType { A_COMMAND, C_COMMAND, L_COMMAND }

    private final List<String> lines;
    private int currentIndex;
    private String currentLine;

    public Parser(String filePath) throws IOException {
        lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String raw;
            while ((raw = reader.readLine()) != null) {
                String clean = stripCommentsAndWhitespace(raw);
                if (!clean.isEmpty()) {
                    lines.add(clean);
                }
            }
        }
        currentIndex = -1;
        currentLine = null;
    }

    /** Quita comentarios (// ...) y espacios en blanco al inicio/final. */
    private String stripCommentsAndWhitespace(String line) {
        int commentIndex = line.indexOf("//");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex);
        }
        return line.trim();
    }

    /** ¿Hay más comandos en el archivo de entrada? */
    public boolean hasMoreCommands() {
        return currentIndex + 1 < lines.size();
    }

    /** Lee el siguiente comando y lo convierte en el comando actual. */
    public void advance() {
        currentIndex++;
        currentLine = lines.get(currentIndex);
    }

    /** Reinicia el recorrido para hacer una segunda pasada sobre el archivo. */
    public void reset() {
        currentIndex = -1;
        currentLine = null;
    }

    /** Devuelve el tipo del comando actual. */
    public CommandType commandType() {
        if (currentLine.startsWith("@")) {
            return CommandType.A_COMMAND;
        } else if (currentLine.startsWith("(")) {
            return CommandType.L_COMMAND;
        } else {
            return CommandType.C_COMMAND;
        }
    }

    /**
     * Devuelve el símbolo o valor decimal de la instrucción actual.
     * Solo debe llamarse cuando commandType() es A_COMMAND o L_COMMAND.
     */
    public String symbol() {
        if (commandType() == CommandType.A_COMMAND) {
            return currentLine.substring(1);
        } else { // L_COMMAND
            return currentLine.substring(1, currentLine.length() - 1);
        }
    }

    /**
     * Devuelve la parte "dest" de la instrucción C actual (puede ser "" si no
     * hay destino).
     */
    public String dest() {
        int eqIndex = currentLine.indexOf('=');
        if (eqIndex == -1) {
            return "";
        }
        return currentLine.substring(0, eqIndex);
    }

    /** Devuelve la parte "comp" de la instrucción C actual. */
    public String comp() {
        int eqIndex = currentLine.indexOf('=');
        int semiIndex = currentLine.indexOf(';');
        int start = (eqIndex == -1) ? 0 : eqIndex + 1;
        int end = (semiIndex == -1) ? currentLine.length() : semiIndex;
        return currentLine.substring(start, end);
    }

    /**
     * Devuelve la parte "jump" de la instrucción C actual (puede ser "" si no
     * hay salto).
     */
    public String jump() {
        int semiIndex = currentLine.indexOf(';');
        if (semiIndex == -1) {
            return "";
        }
        return currentLine.substring(semiIndex + 1);
    }
}
