load ALUExtendida.hdl,
compare-to ALUExtendida.cmp,
output-list x%B1.16.1 y%B1.16.1 zx nx zy ny f no out%B1.16.1 zr ng;

// XOR (010110)

set x %B0000000000000000,
set y %B0000000000000000,
set zx 0, set nx 1, set zy 0, set ny 1, set f 1, set no 0,
eval, output;

set x %B0000000000000000,
set y %B0000000000000001,
set zx 0, set nx 1, set zy 0, set ny 1, set f 1, set no 0,
eval, output;

set x %B0000000000000001,
set y %B0000000000000000,
set zx 0, set nx 1, set zy 0, set ny 1, set f 1, set no 0,
eval, output;

set x %B0000000000000001,
set y %B0000000000000001,
set zx 0, set nx 1, set zy 0, set ny 1, set f 1, set no 0,
eval, output;

// NAND (000001)

set x %B0000000000000000,
set y %B0000000000000000,
set zx 0, set nx 0, set zy 0, set ny 0, set f 0, set no 1,
eval, output;

set x %B0000000000000000,
set y %B0000000000000001,
set zx 0, set nx 0, set zy 0, set ny 0, set f 0, set no 1,
eval, output;

set x %B0000000000000001,
set y %B0000000000000000,
set zx 0, set nx 0, set zy 0, set ny 0, set f 0, set no 1,
eval, output;

set x %B0000000000000001,
set y %B0000000000000001,
set zx 0, set nx 0, set zy 0, set ny 0, set f 0, set no 1,
eval, output;

// NOR (110100)

set x %B0000000000000000,
set y %B0000000000000000,
set zx 1, set nx 1, set zy 0, set ny 1, set f 0, set no 0,
eval, output;

set x %B0000000000000000,
set y %B0000000000000001,
set zx 1, set nx 1, set zy 0, set ny 1, set f 0, set no 0,
eval, output;

set x %B0000000000000001,
set y %B0000000000000000,
set zx 1, set nx 1, set zy 0, set ny 1, set f 0, set no 0,
eval, output;

set x %B0000000000000001,
set y %B0000000000000001,
set zx 1, set nx 1, set zy 0, set ny 1, set f 0, set no 0,
eval, output;

// EQ (101000)

set x %B0000000000000101,
set y %B0000000000000101,
set zx 1, set nx 0, set zy 1, set ny 0, set f 0, set no 0,
eval, output;

set x %B0000000000000101,
set y %B0000000000000110,
set zx 1, set nx 0, set zy 1, set ny 0, set f 0, set no 0,
eval, output;

// ABS (100010)

// Positivo: +5
set x %B0000000000000101,
set y %B0000000000000000,
set zx 1, set nx 0, set zy 0, set ny 0, set f 1, set no 0,
eval, output;

// Negativo: -5
set x %B1111111111111011,
set y %B0000000000000000,
set zx 1, set nx 0, set zy 0, set ny 0, set f 1, set no 0,
eval, output;

// Cero
set x %B0000000000000000,
set y %B0000000000000000,
set zx 1, set nx 0, set zy 0, set ny 0, set f 1, set no 0,
eval, output;

// Máximo positivo representable: 32767
set x %B0111111111111111,
set y %B0000000000000000,
set zx 1, set nx 0, set zy 0, set ny 0, set f 1, set no 0,
eval, output;
