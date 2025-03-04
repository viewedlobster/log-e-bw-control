#!/bin/sh
BWHOME=~/Documents/Bitwig\ Studio/Extensions
EXTPATH=./target/MPK261.bwextension

[ ! -d "$BWHOME" ] && echo "$BWHOME not a directory, edit script and set it to the bitwig studio extensions directory" >> /dev/stderr && exit 1
[ ! -f "$EXTPATH" ] && echo "$EXTPATH not a file, edit script and set it to the path of build target .bwextension" >> /dev/stderr && exit 1

echo "copying $EXTPATH to $BWHOME"
cp "$EXTPATH" "$BWHOME"
