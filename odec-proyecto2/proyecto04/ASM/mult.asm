// R2= R0 * R1
    //Inicializar R2 = 0
    @R2
    M=0

    //Copiar R1 a una variable 'i' para usarla como contador
    @R1
    D=M
    @i
    M=D

(LOOP)
    //Mientras i>0 se hace la suma
    @i
    D=M
    @END
    D;JLE    //Si i <= 0, ir a END

    //R2 = R2 + R0
    @R0
    D=M
    @R2
    M=D+M

    //i = i - 1
    @i
    M=M-1

    @LOOP
    0;JMP    //Repetir ciclo

(END)
    // Bucle infinito final
    @END
    0;JMP