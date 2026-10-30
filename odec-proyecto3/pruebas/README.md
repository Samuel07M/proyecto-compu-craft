# Pruebas

Este documento describe los programas de prueba del Proyecto 3 y el resultado que se obtiene al ejecutarlos con el ensamblador y el desensamblador. Todos los resultados se obtuvieron ejecutando las herramientas sobre cada archivo.

Antes de probar hay que compilar una vez:

    ./compilar.sh

Todos los comandos se ejecutan desde la carpeta odec-proyecto3/.

## Ensamblador (.asm a .hack)

Uso:

    ./ensamblador.sh pruebas/ensamblador/Resta.asm

Si el programa es correcto se crea Programa.hack junto al .asm, con una instrucción de 16 bits por línea. Si tiene un error se imprime una línea con el formato "ERROR *** Línea N: mensaje", no se crea el archivo de salida y el programa termina con código 1. N cuenta desde 1 e incluye comentarios y líneas en blanco.

### Programas correctos (carpeta pruebas/ensamblador/)

Resta.asm: calcula R2 = R0 - R1. Genera 6 instrucciones.

Sumatoria.asm: usa variables (i, suma), etiquetas (LOOP, FIN, END) y espacios dentro de las instrucciones, por ejemplo "M = 1" o "D ; JGT". Genera 24 instrucciones. Sirve para probar que los espacios se ignoran.

Absoluto.asm: calcula R1 = |R0| y usa el símbolo predefinido SCREEN. Genera 11 instrucciones.

En los tres casos todas las líneas de salida miden 16 caracteres.

### Programas con error (carpeta pruebas/errores/)

errorSalto.asm
Contiene "0;jmp" en minúscula. Los mnemónicos distinguen mayúsculas.
Salida: ERROR *** Línea 8: salto inválido 'jmp'

errorDestino.asm
Usa el destino DM. En la tabla de la sección 7 el destino se escribe MD.
Salida: ERROR *** Línea 7: destino inválido 'DM'

errorComputo.asm
Usa D*A, que no está en la tabla de cómputos.
Salida: ERROR *** Línea 7: cómputo inválido 'D*A'

errorEstructura.asm
Contiene "D=M=A", con dos signos igual.
Salida: ERROR *** Línea 7: instrucción con más de un '=': 'D=M=A'

errorA.asm
Contiene @32768, que no cabe en 15 bits (el máximo es 32767).
Salida: ERROR *** Línea 6: constante fuera de rango '32768' (debe estar entre 0 y 32767)

errorEtiqueta.asm
Declara la etiqueta LOOP dos veces.
Salida: ERROR *** Línea 7: etiqueta 'LOOP' declarada dos veces (antes en la línea 4)

## Desensamblador (.hack a .asm)

Uso:

    ./desensamblador.sh pruebas/desensamblador/Llenar.hack

Si el archivo es correcto se crea ProgramaDis.asm. Si tiene un error se imprime "ERROR *** Línea N: mensaje", no se crea el archivo y el código de salida es 1.

### Programas correctos (carpeta pruebas/desensamblador/)

Llenar.hack (25 instrucciones), CuentaRegresiva.hack (16) y Signo.hack (18). En los tres, el .asm generado se vuelve a ensamblar y el .hack que resulta es idéntico al original. Esto muestra que las dos herramientas son inversas una de la otra.

Los saltos se reconstruyen con etiquetas L0, L1, ... Cuando una instrucción C tiene salto y la instrucción anterior es una A con un valor que apunta a una instrucción del programa, ese valor se escribe como etiqueta. Por ejemplo, en Llenar.hack el desensamblador genera (L0) y (L1) y escribe "@L1" antes de "D;JEQ".

### Programas con error (carpeta pruebas/errores/)

errorLongitud.hack
La línea 3 tiene 15 bits.
Salida: ERROR *** Línea 3: la instrucción debe tener 16 caracteres y tiene 15

errorCaracteres.hack
La línea 3 contiene un 2.
Salida: ERROR *** Línea 3: carácter inválido '2' en la posición 14 (solo se permiten 0 y 1)

errorBits.hack
La línea 4 es una instrucción C cuyos bits 14 y 13 son 01 y deben ser 11.
Salida: ERROR *** Línea 4: instrucción C con bits 14-13 '01' (deben ser 11)

errorComputo.hack
La línea 4 tiene el código de cómputo 0000001, que no corresponde a ningún mnemónico.
Salida: ERROR *** Línea 4: código de cómputo inválido '0000001'
