# Proyecto Integrador – Organización de Computadores

## Estudiantes
- Samuel Molina Garcés
- Juan Diego Parra Castañeda
- Juan Diego Martinez Jaramillo

---

## Descripción del Repositorio
Este repositorio contiene el desarrollo completo de las prácticas del curso de **Organización de Computadores (Universidad EAFIT)**, basadas en la plataforma **Nand2Tetris** y extendidas con componentes académicos específicos del curso: hardware digital, lenguaje ensamblador, ensamblador/desensamblador y un juego en Jack con rutinas escritas a mano en lenguaje VM.

El proyecto se encuentra estructurado en cuatro entregas principales:

---

## Estructura del Repositorio

```text
proyecto-compu-craft/
├── proyecto1/           # Entrega 1: Fundamentos de Hardware Digital (Nand2Tetris 1, 2, 3 + ALU Extendida)
│   ├── proyecto01/
│   ├── proyecto02/
│   ├── proyecto03/
│   ├── alu-extendida/
│   └── README.md        # Documentación técnica detallada del Proyecto 1
│
├── proyecto2/           # Entrega 2: Lenguaje Ensamblador y Arquitectura Hack (Nand2Tetris 4 y 5)
│   ├── proyecto04/      # Programas en ensamblador (Mult, Fill, SumN, CopyBlock)
│   ├── proyecto05/      # Hardware de la Hack Computer (Memory, CPU, Computer)
│   ├── InformeTecnico.docx
│   └── README.md        # Documentación técnica detallada del Proyecto 2
│
├── proyecto3/           # Entrega 3: Ensamblador y Desensamblador Hack (Nand2Tetris 6 + Desensamblador)
│   ├── ensamblador/     # Código fuente del ensamblador (Java): Prog.asm -> Prog.hack
│   ├── desensamblador/  # Código fuente del desensamblador (Java): Prog.hack -> ProgDis.asm
│   ├── pruebas/         # Programas válidos y casos de error propios
│   ├── DOCUMENTACION/   # Informe técnico (INFORME.md)
│   ├── compilar.sh      # Compila la solución
│   ├── ensamblador.sh   # Ejecuta el ensamblador
│   ├── desensamblador.sh # Ejecuta el desensamblador
│   └── README.md        # Documentación técnica detallada del Proyecto 3
│
├── proyecto4/           # Entrega 4: Juego de carreras de tres carriles en Jack y lenguaje VM (Nand2Tetris 7, 8 y 9)
│   ├── Main.jack        # Punto de entrada: crea, ejecuta y libera el juego
│   ├── Juego.jack       # Bucle del juego, teclas, pausa, reinicio y salida
│   ├── Carrera.jack     # Lógica del juego (interfaz fija, sin dibujo ni teclado)
│   ├── Obstaculo.jack   # Única clase de obstáculo, especializada por un Perfil
│   ├── Perfil.jack      # Datos de cada tipo de obstáculo (cono, camión, moto, moneda)
│   ├── Vista.jack       # Dibujo de la vía, obstáculos, marcador y mensajes
│   ├── Sprites.jack     # Dibujos de 16 × 16 píxeles
│   ├── Teclado.jack     # Lectura del teclado por pulsación
│   ├── Grafico.vm       # Rutinas gráficas escritas a mano en VM
│   ├── Aleatorio.vm     # Generador pseudoaleatorio escrito a mano en VM
│   └── README.md        # Documentación técnica detallada del Proyecto 4
│
└── README.md            # Presentación general del repositorio

```
Consulte el README.md interno en proyecto1/, proyecto2/, proyecto3/ y proyecto4/ para ver los detalles de cada proyecto junto a sus entregables.
