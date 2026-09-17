// Copia N elementos desde una dirección ORIGEN hasta una dirección DESTINO.
// RAM[0] = ORIGEN
// RAM[1] = DESTINO
// RAM[2] = N

    //Inicializar el contador con el valor de N (RAM[2])
    @R2
    D=M
    @n
    M=D

    //Inicializar puntero src con la dirección ORIGEN (RAM[0])
    @R0
    D=M
    @src
    M=D

    //Inicializar puntero dest con la dirección DESTINO (RAM[1])
    @R1
    D=M
    @dest
    M=D

(LOOP)
    //Mientras n >= 0 copiar elementos
    @n
    D=M
    @END
    D;JLE    // Si n <= 0, terminar el programa

    //Leer el valor de la memoria de ORIGEN usando el puntero src
    @src
    A=M      
    D=M      

    //Escribir el valor en la memoria de DESTINO usando el puntero 'dest'
    @dest
    A=M      
    M=D      

    //src = src + 1
    @src
    M=M+1

    //dest = dest + 1
    @dest
    M=M+1

    //n = n - 1
    @n
    M=M-1

    
    @LOOP
    0;JMP   //Repetir ciclo

(END)
    // Bucle infinito final
    @END
    0;JMP