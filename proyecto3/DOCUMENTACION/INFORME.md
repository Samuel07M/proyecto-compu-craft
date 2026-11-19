# Informe técnico: ensamblador y desensamblador para la plataforma Hack

Organización de Computadores 2026-2, Universidad EAFIT
Docente: José Luis Montoya Pareja

## 1. Integrantes y aportes

Juan Diego Martínez Jaramillo (usuario de GitHub: Juanea2008)
- Creó la estructura de carpetas del proyecto 3.
- Escribió la primera versión del ensamblador: la clase principal HackAssembler con las dos pasadas, el Parser, Code y SymbolTable.
- Preparó la documentación del proyecto.

Juan Diego Parra Castañeda (usuario de GitHub: JuanD1847)
- Escribió las pruebas propias: los programas del ensamblador, los del desensamblador y los casos de error.

Samuel Molina Garcés (usuario de GitHub: Samuel07M)
- Reorganizó las carpetas del proyecto y escribió los scripts compilar.sh, ensamblador.sh y desensamblador.sh, además del .gitattributes.
- Mejoró el ensamblador: tablas exactas de dest, comp y jump; validación de los símbolos predefinidos; Parser con número de línea, eliminación de espacios y detección de errores; validación en la primera pasada y formato "ERROR *** Línea N".
- Escribió el desensamblador completo: tablas inversas, clase de error, validación, etiquetas de salto y generación de ProgDis.asm.
- Actualizó el README principal del repositorio.

## 2. Qué se construyó

Dos programas en Java:

    Prog.asm  --->  ENSAMBLADOR  --->  Prog.hack  --->  DESENSAMBLADOR  --->  ProgDis.asm

El ensamblador pasa de lenguaje ensamblador a lenguaje de máquina. El desensamblador hace el camino contrario. La condición de corrección es que ensamblar ProgDis.asm dé como resultado el mismo Prog.hack.

No se usó el ensamblador oficial de Nand2Tetris ni ningún otro ensamblador de Hack. Las tablas de la sección 7 del enunciado (comp, dest, jump y símbolos predefinidos) se escribieron a mano en el código.

## 3. Arquitectura del ensamblador

### 3.1 Módulos

El ensamblador está dividido en los módulos que se vieron en clase, más dos clases pequeñas de apoyo:

- HackAssembler: es el programa principal. Recibe la ruta del archivo, pide al Parser las instrucciones, hace las dos pasadas y escribe el .hack.
- Parser: lee el archivo, quita comentarios y espacios de cada línea y revisa que la línea sea una instrucción A, una instrucción C o una etiqueta bien escrita.
- Code: tiene las tablas de comp, dest y jump y entrega el código binario de cada mnemónico. Si el mnemónico no está en la tabla, entrega null, y eso se interpreta como error.
- SymbolTable: la tabla de símbolos.
- Instruction: guarda una instrucción que el Parser ya revisó (de qué tipo es y sus partes).
- AsmError: un error que además del mensaje guarda el número de línea, para poder imprimir "ERROR *** Línea N: mensaje".

### 3.2 Limpieza de cada línea

Antes de analizar una línea, el Parser:

1. Corta todo lo que viene desde "//" (el comentario).
2. Borra todos los espacios y tabulaciones, también los que están dentro de la instrucción. Así "AMD = M - 1 ; JNE" queda como "AMD=M-1;JNE".
3. Si la línea queda vacía, se ignora.

Como los espacios se borran, las líneas en blanco y los comentarios no generan nada en la salida.

### 3.3 Las dos pasadas

Primera pasada. Se recorren todas las líneas en orden. Cada línea se revisa con el Parser. Si es una etiqueta, se guarda en la tabla de símbolos con el número de la siguiente instrucción (la etiqueta no ocupa una línea en la salida). Si es una instrucción A o C, se guarda en una lista y se suma uno al contador de instrucciones.

Segunda pasada. Se recorre la lista de instrucciones ya revisadas y se escribe una línea de 16 bits por cada una:

- Instrucción A: se escribe un 0 y el valor en 15 bits. Si el valor es un número, se usa tal cual. Si es un símbolo, se busca en la tabla; si no está, es una variable nueva y se le asigna la siguiente dirección libre de RAM, empezando en 16.
- Instrucción C: se escribe 111, luego los 7 bits de comp (el bit a y c1 a c6), luego los 3 de dest y los 3 de jump.

