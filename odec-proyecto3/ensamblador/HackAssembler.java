import java.io.FileWriter;
import java.io.IOException;

/**
 * HackAssembler: punto de entrada del ensamblador de la plataforma Hack
 * (Proyecto 6 de Nand2Tetris).
 *
 * Traduce un programa fuente en lenguaje ensamblador Hack (.asm) a su
 * representación en código de máquina binario (.hack), mediante dos pasadas:
 *
 *   Primera pasada:  recorre el programa y registra en la tabla de símbolos
 *                     la dirección ROM de cada etiqueta (xxx).
 *   Segunda pasada:   recorre el programa de nuevo, traduce cada instrucción
 *                     A y C a binario, y asigna direcciones RAM (a partir de
 *                     16) a las variables encontradas por primera vez.
 *
 * Uso:  java HackAssembler ruta/al/programa.asm
 * Genera: ruta/al/programa.hack
 */
public class HackAssembler {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java HackAssembler <archivo.asm>");
            System.exit(1);
        }

        String inputPath = args[0];
        if (!inputPath.endsWith(".asm")) {
            System.err.println("Error: el archivo de entrada debe tener extensión .asm");
            System.exit(1);
        }
        String outputPath = inputPath.substring(0, inputPath.length() - 4) + ".hack";

        try {
            SymbolTable symbolTable = new SymbolTable();
            Code code = new Code();

            // ---------- Primera pasada: etiquetas (L_COMMAND) ----------
            Parser firstPass = new Parser(inputPath);
            int romAddress = 0;
            while (firstPass.hasMoreCommands()) {
                firstPass.advance();
                switch (firstPass.commandType()) {
                    case L_COMMAND:
                        String label = firstPass.symbol();
                        if (!symbolTable.contains(label)) {
                            symbolTable.addEntry(label, romAddress);
                        }
                        break;
                    case A_COMMAND:
                    case C_COMMAND:
                        romAddress++;
                        break;
                }
            }

            // ---------- Segunda pasada: traducción + variables ----------
            Parser secondPass = new Parser(inputPath);
            StringBuilder output = new StringBuilder();
            int nextVariableAddress = 16;

            while (secondPass.hasMoreCommands()) {
                secondPass.advance();
                Parser.CommandType type = secondPass.commandType();

                if (type == Parser.CommandType.A_COMMAND) {
                    String symbol = secondPass.symbol();
                    int address;
                    if (symbol.matches("\\d+")) {
                        address = Integer.parseInt(symbol);
                    } else {
                        if (!symbolTable.contains(symbol)) {
                            symbolTable.addEntry(symbol, nextVariableAddress);
                            nextVariableAddress++;
                        }
                        address = symbolTable.getAddress(symbol);
                    }
                    output.append("0").append(toBinary(address, 15)).append("\n");

                } else if (type == Parser.CommandType.C_COMMAND) {
                    String dest = code.dest(secondPass.dest());
                    String comp = code.comp(secondPass.comp());
                    String jump = code.jump(secondPass.jump());
                    output.append("111").append(comp).append(dest).append(jump).append("\n");
                }
                // L_COMMAND no genera código
            }

            try (FileWriter writer = new FileWriter(outputPath)) {
                writer.write(output.toString());
            }

            System.out.println("Ensamblado exitoso: " + outputPath);

        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de traducción: " + e.getMessage());
            System.exit(1);
        }
    }

    /** Convierte un entero no negativo a una cadena binaria de 'bits' dígitos. */
    private static String toBinary(int value, int bits) {
        String binary = Integer.toBinaryString(value);
        StringBuilder padded = new StringBuilder();
        for (int i = binary.length(); i < bits; i++) {
            padded.append('0');
        }
        return padded.append(binary).toString();
    }
}
