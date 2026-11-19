// Sumatoria.asm
// Calcula R1 = 1 + 2 + ... + R0  (se asume R0 >= 0)
// Usa variables (i, suma) y etiquetas

    @i
    M = 1          // i = 1
    @suma
    M = 0          // suma = 0
(LOOP)
    @i
    D = M
    @R0
    D = D - M      // D = i - R0
    @FIN
    D ; JGT        // si i > R0 termina
    @i
    D = M
    @suma
    M = D + M      // suma = suma + i
    @i
    M = M + 1      // i = i + 1
    @LOOP
    0 ; JMP
(FIN)
    @suma
    D = M
    @R1
    M = D          // R1 = suma
(END)
    @END
    0;JMP
