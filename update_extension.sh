#!/bin/sh

# Copyright (C) 2025 Ellen Arvidsson
# 
# This file is part of log-e-bw-control.
# 
# log-e-bw-control is free software: you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as published by the Free
# Software Foundation, either version 3 of the License, or (at your option) any
# later version.
# 
# log-e-bw-control is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
# 
# You should have received a copy of the GNU Lesser General Public License
# along with log-e-bw-control. If not, see <https://www.gnu.org/licenses/>.

BWHOME=~/Documents/Bitwig\ Studio/Extensions
EXTPATH=./target/MPK261.bwextension

[ ! -d "$BWHOME" ] && echo "$BWHOME not a directory, edit script and set it to the bitwig studio extensions directory" >> /dev/stderr && exit 1
[ ! -f "$EXTPATH" ] && echo "$EXTPATH not a file, edit script and set it to the path of build target .bwextension" >> /dev/stderr && exit 1

echo "copying $EXTPATH to $BWHOME"
cp "$EXTPATH" "$BWHOME"