### 3.4 Ejemplo con Absoluto.asm

    0   @R0
    1   D=M
    2   @POSITIVO
    3   D;JGE
    4   D=-D
    5   @SCREEN
    6   M=-1
        (POSITIVO)
    7   @R1
    8   M=D
        (END)
    9   @END
    10  0;JMP

Primera pasada: POSITIVO vale 7 y END vale 9, porque son las direcciones de las instrucciones que vienen después de cada etiqueta. En la segunda pasada, @POSITIVO se escribe 0000000000000111 (7), @SCREEN se escribe 0100000000000000 (16384, valor predefinido) y @END se escribe 0000000000001001 (9). El archivo final tiene 11 líneas, todas de 16 caracteres.

## 4. Tabla de símbolos

Se usó una tabla hash (HashMap de Java) que guarda pares nombre y dirección. Se eligió porque buscar un símbolo es rápido, aun con programas grandes.

Al crearla ya contiene los símbolos predefinidos:

- SP, LCL, ARG, THIS, THAT con los valores 0, 1, 2, 3, 4
- R0 a R15 con los valores 0 a 15
- SCREEN con 16384 y KBD con 24576

Las operaciones son las de la clase: addEntry (agregar un par), contains (preguntar si un símbolo existe) y getAddress (pedir su dirección).

Asignación de las variables:

- Las etiquetas se agregan en la primera pasada, con la dirección de ROM de la siguiente instrucción.
- En la segunda pasada, cuando aparece @nombre y el nombre no está en la tabla, es una variable. Se le asigna el valor de un contador que empieza en 16 y sube de uno en uno. Por eso las variables quedan numeradas en el orden en que aparecen por primera vez.
- Si la variable vuelve a aparecer, ya está en la tabla y se reutiliza su dirección.

Ejemplo con Sumatoria.asm: la primera variable que aparece es i y se le asigna la dirección 16; la segunda es suma y recibe la 17. Las etiquetas LOOP, FIN y END valen 4, 18 y 22. Por eso @i se escribe 0000000000010000 y @LOOP se escribe 0000000000000100.

Reglas que se revisan sobre los símbolos: un símbolo puede tener letras, dígitos, _, ., $ y :, y no puede empezar por un dígito. Una etiqueta no puede declararse dos veces ni llamarse igual que un símbolo predefinido.

## 5. Generación de etiquetas en el desensamblador

### 5.1 Algoritmo

El desensamblador trabaja en tres pasos. Sea n el número de instrucciones del programa.

1. Validar. Se revisa cada línea del .hack (ver sección 6). Si hay un error, se informa y el programa termina.
2. Buscar destinos de salto. Se recorren las instrucciones desde la 1 hasta la n-1. Si la instrucción i es una C con salto (los 3 bits de jump son distintos de 000) y la instrucción i-1 es una A con valor v menor o igual que n, entonces v es un destino de salto. Se anota v en un conjunto ordenado y se marca la instrucción i-1 para escribirla con etiqueta. Si varios saltos llegan a la misma dirección, esa dirección se anota una sola vez, y por eso tiene una sola etiqueta.
3. Nombrar y escribir. Las direcciones anotadas se recorren de menor a mayor y se llaman L0, L1, L2, ... Luego se escribe el programa instrucción por instrucción:
   - Antes de la instrucción v se escribe (Lk).
   - La instrucción A marcada se escribe @Lk en lugar de @v.
   - Las demás instrucciones A se escriben @ seguido del número en decimal.
   - Las instrucciones C se escriben dest=comp;jump, sin el "dest=" ni el ";jump" cuando están vacíos. Para esto se usa DisCode, que tiene las mismas tablas del ensamblador, con la clave y el valor cambiados.
   - Si v es igual a n, la etiqueta se escribe al final del archivo.

### 5.2 Ejemplo con CuentaRegresiva.hack

El programa tiene 16 instrucciones (n = 16). Las instrucciones 8 y 9 son "@14" y "D;JEQ". Las instrucciones 12 y 13 son "@4" y "0;JMP". Las instrucciones 14 y 15 son "@14" y "0;JMP". Entonces los destinos de salto son 4 y 14. Se nombran L0 (para 4) y L1 (para 14). Observe que dos saltos llegan a 14 y usan la misma etiqueta. La salida es:

    @0
    D=M
    @16
    M=D
    (L0)
    @16
    D=M
    @1
    M=D
    @L1
    D;JEQ
    @16
    M=M-1
    @L0
    0;JMP
    (L1)
    @L1
    0;JMP

