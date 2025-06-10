#!/bin/bash

# Imposta le directory
SRC_DIR=./hedge-dashboard
BUILD_DIR=$SRC_DIR/dist/hedge-dashboard
DEST_DIR=./src/main/resources/static

# Vai nella directory Angular
cd "$SRC_DIR" || { echo "❌ Cartella $SRC_DIR non trovata."; exit 1; }

# Esegui la build Angular
echo "🏗️  Avvio della build di Angular..."
npm run build -- --configuration production
if [ $? -ne 0 ]; then
  echo "❌ Errore durante la build di Angular."
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
cp -R "$BUILD_DIR"/* "$DEST_DIR"

echo "✅ Build e copia completate con successo."
