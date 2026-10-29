#!/usr/bin/env bash
# Uso: ./ensamblador.sh ruta/Prog.asm -> genera ruta/Prog.hack
DIR="$(cd "$(dirname "$0")" && pwd)"
if [ $# -ne 1 ]; then
  echo "Uso: ./ensamblador.sh <archivo.asm>"
  exit 2
fi
CP="$DIR/build/ensamblador"
ENTRADA="$1"
if command -v cygpath >/dev/null 2>&1; then
  CP="$(cygpath -w "$CP")"
  ENTRADA="$(cygpath -w "$ENTRADA")"
fi
exec java -cp "$CP" HackAssembler "$ENTRADA"