Al ensamblar este archivo se obtiene un .hack idéntico a CuentaRegresiva.hack. Esto mismo se comprobó con Llenar.hack y Signo.hack.

## 6. Manejo de errores

### 6.1 Cómo se calcula el número de línea

Al leer el archivo no se descarta ninguna línea, ni siquiera las vacías o las de comentario. La lista de líneas queda con la línea 1 en la posición 0, la línea 2 en la posición 1, y así. Por eso el número de línea que se imprime es la posición más uno, y cuenta comentarios y líneas en blanco, como pide el enunciado.

### 6.2 Primer error y salida

Las líneas se revisan en orden desde la primera, y al encontrar un error se detiene el programa. Por eso siempre se informa el primer error en orden de líneas. El archivo de salida solo se escribe cuando todo el programa pasó la revisión, así que cuando hay un error no se crea. El mensaje se imprime con el formato "ERROR *** Línea N: mensaje" y el programa termina con código 1.

### 6.3 Errores del ensamblador

Todos estos casos los detecta el Parser:

- Instrucción A: "@" sin valor; un número mayor que 32767 (fuera de rango); un número negativo como @-1; un símbolo que empieza por dígito como @1abc; un símbolo con caracteres no permitidos como @VAR#1.
- Etiquetas: sin paréntesis de cierre o de apertura; vacía "()"; con nombre inválido como (1LOOP); con nombre de símbolo predefinido como (SCREEN). La etiqueta declarada dos veces se detecta en la primera pasada, porque ahí se guardan las etiquetas ya vistas con su línea.
- Destino: se busca en la tabla de Code. Solo valen M, D, MD, A, AM, AD y AMD. Cualquier otra cosa (X, DM, ADM, MA, MM) es error. Un destino vacío antes del "=" también es error.
- Cómputo: se busca en la tabla de Code. Los que no están (D*A, M+D, M+M) son error. Un cómputo vacío también.
- Salto: se busca en la tabla. Solo valen JGT, JEQ, JGE, JLT, JNE, JLE y JMP, en mayúsculas. Un salto vacío después del ";" es error.
- Estructura: más de un "=" (D=M=A), más de un ";" (D;JGT;JMP), el "=" después del ";", o texto que no es ninguna instrucción (HOLA MUNDO).

### 6.4 Errores del desensamblador

Cada línea del .hack se revisa con cuatro pruebas, en este orden:

1. Longitud: debe tener exactamente 16 caracteres. Esto incluye espacios al final y líneas vacías en medio del archivo (una línea vacía tiene longitud 0). Solo el último salto de línea del archivo es opcional.
2. Caracteres: cada uno debe ser 0 o 1. El mensaje dice cuál es el carácter y en qué posición está.
3. Bits 14 y 13: si la instrucción es C (el bit 15 es 1), esos dos bits deben ser 11.
4. Cómputo: si es C, los 7 bits a c1 a c6 deben estar en la tabla de cómputos.

Como en el ensamblador, se informa solo el primer error y no se crea el archivo ProgDis.asm.

## 7. Pruebas realizadas y resultados

Las pruebas están en la carpeta pruebas/ y en pruebas/README.md está el resultado esperado de cada una.

Ensamblador, programas válidos:

- Resta.asm (R2 = R0 - R1): 6 instrucciones.
- Sumatoria.asm (usa variables, etiquetas y espacios dentro de las instrucciones): 24 instrucciones.
- Absoluto.asm (usa el símbolo SCREEN): 11 instrucciones.

En los tres, todas las líneas de la salida miden 16 caracteres.

Ensamblador, casos de error (todos dan el mensaje esperado, no crean el .hack y terminan con código 1):

- errorSalto.asm: línea 8, salto inválido 'jmp'.
- errorDestino.asm: línea 7, destino inválido 'DM'.
- errorComputo.asm: línea 7, cómputo inválido 'D*A'.
- errorEstructura.asm: línea 7, instrucción con más de un '='.
- errorA.asm: línea 6, constante fuera de rango '32768'.
- errorEtiqueta.asm: línea 7, etiqueta 'LOOP' declarada dos veces (antes en la línea 4).

