# Proyecto 3: ensamblador y desensamblador para la plataforma Hack

Organización de Computadores 2026-2, Universidad EAFIT
Docente: José Luis Montoya Pareja

## Integrantes

- Juan Diego Martínez Jaramillo
- Juan Diego Parra Castañeda
- Samuel Molina Garcés

## Descripción

El proyecto tiene dos programas escritos en Java.

Ensamblador: lee Prog.asm y escribe Prog.hack. Cada línea del resultado es una instrucción de 16 bits. Resuelve los símbolos predefinidos, las etiquetas y las variables.

Desensamblador: lee Prog.hack y escribe ProgDis.asm. Cuando un salto apunta a otra instrucción del programa, escribe una etiqueta (L0, L1, ...) en lugar del número.

Los dos son uno el inverso del otro: si se ensambla ProgDis.asm se obtiene otra vez el Prog.hack original.

## Compilación y ejecución

### Requisitos

- Windows con Git Bash.
- Java: JDK 17 (probado con esa versión). Debe ser el JDK completo, no solo el JRE, porque se necesita javac.
- No se usan librerías externas.

### Instalar el JDK

1. Descargar el JDK 17 (por ejemplo, Temurin) desde https://adoptium.net e instalarlo, con la opción de agregar Java al PATH.
2. Cerrar Git Bash y abrir una ventana nueva.
3. Comprobar la instalación:

       java -version
       javac -version

### Comandos

Todos se ejecutan en Git Bash, desde la carpeta proyecto3/.

Compilar (una sola vez, o cuando cambie el código):

    ./compilar.sh

Imprime "Compilación exitosa" y deja los archivos compilados en build/.

Ensamblar:

    ./ensamblador.sh pruebas/ensamblador/Prog.asm

Crea pruebas/ensamblador/Prog.hack, en la misma carpeta de la entrada.

Desensamblar:

    ./desensamblador.sh pruebas/desensamblador/Prog.hack

Crea pruebas/desensamblador/ProgDis.asm, en la misma carpeta de la entrada.

Los scripts reciben un solo argumento, la ruta del archivo (relativa o absoluta). Funcionan con rutas que tienen espacios y no dependen de rutas propias de un computador.

### Resultado y errores

Si la entrada es correcta, el programa crea el archivo de salida y termina con código 0.

Si la entrada tiene un error, imprime una sola línea con este formato, no crea el archivo de salida y termina con código 1:

    ERROR *** Línea N: mensaje

N es el número de línea del archivo de entrada, contando comentarios y líneas en blanco. Si se llama al script sin el argumento, el código de salida es 2.

### Si algo falla

- "Permission denied": ejecutar chmod +x compilar.sh ensamblador.sh desensamblador.sh
- "javac: command not found": falta el JDK, o hay que abrir una ventana nueva de Git Bash.
- "Could not find or load main class": falta ejecutar ./compilar.sh

## Estructura del proyecto

    proyecto3/
    ├── compilar.sh
    ├── ensamblador.sh
    ├── desensamblador.sh
    ├── README.md
    ├── ensamblador/
    ├── desensamblador/
    ├── pruebas/
    │   ├── ensamblador/
    │   ├── desensamblador/
    │   ├── errores/
    │   └── README.md
    └── DOCUMENTACION/
        └── INFORME.md

## Estructura del código

ensamblador/

- HackAssembler.java: programa principal; hace las dos pasadas y escribe el .hack.
- Parser.java: lee el archivo, limpia cada línea y revisa que esté bien escrita.
- Code.java: tablas que pasan dest, comp y jump a binario.
- SymbolTable.java: tabla de símbolos (predefinidos, etiquetas y variables).
- Instruction.java: guarda una instrucción ya revisada (A, C o etiqueta).
- AsmError.java: error con el número de línea.

desensamblador/

- HackDisassembler.java: programa principal; valida, busca los destinos de salto, crea las etiquetas y escribe el .asm.
- DisCode.java: las tablas de Code al revés (de binario a mnemónico).
- DisError.java: error con el número de línea.

## Pruebas y documentación

- pruebas/README.md: programas de prueba y resultado esperado de cada uno.
- DOCUMENTACION/INFORME.md: informe técnico.
