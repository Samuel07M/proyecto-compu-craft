Documentación ALU Extendida

1. Interfaz del chip

CHIP ALUExtendida {
    IN  x[16], y[16], a, zx, nx, zy, ny, f, no;
    OUT out[16], zr, ng;
}


 Entradas

Pin - Ancho - Descripción 

`x` - 16 bits - Primer operando 
`y` - 16 bits - Segundo operando 
`a` - 1 bit 
`zx` - 1 bit - Bit de control ALU
`nx` - 1 bit - Bit de control ALU 
`zy` - 1 bit - Bit de control ALU 
`ny` - 1 bit - Bit de control ALU 
`f`  - 1 bit - Bit de control ALU 
`no` - 1 bit - Bit de control ALU 

 Salidas

Pin - Ancho - Descripción 

`out` - 16 bits - Resultado de la operación seleccionada 
`zr` - 1 bit - `1` si `out == 0` 
`ng` - 1 bit - `1` si `out < 0` (Utiliza el bit de signo que tiene ´out´) 





3. ALU extendida 

El chipdecodifica `zx nx zy ny f no` para reconocer 5 códigos nuevos y calcular una operación distinta a las del ALU normal.

4. Tabla de operaciones nuevas

Operación - Código `zx nx zy ny f no` - Resultado 

XOR - `0 1 0 1 1 0` - `x XOR y` (bit a bit) 
NAND - `0 0 0 0 0 1` - `!(x AND y)` (bit a bit) 
NOR - `1 1 0 1 0 0` - `!(x OR y)` (bit a bit) 
EQ - `1 0 1 0 0 0` - `00000000000000001` si `x == y`, `00000000000000000` si `x != y` 
ABS - `1 0 0 0 1 0` - `\|x\|` (valor absoluto de `x`, en complemento a 2) 


5 Decodificación de cada código

El HDL genera las negaciones de las 6 señales de control (`nzx, nnx, nzy, nny, nf, nno`) y luego crea una cadena de compuertas `And` que detecta los patrones de 6 bits: `selXor`, `selNand`, `selNor`, `selEq`, `selAbs`. 

6 Cálculo de cada operación 

El chip calcula las 5 operaciones en paralelo siempre, por lo que usa la arquitectura "calcular todo y enmascarar"

XOR - `xorOut = (x AND !y) OR (!x AND y)`, usa `Not16`, `And16` y `Or16`.

NAND - `nandOut = !(x AND y)`, usa `And16` + `Not16`.

NOR -`norOut = !(x OR y)`, usa `Or16` + `Not16`.

EQ - Reutiliza el resultado de XOR y si todos los bits de `xorOut` son `0`, entonces `x == y`. Se usa `Or8Way` en  las dos mitades de `xorOut` para detectar si hay algún bit distinto (`anyDiff`) y se niega (`eqBit`) y con un `Mux16` se produce `00000000000000001` o `00000000000000000`usando Inc16(in=false) 

ABS - Calcula `-x` como `!x + 1` y usa un `Mux16` controlado por `x[15]`  para elegir entre `x` cuando es positivo y `-x` cuando es negativo

7 Selección/enmascarado

Cada resultado se combina con un `sel*` con un `Mux16(a=false, b=true, sel=sel*)` que genera una máscara de 16 bits, que significa 16 bits de todo ceros o todo unos, y un `And16` que aplica esa máscara al resultado calculado (`cXor`, `cNand`, `cNor`, `cEq`, `cAbs`), Se puede usar como como máximo una señal `sel*` a la vez, y haciendo un `Or16` en cadena de los cinco resultados enmascarados podemos tener el `newOut` que es la salida de la alu extendida


8 Archivos del proyecto


 `ALUExtendida.hdl` - Implementación estructural del chip 
 `ALUExtendida.tst` - Script de prueba para el simulador de hardware HardwareSimulator, que usa las 5 operaciones nuevas (4 casos XOR, 4 NAND, 4 NOR, 2 EQ, 4 ABS). 
 `ALUExtendida.cmp` - Archivo de comparación con las salidas esperadas para cada línea del `.tst`. 
 `README.md` - usado como Documentación Tecnica de lla ALU extendida


9 Chips usados para la ALU extendida

`Mux16`, `Not16`, `And16`, `Or16`, `Add16`, `Inc16`, `Or8Way`, `Not`, `And`, `Or`, Todos estos chips son estandar de Nand2Tetris y vistos durante el curso