Desensamblador, programas válidos: Llenar.hack, CuentaRegresiva.hack y Signo.hack. En los tres se desensambló, se volvió a ensamblar el ProgDis.asm y el .hack resultante es idéntico al original (se comparó con el comando cmp).

Desensamblador, casos de error:

- errorLongitud.hack: línea 3, la instrucción tiene 15 caracteres.
- errorCaracteres.hack: línea 3, carácter inválido '2' en la posición 14.
- errorBits.hack: línea 4, bits 14-13 '01' (deben ser 11).
- errorComputo.hack: línea 4, código de cómputo inválido '0000001'.

Pruebas adicionales con líneas sueltas, tomadas de la sección 10 del enunciado: "@", "@-1", "@1abc", "@VAR#1", "(LOOP", "LOOP)", "()", "(1LOOP)", "(SCREEN)", "X=D", "ADM=M", "MA=1", "MM=0", "=D", "D=", ";JMP", "D=M+M", "M=M+D", "D;", "D;JXX", "D;JGT;JMP" y "HOLA MUNDO". Todas se rechazan con un mensaje claro. La línea "AMD = M - 1 ; JNE" se acepta. También se probó con una carpeta que tiene espacios en el nombre, y funcionan los dos scripts.

Rendimiento: un programa generado de 30 000 instrucciones se ensambló en menos de un segundo, y desensamblarlo y volver a ensamblarlo dio el mismo archivo.

## 8. Análisis

### 8.1 ¿Qué información del programa original se pierde al ensamblar?

Se pierde todo lo que solo le sirve a una persona y no a la máquina:

- Los comentarios, las líneas en blanco y los espacios.
- Los nombres de las etiquetas: (LOOP) desaparece y solo queda el número de la instrucción a la que apunta.
- Los nombres de las variables: @i y @suma se vuelven @16 y @17.
- Los nombres de los símbolos predefinidos: @R0 y @SCREEN se vuelven @0 y @16384.

### 8.2 ¿Por qué el desensamblado no es único?

Porque programas escritos de forma distinta producen el mismo binario. Por ejemplo, @R1 y @1 dan la misma línea; dos programas que solo cambian el nombre de una etiqueta o de una variable, o que tienen comentarios distintos, dan el mismo .hack. Al volver atrás hay muchas respuestas posibles y el desensamblador elige una. Nuestro desensamblador escribe los números en decimal y a las etiquetas les pone L0, L1, ..., pero otro podría usar otros nombres y también sería correcto, porque al ensamblarlo daría el mismo binario. Lo que sí es único es el camino de ida: un mismo .asm siempre da el mismo .hack.

### 8.3 ¿Por qué el binario no permite distinguir una variable de una constante o de un símbolo predefinido?

Porque una instrucción A guarda solo un número de 15 bits. La línea 0000000000010000 puede venir de @16 (una constante) o de @i (la primera variable del programa). El ensamblador reemplazó el nombre por el número y ese dato no se guardó. Al leer el binario no hay forma de saber de dónde salió el número. Por eso el enunciado no exige recuperar nombres de variables ni de símbolos predefinidos: el desensamblador escribe siempre @ seguido del número.

### 8.4 ¿Qué saltos no puede etiquetar la regla de la sección 9.1 y por qué?

La regla solo mira dos instrucciones seguidas: una A y, justo después, una C con salto. Por eso no puede etiquetar:

- Los saltos en los que la instrucción A no está justo antes del salto. Por ejemplo, si el programa carga la dirección con @x, hace otras instrucciones en medio (o cambia el valor de A, por ejemplo con A=A+1) y salta después, el desensamblador no sabe que ese valor era un destino.
- Los saltos en los que el valor v es mayor que n, es decir, que apuntan fuera del programa. No hay ninguna instrucción donde colocar la etiqueta, así que la instrucción A se deja como @v. Se probó con un programa de 2 instrucciones que hacía @5 y D;JGT, y la salida conserva @5.

En todos esos casos el programa sigue siendo correcto: las instrucciones A que no se etiquetan se dejan con su número en decimal, y al ensamblar se obtiene el mismo binario.
