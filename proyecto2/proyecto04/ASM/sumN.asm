//Calcula la suma de los primeros N números naturales.
    //Inicializar sum = 0
    @sum
    M=0

    //Inicializar el contador i = 1
    @i
    M=1

(LOOP)
    //Mientras (i - N) > 0 se hace la suma
    @i
    D=M
    @R0
    D=D-M    //D = i - RAM[0]
    @WRITE_RESULT
    D;JGT    // Si (i - N) > 0, ir a WRITE_RESULT

    //sum = sum + i
    @i
    D=M
    @sum
    M=D+M

    //i = i + 1
    @i
    M=M+1

    @LOOP
    0;JMP   //Repetir ciclo

(WRITE_RESULT)
    //Guardar el resultado acumulado en RAM[1]
    @sum
    D=M
    @R1
    M=D

(END)
    //Bucle infinito final
    @END
    0;JMP