/*
   Instrucción ya validada por el Parser
    - A: symbol = constante decimal o símbolo (sin la @)
    - C: dest, comp, jump (dest y jump pueden ser "")
    - L: symbol = nombre de la etiqueta (sin paréntesis)
*/
public class Instruction {

    public enum Type { A, C, L }

    public final Type type;
    public final int line; // línea del archivo fuente (desde 1)
    public final String symbol; // A y L
    public final String dest; // C
    public final String comp; // C
    public final String jump; // C

    private Instruction(Type type, int line, String symbol, String dest, String comp, String jump) {
        this.type = type;
        this.line = line;
        this.symbol = symbol;
        this.dest = dest;
        this.comp = comp;
        this.jump = jump;
    }

    public static Instruction a(int line, String value) {
        return new Instruction(Type.A, line, value, null, null, null);
    }

    public static Instruction label(int line, String name) {
        return new Instruction(Type.L, line, name, null, null, null);
    }

    public static Instruction c(int line, String dest, String comp, String jump) {
        return new Instruction(Type.C, line, null, dest, comp, jump);
    }

    // true si el valor de una instrucción A es una constante decimal
    public boolean isConstant() {
        return type == Type.A && Character.isDigit(symbol.charAt(0));
    }
}