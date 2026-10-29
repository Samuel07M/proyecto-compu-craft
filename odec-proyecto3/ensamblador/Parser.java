import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/*
   Parser: lee el archivo .asm, limpia cada línea (comentarios y espacios)
   y valida su sintaxis según la sección 7. Si la línea es inválida lanza
   AsmError con el número de línea real del archivo
*/
public class Parser {

    // Símbolo: letras, dígitos, _ . $ : y que no empiece por dígito
    private static final Pattern SYMBOL = Pattern.compile("[A-Za-z_.$:][A-Za-z0-9_.$:]*");
    private static final Pattern DIGITS = Pattern.compile("[0-9]+");

    /*
       Lee todas las líneas del archivo SIN descartar ninguna, para que la
       posición i de la lista corresponda a la línea i+1 del archivo.
       Acepta fin de línea CRLF y LF
    */
    public static List<String> readLines(String path) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        if (text.startsWith("﻿")) {          // BOM de algunos editores de Windows
            text = text.substring(1);
        }
        List<String> lines = new ArrayList<String>();
        for (String line : text.split("\n", -1)) {
            if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
            }
            lines.add(line);
        }
        return lines;
    }

    // Quita el comentario (// ...) y TODOS los espacios y tabulaciones
    public static String clean(String raw) {
        int comment = raw.indexOf("//");
        if (comment != -1) {
            raw = raw.substring(0, comment);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch != ' ' && ch != '\t' && ch != '\r') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static boolean isValidSymbol(String s) {
        return SYMBOL.matcher(s).matches();
    }

    /*
       Analiza una línea. Devuelve null si está vacía (o solo tiene
       comentario), o la instrucción validada. Lanza AsmError si es inválida
    */
    public static Instruction parseLine(String raw, int lineNo) throws AsmError {
        String s = clean(raw);
        if (s.isEmpty()) {
            return null;
        }
        if (s.startsWith("@")) {
            return parseA(s, lineNo);
        }
        if (s.startsWith("(")) {
            return parseLabel(s, lineNo);
        }
        if (s.endsWith(")")) {
            throw new AsmError(lineNo, "etiqueta sin paréntesis de apertura '" + s + "'");
        }
        return parseC(s, lineNo);
    }

    // A
    private static Instruction parseA(String s, int lineNo) throws AsmError {
        String value = s.substring(1);
        if (value.isEmpty()) {
            throw new AsmError(lineNo, "instrucción A sin valor");
        }
        if (Character.isDigit(value.charAt(0))) {
            if (!DIGITS.matcher(value).matches()) {
                throw new AsmError(lineNo, "símbolo inválido '" + value + "' (no puede empezar por dígito)");
            }
            String sinCeros = value.replaceFirst("^0+(?!$)", "");
            if (sinCeros.length() > 5 || Integer.parseInt(sinCeros) > 32767) {
                throw new AsmError(lineNo, "constante fuera de rango '" + value + "' (debe estar entre 0 y 32767)");
            }
            return Instruction.a(lineNo, value);
        }
        if (value.startsWith("-") && value.length() > 1 && DIGITS.matcher(value.substring(1)).matches()) {
            throw new AsmError(lineNo, "constante negativa '" + value + "' (debe estar entre 0 y 32767)");
        }
        if (!isValidSymbol(value)) {
            throw new AsmError(lineNo, "símbolo inválido '" + value + "'");
        }
        return Instruction.a(lineNo, value);
    }

    // L
    private static Instruction parseLabel(String s, int lineNo) throws AsmError {
        if (!s.endsWith(")") || s.length() < 2) {
            throw new AsmError(lineNo, "etiqueta sin paréntesis de cierre '" + s + "'");
        }
        String name = s.substring(1, s.length() - 1);
        if (name.isEmpty()) {
            throw new AsmError(lineNo, "etiqueta vacía '()'");
        }
        if (!isValidSymbol(name)) {
            throw new AsmError(lineNo, "nombre de etiqueta inválido '" + name + "'");
        }
        if (SymbolTable.isPredefined(name)) {
            throw new AsmError(lineNo, "la etiqueta '" + name + "' usa el nombre de un símbolo predefinido");
        }
        return Instruction.label(lineNo, name);
    }

    // C
    private static Instruction parseC(String s, int lineNo) throws AsmError {
        int eqCount = count(s, '=');
        int semiCount = count(s, ';');
        if (eqCount > 1) {
            throw new AsmError(lineNo, "instrucción con más de un '=': '" + s + "'");
        }
        if (semiCount > 1) {
            throw new AsmError(lineNo, "instrucción con más de un ';': '" + s + "'");
        }
        int eq = s.indexOf('=');
        int semi = s.indexOf(';');
        if (eq != -1 && semi != -1 && eq > semi) {
            throw new AsmError(lineNo, "instrucción mal formada, '=' después de ';': '" + s + "'");
        }

        String dest = (eq == -1) ? "" : s.substring(0, eq);
        String comp = s.substring(eq + 1, (semi == -1) ? s.length() : semi);
        String jump = (semi == -1) ? "" : s.substring(semi + 1);

        if (eq == -1 && semi == -1 && Code.comp(comp) == null) {
            throw new AsmError(lineNo, "instrucción no reconocida '" + s + "'");
        }
        if (eq != -1) {
            if (dest.isEmpty()) {
                throw new AsmError(lineNo, "destino vacío antes de '='");
            }
            if (Code.dest(dest) == null) {
                throw new AsmError(lineNo, "destino inválido '" + dest + "'");
            }
        }
        if (comp.isEmpty()) {
            throw new AsmError(lineNo, "cómputo vacío");
        }
        if (Code.comp(comp) == null) {
            throw new AsmError(lineNo, "cómputo inválido '" + comp + "'");
        }
        if (semi != -1) {
            if (jump.isEmpty()) {
                throw new AsmError(lineNo, "salto vacío después de ';'");
            }
            if (Code.jump(jump) == null) {
                throw new AsmError(lineNo, "salto inválido '" + jump + "'");
            }
        }
        return Instruction.c(lineNo, dest, comp, jump);
    }

    private static int count(String s, char c) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                n++;
            }
        }
        return n;
    }
}