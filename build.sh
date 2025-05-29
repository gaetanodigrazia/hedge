#!/bin/bash

# Imposta le directory
BUILD_DIR=./build
DEST_DIR=./src/main/resources/static

cd api-graph-ui

# Esegui la build di React
echo "🏗️  Avvio della build di React..."
npm run build
if [ $? -ne 0 ]; then
  echo "❌ Errore durante la build di React."
  exit 1
fi
cd ..

# Verifica se la cartella di destinazione esiste
if [ -d "$DEST_DIR" ]; then
  echo "🧹 Pulizia della cartella di destinazione: $DEST_DIR"
  rm -rf "$DEST_DIR"/*
else
  echo "📁 Creazione della cartella di destinazione: $DEST_DIR"
  mkdir -p "$DEST_DIR"
fi

# Copia i nuovi file
echo "📂 Copia dei file da $BUILD_DIR a $DEST_DIR"
cp -R "./api-graph-ui/build"/* "$DEST_DIR"

echo "✅ Build e copia completate con successo."
