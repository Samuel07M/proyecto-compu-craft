import java.util.HashMap;
import java.util.Map;

/*
   DisCode: tablas inversas (binario -> mnemónico) de la sección 7
   Son las mismas tablas del ensamblador, pero con la clave y el valor
   intercambiados
*/
public class DisCode {

    private static final Map<String, String> COMP = new HashMap<String, String>();
    private static final Map<String, String> DEST = new HashMap<String, String>();
    private static final Map<String, String> JUMP = new HashMap<String, String>();

    static {
        // a c1 c2 c3 c4 c5 c6 -> comp (28 códigos válidos)
        COMP.put("0101010", "0");
        COMP.put("0111111", "1");
        COMP.put("0111010", "-1");
        COMP.put("0001100", "D");
        COMP.put("0110000", "A");
        COMP.put("0001101", "!D");
        COMP.put("0110001", "!A");
        COMP.put("0001111", "-D");
        COMP.put("0110011", "-A");
        COMP.put("0011111", "D+1");
        COMP.put("0110111", "A+1");
        COMP.put("0001110", "D-1");
        COMP.put("0110010", "A-1");
        COMP.put("0000010", "D+A");
        COMP.put("0010011", "D-A");
        COMP.put("0000111", "A-D");
        COMP.put("0000000", "D&A");
        COMP.put("0010101", "D|A");
        COMP.put("1110000", "M");
        COMP.put("1110001", "!M");
        COMP.put("1110011", "-M");
        COMP.put("1110111", "M+1");
        COMP.put("1110010", "M-1");
        COMP.put("1000010", "D+M");
        COMP.put("1010011", "D-M");
        COMP.put("1000111", "M-D");
        COMP.put("1000000", "D&M");
        COMP.put("1010101", "D|M");

        // d1 d2 d3 -> dest (los 8 códigos son válidos)
        DEST.put("000", "");
        DEST.put("001", "M");
        DEST.put("010", "D");
        DEST.put("011", "MD");
        DEST.put("100", "A");
        DEST.put("101", "AM");
        DEST.put("110", "AD");
        DEST.put("111", "AMD");

        // j1 j2 j3 -> jump (los 8 códigos son válidos)
        JUMP.put("000", "");
        JUMP.put("001", "JGT");
        JUMP.put("010", "JEQ");
        JUMP.put("011", "JGE");
        JUMP.put("100", "JLT");
        JUMP.put("101", "JNE");
        JUMP.put("110", "JLE");
        JUMP.put("111", "JMP");
    }

    // Mnemónico comp para los 7 bits a c1..c6, o null si no existe
    public static String comp(String bits) {
        return COMP.get(bits);
    }

    public static String dest(String bits) {
        return DEST.get(bits);
    }

    public static String jump(String bits) {
        return JUMP.get(bits);
    }
}