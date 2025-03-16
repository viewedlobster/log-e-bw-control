/*
 * Copyright (C) 2025 Ellen Arvidsson
 *
 * This file is part of log-e-bw-control.
 *
 * log-e-bw-control is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * log-e-bw-control is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with log-e-bw-control. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package se.loge.bwcontrol.common;

import java.util.ArrayList;

public class SysexBuilder {
  private ArrayList<Byte> data;

  public SysexBuilder() {
    data = new ArrayList<>();
  }

  public void add(int b) {
    // TODO: reenable check?
    //if  (0 > b || b > 0x7f) {
      //throw new HWError(
        //String.format("error: couldn't convert integer %x to 7 bit byte in sysex bilder", b));
    //}
    data.add((byte)b);
  }

  public void add(byte b) {
    data.add(b);
  }

  public void add(String s) {
    int i = 0;

    String hex = s.replace(" ", "");

    while (i < hex.length() - 1) {
      add(Integer.parseUnsignedInt(hex, i, i+2, 16));
      i += 2;
    }
  }

  public byte[] build() {
    byte[] bytes = new byte[data.size()];
    for (int i = 0; i < data.size(); i++) {
      bytes[i] = data.get(i);
    }
    return bytes;
  }

  public static int msb(int v) {
    return ((v >> 7) & 0xff);
  }

  public static int lsb(int v) {
    return (v & 0x7f);
  }

  /* borrowed this from
   * https://stackoverflow.com/questions/9655181/java-convert-a-byte-array-to-a-hex-string
   * 
   * it's not difficult, just couldn't be arsed
   */
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  public static String bytesToHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

}