# Proyecto 4: juego de carreras de tres carriles en Jack y lenguaje VM

Organización de Computadores 2026-2, Universidad EAFIT
Docente: José Luis Montoya Pareja

El jugador conduce un carro por una vía de tres carriles y esquiva conos, camiones y motos que vienen de derecha a izquierda. También puede recoger monedas. Gana cuando llega a la meta y pierde cuando se queda sin vidas. El juego está escrito en Jack y se ejecuta en el VM Emulator. Las rutinas gráficas y el generador pseudoaleatorio están escritos a mano en lenguaje VM.

# Integrantes

| Nombre |  Aportes principales |
|---|---|
| Juan Diego Martínez Jaramillo | Clases `Perfil`, `Obstaculo` y `Carrera` (lógica del juego), `Aleatorio.vm` y clase `Juego` (bucle, teclas, pausa y reinicio). |
| Juan Diego Parra Castañeda |  Rutinas VM escritas a mano en `Grafico.vm` (`dibujarCelda`, `borrarCelda` y `desplazarFila`), ajustes a `Aleatorio.vm` y clase `Vista` (dibujo de la vía, obstáculos, marcador, barra de progreso y mensajes). |
| Samuel Molina Garcés | Clases `Teclado` (lectura por pulsación), `Sprites` (dibujos de 16 × 16) y `Main`; estructura de la carpeta `proyecto4/`, reglas de calidad de código (sección 12), pruebas y `README.md`. |

# Compilación y ejecución

## Requisitos

- Java (JDK o JRE 8 o superior) en el PATH.
- Herramientas de Nand2Tetris, 2.ª edición (carpeta `tools` del curso), con `JackCompiler` y `VMEmulator`.
- En Windows, usar Git Bash para los comandos de abajo.

## Compilar

Desde la carpeta `tools` de Nand2Tetris:

    ./JackCompiler.sh RUTA/proyecto4

En Windows (cmd) también se puede usar `JackCompiler.bat RUTA\proyecto4`.

El compilador genera un `.vm` por cada `.jack` dentro de `proyecto4/`. Los archivos `Grafico.vm` y `Aleatorio.vm` están escritos a mano y no tienen `.jack`, así que el compilador no los toca.

## Ejecutar

1. Abrir el VM Emulator (`VMEmulator.sh` o `VMEmulator.bat`).
2. Elegir «File → Load Program» y seleccionar la carpeta `proyecto4`.
3. Elegir «No animation» y poner la velocidad al máximo.
4. Presionar «Run».
5. Hacer clic sobre la pantalla del emulador para que reciba las teclas.

No se deben copiar los `.vm` del sistema operativo de Jack a la carpeta: el emulador usa los suyos.

## Pruebas públicas

    cd material-apoyo/pruebas
    ./correr_pruebas.sh RUTA/proyecto4 RUTA/nand2tetris/tools

Resultado: 24/24 casos aprobados (carrera 18/18, grafico 3/3, bono 3/3).

# Controles

| Tecla | Acción |
|---|---|
| 1, 2, 3 | En la pantalla inicial: elegir la distancia a la meta (50, 100 o 200 pasos) |
| Flecha arriba | Subir un carril (una vez por pulsación) |
| Flecha abajo | Bajar un carril (una vez por pulsación) |
| P | Pausar y continuar |
| R | Reiniciar: volver a la pantalla inicial sin recargar el programa |
| ESC | Salir del juego |

Al terminar la partida aparece el mensaje de victoria o de derrota con el puntaje. Con R se vuelve a la pantalla inicial y con ESC se sale.

# Diseño de clases

El juego tiene 8 clases Jack propias y 2 archivos VM escritos a mano. Las clases con constructor (crean objetos) son `Juego`, `Carrera`, `Obstaculo`, `Perfil`, `Vista`, `Sprites` y `Teclado`.

| Clase | Responsabilidad |
|---|---|
| `Main` | Solo crea el `Juego`, lo ejecuta y lo libera. Ninguna otra clase llama a `Main`. |
| `Juego` | Controla el programa: pantalla inicial, bucle de cada partida, teclas, pausa, reinicio, salida y mensaje final. Une la lógica (`Carrera`), el dibujo (`Vista`) y la entrada (`Teclado`). |
| `Carrera` | Lógica del juego con la interfaz fija de la sección 8: carril del carro, obstáculos, choques, vidas, puntaje, progreso, estado y generación de obstáculos. No dibuja ni lee el teclado. |
| `Obstaculo` | Un obstáculo en la vía: carril, columna y columna anterior. Sabe moverse, si ocupa una celda, si tocó el carro y si ya salió de la pantalla. |
| `Perfil` | Datos de un tipo de obstáculo: tipo, largo, velocidad, si es letal y puntos. |
| `Vista` | Todo lo que se dibuja: vía, obstáculos, carro, marcador, barra de progreso y mensajes. Solo lee de `Carrera`, nunca la modifica. Guarda lo último que dibujó y redibuja solo las celdas que cambian. |
| `Sprites` | Dibujos de 16 × 16 píxeles (un `Array` de 16 palabras por sprite). Solo guarda datos. |
| `Teclado` | Convierte `Keyboard.keyPressed()` en pulsaciones: devuelve el código solo cuando la tecla se acaba de oprimir, así mantenerla oprimida no cambia más de un carril. |

