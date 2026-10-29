// Resta.asm
// Calcula R2 = R0 - R1

@R0
D=M          // D = R0
@R1
D=D-M        // D = R0 - R1
@R2
M=D          // R2 = D
