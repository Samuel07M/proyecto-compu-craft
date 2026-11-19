// errorEtiqueta.asm
// Error de etiqueta: LOOP se declara dos veces

(LOOP)
@R0
M=M+1
(LOOP)
@LOOP
0;JMP
