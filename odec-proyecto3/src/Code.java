import java.util.HashMap;

/**
 * Code: traduce cada mnemónico de Hack assembly (dest, comp, jump) al código
 * binario correspondiente, según la especificación del lenguaje Hack.
 */
public class Code {

    private final HashMap<String, String> compTable;
    private final HashMap<String, String> jumpTable;

    public Code() {
        // Tabla "comp": 7 bits -> a + c1 c2 c3 c4 c5 c6
        compTable = new HashMap<>();
        compTable.put("0", "0101010");
        compTable.put("1", "0111111");
        compTable.put("-1", "0111010");
        compTable.put("D", "0001100");
        compTable.put("A", "0110000");
        compTable.put("!D", "0001101");
        compTable.put("!A", "0110001");
        compTable.put("-D", "0001111");
        compTable.put("-A", "0110011");
        compTable.put("D+1", "0011111");
        compTable.put("A+1", "0110111");
        compTable.put("D-1", "0001110");
        compTable.put("A-1", "0110010");
        compTable.put("D+A", "0000010");
        compTable.put("D-A", "0010011");
        compTable.put("A-D", "0000111");
        compTable.put("D&A", "0000000");
        compTable.put("D|A", "0010101");
        compTable.put("M", "1110000");
        compTable.put("!M", "1110001");
        compTable.put("-M", "1110011");
        compTable.put("M+1", "1110111");
        compTable.put("M-1", "1110010");
        compTable.put("D+M", "1000010");
        compTable.put("D-M", "1010011");
        compTable.put("M-D", "1000111");
        compTable.put("D&M", "1000000");
        compTable.put("D|M", "1010101");

        // Tabla "jump": 3 bits -> j1 j2 j3
        jumpTable = new HashMap<>();
        jumpTable.put("", "000");
        jumpTable.put("JGT", "001");
        jumpTable.put("JEQ", "010");
        jumpTable.put("JGE", "011");
        jumpTable.put("JLT", "100");
        jumpTable.put("JNE", "101");
        jumpTable.put("JLE", "110");
        jumpTable.put("JMP", "111");
    }

    /**
     * Traduce el campo dest (cualquier permutación de A, D, M) a sus 3 bits
     * (d1 d2 d3 -> A D M).
     */
    public String dest(String mnemonic) {
        char d1 = mnemonic.contains("A") ? '1' : '0';
        char d2 = mnemonic.contains("D") ? '1' : '0';
        char d3 = mnemonic.contains("M") ? '1' : '0';
        return "" + d1 + d2 + d3;
    }

    /** Traduce el campo comp a sus 7 bits (a c1 c2 c3 c4 c5 c6). */
    public String comp(String mnemonic) {
        String bits = compTable.get(mnemonic);
        if (bits == null) {
            throw new IllegalArgumentException("Mnemónico 'comp' inválido: " + mnemonic);
        }
        return bits;
    }

    /** Traduce el campo jump a sus 3 bits (j1 j2 j3). */
    public String jump(String mnemonic) {
        String bits = jumpTable.get(mnemonic);
        if (bits == null) {
            throw new IllegalArgumentException("Mnemónico 'jump' inválido: " + mnemonic);
        }
        return bits;
    }
}
