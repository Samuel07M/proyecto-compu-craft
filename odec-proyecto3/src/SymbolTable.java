import java.util.HashMap;

/**
 * SymbolTable: mantiene la correspondencia entre nombres simbólicos (etiquetas
 * y variables) y direcciones de memoria/ROM.
 *
 * Se inicializa con los símbolos predefinidos del lenguaje Hack: R0..R15,
 * SCREEN, KBD, SP, LCL, ARG, THIS y THAT.
 */
public class SymbolTable {

    private final HashMap<String, Integer> table;

    public SymbolTable() {
        table = new HashMap<>();

        // Registros R0..R15 (direcciones 0 a 15)
        for (int i = 0; i <= 15; i++) {
            table.put("R" + i, i);
        }

        // Símbolos predefinidos del lenguaje Hack
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
        table.put("SCREEN", 16384);
        table.put("KBD", 24576);
    }

    /** Agrega el par (symbol, address) a la tabla. */
    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    /** Indica si la tabla de símbolos ya contiene el símbolo dado. */
    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    /** Devuelve la dirección asociada al símbolo. */
    public int getAddress(String symbol) {
        return table.get(symbol);
    }
}
