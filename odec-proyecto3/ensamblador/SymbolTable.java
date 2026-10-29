import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable: tabla hash nombre -> dirección.
 * Se inicializa con los símbolos predefinidos; luego la primera pasada
 * agrega las etiquetas (dirección ROM) y la segunda las variables
 * (dirección RAM desde 16, en orden de primera aparición).
 */
public class SymbolTable {

    private static final Map<String, Integer> PREDEFINED = new HashMap<String, Integer>();

    static {
        for (int i = 0; i <= 15; i++) {
            PREDEFINED.put("R" + i, i);
        }
        PREDEFINED.put("SP", 0);
        PREDEFINED.put("LCL", 1);
        PREDEFINED.put("ARG", 2);
        PREDEFINED.put("THIS", 3);
        PREDEFINED.put("THAT", 4);
        PREDEFINED.put("SCREEN", 16384);
        PREDEFINED.put("KBD", 24576);
    }

    private final Map<String, Integer> table = new HashMap<String, Integer>(PREDEFINED);

    public static boolean isPredefined(String symbol) {
        return PREDEFINED.containsKey(symbol);
    }

    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return table.get(symbol);
    }
}
