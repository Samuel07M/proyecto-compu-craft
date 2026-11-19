import java.util.HashMap;
import java.util.Map;

/*
   Code: traduce los mnemónicos dest, comp y jump a binario
   Solo acepta los mnemónicos EXACTOS de las tablas de la sección 7
   (1.ª edición). Cualquier otra escritura devuelve null (= error)
*/
public class Code {

    private static final Map<String, String> COMP = new HashMap<String, String>();
    private static final Map<String, String> DEST = new HashMap<String, String>();
    private static final Map<String, String> JUMP = new HashMap<String, String>();

    static {
        // comp -> a c1 c2 c3 c4 c5 c6  (28 mnemónicos)
        COMP.put("0",   "0101010");
        COMP.put("1",   "0111111");
        COMP.put("-1",  "0111010");
        COMP.put("D",   "0001100");
        COMP.put("A",   "0110000");
        COMP.put("!D",  "0001101");
        COMP.put("!A",  "0110001");
        COMP.put("-D",  "0001111");
        COMP.put("-A",  "0110011");
        COMP.put("D+1", "0011111");
        COMP.put("A+1", "0110111");
        COMP.put("D-1", "0001110");
        COMP.put("A-1", "0110010");
        COMP.put("D+A", "0000010");
        COMP.put("D-A", "0010011");
        COMP.put("A-D", "0000111");
        COMP.put("D&A", "0000000");
        COMP.put("D|A", "0010101");
        COMP.put("M",   "1110000");
        COMP.put("!M",  "1110001");
        COMP.put("-M",  "1110011");
        COMP.put("M+1", "1110111");
        COMP.put("M-1", "1110010");
        COMP.put("D+M", "1000010");
        COMP.put("D-M", "1010011");
        COMP.put("M-D", "1000111");
        COMP.put("D&M", "1000000");
        COMP.put("D|M", "1010101");

        // dest -> d1 d2 d3  (7 mnemónicos + vacío)
        DEST.put("",    "000");
        DEST.put("M",   "001");
        DEST.put("D",   "010");
        DEST.put("MD",  "011");
        DEST.put("A",   "100");
        DEST.put("AM",  "101");
        DEST.put("AD",  "110");
        DEST.put("AMD", "111");

        // jump -> j1 j2 j3  (7 mnemónicos + vacío)
        JUMP.put("",    "000");
        JUMP.put("JGT", "001");
        JUMP.put("JEQ", "010");
        JUMP.put("JGE", "011");
        JUMP.put("JLT", "100");
        JUMP.put("JNE", "101");
        JUMP.put("JLE", "110");
        JUMP.put("JMP", "111");
    }

    // 7 bits de comp, o null si el mnemónico no existe
    public static String comp(String mnemonic) {
        return COMP.get(mnemonic);
    }

    // 3 bits de dest, o null si el mnemónico no existe
    public static String dest(String mnemonic) {
        return DEST.get(mnemonic);
    }

    // 3 bits de jump, o null si el mnemónico no existe
    public static String jump(String mnemonic) {
        return JUMP.get(mnemonic);
    }
}