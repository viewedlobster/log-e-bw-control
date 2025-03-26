package se.loge.bwcontrol.common;

public class SPair<T1, T2> {
  public final T1 fst;
  public final T2 snd;

  private SPair(T1 t1, T2 t2) {
    this.fst = t1;
    this.snd = t2;
  }

  public static <T1, T2>  SPair<T1, T2> p(T1 t1, T2 t2) {
    return new SPair<>(t1, t2);
  }
}