## Cómo se especializan los obstáculos sin herencia

Jack no tiene herencia, así que hay **una sola clase `Obstaculo`**. Lo que cambia entre un cono, un camión, una moto y una moneda está en un objeto `Perfil` que cada obstáculo contiene (**composición**). El comportamiento depende de esos datos (**parametrización por datos**):

| Tipo | Perfil (largo, velocidad, letal, puntos) |
|---|---|
| 0 cono | 1, 1, sí, 0 |
| 1 camión | 3, 1, sí, 0 |
| 2 moto | 1, 2, sí, 0 |
| 3 moneda | 1, 1, no, 10 |

`Carrera` crea los 4 perfiles una sola vez (su catálogo) y todos los obstáculos del mismo tipo comparten el mismo `Perfil`. Por ejemplo, `Obstaculo.mover()` resta `perfil.velocidad()` a la columna, y así la moto avanza 2 celdas sin código especial.

## Separación de lógica y presentación

`Carrera`, `Obstaculo` y `Perfil` no llaman a `Screen`, `Output`, `Keyboard`, `Grafico` ni `Sys.wait`. `Vista` dibuja, `Teclado` lee el teclado y `Juego` los coordina. Por eso las pruebas automáticas pueden usar `Carrera` sin pantalla ni teclado.

## Memoria

- Cada obstáculo eliminado (por choque o por salir de la pantalla) se libera con `dispose`.
- `Carrera.dispose` libera los obstáculos activos, los 4 perfiles, los arreglos y la carrera.
- `Juego` libera la carrera al final de cada partida, así se pueden jugar muchas partidas seguidas sin llenar el heap.
- Las cadenas de texto que se escriben una vez se liberan después de escribirlas (`Vista.texto`), y las que se usan en cada paso se crean una sola vez en el constructor de `Vista`.

# Rutinas VM

Todas están escritas a mano. No usan `Memory.poke`, `Memory.peek` ni `Math.multiply`: la memoria se lee y se escribe con los segmentos `pointer`, `this` y `that`, y las multiplicaciones se hacen con sumas.

| Función | Archivo | Qué hace | Dónde la usa el juego |
|---|---|---|---|
| `Grafico.dibujarCelda(fila, col, sprite)` | `Grafico.vm` | Copia las 16 palabras del sprite a la celda: `RAM[16384 + fila*512 + k*32 + col] = sprite[k]`. `this` recorre el sprite y `that` recorre la pantalla. | `Vista.celda` (carro y obstáculos), `Vista.dibujarFondo` (bordes y divisorias) y `Vista.barra` (barra de progreso). |
| `Grafico.direccion(fila, col)` | `Grafico.vm` | Función auxiliar: calcula `16384 + fila*512 + col`. `fila*512` se obtiene duplicando la fila 9 veces. | `dibujarCelda`, `borrarCelda` y `desplazarFila`. |
| `Grafico.borrarCelda(fila, col)` (bono) | `Grafico.vm` | Pone en 0 las 16 palabras de la celda. | `Vista.celda`, cuando una celda de la vía queda vacía. |
| `Grafico.desplazarFila(fila)` (bono) | `Grafico.vm` | En cada una de las 16 líneas de la fila de celdas rota las 32 palabras una posición a la izquierda: `w[c] = w[c+1]` y `w[31] = w[0]`. | `Vista.moverDivisorias`, para que las líneas divisorias se muevan. |
| `Aleatorio.semilla(s)` (bono) | `Aleatorio.vm` | Guarda `s` como estado `x` (en `static 0`). | `Carrera.new`, con la semilla de la partida. |
| `Aleatorio.siguiente()` (bono) | `Aleatorio.vm` | Hace `x = 5x + 3` con aritmética de 16 bits y retorna `x`. `5x` se calcula como `2x + 2x + x`. | `Carrera.sortear`, que usa `generar()` para elegir si aparece un obstáculo, su carril y su tipo. |

# Uso de IA

- **Herramienta:** Claude (Anthropic).
- **Para qué:** revisar el avance del proyecto contra el enunciado, encontrar y corregir un error en `Grafico.vm` (el puntero `that` se salía de la pantalla al dibujar o borrar en la fila 15), quitar bloques repetidos en los archivos `.vm` (regla de duplicación), ajustar la posición de dos textos de `Vista.jack` que no cabían en la pantalla, correr las pruebas públicas y redactar este `README.md`.
- Todos los integrantes revisaron estos cambios y pueden explicar cada línea del proyecto.