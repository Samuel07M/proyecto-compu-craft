//Si se presiona cualquier tecla pinta la pantalla de negro, si se suelta la pinta de blanco

(LOOP)
    //i = SCREEN (16384), guarda la posicion de inicio de pantalla
    @SCREEN
    D=A
    @i
    M=D

    //color = -1 si hay tecla; 0 si no
    @KBD
    D=M
    @NEGRO
    D;JNE

    @color
    M=0
    @FILL
    0;JMP

(NEGRO)
    @color
    M=-1

(FILL)
    //si i == 24576 (fin de la pantalla) volvemos a leer el teclado
    @i
    D=M
    @24576
    D=D-A
    @LOOP
    D;JEQ

    //RAM[i] = color
    @color
    D=M
    @i
    A=M
    M=D

    //i = i + 1
    @i
    M=M+1

    @FILL
    0;JMP