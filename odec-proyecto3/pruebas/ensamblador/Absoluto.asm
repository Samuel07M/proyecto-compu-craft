// Absoluto.asm
// Calcula R1 = |R0| y pinta la primera palabra de la pantalla
// si R0 era negativo

@R0
D=M
@POSITIVO
D;JGE           // si R0 >= 0 no hay que cambiar el signo
D=-D            // D = -R0
@SCREEN
M=-1            // marca en pantalla: el numero era negativo
(POSITIVO)
@R1
M=D             // R1 = |R0|
(END)
@END
0;JMP
