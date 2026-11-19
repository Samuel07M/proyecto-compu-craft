#!/usr/bin/env bash
# Compila el ensamblador y el desensamblador
set -e
cd "$(dirname "$0")"
rm -rf build
mkdir -p build/ensamblador build/desensamblador
javac -encoding UTF-8 -d build/ensamblador ensamblador/*.java
if ls desensamblador/*.java >/dev/null 2>&1; then
  javac -encoding UTF-8 -d build/desensamblador desensamblador/*.java
fi
echo "Compilación exitosa"