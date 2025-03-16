package se.loge.bwcontrol.common;

import java.util.ArrayList;

public class SysexBuilder {
  private ArrayList<Byte> data;

  public SysexBuilder() {
    data = new ArrayList<>();
  }

  public void addRaw(byte[] b) {
    for (int i = 0; i < b.length; i++) {
      data.add(b[i]);
    }
  }

  public void add(int b) {
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