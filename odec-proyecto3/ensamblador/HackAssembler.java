import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
   HackAssembler: traduce Prog.asm a Prog.hack en dos pasadas
  
    Primera pasada: recorre TODAS las líneas en orden, valida su sintaxis y
                    registra cada etiqueta con su dirección ROM. Así el primer
                    error que se reporta es siempre el de menor número de línea
    Segunda pasada: traduce cada instrucción A y C. Los símbolos que no son
                    predefinidos ni etiquetas son variables (RAM desde 16)
  
   Ante un error imprime "ERROR *** Línea N: mensaje", no genera el archivo
   de salida y termina con código 1
*/
public class HackAssembler {

    public static void main(String[] args) {
        PrintStream out = utf8Stdout();

        if (args.length != 1) {
            out.println("Uso: java HackAssembler <archivo.asm>");
            System.exit(2);
        }
        String inputPath = args[0];
        String outputPath = inputPath.endsWith(".asm")
                ? inputPath.substring(0, inputPath.length() - 4) + ".hack"
                : inputPath + ".hack";

        List<String> lines;
        try {
            lines = Parser.readLines(inputPath);
        } catch (IOException e) {
            out.println("No se pudo leer el archivo: " + inputPath);
            System.exit(1);
            return;
        }

        try {
            String result = assemble(lines);
            Files.write(Paths.get(outputPath), result.getBytes(StandardCharsets.UTF_8));
        } catch (AsmError e) {
            out.println("ERROR *** Línea " + e.getLine() + ": " + e.getMessage());
            out.flush();
            System.exit(1);
        } catch (IOException e) {
            out.println("No se pudo escribir el archivo: " + outputPath);
            System.exit(1);
        }
        out.flush();
        System.exit(0);
    }

    static String assemble(List<String> lines) throws AsmError {
        SymbolTable symbols = new SymbolTable();
        List<Instruction> program = new ArrayList<Instruction>();
        Map<String, Integer> labelLine = new HashMap<String, Integer>();

        // Primera pasada: validación + etiquetas
        int romAddress = 0;
        for (int i = 0; i < lines.size(); i++) {
            int lineNo = i + 1;
            Instruction ins = Parser.parseLine(lines.get(i), lineNo);
            if (ins == null) {
                continue;
            }
            if (ins.type == Instruction.Type.L) {
                Integer previous = labelLine.get(ins.symbol);
                if (previous != null) {
                    throw new AsmError(lineNo, "etiqueta '" + ins.symbol
                            + "' declarada dos veces (antes en la línea " + previous + ")");
                }
                labelLine.put(ins.symbol, lineNo);
                symbols.addEntry(ins.symbol, romAddress);
            } else {
                program.add(ins);
                romAddress++;
            }
        }

        // Segunda pasada: traducción + variables
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder(program.size() * 18);
        int nextVariable = 16;
        for (Instruction ins : program) {
            if (ins.type == Instruction.Type.A) {
                int value;
                if (ins.isConstant()) {
                    value = Integer.parseInt(ins.symbol);
                } else {
                    if (!symbols.contains(ins.symbol)) {
                        symbols.addEntry(ins.symbol, nextVariable++);
                    }
                    value = symbols.getAddress(ins.symbol);
                }
                sb.append('0').append(toBinary(value, 15)).append(nl);
            } else {
                sb.append("111")
                  .append(Code.comp(ins.comp))
                  .append(Code.dest(ins.dest))
                  .append(Code.jump(ins.jump))
                  .append(nl);
            }
        }
        return sb.toString();
    }

    private static String toBinary(int value, int bits) {
        String b = Integer.toBinaryString(value);
        StringBuilder sb = new StringBuilder(bits);
        for (int i = b.length(); i < bits; i++) {
            sb.append('0');
        }
        return sb.append(b).toString();
    }

    // Salida estándar en UTF-8 para que "Línea" se vea igual en Git Bash
    private static PrintStream utf8Stdout() {
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8");
            System.setOut(ps);
            return ps;
        } catch (UnsupportedEncodingException e) {
            return System.out;
        }
    }
}