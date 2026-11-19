#!/usr/bin/env bash
# Uso: ./desensamblador.sh ruta/Prog.hack -> genera ruta/ProgDis.asm
DIR="$(cd "$(dirname "$0")" && pwd)"
if [ $# -ne 1 ]; then
  echo "Uso: ./desensamblador.sh <archivo.hack>"
  exit 2
fi
CP="$DIR/build/desensamblador"
ENTRADA="$1"
if command -v cygpath >/dev/null 2>&1; then
  CP="$(cygpath -w "$CP")"
  ENTRADA="$(cygpath -w "$ENTRADA")"
fi
exec java -cp "$CP" HackDisassembler "$ENTRADA"