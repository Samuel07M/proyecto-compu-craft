# Proyecto 2 — Plataforma Hack (Nand2Tetris)

**Curso:** Organización de Computadores 2026-2
**Docente:** José Luis Montoya Pareja
**Universidad EAFIT — Ingeniería de Sistemas**
**Práctica 2:** Construcción de la Plataforma Hack — Lenguaje Máquina, CPU y Computador

---

## 1. Descripción general

Este repositorio contiene la implementación de los Proyectos 4 y 5 de Nand2Tetris, correspondientes a la Práctica 2 del curso. El objetivo es construir programas en lenguaje ensamblador Hack y los componentes de hardware (Memory, CPU y Computer) que integran la arquitectura Hack completa, capaz de ejecutar programas reales.

## 2. Estructura del repositorio

```
proyecto-alu-craft/
├── odec-proyecto1/          # Proyectos 1-3 (compuertas, ALU, memoria)
├── odec-proyecto2/
│   ├── proyecto04/
│   │   ├── ASM/
│   │   │   ├── mult.asm
│   │   │   ├── fill.asm
│   │   │   ├── sumN.asm
│   │   │   └── copyBlock.asm
│   │   └── TESTS/            # Capturas de las simulaciones exitosas
│   └── proyecto05/
│       ├── HDL/
│       │   ├── Memory.hdl
│       │   ├── CPU.hdl
│       │   └── Computer.hdl
│       └── TESTS/            # Capturas de las simulaciones exitosas
└── README.md
```

## 3. Proyecto 4 — Programas en ensamblador Hack

### 3.1 Mult.asm (programa oficial)
Multiplica dos números almacenados en `RAM[0]` y `RAM[1]`, dejando el resultado en `RAM[2]`. Utiliza un ciclo de sumas sucesivas.

### 3.2 Fill.asm (programa oficial)
Escucha el teclado en un ciclo infinito: si hay una tecla presionada, pinta la pantalla completa de negro; si no hay ninguna, la deja en blanco.

### 3.3 SumN.asm (programa adicional del curso)
Calcula la suma de los primeros N números naturales.

- **Entrada:** `RAM[0] = N`
- **Salida:** `RAM[1] = 1 + 2 + 3 + ... + N`

**Lógica:** se inicializa un acumulador `sum` en 0 y un contador `i` en 1. Mientras `i` sea menor o igual a N, se suma `i` al acumulador y se incrementa el contador. Al salir del ciclo, el resultado se guarda en `RAM[1]`.

**Ejemplo:** `RAM[0] = 5` → `RAM[1] = 15`

### 3.4 CopyBlock.asm (programa adicional del curso)
Copia un bloque de N posiciones de memoria desde una dirección origen hacia una dirección destino.

- **Entradas:** `RAM[0] = ORIGEN`, `RAM[1] = DESTINO`, `RAM[2] = N`
- **Operación:** copia `RAM[ORIGEN..ORIGEN+N-1]` hacia `RAM[DESTINO..DESTINO+N-1]`

**Lógica:** se guardan en variables auxiliares (`n`, `src`, `dest`) los valores de N, origen y destino. En cada iteración se lee la posición apuntada por `src` (usando direccionamiento indirecto con `A=M`), se escribe en la posición apuntada por `dest`, y ambos punteros se incrementan hasta que el contador `n` llega a cero.

**Ejemplo:** con `RAM[0]=100`, `RAM[1]=200`, `RAM[2]=3` y `RAM[100..102] = 7, 9, 4` → `RAM[200..202] = 7, 9, 4`

## 4. Proyecto 5 — Componentes de hardware

### 4.1 Memory.hdl
Implementa la memoria completa del computador Hack, integrando `RAM16K`, `Screen` y `Keyboard`. Usa `address[14]` para separar la RAM del espacio de memoria mapeada (Screen/Keyboard), y `address[13]` para diferenciar entre Screen y Keyboard dentro de ese rango. Un `Mux4Way16` selecciona la salida correcta según la dirección.

### 4.2 CPU.hdl
Implementa la unidad central de procesamiento: decodifica instrucciones tipo A y tipo C, controla la carga de los registros A y D, ejecuta operaciones mediante la ALU, evalúa las condiciones de salto (`JGT`, `JEQ`, `JGE`, `JLT`, `JNE`, `JLE`, `JMP`) y actualiza el program counter (`PC`).

### 4.3 Computer.hdl
Integra `CPU`, `ROM32K` y `Memory` en una arquitectura ejecutable completa, cerrando el ciclo instrucción → ejecución → memoria.

## 5. Resultados de las pruebas

Todos los componentes fueron validados con los simuladores oficiales de Nand2Tetris (Hardware Simulator y CPU Emulator), obteniendo el mensaje **"Simulation successful: The output file is identical to the compare file"** en cada test:

| Componente / Programa | Test | Resultado |
|---|---|---|
| Memory.hdl | Test oficial | ✅ Exitoso |
| CPU.hdl | CPU (interno) | ✅ Exitoso |
| CPU.hdl | CPU-external | ✅ Exitoso |
| Computer.hdl | ComputerAdd | ✅ Exitoso |
| Computer.hdl | ComputerMax | ✅ Exitoso |
| Computer.hdl | ComputerRect | ✅ Exitoso |
| Mult.asm | Test oficial | ✅ Exitoso |
| Fill.asm | Test oficial | ✅ Exitoso |
| SumN.asm | Casos N=0, N=1, N=5, N=10 | ✅ Exitoso |
| CopyBlock.asm | Copia simple, múltiple y bloque vacío | ✅ Exitoso |

Las capturas de cada simulación se encuentran en las carpetas `TESTS/` correspondientes.

## 6. Cómo ejecutar las pruebas

1. Descargar el [Nand2Tetris Software Suite](https://www.nand2tetris.org/software).
2. Para los programas `.asm`: abrir el **CPU Emulator**, cargar el archivo `.hack` correspondiente (compilado desde el `.asm` con el Assembler) o directamente el script de test `.tst` asociado, y ejecutar.
3. Para los archivos `.hdl`: abrir el **Hardware Simulator**, cargar el `.hdl` correspondiente y su script de test (`.tst`), y verificar que el resultado sea "Simulation successful".

## 7. Uso de Git

El repositorio se gestionó con commits incrementales por funcionalidad (cada programa y cada componente de hardware en commits independientes), conservando un historial trazable del desarrollo del proyecto.

## 8. Integrantes

- Juan Diego Parra Castañeda
- Samuel Molina Garces
- Juan Diego Martínez Jaramillo

---
*Entrega correspondiente a la Práctica 2 — Organización de Computadores, Universidad EAFIT.*
