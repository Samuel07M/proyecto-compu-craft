import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * HackDisassembler: traduce Prog.hack a ProgDis.asm.
 *
 *  1. Lee y valida cada línea en orden (longitud, caracteres, bits 14-13,
 *     código de cómputo). El primer error detiene el programa.
 *  2. Busca los saltos de la regla 9.1: instrucción i es C con salto y la
 *     instrucción i-1 es A con valor v <= n. Cada destino v distinto recibe
 *     una etiqueta L0, L1, ... (ordenadas por dirección).
 *  3. Escribe el programa: (Lk) antes de la instrucción v (o al final si
 *     v == n) y @Lk en lugar de @v en la instrucción i-1.
 *
 * Ante un error imprime "ERROR *** Línea N: mensaje", no genera el archivo
 * de salida y termina con código 1.
 */
public class HackDisassembler {

    public static void main(String[] args) {
        PrintStream out = utf8Stdout();

        if (args.length != 1) {
            out.println("Uso: java HackDisassembler <archivo.hack>");
            System.exit(2);
        }
        String inputPath = args[0];
        String base = inputPath.endsWith(".hack")
                ? inputPath.substring(0, inputPath.length() - 5)
                : inputPath;
        String outputPath = base + "Dis.asm";

        List<String> lines;
        try {
            lines = readLines(inputPath);
        } catch (IOException e) {
            out.println("No se pudo leer el archivo: " + inputPath);
            System.exit(1);
            return;
        }

        try {
            String result = disassemble(lines);
            Files.write(Paths.get(outputPath), result.getBytes(StandardCharsets.UTF_8));
        } catch (DisError e) {
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

    /**
     * Lee las líneas aceptando CRLF y LF. Solo el ÚLTIMO salto de línea es
     * opcional: una línea vacía en medio (o al final después del salto final)
     * se conserva para que la validación la reporte como error de longitud.
     */
    static List<String> readLines(String path) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        if (text.startsWith("﻿")) {
            text = text.substring(1);
        }
        List<String> lines = new ArrayList<String>();
        if (text.isEmpty()) {
            return lines;
        }
        String[] parts = text.split("\n", -1);
        int count = parts.length;
        if (text.endsWith("\n")) {
            count--;                       // el salto de línea final es opcional
        }
        for (int i = 0; i < count; i++) {
            String line = parts[i];
            if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
            }
            lines.add(line);
        }
        return lines;
    }

    /** Valida una línea según la sección 10.2. */
    static void validate(String s, int lineNo) throws DisError {
        if (s.length() != 16) {
            throw new DisError(lineNo, "la instrucción debe tener 16 caracteres y tiene " + s.length());
        }
        for (int k = 0; k < 16; k++) {
            char c = s.charAt(k);
            if (c != '0' && c != '1') {
                throw new DisError(lineNo, "carácter inválido '" + c + "' en la posición " + (k + 1)
                        + " (solo se permiten 0 y 1)");
            }
        }
        if (s.charAt(0) == '1') {
            if (!s.substring(1, 3).equals("11")) {
                throw new DisError(lineNo, "instrucción C con bits 14-13 '" + s.substring(1, 3)
                        + "' (deben ser 11)");
            }
            if (DisCode.comp(s.substring(3, 10)) == null) {
                throw new DisError(lineNo, "código de cómputo inválido '" + s.substring(3, 10) + "'");
            }
        }
    }

    static String disassemble(List<String> lines) throws DisError {
        int n = lines.size();

        // ---------------- 1. Validación ----------------
        for (int i = 0; i < n; i++) {
            validate(lines.get(i), i + 1);
        }

        // ---------------- 2. Destinos de salto (regla 9.1) ----------------
        boolean[] usesLabel = new boolean[n];               // la instrucción A i-1 se escribe @Lk
        TreeMap<Integer, String> labels = new TreeMap<Integer, String>(); // dirección -> nombre
        for (int i = 1; i < n; i++) {
            String cur = lines.get(i);
            String prev = lines.get(i - 1);
            boolean isJump = cur.charAt(0) == '1' && !cur.substring(13, 16).equals("000");
            if (isJump && prev.charAt(0) == '0') {
                int v = Integer.parseInt(prev.substring(1), 2);
                if (v <= n) {
                    usesLabel[i - 1] = true;
                    labels.put(v, null);
                }
            }
        }
        int k = 0;
        for (Integer address : labels.keySet()) {
            labels.put(address, "L" + (k++));
        }

        // ---------------- 3. Escritura ----------------
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder(n * 12);
        for (int i = 0; i < n; i++) {
            if (labels.containsKey(i)) {
                sb.append('(').append(labels.get(i)).append(')').append(nl);
            }
            String s = lines.get(i);
            if (s.charAt(0) == '0') {
                int v = Integer.parseInt(s.substring(1), 2);
                if (usesLabel[i]) {
                    sb.append('@').append(labels.get(v));
                } else {
                    sb.append('@').append(v);
                }
            } else {
                String comp = DisCode.comp(s.substring(3, 10));
                String dest = DisCode.dest(s.substring(10, 13));
                String jump = DisCode.jump(s.substring(13, 16));
                if (!dest.isEmpty()) {
                    sb.append(dest).append('=');
                }
                sb.append(comp);
                if (!jump.isEmpty()) {
                    sb.append(';').append(jump);
                }
            }
            sb.append(nl);
        }
        if (labels.containsKey(n)) {                         // salto a v == n
            sb.append('(').append(labels.get(n)).append(')').append(nl);
        }
        return sb.toString();
    }

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
