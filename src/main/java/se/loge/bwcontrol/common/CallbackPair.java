package se.loge.bwcontrol.common;

public class CallbackPair<T1, T2> {
  public final T1 midi;
  public final T2 sysex;

  public CallbackPair(T1 t1, T2 t2) {
    midi = t1;
    sysex = t2;
  }
}
